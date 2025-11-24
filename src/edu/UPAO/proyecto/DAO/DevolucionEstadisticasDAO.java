package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DevolucionEstadisticasDAO {

    private Connection conectar() {
        return new Conexion().establecerConexion();
    }

    // 1. KPI: Cantidad Total (Filtrado por NOMBRE de sucursal)
    public int obtenerCantidadTotal(String nombreSucursal) {
        String sql = "SELECT COUNT(*) FROM devolucion d " +
                     "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                     "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal " + // JOIN con sucursal
                     "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?)"; // Filtramos por nombre
        
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreSucursal);
            ps.setString(2, nombreSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 2. KPI: Monto Total (Filtrado por NOMBRE de sucursal)
    public double obtenerMontoTotal(String nombreSucursal) {
        String sql = "SELECT COALESCE(SUM(d.total_devuelto), 0) FROM devolucion d " +
                     "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                     "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal " +
                     "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?)";
        
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreSucursal);
            ps.setString(2, nombreSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    // 3. KPI: Tasa de Devolución
    public double obtenerTasaDevolucion(String nombreSucursal) {
        double totalDevoluciones = obtenerMontoTotal(nombreSucursal);
        double totalVentas = 0;

        // También ajustamos la consulta de ventas totales
        String sqlVentas = "SELECT COALESCE(SUM(v.total), 1) FROM venta v " +
                           "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal " +
                           "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?)";
        
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sqlVentas)) {
            ps.setString(1, nombreSucursal);
            ps.setString(2, nombreSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) totalVentas = rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }

        if (totalVentas == 0) return 0.0;
        return (totalDevoluciones / totalVentas) * 100;
    }

    // 4. Gráfico Pastel: Motivos
    public Map<String, Integer> obtenerMotivosFrecuentes(String nombreSucursal) {
        Map<String, Integer> datos = new HashMap<>();
        String sql = "SELECT d.motivo, COUNT(*) as cantidad FROM devolucion d " +
                     "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                     "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal " +
                     "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?) " +
                     "GROUP BY d.motivo";
        
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreSucursal);
            ps.setString(2, nombreSucursal);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("motivo"), rs.getInt("cantidad"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }

    // 5. Gráfico Línea: Tendencia
    public Map<String, Double> obtenerTendenciaFechas(String nombreSucursal) {
        Map<String, Double> datos = new TreeMap<>();
        String sql = "SELECT DATE(d.fecha_hora) as fecha, SUM(d.total_devuelto) as total " +
                     "FROM devolucion d " +
                     "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                     "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal " +
                     "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?) " +
                     "GROUP BY DATE(d.fecha_hora) ORDER BY fecha ASC LIMIT 30";
        
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreSucursal);
            ps.setString(2, nombreSucursal);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("fecha"), rs.getDouble("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }
}