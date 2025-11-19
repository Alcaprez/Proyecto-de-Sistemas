package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Compra;
import edu.UPAO.proyecto.Modelo.DetalleCompra;
import edu.UPAO.proyecto.Modelo.MovimientoInventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    private Connection conexion;
    private ProductoDAO productoDAO;

    public CompraDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("Conectado");
        } catch (Exception e) {
            System.err.println("Error conectando : " + e.getMessage());
        }
    }

    // ✅ REGISTRAR COMPRA COMPLETA
    public int registrarCompra(Compra compra) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtCompra = null;
        PreparedStatement stmtDetalle = null;
        ResultSet generatedKeys = null;

        try {
            conn = this.conexion;
            conn.setAutoCommit(false);

            // 1. INSERTAR EN TABLA COMPRA
            String sqlCompra = "INSERT INTO compra (fecha_hora, total, id_proveedor, id_empleado, id_sucursal) VALUES (?, ?, ?, ?, ?)";
            stmtCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);

            stmtCompra.setTimestamp(1, new Timestamp(compra.getFechaHora().getTime()));
            stmtCompra.setDouble(2, compra.getTotal());
            stmtCompra.setString(3, compra.getIdProveedor());
            stmtCompra.setString(4, compra.getIdEmpleado());
            stmtCompra.setInt(5, compra.getIdSucursal());

            int filasAfectadas = stmtCompra.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar compra, ninguna fila afectada.");
            }

            // 2. OBTENER ID GENERADO DE LA COMPRA
            generatedKeys = stmtCompra.getGeneratedKeys();
            int idCompraGenerada = 0;
            if (generatedKeys.next()) {
                idCompraGenerada = generatedKeys.getInt(1);
            }

            // 3. INSERTAR DETALLES DE COMPRA
            String sqlDetalle = "INSERT INTO detalle_compra (id_compra, id_producto, cantidad, precio_compra, subtotal) VALUES (?, ?, ?, ?, ?)";
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleCompra detalle : compra.getDetalles()) {
                stmtDetalle.setInt(1, idCompraGenerada);
                stmtDetalle.setInt(2, detalle.getIdProducto());
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioCompra());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();

            // 4. ACTUALIZAR STOCK DE PRODUCTOS
            actualizarStockProductos(compra.getDetalles(), conn);

            // 5. REGISTRAR MOVIMIENTO DE INVENTARIO
            registrarMovimientosInventarioCompra(compra.getDetalles(), idCompraGenerada, conn);

            // 6. REGISTRAR MOVIMIENTO DE CAJA
            registrarMovimientoCajaCompra(compra, idCompraGenerada, conn);

            conn.commit();

            System.out.println("Compra registrada exitosamente - ID: " + idCompraGenerada);
            return idCompraGenerada;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                System.err.println("Rollback realizado por error: " + e.getMessage());
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
            if (stmtCompra != null) {
                stmtCompra.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    // ✅ ACTUALIZAR STOCK (AUMENTAR)
    private void actualizarStockProductos(List<DetalleCompra> detalles, Connection conn) throws SQLException {
        String sql = "UPDATE producto SET stock_actual = stock_actual + ? WHERE id_producto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleCompra detalle : detalles) {
                stmt.setInt(1, detalle.getCantidad());
                stmt.setInt(2, detalle.getIdProducto());
                stmt.addBatch();

                System.out.println("🔁 Actualizando stock + COMPRA - Producto: " + detalle.getIdProducto() + ", Cantidad: +" + detalle.getCantidad());
            }
            stmt.executeBatch();
            System.out.println("✅ Stock de productos actualizado por compra");
        }
    }

    // ✅ REGISTRAR MOVIMIENTOS DE INVENTARIO (COMPRA)
    private void registrarMovimientosInventarioCompra(List<DetalleCompra> detalles, int idCompraGenerada, Connection conn) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetalleCompra detalle : detalles) {
                int stockAnterior = productoDAO.obtenerStockActual(detalle.getIdProducto());
                int stockNuevo = stockAnterior + detalle.getCantidad();

                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setString(2, "ENTRADA");
                stmt.setInt(3, detalle.getCantidad());
                stmt.setInt(4, stockAnterior);
                stmt.setInt(5, stockNuevo);
                stmt.setString(6, "ACTIVO");
                stmt.setInt(7, detalle.getIdProducto());
                stmt.addBatch();

                System.out.println("Registrando movimiento COMPRA - Producto: " + detalle.getIdProducto() + ", Stock: " + stockAnterior + " → " + stockNuevo);
            }
            stmt.executeBatch();
            System.out.println("Movimientos de inventario por compra registrados");
        }
    }

    // ✅ REGISTRAR MOVIMIENTO DE CAJA (COMPRA)
    private void registrarMovimientoCajaCompra(Compra compra, int idCompraGenerada, Connection conn) throws SQLException {
        String sql = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_compra, id_sucursal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "COMPRA");
            stmt.setDouble(2, compra.getTotal());
            stmt.setTimestamp(3, new Timestamp(compra.getFechaHora().getTime()));
            stmt.setString(4, "Compra de productos - Proveedor: " + compra.getIdProveedor());
            stmt.setInt(5, obtenerIdCajaActiva());
            stmt.setInt(6, idCompraGenerada);
            stmt.setInt(7, compra.getIdSucursal());

            stmt.executeUpdate();
            System.out.println("💰 Movimiento de caja por compra registrado: S/ " + compra.getTotal());
        }
    }

    // ✅ OBTENER ID DE CAJA ACTIVA
    private int obtenerIdCajaActiva() throws SQLException {
        String sql = "SELECT id_caja FROM caja WHERE estado = 'ABIERTA' LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_caja");
            } else {
                return 1; // Caja por defecto
            }
        }
    }

    // ✅ MÉTODOS PARA RENTABILIDAD
    public double obtenerTotalCompras(java.util.Date fechaInicio, java.util.Date fechaFin, Integer idSucursal) {
        String sql = "SELECT COALESCE(SUM(total), 0) as total_compras FROM compra WHERE DATE(fecha_hora) BETWEEN DATE(?) AND DATE(?)";

        if (idSucursal != null) {
            sql += " AND id_sucursal = ?";
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            if (idSucursal != null) {
                stmt.setInt(3, idSucursal);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_compras");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo total de compras: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Object[]> obtenerMovimientosCompras(java.util.Date fechaInicio, java.util.Date fechaFin, String sucursal) {
        List<Object[]> movimientos = new ArrayList<>();

        String sql = "SELECT DATE(c.fecha_hora) as fecha, s.nombre_sucursal, "
                + "GROUP_CONCAT(p.nombre SEPARATOR ', ') as productos, "
                + "SUM(dc.cantidad) as cantidad, "
                + "0 as ingreso, "
                + "SUM(dc.subtotal) as costo, "
                + "SUM(dc.subtotal) * -1 as ganancia "
                + "FROM compra c "
                + "INNER JOIN detalle_compra dc ON c.id_compra = dc.id_compra "
                + "INNER JOIN producto p ON dc.id_producto = p.id_producto "
                + "INNER JOIN sucursal s ON c.id_sucursal = s.id_sucursal "
                + "WHERE DATE(c.fecha_hora) BETWEEN DATE(?) AND DATE(?) "
                + "AND (? = 'TODAS' OR s.nombre_sucursal = ?) "
                + "GROUP BY c.id_compra, DATE(c.fecha_hora), s.nombre_sucursal "
                + "ORDER BY c.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, sucursal);
            stmt.setString(4, sucursal);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] movimiento = {
                    rs.getDate("fecha"),
                    rs.getString("nombre_sucursal"),
                    limitarTexto(rs.getString("productos"), 50),
                    rs.getInt("cantidad"),
                    String.format("S/ %.2f", rs.getDouble("ingreso")),
                    String.format("S/ %.2f", rs.getDouble("costo")),
                    String.format("S/ %.2f", rs.getDouble("ganancia"))
                };
                movimientos.add(movimiento);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo movimientos de compras: " + e.getMessage());
        }
        return movimientos;
    }

    private String limitarTexto(String texto, int maxLength) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
}
