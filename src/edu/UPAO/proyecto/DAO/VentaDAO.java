package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Producto;
import edu.UPAO.proyecto.Modelo.MovimientoInventario; // ✅ NUEVA IMPORT
import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class VentaDAO {

    private Connection conexion;
    private ProductoDAO productoDAO;

    public VentaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            this.productoDAO = new ProductoDAO();
            System.out.println("Conectado");
        } catch (Exception e) {
            System.err.println("Error conectando DAO: " + e.getMessage());
        }
    }

    public List<Venta> listarPorRangoFecha(java.sql.Date fechaInicio, java.sql.Date fechaFin) {
        List<Venta> lista = new ArrayList<>();
        // SQL para obtener ventas en rango. Necesito JOINs si quiero mostrar nombres, pero por ahora con IDs basta para cumplir.
        // Mejor hacer un JOIN simple para mostrar algo decente si es posible, pero la tabla Venta tiene id_cliente char(8).
        String sql = "SELECT * FROM venta WHERE fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Ajustar fechas para cubrir el rango completo (inicio del día 1 al final del día 2)
            stmt.setTimestamp(1, new Timestamp(fechaInicio.getTime()));
            // Para fechaFin, sumamos 1 día o ajustamos hora a 23:59:59
            Calendar c = Calendar.getInstance();
            c.setTime(fechaFin);
            c.add(Calendar.DAY_OF_MONTH, 1);
            stmt.setTimestamp(2, new Timestamp(c.getTimeInMillis()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFecha(rs.getTimestamp("fecha_hora").toLocalDateTime());
                v.setTotal(rs.getDouble("total"));
                v.setIdCliente(rs.getString("id_cliente"));
                v.setIdEmpleado(rs.getString("id_empleado")); // String según modelo actualizado
                // v.setMetodoPago(...); // Necesitaría JOIN con metodo_pago o hacer otra consulta. 
                // Para optimizar, asumimos que en la tabla visual mostraremos el ID o haremos una carga perezosa/cache.
                // Dado el DAO previo, id_metodo_pago es int.
                v.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Venta> listarHistorialPorEmpleado(java.util.Date fechaInicio, java.util.Date fechaFin, String idEmpleado) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha_hora, v.total, v.id_cliente, v.id_empleado, mp.nombre AS metodo_pago "
                + "FROM venta v "
                + "LEFT JOIN metodo_pago mp ON v.id_metodo_pago = mp.id_metodo_pago "
                + "WHERE v.id_empleado = ? "
                + // <--- FILTRO CLAVE
                "AND v.fecha_hora BETWEEN ? AND ? "
                + "ORDER BY v.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);

            // Configurar fechas (Inicio del día 1 al final del día 2)
            Calendar cal = Calendar.getInstance();

            cal.setTime(fechaInicio);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            stmt.setTimestamp(2, new Timestamp(cal.getTimeInMillis()));

            cal.setTime(fechaFin);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            stmt.setTimestamp(3, new Timestamp(cal.getTimeInMillis()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFecha(rs.getTimestamp("fecha_hora").toLocalDateTime());
                v.setTotal(rs.getDouble("total"));
                v.setIdCliente(rs.getString("id_cliente"));
                v.setIdEmpleado(rs.getString("id_empleado"));
                v.setMetodoPago(rs.getString("metodo_pago"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar ventas del empleado: " + e.getMessage());
        }
        return lista;
    }

    public List<Venta> listarHistorial(java.util.Date fechaInicio, java.util.Date fechaFin) {
        List<Venta> lista = new ArrayList<>();
        // Usamos JOIN para obtener el nombre del método de pago directamente
        String sql = "SELECT v.id_venta, v.fecha_hora, v.total, v.id_cliente, v.id_empleado, mp.nombre AS nombre_pago "
                + "FROM venta v "
                + "LEFT JOIN metodo_pago mp ON v.id_metodo_pago = mp.id_metodo_pago "
                + "WHERE v.fecha_hora BETWEEN ? AND ? "
                + "ORDER BY v.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Convertir fechas de Java util a SQL Timestamp
            // Fecha Inicio: 00:00:00
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(fechaInicio);
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calInicio.set(Calendar.SECOND, 0);
            stmt.setTimestamp(1, new Timestamp(calInicio.getTimeInMillis()));

            // Fecha Fin: 23:59:59
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(fechaFin);
            calFin.set(Calendar.HOUR_OF_DAY, 23);
            calFin.set(Calendar.MINUTE, 59);
            calFin.set(Calendar.SECOND, 59);
            stmt.setTimestamp(2, new Timestamp(calFin.getTimeInMillis()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                // Convertir Timestamp SQL a LocalDateTime del modelo
                v.setFecha(rs.getTimestamp("fecha_hora").toLocalDateTime());
                v.setTotal(rs.getDouble("total"));
                v.setIdCliente(rs.getString("id_cliente"));
                v.setIdEmpleado(rs.getString("id_empleado"));
                v.setMetodoPago(rs.getString("nombre_pago")); // Asignamos el nombre recuperado

                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando historial de ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private void actualizarInventarioSucursal(List<DetalleVenta> detalles, int idSucursal, Connection conn) throws SQLException {
        String sql = "UPDATE inventario_sucursal SET stock_actual = stock_actual - ? WHERE id_producto = ? AND id_sucursal = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                stmt.setInt(1, detalle.getCantidad());
                stmt.setInt(2, idProducto);
                stmt.setInt(3, idSucursal);
                stmt.addBatch();

                System.out.println("🔁 Actualizando inventario_sucursal - Producto: " + idProducto
                        + ", Sucursal: " + idSucursal + ", Cantidad: -" + detalle.getCantidad());
            }
            stmt.executeBatch();
            System.out.println("✅ Inventario_sucursal actualizado");
        }
    }

    public int registrarVenta(Venta venta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;

        try {
            conn = this.conexion;
            conn.setAutoCommit(false);

            // ✅ CONFIGURAR TIMEOUT MÁS CORTO
            conn.setNetworkTimeout(java.util.concurrent.Executors.newSingleThreadExecutor(), 10000); // 10 segundos

            // 1. DATOS RÁPIDOS
            ClienteDAO clienteDAO = new ClienteDAO();
            String idClienteReal = clienteDAO.obtenerIdClienteParaVenta(venta.getDniCliente());
            clienteDAO.cerrarConexion();

            int idSucursal = venta.getIdSucursal();
            int idMetodoPago = obtenerIdMetodoPago(venta.getMetodoPago());
            int idCaja = obtenerIdCajaActiva(idSucursal);

            // 2. INSERTAR VENTA
            String sqlVenta = "INSERT INTO venta (fecha_hora, total, id_cliente, id_empleado, id_metodo_pago, id_caja, id_sucursal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            // ✅ USAR TIMESTAMP CON CALENDAR DE LIMA
            java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/Lima"));
            stmtVenta.setTimestamp(1, new Timestamp(System.currentTimeMillis()), cal);
            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, idClienteReal);
            stmtVenta.setString(4, venta.getIdEmpleado());
            stmtVenta.setInt(5, idMetodoPago);
            stmtVenta.setInt(6, idCaja);
            stmtVenta.setInt(7, idSucursal);

            int filasAfectadas = stmtVenta.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar venta, ninguna fila afectada.");
            }

            int idVentaGenerada = obtenerIdGenerado(stmtVenta);

            // 3. INSERTAR DETALLES (BATCH OPTIMIZADO)
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                if (idProducto == -1) {
                    throw new SQLException("Producto no encontrado: " + detalle.getProducto().getCodigo());
                }

                stmtDetalle.setInt(1, idVentaGenerada);
                stmtDetalle.setInt(2, idProducto);
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.addBatch();
            }
            stmtDetalle.executeBatch();
            if (venta.getIdCupon() != null && venta.getIdCupon() > 0) {
                CuponDAO.incrementarUso(venta.getIdCupon());
            }
            // 4. ACTUALIZAR STOCK (BATCH)
            actualizarInventarioSucursal(venta.getDetalleVenta(), idSucursal, conn);

            // ✅ 5. MOVIMIENTO CAJA EN LA MISMA TRANSACCIÓN (EVITA LOCKS)
            registrarMovimientoCajaDirecto(conn, idCaja, idVentaGenerada, venta.getTotal(), idSucursal, venta.getMetodoPago());

            conn.commit();
            System.out.println("✅ Venta registrada exitosamente - ID: " + idVentaGenerada);
            return idVentaGenerada;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            // ✅ CERRAR RECURSOS INMEDIATAMENTE
            if (stmtDetalle != null) try {
                stmtDetalle.close();
            } catch (SQLException e) {
            }
            if (stmtVenta != null) try {
                stmtVenta.close();
            } catch (SQLException e) {
            }
            if (conn != null) try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
    }

    private void registrarMovimientoCajaDirecto(Connection conn, int idCaja, int idVenta, double monto, int idSucursal, String metodoPago) throws SQLException {
        String sql = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_venta, id_sucursal, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/Lima"));

            stmt.setString(1, "VENTA");
            stmt.setDouble(2, monto); // ✅ USAR EL MONTO CORRECTO DE LA VENTA
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()), cal);
            stmt.setString(4, "Venta ID: " + idVenta + " - " + metodoPago);
            stmt.setInt(5, idCaja);
            stmt.setInt(6, idVenta);
            stmt.setInt(7, idSucursal);
            stmt.setString(8, "ACTIVO");

            stmt.executeUpdate();
            System.out.println("✅ Movimiento caja registrado - Monto: " + monto);
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

    private void registrarMovimientosInventario(List<DetalleVenta> detalles, int idSucursal, int idVenta, Connection conn) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto, id_sucursal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            InventarioSucursalDAO invSucursalDAO = new InventarioSucursalDAO();

            for (DetalleVenta detalle : detalles) {
                int idProducto = productoDAO.obtenerIdPorCodigo(detalle.getProducto().getCodigo());
                int stockAnterior = invSucursalDAO.obtenerStock(idProducto, idSucursal);
                int stockNuevo = stockAnterior - detalle.getCantidad();

                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setString(2, "SALIDA");
                stmt.setInt(3, detalle.getCantidad());
                stmt.setInt(4, stockAnterior);
                stmt.setInt(5, stockNuevo);
                stmt.setString(6, "ACTIVO");
                stmt.setInt(7, idProducto);
                stmt.setInt(8, idSucursal); // ✅ NUEVO PARÁMETRO
                stmt.addBatch();
            }
            stmt.executeBatch();
            invSucursalDAO.cerrarConexion();
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

    private int obtenerIdCajaActiva(int idSucursal) throws SQLException {
        // ✅ AHORA FILTRAMOS POR SUCURSAL PARA EVITAR ERRORES
        String sql = "SELECT id_caja FROM caja WHERE estado = 'ABIERTA' AND id_sucursal = ? ORDER BY id_caja DESC LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_caja");
            } else {
                System.out.println("⚠️ No hay caja abierta para sucursal " + idSucursal + ". Creando una...");
                return crearCajaAutomaticamente(idSucursal); // Pasamos la sucursal
            }
        }
    }

    private int crearCajaAutomaticamente(int idSucursal) throws SQLException {
        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal) VALUES (NOW(), 0.00, 0.00, 'ABIERTA', ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idSucursal); // ✅ Usamos la sucursal correcta
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo crear la caja automática.");
                }
            }
        }
    }

    private int obtenerIdGenerado(PreparedStatement stmt) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta generada.");
            }
        }
    }

    public List<Venta> listar() {
        return new ArrayList<>();
    }
}
