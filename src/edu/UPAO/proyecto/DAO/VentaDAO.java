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
            conn.setAutoCommit(false);

            // ✅ OBTENER ID_CLIENTE REAL (no usar DNI directamente)
            ClienteDAO clienteDAO = new ClienteDAO();
            String idClienteReal = clienteDAO.obtenerIdClienteParaVenta(venta.getDniCliente());
            clienteDAO.cerrarConexion();

            // 1. INSERTAR EN TABLA VENTA
            String sqlVenta = "INSERT INTO venta (fecha_hora, total, id_cliente, id_empleado, id_metodo_pago, id_caja) VALUES (?, ?, ?, ?, ?, ?)";
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            stmtVenta.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, idClienteReal);
            stmtVenta.setString(4, venta.getIdEmpleado()); // ✅ SOLO UNA VEZ - índice 4
            stmtVenta.setInt(5, obtenerIdMetodoPago(venta.getMetodoPago())); // ✅ índice 5  
            int idCaja = obtenerIdCajaActiva();
            stmtVenta.setInt(6, idCaja);

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
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleVenta detalle : venta.getDetalleVenta()) {
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

            conn.commit();
            return idVentaGenerada;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
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
                conn.setAutoCommit(true);
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

    //  MÉTODO PARA OBTENER ID DE MÉTODO DE PAGO
    private int obtenerIdMetodoPago(String metodoPago) throws SQLException {
        System.out.println("🔍 Buscando ID para método de pago: " + metodoPago);

        // Buscar por nombre exacto primero
        String sql = "SELECT id_metodo_pago FROM metodo_pago WHERE nombre = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, metodoPago);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_metodo_pago");
                System.out.println("✅ Método encontrado en BD: " + metodoPago + " -> ID: " + id);
                return id;
            } else {
                // Si no encuentra, buscar por coincidencia parcial
                System.err.println("⚠️ No se encontró método pago exacto: " + metodoPago);

                String sqlLike = "SELECT id_metodo_pago FROM metodo_pago WHERE nombre LIKE ? LIMIT 1";
                try (PreparedStatement stmtLike = conexion.prepareStatement(sqlLike)) {
                    stmtLike.setString(1, "%" + metodoPago + "%");
                    ResultSet rsLike = stmtLike.executeQuery();

                    if (rsLike.next()) {
                        int id = rsLike.getInt("id_metodo_pago");
                        System.out.println("✅ Encontrado por LIKE: " + metodoPago + " -> ID: " + id);
                        return id;
                    } else {
                        // Por defecto usar Efectivo (ID 1)
                        System.err.println("❌ No se encontró método pago. Usando Efectivo por defecto.");
                        return 1;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID método pago: " + e.getMessage());
            return 1; // Por defecto efectivo
        }
    }

    private int obtenerIdCajaActiva() throws SQLException {
        String sql = "SELECT id_caja FROM caja WHERE estado = 'ABIERTA' LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idCaja = rs.getInt("id_caja");
                System.out.println("✅ Caja activa encontrada: " + idCaja);
                return idCaja;
            } else {
                // Si no hay caja activa, crear una automáticamente
                System.out.println("⚠️ No hay caja activa. Creando una nueva...");
                return crearCajaAutomaticamente();
            }
        }
    }

    private int crearCajaAutomaticamente() throws SQLException {
        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal) VALUES (NOW(), 0.00, 0.00, 'ABIERTA', 1)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la caja automáticamente");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idCaja = generatedKeys.getInt(1);
                    System.out.println("✅ Caja creada automáticamente con ID: " + idCaja);
                    return idCaja;
                } else {
                    throw new SQLException("No se pudo obtener el ID de la caja creada");
                }
            }
        }
    }

    public List<Venta> listar() {
        return new ArrayList<>();
    }

}
