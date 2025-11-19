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
            System.out.println("✅ ProductoDAO conectado");
        } catch (Exception e) {
            System.err.println("❌ Error conectando ProductoDAO: " + e.getMessage());
        }
    }

// Listar productos por sucursal con creación automática
    public List<Producto> listarPorSucursal(int idSucursal) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, p.vendidos, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal " // ✅ MOVIDA A inventario_sucursal
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

                // ✅ MANEJAR FECHA CADUCIDAD DESDE INVENTARIO_SUCURSAL
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

    // ✅ MÉTODO: Buscar por código con sucursal
    public Producto buscarPorCodigo(String codigo, int idSucursal) {
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, p.vendidos, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal " // ✅ MOVIDA A inventario_sucursal
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

                // ✅ MANEJAR FECHA CADUCIDAD DESDE INVENTARIO_SUCURSAL
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

    // ✅ MÉTODO: Buscar por nombre con sucursal
    public List<Producto> buscarPorNombre(String nombre, int idSucursal) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.stock_minimo, "
                + "p.precio_compra, p.precio_venta, p.estado, "
                + "p.codigo, p.vendidos, c.nombre as categoria_nombre, "
                + "COALESCE(iss.stock_actual, 0) as stock_sucursal, "
                + "iss.fecha_caducidad as fecha_caducidad_sucursal " // ✅ MOVIDA A inventario_sucursal
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

                // ✅ MANEJAR FECHA CADUCIDAD DESDE INVENTARIO_SUCURSAL
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

    // ✅ MÉTODO: Obtener stock específico por sucursal
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

    // ✅ MÉTODO: Obtener ID por código
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

    // ✅ MÉTODO: Actualizar producto
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo=?, nombre=?, stock_minimo=?, "
                + "precio_compra=?, precio_venta=?, estado=?, id_categoria=? " // ❌ ELIMINADO: fecha_caducidad
                + "WHERE id_producto=?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStockMinimo());
            stmt.setDouble(4, producto.getPrecioCompra());
            stmt.setDouble(5, producto.getPrecioVenta());
            stmt.setString(6, producto.getEstado());
            stmt.setInt(7, 1); // Categoría por defecto
            stmt.setInt(8, producto.getId());

            // ❌ ELIMINADO: Manejo de fecha_caducidad
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ MÉTODO: Insertar producto
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, stock_minimo, "
                + "precio_compra, precio_venta, estado, id_categoria) " // ❌ ELIMINADO: fecha_caducidad
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setInt(3, producto.getStockMinimo());
            stmt.setDouble(4, producto.getPrecioCompra());
            stmt.setDouble(5, producto.getPrecioVenta());
            stmt.setString(6, producto.getEstado());
            stmt.setInt(7, 1); // Categoría por defecto

            // ❌ ELIMINADO: Manejo de fecha_caducidad
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

        try {
            p.setVendidos(rs.getInt("vendidos"));
        } catch (SQLException e) {
            p.setVendidos(0);
        }

        // La fecha de caducidad se establece desde inventario_sucursal en los métodos anteriores
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
