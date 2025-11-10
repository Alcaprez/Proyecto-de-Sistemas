package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Producto;
import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private Connection conexion;
    private ProductoDAO productoDAO;

    public VentaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            this.productoDAO = new ProductoDAO();
        } catch (Exception e) {
            System.err.println("❌ Error al conectar VentaDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO CORREGIDO - usa los nombres de columnas correctos de tu BD
    public int registrarVenta(Venta venta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        ResultSet generatedKeys = null;

        try {
            conn = this.conexion;
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. INSERTAR EN TABLA VENTA - ESTRUCTURA ACTUALIZADA
            String sqlVenta = "INSERT INTO venta (fecha_hora, total, id_cliente, id_empleado, id_metodo_pago, id_caja) VALUES (?, ?, ?, ?, ?, ?)";
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            // Usar TIMESTAMP para fecha_hora
            stmtVenta.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmtVenta.setDouble(2, venta.getTotal());

            // Convertir DNI a id_cliente (CHAR(8))
            String idCliente = venta.getDniCliente().length() <= 8
                    ? venta.getDniCliente() : venta.getDniCliente().substring(0, 8);
            stmtVenta.setString(3, idCliente);

            stmtVenta.setString(4, "1"); // id_empleado temporal (CHAR(8))
            stmtVenta.setInt(5, obtenerIdMetodoPago(venta.getMetodoPago()));
            stmtVenta.setInt(6, 1); // id_caja temporal

            int filasAfectadas = stmtVenta.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar venta, ninguna fila afectada.");
            }

            // 2. OBTENER ID GENERADO DE LA VENTA
            generatedKeys = stmtVenta.getGeneratedKeys();
            int idVentaGenerada = 0;
            if (generatedKeys.next()) {
                idVentaGenerada = generatedKeys.getInt(1);
            }

            // 3. INSERTAR DETALLES DE VENTA
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal, estado) VALUES (?, ?, ?, ?, ?, 'ACTIVO')";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                // Obtener ID del producto usando el código
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());

                if (idProducto == -1) {
                    throw new SQLException("Producto no encontrado con código: " + detalle.getProducto().getCodigo());
                }

                stmtDetalle.setInt(1, idVentaGenerada);
                stmtDetalle.setInt(2, idProducto);
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();

            // 4. ACTUALIZAR STOCK DE PRODUCTOS
            actualizarStockProductos(venta.getDetalleVenta(), conn);

            conn.commit(); // ✅ CONFIRMAR TRANSACCIÓN
            return idVentaGenerada;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // ❌ REVERTIR EN ERROR
            }
            throw e;
        } finally {
            // Cerrar recursos
            if (generatedKeys != null) {
                generatedKeys.close();
            }
            if (stmtDetalle != null) {
                stmtDetalle.close();
            }
            if (stmtVenta != null) {
                stmtVenta.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
        }
    }

    private void actualizarStockProductos(List<DetalleVenta> detalles, Connection conn) throws SQLException {
        String sql = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                stmt.setInt(1, detalle.getCantidad());
                stmt.setInt(2, idProducto);
                stmt.addBatch();
            }
            stmt.executeBatch();
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

    // ✅ MÉTODO PARA OBTENER ID DE MÉTODO DE PAGO
    private int obtenerIdMetodoPago(String metodoPago) throws SQLException {
        String sql = "SELECT id_metodo_pago FROM metodo_pago WHERE nombre LIKE ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "%" + metodoPago + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_metodo_pago");
            } else {
                // Si no encuentra, usar efectivo por defecto (ID 1)
                return 1;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID método pago: " + e.getMessage());
            e.printStackTrace();
            return 1; // Por defecto efectivo
        }
    }

    // ✅ MÉTODOS COMPATIBILIDAD
    public List<Venta> listar() {
        return new ArrayList<>();
    }
}
