package edu.UPAO.proyecto.DAO;

import java.sql.Connection;
import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Producto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Connection conexion;

    public ProductoDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error al conectar con BD: " + e.getMessage());
        }
    }

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, p.fecha_caducidad, "
                + "p.codigo, c.nombre as categoria_nombre "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "WHERE p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = mapearProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // ✅ MÉTODO BUSCAR POR ID CORREGIDO
    public Producto buscarPorId(int id) {
        String sql = "SELECT id_producto, codigo, nombre, stock_actual, stock_minimo, precio_venta, estado, fecha_caducidad FROM producto WHERE id_producto = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error buscando producto ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, p.fecha_caducidad, "
                + "p.codigo, c.nombre as categoria_nombre "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "WHERE p.codigo = ? AND p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar producto por código: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarStock(String codigo, int cantidadVendida) {
        String sql = "UPDATE producto SET stock_actual = stock_actual - ? WHERE codigo = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidadVendida);
            stmt.setString(2, codigo);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ MÉTODO MAPEAR PRODUCTO CORREGIDO - sin campos que no existen
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setStock(rs.getInt("stock_actual"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setEstado(rs.getString("estado"));

        // Manejar fecha_caducidad (campo que SÍ existe)
        java.sql.Date fechaCaducidad = rs.getDate("fecha_caducidad");
        if (fechaCaducidad != null) {
            p.setFechaCaducidad(new java.util.Date(fechaCaducidad.getTime()));
        }

        p.setCategoria(rs.getString("categoria_nombre"));

        return p;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo=?, nombre=?, stock_actual=?, stock_minimo=?, "
                + "precio_compra=?, precio_venta=?, estado=?, fecha_caducidad=?, id_categoria=? "
                + "WHERE id_producto=?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStock());
            stmt.setInt(4, producto.getStockMinimo());
            stmt.setDouble(5, producto.getPrecioCompra());
            stmt.setDouble(6, producto.getPrecioVenta());
            stmt.setString(7, producto.getEstado());

            // Manejar fecha de caducidad
            if (producto.getFechaCaducidad() != null) {
                stmt.setDate(8, new java.sql.Date(producto.getFechaCaducidad().getTime()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }

            // ID categoría temporal - puedes mejorarlo obteniendo el ID real de la categoría
            stmt.setInt(9, 1); // Categoría por defecto

            stmt.setInt(10, producto.getId());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String generarSiguienteCodigo(String letraCategoria) {
        String sql = "SELECT MAX(codigo) as ultimo_codigo FROM producto WHERE codigo LIKE ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, letraCategoria + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String ultimoCodigo = rs.getString("ultimo_codigo");
                if (ultimoCodigo != null) {
                    try {
                        // Extraer el número y incrementar (formato: L00001)
                        int numero = Integer.parseInt(ultimoCodigo.substring(1));
                        return String.format("%s%05d", letraCategoria, numero + 1);
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Formato de código inválido: " + ultimoCodigo);
                    }
                }
            }
            // Si no hay códigos existentes para esta categoría
            return String.format("%s%05d", letraCategoria, 1);

        } catch (SQLException e) {
            System.err.println("❌ Error al generar código: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, p.fecha_caducidad, "
                + "p.codigo, c.nombre as categoria_nombre "
                + "FROM producto p "
                + "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria "
                + "WHERE p.nombre LIKE ? AND p.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // ✅ MÉTODO PRODUCTOS MÁS VENDIDOS CORREGIDO
    public List<Producto> productosMasVendidos() {
        List<Producto> productos = new ArrayList<>();
        // Por ahora, ordenar por stock (después implementaremos vendidos)
        String sql = "SELECT id_producto, codigo, nombre, stock_actual, stock_minimo, precio_venta, estado, fecha_caducidad FROM producto WHERE estado = 'ACTIVO' ORDER BY stock_actual ASC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en productosMasVendidos: " + e.getMessage());
        }
        return productos;
    }

    // ✅ MÉTODOS COMPATIBILIDAD (pueden quedar vacíos temporalmente)
    public void agregar(Producto producto) {
        // Implementar después
    }

    public void eliminar(int id) {
        // Implementar después  
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, stock_actual, stock_minimo, "
                + "precio_compra, precio_venta, estado, fecha_caducidad, id_categoria) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStock());
            stmt.setInt(4, producto.getStockMinimo());
            stmt.setDouble(5, producto.getPrecioCompra());
            stmt.setDouble(6, producto.getPrecioVenta());
            stmt.setString(7, producto.getEstado());

            if (producto.getFechaCaducidad() != null) {
                stmt.setDate(8, new java.sql.Date(producto.getFechaCaducidad().getTime()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }

            stmt.setInt(9, 1); // Categoría por defecto

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error insertando producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean estaBajoStock(int id) {
        Producto p = buscarPorId(id);
        return p != null && p.getStock() <= p.getStockMinimo();
    }

    public boolean eliminar(String codigo) {
        return false; // Temporal
    }
    
    public boolean cambiarEstado(String codigo, String nuevoEstado) {
        String sql = "UPDATE producto SET estado = ? WHERE codigo = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, codigo);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error cambiando estado del producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
