package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Caja;
import java.sql.*;

public class CajaDAO {
    private Connection conexion;

    public CajaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conexión CajaDAO: " + e.getMessage());
        }
    }

    // ✅ MÉTODO SOLUCIÓN: Suma ABSOLUTA de todas las ventas de la sucursal (Histórico Total)
    public double obtenerVentasTotalesHistoricas(int idSucursal) {
        // Sumamos directamente de la tabla VENTA, ignorando cierres de caja
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error sumando ventas históricas: " + e.getMessage());
        }
        return 0.0;
    }

    // --- MÉTODOS ESTÁNDAR NECESARIOS ---

    public Caja obtenerCajaAbierta(int idSucursal) {
        String sql = "SELECT * FROM caja WHERE id_sucursal = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Caja c = new Caja();
                c.setIdCaja(rs.getInt("id_caja"));
                c.setFechaApertura(rs.getTimestamp("fecha_hora_apertura"));
                // IMPORTANTE: Aunque la BD tenga un saldo inicial guardado, 
                // nosotros usaremos el cálculo histórico en la interfaz.
                c.setSaldoInicial(rs.getDouble("saldo_inicial")); 
                c.setEstado(rs.getString("estado"));
                return c;
            }
        } catch (SQLException e) {}
        return null;
    }

    public boolean abrirCaja(int idSucursal, double saldoInicial) {
        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal) VALUES (NOW(), ?, 0, 'ABIERTA', ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoInicial);
            stmt.setInt(2, idSucursal);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean cerrarCaja(int idCaja, double saldoFinal) {
        String sql = "UPDATE caja SET fecha_hora_cierre = NOW(), saldo_final = ?, estado = 'CERRADA' WHERE id_caja = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoFinal);
            stmt.setInt(2, idCaja);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public double obtenerTotalVentasSesion(int idCaja) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja WHERE id_caja = ? AND tipo = 'VENTA'";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCaja);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {}
        return 0.0;
    }
}