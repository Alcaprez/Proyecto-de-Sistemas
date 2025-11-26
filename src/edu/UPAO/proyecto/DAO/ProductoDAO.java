package edu.UPAO.proyecto.DAO;

import java.sql.Connection;
import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Producto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductoDAO {

    private Connection conexion;

    public ProductoDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("ProductoDAO conectado");
        } catch (Exception e) {
            System.err.println("Error conectando ProductoDAO: " + e.getMessage());
        }
    }

    public int obtenerStockActual(int idProducto, int idSucursal) {
        String sql = "SELECT COALESCE(stock_actual, 0) as stock_actual "
                + "FROM inventario_sucursal "
                + "WHERE id_producto = ? AND id_sucursal = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setInt(2, idSucursal);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock_actual");
            } else {
                crearRegistroInventarioSucursal(idProducto, idSucursal, 0);
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo stock actual: " + e.getMessage());
            return 0;
        }
    }
    
    
    public Map<String, Integer> obtenerTopProductos(Date fechaInicio, Date fechaFin, String nombreSucursal, String categoria) {
        // Usamos LinkedHashMap para mantener el orden (Mayor a menor)
        Map<String, Integer> resultado = new LinkedHashMap<>();
        
        String sql = "SELECT p.nombre, SUM(dv.cantidad) as total_vendido " +
                     "FROM detalle_venta dv " +
                     "JOIN venta v ON dv.id_venta = v.id_venta " +
                     "JOIN producto p ON dv.id_producto = p.id_producto " +
                     "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "JOIN caja ca ON v.id_caja = ca.id_caja " +
                     "JOIN sucursal s ON ca.id_sucursal = s.id_sucursal " +
                     "WHERE v.fecha_hora BETWEEN ? AND ? ";

        // Filtros dinámicos
        if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
            sql += " AND s.nombre = ? ";
        }
        if (categoria != null && !categoria.equals("TODAS")) {
            sql += " AND c.nombre = ? ";
        }

        sql += " GROUP BY p.nombre ORDER BY total_vendido DESC LIMIT 10";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Convertir java.util.Date a java.sql.Timestamp para cubrir todo el día
            pst.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime())); 
            // Ajustar fecha fin al final del día
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            cal.set(java.util.Calendar.SECOND, 59);
            pst.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));

            int index = 3;
            if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
                pst.setString(index++, nombreSucursal);
            }
            if (categoria != null && !categoria.equals("TODAS")) {
                pst.setString(index++, categoria);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString("nombre"), rs.getInt("total_vendido"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top productos: " + e.getMessage());
        }
        return resultado;
    }
    
    public Map<String, Integer> obtenerVentasPorCategoria(Date fechaInicio, Date fechaFin, String nombreSucursal) {
        Map<String, Integer> resultado = new HashMap<>();
        
        String sql = "SELECT c.nombre, SUM(dv.cantidad) as total " +
                     "FROM detalle_venta dv " +
                     "JOIN venta v ON dv.id_venta = v.id_venta " +
                     "JOIN producto p ON dv.id_producto = p.id_producto " +
                     "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "JOIN caja ca ON v.id_caja = ca.id_caja " + // Asumiendo relación por caja o sucursal directa
                     "JOIN sucursal s ON ca.id_sucursal = s.id_sucursal " +
                     "WHERE v.fecha_hora BETWEEN ? AND ? ";

        if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
            sql += " AND s.nombre = ? ";
        }

        sql += " GROUP BY c.nombre";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            pst.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));

            if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
                pst.setString(3, nombreSucursal);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString("nombre"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }
        return resultado;
    }

    // ✅ LISTAR PRODUCTOS (SIN la columna vendidos)
    public List<Producto> listarPorSucursal(int idSucursal) {
        List<Producto> productos = new ArrayList<>();
        // CORREGIDO: Se eliminó 'p.vendidos' de la consulta
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "LEFT JOIN inventario_sucursal iss ON p.id_producto = iss.id_producto AND iss.id_sucursal = ? "
                + "WHERE p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = mapearProducto(rs);
                producto.setStockTemporal(rs.getInt("stock_sucursal"));

                java.sql.Date fechaCaducidad = rs.getDate("fecha_caducidad_sucursal");
                if (fechaCaducidad != null && !rs.wasNull()) {
                    producto.setFechaCaducidad(new java.util.Date(fechaCaducidad.getTime()));
                }

                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar productos por sucursal: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // ✅ BUSCAR POR CÓDIGO (SIN la columna vendidos)
    public Producto buscarPorCodigo(String codigo, int idSucursal) {
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "LEFT JOIN inventario_sucursal iss ON p.id_producto = iss.id_producto AND iss.id_sucursal = ? "
                + "WHERE p.codigo = ? AND p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Producto producto = mapearProducto(rs);
                producto.setStockTemporal(rs.getInt("stock_sucursal"));

                java.sql.Date fechaCaducidad = rs.getDate("fecha_caducidad_sucursal");
                if (fechaCaducidad != null && !rs.wasNull()) {
                    producto.setFechaCaducidad(new java.util.Date(fechaCaducidad.getTime()));
                }

                return producto;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar producto por código: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ BUSCAR POR NOMBRE (SIN la columna vendidos)
    public List<Producto> buscarPorNombre(String nombre, int idSucursal) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "LEFT JOIN inventario_sucursal iss ON p.id_producto = iss.id_producto AND iss.id_sucursal = ? "
                + "WHERE p.nombre LIKE ? AND p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = mapearProducto(rs);
                producto.setStockTemporal(rs.getInt("stock_sucursal"));

                java.sql.Date fechaCaducidad = rs.getDate("fecha_caducidad_sucursal");
                if (fechaCaducidad != null && !rs.wasNull()) {
                    producto.setFechaCaducidad(new java.util.Date(fechaCaducidad.getTime()));
                }

                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    public int obtenerStockSucursal(int idProducto, int idSucursal) {
        String sql = "SELECT stock_actual FROM inventario_sucursal WHERE id_producto = ? AND id_sucursal = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setInt(2, idSucursal);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock_actual");
            } else {
                crearRegistroInventarioSucursal(idProducto, idSucursal, 0);
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo stock por sucursal: " + e.getMessage());
            return 0;
        }
    }

    public int obtenerIdPorCodigo(String codigo) {
        String sql = "SELECT id_producto FROM producto WHERE codigo = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_producto");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID por código: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean eliminar(int idProducto) {
        String sql = "UPDATE producto SET estado = 'INACTIVO' WHERE id_producto = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error eliminando producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listarGlobal() {
        List<Producto> productos = new ArrayList<>();
        // Seleccionamos datos del producto y la SUMA del stock de todas las sucursales
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, c.nombre as categoria_nombre, "
                + "COALESCE(SUM(iss.stock_actual), 0) as stock_total " // Suma global
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "LEFT JOIN inventario_sucursal iss ON p.id_producto = iss.id_producto "
                + "WHERE p.estado = 'ACTIVO' "
                + "GROUP BY p.id_producto, p.nombre, p.stock_minimo, p.precio_compra, "
                + "p.precio_venta, p.estado, p.codigo, c.nombre";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id_producto"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setStockMinimo(rs.getInt("stock_minimo"));
                p.setPrecioCompra(rs.getDouble("precio_compra"));
                p.setPrecioVenta(rs.getDouble("precio_venta"));
                p.setEstado(rs.getString("estado"));
                p.setCategoria(rs.getString("categoria_nombre"));

                // Usamos el setter temporal para guardar la suma total del stock
                p.setStockTemporal(rs.getInt("stock_total"));

                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar productos globales: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo=?, nombre=?, stock_minimo=?, "
                + "precio_compra=?, precio_venta=?, estado=?, id_categoria=? "
                + "WHERE id_producto=?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStockMinimo());
            stmt.setDouble(4, producto.getPrecioCompra());
            stmt.setDouble(5, producto.getPrecioVenta());
            stmt.setString(6, producto.getEstado());
            stmt.setInt(7, 1);
            stmt.setInt(8, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, stock_minimo, "
                + "precio_compra, precio_venta, estado, id_categoria) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStockMinimo());
            stmt.setDouble(4, producto.getPrecioCompra());
            stmt.setDouble(5, producto.getPrecioVenta());
            stmt.setString(6, producto.getEstado());
            stmt.setInt(7, 1);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error insertando producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================
    private boolean crearRegistroInventarioSucursal(int idProducto, int idSucursal, int stockInicial) {
        String sql = "INSERT INTO inventario_sucursal (id_producto, id_sucursal, stock_actual) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, stockInicial);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error creando registro inventario_sucursal: " + e.getMessage());
            return false;
        }
    }

    // ✅ MAPEO CORREGIDO (Sin 'vendidos')
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setEstado(rs.getString("estado"));
        p.setCategoria(rs.getString("categoria_nombre"));

        // Ya no intentamos leer "vendidos" porque no existe. 
        // El objeto Producto se inicializa con 0 por defecto.
        p.setVendidos(0);

        return p;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }
}
