// edu.UPAO.proyecto.DAO.MovimientoInventarioDAO.java
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.MovimientoInventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    private Connection conexion;

    public MovimientoInventarioDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conectando MovimientoInventarioDAO: " + e.getMessage());
        }
    }

    // Registrar movimiento (ADAPTADO A TU BD)
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        String sql = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(movimiento.getFechaHora()));
            stmt.setString(2, movimiento.getTipo());
            stmt.setInt(3, movimiento.getCantidad());
            stmt.setInt(4, movimiento.getStockAnterior());
            stmt.setInt(5, movimiento.getStockNuevo());
            stmt.setString(6, movimiento.getEstado());
            stmt.setInt(7, movimiento.getIdProducto());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrando movimiento inventario: " + e.getMessage());
            return false;
        }
    }

    // Obtener último movimiento de un producto para saber stock actual
    public MovimientoInventario obtenerUltimoMovimiento(int idProducto) {
        String sql = "SELECT * FROM movimiento_inventario WHERE id_producto = ? ORDER BY fecha_hora DESC LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearMovimiento(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo último movimiento: " + e.getMessage());
        }
        return null;
    }

    // Obtener stock actual (stock_nuevo del último movimiento)
    public int obtenerStockActual(int idProducto) {
        MovimientoInventario ultimoMovimiento = obtenerUltimoMovimiento(idProducto);
        if (ultimoMovimiento != null) {
            return ultimoMovimiento.getStockNuevo();
        }

        // Si no hay movimientos, consultar stock de la tabla producto
        return obtenerStockDesdeProducto(idProducto);
    }

    // Obtener movimientos por producto
    public List<MovimientoInventario> obtenerMovimientosPorProducto(int idProducto) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimiento_inventario WHERE id_producto = ? ORDER BY fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo movimientos por producto: " + e.getMessage());
        }
        return movimientos;
    }

    // Obtener movimientos por fecha
    public List<MovimientoInventario> obtenerMovimientosPorFecha(Date fecha) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimiento_inventario WHERE DATE(fecha_hora) = ? ORDER BY fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, fecha);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo movimientos por fecha: " + e.getMessage());
        }
        return movimientos;
    }

    // Método auxiliar para obtener stock desde tabla producto (backup)
    private int obtenerStockDesdeProducto(int idProducto) {
        String sql = "SELECT stock_actual FROM producto WHERE id_producto = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock_actual");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo stock desde producto: " + e.getMessage());
        }
        return 0;
    }

    // Mapear ResultSet a objeto MovimientoInventario
    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimientoInventario(rs.getInt("id_movimiento_inventario"));
        mov.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        mov.setTipo(rs.getString("tipo"));
        mov.setCantidad(rs.getInt("cantidad"));
        mov.setStockAnterior(rs.getInt("stock_anterior"));
        mov.setStockNuevo(rs.getInt("stock_nuevo"));
        mov.setEstado(rs.getString("estado"));
        mov.setIdProducto(rs.getInt("id_producto"));
        return mov;
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
