package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class NominaDAO {

    // En edu.UPAO.proyecto.DAO.NominaDAO
    public List<Object[]> listarNominaMes(int idSucursal, String mes, int anio, String textoBusqueda) {
        List<Object[]> lista = new ArrayList<>();

        // MODIFICACIÓN CLAVE:
        // 1. "(? = 0 OR e.id_sucursal = ?)" -> Si idSucursal es 0, trae todo. Si no, filtra.
        // 2. "AND e.rol <> 'GERENTE'" -> Oculta al gerente de la lista de pagos.
        String sql = "SELECT e.id_empleado, p.nombres, p.apellidos, e.rol, e.sueldo, "
                + "CASE WHEN pn.id_pago IS NOT NULL THEN 'PAGADO' ELSE 'PENDIENTE' END as estado_pago, "
                + "pn.fecha_pago "
                + "FROM empleado e "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "LEFT JOIN pago_nomina pn ON e.id_empleado = pn.id_empleado AND pn.mes = ? AND pn.anio = ? "
                + "WHERE (? = 0 OR e.id_sucursal = ?) "
                + "AND e.estado = 'ACTIVO' "
                + "AND e.rol <> 'GERENTE' "
                + "AND (p.nombres LIKE ? OR p.apellidos LIKE ?)";

        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, mes);
            ps.setInt(2, anio);
            ps.setInt(3, idSucursal); // Para la condición (? = 0)
            ps.setInt(4, idSucursal); // Para el filtro (id_sucursal = ?)
            ps.setString(5, "%" + textoBusqueda + "%");
            ps.setString(6, "%" + textoBusqueda + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("id_empleado"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("rol"),
                    rs.getDouble("sueldo"),
                    rs.getString("estado_pago"),
                    rs.getTimestamp("fecha_pago")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 2. REALIZAR EL PAGO (Transacción: Inserta Nómina + Resta Caja)
    public boolean pagarEmpleado(String idEmpleado, double monto, String mes, int anio, int idSucursal) {
        // Necesitamos una caja abierta en esa sucursal para sacar el dinero
        int idCaja = obtenerCajaAbiertaEnSucursal(idSucursal);

        if (idCaja == -1) {
            JOptionPane.showMessageDialog(null, "No hay ninguna CAJA ABIERTA en esta sucursal para procesar el egreso de dinero.");
            return false;
        }

        String sqlInsertNomina = "INSERT INTO pago_nomina (id_empleado, mes, anio, monto, id_sucursal) VALUES (?, ?, ?, ?, ?)";
        String sqlRestarCaja = "UPDATE caja SET saldo_final = saldo_final - ? WHERE id_caja = ?";
        String sqlMovCaja = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_sucursal, estado) VALUES ('GASTO NOMINA', ?, NOW(), ?, ?, ?, 'ACTIVO')";

        Connection cn = new Conexion().establecerConexion();
        try {
            cn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // A. Registrar en tabla Nómina
            try (PreparedStatement ps = cn.prepareStatement(sqlInsertNomina)) {
                ps.setString(1, idEmpleado);
                ps.setString(2, mes);
                ps.setInt(3, anio);
                ps.setDouble(4, monto);
                ps.setInt(5, idSucursal);
                ps.executeUpdate();
            }

            // B. Restar dinero de la Caja
            try (PreparedStatement ps = cn.prepareStatement(sqlRestarCaja)) {
                ps.setDouble(1, monto);
                ps.setInt(2, idCaja);
                ps.executeUpdate();
            }

            // C. Registrar Movimiento
            try (PreparedStatement ps = cn.prepareStatement(sqlMovCaja)) {
                ps.setDouble(1, monto);
                ps.setString(2, "PAGO SUELDO: " + mes + " - Emp: " + idEmpleado);
                ps.setInt(3, idCaja);
                ps.setInt(4, idSucursal);
                ps.executeUpdate();
            }

            cn.commit(); // CONFIRMAR
            return true;

        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            if (e.getErrorCode() == 1062) { // Error de duplicado
                JOptionPane.showMessageDialog(null, "Este empleado YA FUE PAGADO para este mes.");
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error en pago: " + e.getMessage());
            }
            return false;
        }
    }

    // Método auxiliar para encontrar de dónde sacar la plata
    private int obtenerCajaAbiertaEnSucursal(int idSucursal) {
        String sql = "SELECT id_caja FROM caja WHERE id_sucursal = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";
        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_caja");
            }
        } catch (Exception e) {
        }
        return -1;
    }
}
