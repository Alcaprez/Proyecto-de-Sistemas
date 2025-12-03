package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
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
        boolean filtrarPorSucursal = !nombreSucursal.equals("Todas las Sucursales");

        // --- CONSULTA CORREGIDA ---
        // 1. Quitamos el filtro de ROL para que veas todos los datos de prueba (Gerentes, Admins, etc.)
        // 2. Usamos TRIM en el JOIN para evitar fallos por espacios
        String sql = "SELECT " +
                     "  CONCAT(p.nombres, ' ', p.apellidos) AS nombre_empleado, " + 
                     "  e.rol, " +
                     "  a.fecha_hora_entrada, " +
                     "  a.fecha_hora_salida, " +
                     "  a.estado " + // Traemos el estado tal cual
                     "FROM empleado e " +
                     "INNER JOIN persona p ON e.dni = p.dni " + 
                     "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "LEFT JOIN asistencia a ON TRIM(e.id_empleado) = TRIM(a.id_empleado) ";
                     // "WHERE e.rol = 'CAJERO' " <-- ELIMINADO para ver todo el historial

        if (filtrarPorSucursal) {
            sql += " WHERE s.nombre_sucursal = ? ";
        }
        
        sql += " ORDER BY a.fecha_hora_entrada DESC"; 

        SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM HH:mm"); 

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (filtrarPorSucursal) {
                ps.setString(1, nombreSucursal);
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre_empleado"); 
                String rol = rs.getString("rol");
                
                Timestamp entrada = rs.getTimestamp("fecha_hora_entrada");
                Timestamp salida = rs.getTimestamp("fecha_hora_salida");
                String estadoBD = rs.getString("estado");

                String horaEntrada = (entrada != null) ? sdfHora.format(entrada) : "--:--";
                String horaSalida = (salida != null) ? sdfHora.format(salida) : "--:--";
                
                // Limpieza básica del string
                String estadoFinal = (estadoBD != null) ? estadoBD.trim() : "FALTA"; 

                lista.add(new Object[]{ nombre, rol, "Turno Mañana", horaEntrada, horaSalida, estadoFinal });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // Método auxiliar para los nombres de sucursales
    public List<String> listarNombresSucursales() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_sucursal FROM sucursal";
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) lista.add(rs.getString(1));
        } catch (Exception e) {}
        return lista;
    }

    // Método legacy (ya no se usa para los contadores del panel, pero se mantiene por compatibilidad)
    public Map<String, Integer> obtenerKPIsDiarios(String nombreSucursal) {
        return new HashMap<>();
    }
}