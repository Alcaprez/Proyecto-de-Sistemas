package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Asistencia;
import edu.UPAO.proyecto.Modelo.HorarioEmpleado; // ✅ Importante
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AsistenciaDAO {

    private Connection conexion;

    public AsistenciaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("✅ AsistenciaDAO conectado a BD");
        } catch (Exception e) {
            System.err.println("❌ Error conectando AsistenciaDAO: " + e.getMessage());
        }
    }

    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencia WHERE DATE(fecha_hora_entrada) = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.sql.Timestamp entrada = rs.getTimestamp("fecha_hora_entrada");
                java.sql.Timestamp salida = rs.getTimestamp("fecha_hora_salida");

                Asistencia asistencia = new Asistencia(
                        entrada.toLocalDateTime().toLocalDate(),
                        rs.getString("id_empleado"),
                        entrada.toLocalDateTime().toLocalTime(),
                        salida != null ? salida.toLocalDateTime().toLocalTime() : null
                );
                asistencias.add(asistencia);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando asistencias: " + e.getMessage());
        }
        return asistencias;
    }

    public Optional<Asistencia> obtener(LocalDate fecha, String idEmpleado) {
        String sql = "SELECT * FROM asistencia WHERE DATE(fecha_hora_entrada) = ? AND id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            stmt.setString(2, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                java.sql.Timestamp entrada = rs.getTimestamp("fecha_hora_entrada");
                java.sql.Timestamp salida = rs.getTimestamp("fecha_hora_salida");

                Asistencia asistencia = new Asistencia(
                        entrada.toLocalDateTime().toLocalDate(),
                        rs.getString("id_empleado"),
                        entrada.toLocalDateTime().toLocalTime(),
                        salida != null ? salida.toLocalDateTime().toLocalTime() : null
                );
                return Optional.of(asistencia);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo asistencia: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void marcarEntrada(LocalDate fecha, String idEmpleado, LocalTime hora) {
        Optional<Asistencia> existente = obtener(fecha, idEmpleado);

        if (existente.isPresent()) {
            if (existente.get().getHoraEntrada() != null) {
                throw new IllegalStateException("Ya registró entrada hoy");
            }
            actualizarEntrada(existente.get(), hora);
        } else {
            insertarNuevaAsistencia(fecha, idEmpleado, hora, null, "RESPONSABLE");
        }
    }

    public void marcarSalida(LocalDate fecha, String idEmpleado, LocalTime hora) {
        Optional<Asistencia> existente = obtener(fecha, idEmpleado);

        if (existente.isEmpty()) {
            insertarNuevaAsistencia(fecha, idEmpleado, null, hora, "AUSENTE");
        } else {
            actualizarSalida(existente.get(), hora);
        }
    }

    private void insertarNuevaAsistencia(LocalDate fecha, String idEmpleado, LocalTime entrada, LocalTime salida, String estado) {
        String sql = "INSERT INTO asistencia (fecha_hora_entrada, fecha_hora_salida, id_empleado, id_sucursal, estado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            java.sql.Timestamp timestampEntrada = null;
            if (entrada != null) {
                timestampEntrada = java.sql.Timestamp.valueOf(fecha.atTime(entrada));
            }

            java.sql.Timestamp timestampSalida = null;
            if (salida != null) {
                timestampSalida = java.sql.Timestamp.valueOf(fecha.atTime(salida));
            }

            stmt.setTimestamp(1, timestampEntrada);
            stmt.setTimestamp(2, timestampSalida);
            stmt.setString(3, idEmpleado);

            int idSucursal = new EmpleadoDAO().obtenerSucursalEmpleado(idEmpleado);
            stmt.setInt(4, idSucursal);

            // ✅ CORREGIDO: Usamos el cálculo de estado compatible
            String estadoFinal = estado;
            if (entrada != null) {
                estadoFinal = calcularEstado(idEmpleado, entrada);
            }
            stmt.setString(5, estadoFinal);

            stmt.executeUpdate();
            System.out.println("✅ Asistencia guardada en BD: " + idEmpleado + " - " + fecha);

        } catch (SQLException e) {
            System.err.println("❌ Error insertando asistencia: " + e.getMessage());
            throw new RuntimeException("Error al guardar en base de datos", e);
        }
    }

    private void actualizarEntrada(Asistencia asistencia, LocalTime horaEntrada) {
        String sql = "UPDATE asistencia SET fecha_hora_entrada = ?, estado = ? WHERE id_empleado = ? AND DATE(fecha_hora_entrada) = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(asistencia.getFecha().atTime(horaEntrada));
            stmt.setTimestamp(1, timestamp);

            // ✅ CORREGIDO: Pasamos idEmpleado y hora
            stmt.setString(2, calcularEstado(asistencia.getUsuario(), horaEntrada));

            stmt.setString(3, asistencia.getUsuario());
            stmt.setDate(4, java.sql.Date.valueOf(asistencia.getFecha()));

            stmt.executeUpdate();
            System.out.println("✅ Entrada actualizada en BD: " + asistencia.getUsuario());

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando entrada: " + e.getMessage());
        }
    }

    private void actualizarSalida(Asistencia asistencia, LocalTime horaSalida) {
        String sql = "UPDATE asistencia SET fecha_hora_salida = ? WHERE id_empleado = ? AND DATE(fecha_hora_entrada) = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(asistencia.getFecha().atTime(horaSalida));
            stmt.setTimestamp(1, timestamp);
            stmt.setString(2, asistencia.getUsuario());
            stmt.setDate(3, java.sql.Date.valueOf(asistencia.getFecha()));

            stmt.executeUpdate();
            System.out.println("✅ Salida actualizada en BD: " + asistencia.getUsuario());

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando salida: " + e.getMessage());
        }
    }

    // ✅ MÉTODO CALCULAR ESTADO ACTUALIZADO
    private String calcularEstado(String idEmpleado, LocalTime horaEntrada) {
        HorarioDAO horarioDAO = new HorarioDAO();
        HorarioEmpleado horario = horarioDAO.obtenerHorarioPorEmpleado(idEmpleado);

        LocalTime horaEsperada;
        // Tolerancia fija de 15 minutos (ya no viene de BD)
        int minutosTolerancia = 15;

        if (horario != null && horario.getHoraEntrada() != null) {
            horaEsperada = horario.getHoraEntrada();
        } else {
            // Horario por defecto si no tiene asignado
            System.out.println("⚠️ Sin horario asignado, usando defecto 8:00 AM");
            horaEsperada = LocalTime.of(8, 0);
        }

        LocalTime horaLimite = horaEsperada.plusMinutes(minutosTolerancia);

        if (horaEntrada.isAfter(horaLimite)) {
            return "TARDE";
        }
        return "RESPONSABLE";
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }
}
