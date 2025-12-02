
package edu.UPAO.proyecto.DAO;
import BaseDatos.Conexion;       
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReporteComprasDAO {

    // TODO: ajusta esto al nombre REAL de tu clase de conexión
    private Connection getConnection() throws SQLException {
            return Conexion.getConexion();
    }

    // ---------- COMBOS ----------

    public List<ComboItem> listarSucursales() {
        List<ComboItem> lista = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre_sucursal FROM sucursal WHERE estado = 'ACTIVO'";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_sucursal");
                String nombre = rs.getString("nombre_sucursal");
                lista.add(new ComboItem(id, nombre));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<ComboItem> listarProveedores() {
        List<ComboItem> lista = new ArrayList<>();
        String sql = "SELECT id_proveedor, Razon_Social FROM proveedor WHERE estado = 'ACTIVO'";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_proveedor");
                String nombre = rs.getString("Razon_Social");
                lista.add(new ComboItem(id, nombre));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ---------- TABLA RESUMEN ----------

    public void llenarTablaResumen(JTable tabla,
                                   Integer idSucursal,
                                   String idProveedor,
                                   Date fechaDesde,
                                   Date fechaHasta) {

        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("  p.id_proveedor, ")
           .append("  p.Razon_Social AS proveedor, ")
           .append("  SUM(c.total) AS total_general, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=1 THEN c.total ELSE 0 END) AS enero, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=2 THEN c.total ELSE 0 END) AS febrero, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=3 THEN c.total ELSE 0 END) AS marzo, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=4 THEN c.total ELSE 0 END) AS abril, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=5 THEN c.total ELSE 0 END) AS mayo, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=6 THEN c.total ELSE 0 END) AS junio, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=7 THEN c.total ELSE 0 END) AS julio, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=8 THEN c.total ELSE 0 END) AS agosto, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=9 THEN c.total ELSE 0 END) AS septiembre, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=10 THEN c.total ELSE 0 END) AS octubre, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=11 THEN c.total ELSE 0 END) AS noviembre, ")
           .append("  SUM(CASE WHEN MONTH(c.fecha_hora)=12 THEN c.total ELSE 0 END) AS diciembre ")
           .append("FROM compra c ")
           .append("JOIN proveedor p ON c.id_proveedor = p.id_proveedor ")
           .append("WHERE c.estado <> 'ANULADA' ");

        List<Object> params = new ArrayList<>();

        if (idSucursal != null && idSucursal > 0) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(idSucursal);
        }
        if (idProveedor != null && !idProveedor.isEmpty()) {
            sql.append(" AND c.id_proveedor = ? ");
            params.add(idProveedor);
        }
        if (fechaDesde != null) {
            sql.append(" AND DATE(c.fecha_hora) >= ? ");
            params.add(fechaDesde);
        }
        if (fechaHasta != null) {
            sql.append(" AND DATE(c.fecha_hora) <= ? ");
            params.add(fechaHasta);
        }

        sql.append("GROUP BY p.id_proveedor, p.Razon_Social ")
           .append("ORDER BY total_general DESC");

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[15];
                    fila[0] = rs.getString("id_proveedor");
                    fila[1] = rs.getString("proveedor");
                    fila[2] = rs.getBigDecimal("total_general");
                    fila[3] = rs.getBigDecimal("enero");
                    fila[4] = rs.getBigDecimal("febrero");
                    fila[5] = rs.getBigDecimal("marzo");
                    fila[6] = rs.getBigDecimal("abril");
                    fila[7] = rs.getBigDecimal("mayo");
                    fila[8] = rs.getBigDecimal("junio");
                    fila[9] = rs.getBigDecimal("julio");
                    fila[10] = rs.getBigDecimal("agosto");
                    fila[11] = rs.getBigDecimal("septiembre");
                    fila[12] = rs.getBigDecimal("octubre");
                    fila[13] = rs.getBigDecimal("noviembre");
                    fila[14] = rs.getBigDecimal("diciembre");
                    modelo.addRow(fila);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- TABLA DETALLE ----------

    public void llenarTablaDetalle(JTable tabla,
                                   String idProveedor,
                                   Integer idSucursal,
                                   Date fechaDesde,
                                   Date fechaHasta) {

        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        if (idProveedor == null || idProveedor.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.id_compra, c.fecha_hora, s.nombre_sucursal, c.total, c.estado ")
           .append("FROM compra c ")
           .append("JOIN sucursal s ON c.id_sucursal = s.id_sucursal ")
           .append("WHERE c.id_proveedor = ? ");

        List<Object> params = new ArrayList<>();
        params.add(idProveedor);

        if (idSucursal != null && idSucursal > 0) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(idSucursal);
        }
        if (fechaDesde != null) {
            sql.append(" AND DATE(c.fecha_hora) >= ? ");
            params.add(fechaDesde);
        }
        if (fechaHasta != null) {
            sql.append(" AND DATE(c.fecha_hora) <= ? ");
            params.add(fechaHasta);
        }

        sql.append("ORDER BY c.fecha_hora DESC");

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[5];
                    fila[0] = rs.getInt("id_compra");
                    fila[1] = rs.getTimestamp("fecha_hora");
                    fila[2] = rs.getString("nombre_sucursal");
                    fila[3] = rs.getBigDecimal("total");
                    fila[4] = rs.getString("estado");
                    modelo.addRow(fila);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- CLASE PARA COMBOS ----------

    public static class ComboItem {

        private final Object id;
        private final String label;

        public ComboItem(Object id, String label) {
            this.id = id;
            this.label = label;
        }

        public Object getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}