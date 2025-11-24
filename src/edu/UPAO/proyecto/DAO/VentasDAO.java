
package edu.UPAO.proyecto.DAO;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class VentasDAO {

    private Connection con;

    public VentasDAO(Connection con) {
        this.con = con;
    }

   public List<Object[]> obtenerVentas(Date fechaInicio, Date fechaFin, String sucursal) throws Exception {
    List<Object[]> lista = new ArrayList<>();

    String sql = "SELECT " +
            "v.fecha_hora AS fecha, " +
            "s.nombre_sucursal AS sucursal, " +
            "p.nombre AS producto, " +
            "dv.cantidad AS cantidad, " +
            "dv.subtotal AS ingreso, " +
            "(p.precio_compra * dv.cantidad) AS costo, " +
            "(dv.subtotal - (p.precio_compra * dv.cantidad)) AS ganancia, " +
            "'VENTA' AS tipo, " +
            "mp.nombre AS metodo_pago " +
            "FROM venta v " +
            "JOIN detalle_venta dv ON v.id_venta = dv.id_venta " +
            "JOIN producto p ON dv.id_producto = p.id_producto " +
            "JOIN sucursal s ON v.id_sucursal = s.id_sucursal " +
            "JOIN metodo_pago mp ON v.id_metodo_pago = mp.id_metodo_pago " +
            "WHERE v.fecha_hora BETWEEN ? AND ? " +
            "  AND (? = 'TODAS' OR s.nombre_sucursal = ?) " +
            "ORDER BY v.fecha_hora ASC";

    PreparedStatement ps = con.prepareStatement(sql);
    ps.setTimestamp(1, new Timestamp(fechaInicio.getTime()));
    ps.setTimestamp(2, new Timestamp(fechaFin.getTime()));
    ps.setString(3, sucursal);
    ps.setString(4, sucursal);

    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        lista.add(new Object[]{
                rs.getTimestamp("fecha"),        // [0]
                rs.getString("sucursal"),        // [1]
                rs.getString("producto"),        // [2]
                rs.getInt("cantidad"),           // [3]
                rs.getDouble("ingreso"),         // [4]
                rs.getDouble("costo"),           // [5]
                rs.getDouble("ganancia"),        // [6]
                rs.getString("tipo"),            // [7]
                rs.getString("metodo_pago")      // [8]  <-- NUEVO
        });
    }

    return lista;
}


}
