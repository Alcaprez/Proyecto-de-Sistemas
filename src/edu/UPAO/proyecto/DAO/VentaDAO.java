package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Producto;
import edu.UPAO.proyecto.Modelo.MovimientoInventario; // ✅ NUEVA IMPORT
import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class VentaDAO {

    private Connection conexion;
    private ProductoDAO productoDAO;

    public VentaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("Conectado");
        } catch (Exception e) {
            System.err.println("Error conectando DAO: " + e.getMessage());
        }
    }



    public int registrarVenta(Venta venta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        ResultSet generatedKeys = null;

        try {
            conn = this.conexion;
            conn.setAutoCommit(false);

            // 1. OBTENER ID_CLIENTE REAL
            ClienteDAO clienteDAO = new ClienteDAO();
            String idClienteReal = clienteDAO.obtenerIdClienteParaVenta(venta.getDniCliente());
            clienteDAO.cerrarConexion();

            int idSucursal = obtenerSucursalEmpleado(venta.getIdEmpleado());
            System.out.println("🏪 Venta registrada en sucursal: " + idSucursal);

            // 2. OBTENER ID_MÉTODO_PAGO Y ID_CAJA
            int idMetodoPago = obtenerIdMetodoPago(venta.getMetodoPago());
            int idCaja = obtenerIdCajaActiva();

            // 3. INSERTAR EN TABLA VENTA
            String sqlVenta = "INSERT INTO venta (fecha_hora, total, id_cliente, id_empleado, id_metodo_pago, id_caja, id_sucursal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            stmtVenta.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, idClienteReal);
            stmtVenta.setString(4, venta.getIdEmpleado());
            stmtVenta.setInt(5, idMetodoPago);
            stmtVenta.setInt(6, idCaja);
            stmtVenta.setInt(7, idSucursal); // ✅ NUEVO PARÁMETRO

            int filasAfectadas = stmtVenta.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar venta, ninguna fila afectada.");
            }

            // ✅ 4. OBTENER ID GENERADO DE LA VENTA
            generatedKeys = stmtVenta.getGeneratedKeys();
            int idVentaGenerada = 0;
            if (generatedKeys.next()) {
                idVentaGenerada = generatedKeys.getInt(1);
            }

            // ✅ 5. INSERTAR DETALLES DE VENTA
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            // ✅ 6. PREPARAR DATOS PARA MOVIMIENTOS DE INVENTARIO
            Map<Integer, Integer> stockAnteriorPorProducto = new HashMap<>();

            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());

                if (idProducto == -1) {
                    throw new SQLException("Producto no encontrado con código: " + detalle.getProducto().getCodigo());
                }

                // ✅ OBTENER STOCK ANTERIOR PARA MOVIMIENTO INVENTARIO
                int stockAnterior = productoDAO.obtenerStockActual(idProducto);
                stockAnteriorPorProducto.put(idProducto, stockAnterior);

                // Insertar detalle de venta
                stmtDetalle.setInt(1, idVentaGenerada);
                stmtDetalle.setInt(2, idProducto);
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();

            // ✅ 7. ACTUALIZAR STOCK DE PRODUCTOS
            actualizarStockProductos(venta.getDetalleVenta(), conn);

            // ✅ 8. REGISTRAR MOVIMIENTOS DE INVENTARIO (NUEVA FUNCIONALIDAD)
            registrarMovimientosInventario(venta.getDetalleVenta(), stockAnteriorPorProducto, idVentaGenerada, conn);

            conn.commit();

            try {
                MovimientoCajaDAO movimientoDAO = new MovimientoCajaDAO();
                boolean movimientoRegistrado = movimientoDAO.registrarMovimientoCajaVenta(
                        venta.getTotal(),
                        idVentaGenerada,
                        idSucursal,
                        venta.getMetodoPago()
                );

                if (movimientoRegistrado) {
                    System.out.println("✅ Movimiento de caja registrado para venta: " + idVentaGenerada);
                } else {
                    System.err.println("⚠️ No se pudo registrar movimiento de caja para venta: " + idVentaGenerada);
                }
            } catch (Exception e) {
                System.err.println("❌ Error registrando movimiento de caja: " + e.getMessage());
                // NO hacer rollback aquí, la venta ya se registró
            }

            System.out.println("✅ Venta registrada exitosamente - ID: " + idVentaGenerada);
            return idVentaGenerada;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                System.err.println("❌ Rollback realizado por error: " + e.getMessage());
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
        }
    }

    private int obtenerSucursalEmpleado(String idEmpleado) throws SQLException {
        String sql = "SELECT id_sucursal FROM empleado WHERE id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_sucursal");
            } else {
                throw new SQLException("No se encontró sucursal para empleado: " + idEmpleado);
            }
        }
    }

    private void registrarMovimientosInventario(List<DetalleVenta> detalles,
            Map<Integer, Integer> stockAnteriorPorProducto,
            int idVentaGenerada,
            Connection conn) throws SQLException {

        // ✅ USAR LA MISMA CONEXIÓN DE LA TRANSACCIÓN
        String sql = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                int stockAnterior = stockAnteriorPorProducto.get(idProducto);
                int stockNuevo = stockAnterior - detalle.getCantidad();

                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setString(2, "SALIDA");
                stmt.setInt(3, detalle.getCantidad());
                stmt.setInt(4, stockAnterior);
                stmt.setInt(5, stockNuevo);
                stmt.setString(6, "ACTIVO");
                stmt.setInt(7, idProducto);
                stmt.addBatch();

                System.out.println("📦 Registrando movimiento - Producto: " + idProducto
                        + ", Stock: " + stockAnterior + " → " + stockNuevo);
            }
            stmt.executeBatch();
            System.out.println("✅ Movimientos de inventario registrados en transacción");

        } catch (SQLException e) {
            System.err.println("❌ Error registrando movimientos inventario: " + e.getMessage());
            throw e; // ✅ IMPORTANTE: Propagar el error para hacer rollback
        }
    }

    // MÉTODOS EXISTENTES (MANTENIDOS)
    private void actualizarStockProductos(List<DetalleVenta> detalles, Connection conn) throws SQLException {
        String sql = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                stmt.setInt(1, detalle.getCantidad());
                stmt.setInt(2, idProducto);
                stmt.addBatch();

                System.out.println("🔁 Actualizando stock - Producto: " + idProducto
                        + ", Cantidad: -" + detalle.getCantidad());
            }
            stmt.executeBatch();
            System.out.println("✅ Stock de productos actualizado");
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

    // MÉTODO MEJORADO: OBTENER ID DE MÉTODO DE PAGO
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

    // MÉTODO MEJORADO: OBTENER ID DE CAJA ACTIVA
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

    // MÉTODO COMPATIBILIDAD
    public List<Venta> listar() {
        return new ArrayList<>();
    }
}
