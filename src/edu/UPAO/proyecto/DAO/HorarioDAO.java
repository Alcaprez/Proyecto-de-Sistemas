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

        try (Connection con = new Conexion().establecerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    // ✅ MÉTODO PARA GUARDAR (Aquí sí va el INSERT)
    public void guardarOActualizarHorario(HorarioEmpleado horario) {
        String sql = "INSERT INTO horario_empleado (id_empleado, hora_entrada, hora_salida) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "hora_entrada = VALUES(hora_entrada), " +
                     "hora_salida = VALUES(hora_salida)";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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