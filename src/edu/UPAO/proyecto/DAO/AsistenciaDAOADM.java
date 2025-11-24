package edu.UPAO.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsistenciaDAOADM {

    public List<Object[]> listarAsistenciaDiaria(String nombreSucursal) {
        List<Object[]> lista = new ArrayList<>();
        
        // Lógica: Si seleccionan "Todas...", no filtramos por sucursal
        boolean filtrarPorSucursal = !nombreSucursal.equals("Todas las Sucursales");

        String sql = "SELECT " +
                     "  e.dni, " +
                     "  e.rol, " +
                     "  a.fecha_hora_entrada, " +
                     "  a.fecha_hora_salida, " +
                     "  a.estado " +
                     "FROM empleado e " +
                     "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "LEFT JOIN asistencia a ON e.id_empleado = a.id_empleado " +
                     "     AND DATE(a.fecha_hora_entrada) = CURDATE() "; // Solo hoy
        
        // Agregamos el WHERE solo si es una sucursal específica
        if (filtrarPorSucursal) {
            sql += "WHERE s.nombre_sucursal = ? ";
        }

        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Solo seteamos el parámetro si hay filtro
            if (filtrarPorSucursal) {
                ps.setString(1, nombreSucursal);
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = "DNI: " + rs.getString("dni"); // O p.nombre_persona si lo arreglaste
                String rol = rs.getString("rol");
                
                Timestamp entrada = rs.getTimestamp("fecha_hora_entrada");
                Timestamp salida = rs.getTimestamp("fecha_hora_salida");
                String estadoBD = rs.getString("estado");

                String horaEntrada = (entrada != null) ? sdfHora.format(entrada) : "--:--";
                String horaSalida = (salida != null) ? sdfHora.format(salida) : "--:--";
                
                String estadoFinal = (estadoBD != null) ? estadoBD : "PENDIENTE";

                lista.add(new Object[]{ nombre, rol, "Turno Mañana", horaEntrada, horaSalida, estadoFinal });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Map<String, Integer> obtenerKPIsDiarios(String nombreSucursal) {
        Map<String, Integer> kpis = new HashMap<>();
        kpis.put("Programados", 0);
        kpis.put("Presentes", 0);
        kpis.put("Tardanzas", 0);
        kpis.put("Ausencias", 0);
        
        boolean filtrarPorSucursal = !nombreSucursal.equals("Todas las Sucursales");

        String sql = "SELECT " +
                     "  COUNT(*) as total, " +
                     "  SUM(CASE WHEN a.estado LIKE '%ASISTIO%' OR a.estado = '1' OR a.estado LIKE '%PRESENTE%' THEN 1 ELSE 0 END) as presentes, " +
                     "  SUM(CASE WHEN a.estado LIKE '%TARDANZA%' OR a.estado = '2' OR a.estado LIKE '%TARDE%' THEN 1 ELSE 0 END) as tardanzas, " +
                     "  SUM(CASE WHEN a.estado LIKE '%FALTA%' OR a.estado = '3' OR a.estado LIKE '%AUSENTE%' THEN 1 ELSE 0 END) as faltas " +
                     "FROM empleado e " +
                     "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "LEFT JOIN asistencia a ON e.id_empleado = a.id_empleado AND DATE(a.fecha_hora_entrada) = CURDATE() ";

        if (filtrarPorSucursal) {
            sql += "WHERE s.nombre_sucursal = ?";
        }

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (filtrarPorSucursal) {
                ps.setString(1, nombreSucursal);
            }
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                kpis.put("Programados", rs.getInt("total"));
                kpis.put("Presentes", rs.getInt("presentes"));
                kpis.put("Tardanzas", rs.getInt("tardanzas"));
                kpis.put("Ausencias", rs.getInt("faltas"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kpis;
    }
    
    public List<String> listarNombresSucursales() {
        List<String> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion()) {
            // Backup por si falla 'nombre_sucursal'
            try (PreparedStatement ps = con.prepareStatement("SELECT nombre_sucursal FROM sucursal");
                 ResultSet rs = ps.executeQuery()) {
                while(rs.next()) lista.add(rs.getString(1));
            } catch (Exception ex) {
                 try (PreparedStatement ps2 = con.prepareStatement("SELECT direccion FROM sucursal");
                     ResultSet rs2 = ps2.executeQuery()) {
                    while(rs2.next()) lista.add(rs2.getString(1));
                }
            }
        } catch (Exception e) {}
        return lista;
    }
}