package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import javax.swing.JOptionPane;

public class GastoDAO {

    public boolean registrarGasto(double monto, String descripcion, int idCaja, int idSucursal) {
        // Validaciones de seguridad
        if (idCaja <= 0) {
            JOptionPane.showMessageDialog(null, "Error: No hay caja identificada para descontar el dinero.");
            return false;
        }

        Connection cn = new Conexion().establecerConexion();
        
        // Consultas SQL
        // 1. Insertar el movimiento (Tipo GASTO)
        String sqlMovimiento = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_sucursal, estado) " +
                               "VALUES ('GASTO OPERATIVO', ?, NOW(), ?, ?, ?, 'ACTIVO')";
        
        // 2. Restar el dinero del saldo de la caja
        String sqlCaja = "UPDATE caja SET saldo_final = saldo_final - ? WHERE id_caja = ?";

        try {
            cn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // A. Registrar Movimiento
            try (PreparedStatement psMov = cn.prepareStatement(sqlMovimiento)) {
                psMov.setDouble(1, monto);
                psMov.setString(2, descripcion);
                psMov.setInt(3, idCaja);
                psMov.setInt(4, idSucursal);
                psMov.executeUpdate();
            }

            // B. Actualizar Saldo Caja
            try (PreparedStatement psCaja = cn.prepareStatement(sqlCaja)) {
                psCaja.setDouble(1, monto);
                psCaja.setInt(2, idCaja);
                psCaja.executeUpdate();
            }

            cn.commit(); // CONFIRMAR CAMBIOS
            return true;

        } catch (SQLException e) {
            try { cn.rollback(); } catch (SQLException ex) {}
            System.err.println("Error registrando gasto: " + e.getMessage());
            return false;
        } finally {
            try { cn.close(); } catch (SQLException ex) {}
        }
    }
}