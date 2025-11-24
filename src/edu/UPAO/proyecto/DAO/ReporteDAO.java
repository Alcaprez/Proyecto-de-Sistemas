package edu.UPAO.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ReporteDAO {

    // 1. Contar Total de Empleados
    public int obtenerTotalEmpleados() {
        int total = 0;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM empleado");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // 2. Contar Sucursales Activas
    public int obtenerSucursalesActivas() {
        int total = 0;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(DISTINCT id_sucursal) FROM empleado WHERE id_sucursal IS NOT NULL");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // 3. Distribución por SUCURSAL
    public Map<String, Integer> obtenerDistribucionSucursales() {
        Map<String, Integer> datos = new HashMap<>();
        String sql = "SELECT s.nombre_sucursal, COUNT(e.id_empleado) as cantidad FROM empleado e JOIN sucursal s ON e.id_sucursal = s.id_sucursal GROUP BY s.nombre_sucursal";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.put(rs.getString(1), rs.getInt(2));
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }

    // 4. Distribución por ROL
    public Map<String, Integer> obtenerDistribucionRoles() {
        Map<String, Integer> datos = new HashMap<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT rol, COUNT(*) as cantidad FROM empleado GROUP BY rol");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.put(rs.getString(1), rs.getInt(2));
        } catch (Exception e) { e.printStackTrace(); }
        return datos;
    }
    
    // 5. Faltas REALES
    public int obtenerFaltas() {
        int faltas = 0;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM asistencia WHERE UPPER(estado) = 'FALTA'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) faltas = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return faltas;
    }

    // 6. Tasa de Cobertura DIARIA (Solo cuenta lo de HOY)
    public double obtenerTasaAsistencia() {
        double tasa = 0.0;
        
        try (Connection con = Conexion.getConexion()) {
            // 1. Total Empleados
            int totalEmpleados = 0;
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM empleado");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalEmpleados = rs.getInt(1);
            }
            
            if (totalEmpleados == 0) return 0.0;

            // 2. Asistencias de HOY (Usando la columna correcta)
            int asistenciasHoy = 0;
            // Usamos DATE() para ignorar la hora y comparar solo la fecha
            String sql = "SELECT COUNT(*) FROM asistencia WHERE DATE(fecha_hora_entrada) = CURDATE() AND estado != 'FALTA'";
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) asistenciasHoy = rs.getInt(1);
            }

            tasa = ((double) asistenciasHoy / totalEmpleados) * 100;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasa;
    }
    
    // 7. Listar Detalle (ADAPTADO A 'fecha_hora_entrada')
    public List<Object[]> listarDetalleAsistencias() {
        List<Object[]> lista = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  e.dni, " +
                     "  s.nombre_sucursal, " +
                     "  a.fecha_hora_entrada, " +
                     "  a.fecha_hora_salida, " +
                     "  a.estado " +
                     "FROM asistencia a " +
                     "INNER JOIN empleado e ON a.id_empleado = e.id_empleado " +
                     "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "ORDER BY a.fecha_hora_entrada DESC";

        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                String nombre = "DNI: " + rs.getString("dni");
                String sucursal = rs.getString("nombre_sucursal");
                if (sucursal == null) sucursal = "Sin Asignar";
                
                // Procesar Timestamp (Fecha y Hora juntas)
                Timestamp tsEntrada = rs.getTimestamp("fecha_hora_entrada");
                Timestamp tsSalida = rs.getTimestamp("fecha_hora_salida");
                
                String fechaStr = "-";
                String horaEntradaStr = "-";
                String horaSalidaStr = "-";
                String totalHoras = "0h";

                if (tsEntrada != null) {
                    fechaStr = sdfFecha.format(tsEntrada);
                    horaEntradaStr = sdfHora.format(tsEntrada);
                }
                
                if (tsSalida != null) {
                    horaSalidaStr = sdfHora.format(tsSalida);
                    if (tsEntrada != null) {
                        long diff = tsSalida.getTime() - tsEntrada.getTime();
                        long diffHours = diff / (60 * 60 * 1000);
                        totalHoras = diffHours + "h";
                    }
                }

                lista.add(new Object[]{
                    nombre,
                    sucursal,
                    fechaStr,
                    horaEntradaStr,
                    horaSalidaStr,
                    totalHoras,
                    rs.getString("estado")
                });
            }
        } catch (Exception e) {
            System.out.println("Error Tabla Detalle: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}