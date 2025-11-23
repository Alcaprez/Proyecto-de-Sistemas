package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.HorarioEmpleado;
import java.sql.*;

public class HorarioDAO {

    public HorarioDAO() {
    }

    // ✅ MÉTODO PARA OBTENER (Solo hace SELECT)
    public HorarioEmpleado obtenerHorarioPorEmpleado(String idEmpleado) {
        String sql = "SELECT * FROM horario_empleado WHERE id_empleado = ?";
        HorarioEmpleado horario = null;

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idEmpleado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Time sqlEntrada = rs.getTime("hora_entrada");
                Time sqlSalida = rs.getTime("hora_salida");

                // Ahora este constructor sí coincide con el Modelo (4 argumentos)
                horario = new HorarioEmpleado(
                        rs.getString("id_empleado"),
                        "Empleado",
                        sqlEntrada != null ? sqlEntrada.toLocalTime() : null,
                        sqlSalida != null ? sqlSalida.toLocalTime() : null
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo horario BD: " + e.getMessage());
        }
        return horario;
    }

    // ✅ MÉTODO NUEVO: Obtener horario filtrando por día de la semana
    public HorarioEmpleado obtenerHorarioPorDia(String idEmpleado, String diaSemana) {
        // Asegúrate de que tu tabla tenga la columna 'dia_semana'
        String sql = "SELECT * FROM horario_empleado WHERE id_empleado = ? AND dia_semana = ?";
        HorarioEmpleado horario = null;

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idEmpleado);
            ps.setString(2, diaSemana);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Time sqlEntrada = rs.getTime("hora_entrada");
                Time sqlSalida = rs.getTime("hora_salida");

                horario = new HorarioEmpleado(
                        rs.getString("id_empleado"),
                        "Empleado", // Puedes hacer otro JOIN si necesitas el nombre real
                        sqlEntrada != null ? sqlEntrada.toLocalTime() : null,
                        sqlSalida != null ? sqlSalida.toLocalTime() : null
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo horario por día: " + e.getMessage());
        }
        return horario;
    }

    public boolean existeCruceHorario(int idSucursal, String diaSemana, java.time.LocalTime nuevaEntrada, java.time.LocalTime nuevaSalida) {
        // 1. Imprimir qué estamos buscando (MIRA TU CONSOLA AL DARLE CLICK)
        System.out.println("--- VALIDANDO CRUCE ---");
        System.out.println("Buscando en Sucursal ID: " + idSucursal);
        System.out.println("Día: " + diaSemana);
        System.out.println("Horario Nuevo: " + nuevaEntrada + " - " + nuevaSalida);

        // Query mejorada: UPPER para ignorar mayúsculas en el día
        String sql = "SELECT h.dia_semana, h.hora_entrada, h.hora_salida FROM horario_empleado h "
                + "INNER JOIN empleado e ON h.id_empleado = e.id_empleado "
                + "WHERE e.id_sucursal = ? "
                + "AND UPPER(h.dia_semana) = UPPER(?) "
                + "AND ( ? < h.hora_salida AND h.hora_entrada < ? )";

        try (java.sql.Connection con = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setString(2, diaSemana.trim()); // Quitamos espacios extra por si acaso
            ps.setTime(3, java.sql.Time.valueOf(nuevaEntrada));
            ps.setTime(4, java.sql.Time.valueOf(nuevaSalida));

            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Si entra aquí, ¡ES QUE SÍ LO ENCONTRÓ!
                System.out.println("⛔ CRUCE DETECTADO CON: " + rs.getTime("hora_entrada") + " - " + rs.getTime("hora_salida"));
                return true;
            } else {
                System.out.println("✅ No se encontraron cruces (El horario parece libre para estos criterios).");
                return false;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error SQL validando cruce: " + e.getMessage());
            return false; // Asumimos false por error, pero imprime el fallo
        }
    }

    public java.util.List<Object[]> listarHorariosPorSucursal(int idSucursal) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        // Consulta que trae el día, horas, nombre del empleado y su rol
        String sql = "SELECT h.dia_semana, h.hora_entrada, h.hora_salida, "
                + "CONCAT(p.nombres, ' ', p.apellidos) AS empleado, e.rol "
                + "FROM horario_empleado h "
                + "INNER JOIN empleado e ON h.id_empleado = e.id_empleado "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "WHERE e.id_sucursal = ? "
                + // Ordenamos por día (Lunes primero) y luego por hora de entrada
                "ORDER BY FIELD(h.dia_semana, 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'), "
                + "h.hora_entrada";

        try (java.sql.Connection con = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("dia_semana"),
                    rs.getString("hora_entrada") + " - " + rs.getString("hora_salida"),
                    rs.getString("rol"),
                    rs.getString("empleado")
                });
            }
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error listando horarios sucursal: " + e.getMessage());
        }
        return lista;
    }
    
    // ✅ MÉTODO CORREGIDO: Valida si el turno está ocupado en GENERAL (Toda la semana)
    public boolean esTurnoOcupado(int idSucursal, java.time.LocalTime nuevaEntrada, java.time.LocalTime nuevaSalida) {
        // Esta consulta verifica si existe AL MENOS UN registro en esa tienda que cruce con el horario
        // Ya no filtramos por día, porque el turno aplica para toda la semana.
        String sql = "SELECT COUNT(*) FROM horario_empleado h " +
                     "INNER JOIN empleado e ON h.id_empleado = e.id_empleado " +
                     "WHERE e.id_sucursal = ? " +
                     "AND ( ? < h.hora_salida AND h.hora_entrada < ? )";

        try (java.sql.Connection con = new BaseDatos.Conexion().establecerConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setTime(2, java.sql.Time.valueOf(nuevaEntrada));
            ps.setTime(3, java.sql.Time.valueOf(nuevaSalida));

            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si hay registros, está ocupado
            }
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error validando turno semanal: " + e.getMessage());
        }
        return false;
    }

    public void guardarOActualizarHorario(HorarioEmpleado horario) {
        String sql = "INSERT INTO horario_empleado (id_empleado, hora_entrada, hora_salida) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "hora_entrada = VALUES(hora_entrada), "
                + "hora_salida = VALUES(hora_salida)";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, horario.getIdEmpleado());
            ps.setTime(2, Time.valueOf(horario.getHoraEntrada()));
            ps.setTime(3, Time.valueOf(horario.getHoraSalida()));

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Horario guardado/actualizado en BD para: " + horario.getIdEmpleado());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error guardando horario en BD: " + e.getMessage());
        }
    }
}
