package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RotacionProductoDAO {

public Map<String, Integer> obtenerTopProductos(Date fechaInicio, Date fechaFin, String nombreSucursal, String categoria) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        
        // NOTA: Si tu tabla categoria usa 'nombre_categoria', cambia 'c.nombre' abajo.
        String sql = "SELECT p.nombre, SUM(dv.cantidad) as total_vendido " +
                     "FROM detalle_venta dv " +
                     "JOIN venta v ON dv.id_venta = v.id_venta " +
                     "JOIN producto p ON dv.id_producto = p.id_producto " +
                     "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "JOIN caja ca ON v.id_caja = ca.id_caja " +
                     "JOIN sucursal s ON ca.id_sucursal = s.id_sucursal " +
                     "WHERE v.fecha_hora BETWEEN ? AND ? ";

        // Filtros
        if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
            sql += " AND s.nombre_sucursal = ? ";
        }
        if (categoria != null && !categoria.equals("TODAS")) {
            sql += " AND c.nombre = ? "; // <--- OJO AQUÍ: Verifica si es 'nombre' o 'nombre_categoria' en tu BD
        }

        sql += " GROUP BY p.nombre ORDER BY total_vendido DESC LIMIT 10";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            System.out.println("📊 Consultando Top Productos...");
            System.out.println("   -> Sucursal: " + nombreSucursal);
            System.out.println("   -> Categoría: " + categoria);

            // Fechas
            pst.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime())); 
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            cal.set(java.util.Calendar.SECOND, 59);
            pst.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));

            int index = 3;
            if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
                pst.setString(index++, nombreSucursal);
            }
            if (categoria != null && !categoria.equals("TODAS")) {
                pst.setString(index++, categoria);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString("nombre"), rs.getInt("total_vendido"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error Top Productos: " + e.getMessage());
        }
        return resultado;
    }

    // Obtener distribución por categorías (Para el gráfico de pastel)
    public Map<String, Integer> obtenerVentasPorCategoria(Date fechaInicio, Date fechaFin, String nombreSucursal, String categoria) {
        Map<String, Integer> resultado = new HashMap<>();
        
        String sql = "SELECT c.nombre, SUM(dv.cantidad) as total " +
                     "FROM detalle_venta dv " +
                     "JOIN venta v ON dv.id_venta = v.id_venta " +
                     "JOIN producto p ON dv.id_producto = p.id_producto " +
                     "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "JOIN caja ca ON v.id_caja = ca.id_caja " +
                     "JOIN sucursal s ON ca.id_sucursal = s.id_sucursal " +
                     "WHERE v.fecha_hora BETWEEN ? AND ? ";

        if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
            sql += " AND s.nombre_sucursal = ? ";
        }
        // Agregamos filtro de categoría aquí también para consistencia
        if (categoria != null && !categoria.equals("TODAS")) {
            sql += " AND c.nombre = ? "; 
        }

        sql += " GROUP BY c.nombre";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            pst.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));

            int index = 3;
            if (nombreSucursal != null && !nombreSucursal.equals("TODAS")) {
                pst.setString(index++, nombreSucursal);
            }
            if (categoria != null && !categoria.equals("TODAS")) {
                pst.setString(index++, categoria);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString("nombre"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error Categorías: " + e.getMessage());
        }
        return resultado;
    }
}