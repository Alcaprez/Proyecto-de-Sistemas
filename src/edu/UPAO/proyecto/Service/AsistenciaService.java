package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.AsistenciaDAO;
import edu.UPAO.proyecto.DAO.EmpleadoDAO;
import edu.UPAO.proyecto.Modelo.Asistencia;
import edu.UPAO.proyecto.Modelo.RegistroAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class AsistenciaService {

    private AsistenciaDAO asistenciaDAO;
    private EmpleadoDAO empleadoDAO;

    public AsistenciaService() {
        this.asistenciaDAO = new AsistenciaDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    public RegistroAsistencia registrarEntrada(String idEmpleado, String nombreEmpleado) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        // Verificar si ya registró entrada hoy
        Optional<Asistencia> asistenciaExistente = asistenciaDAO.obtener(hoy, idEmpleado);
        if (asistenciaExistente.isPresent() && asistenciaExistente.get().getHoraEntrada() != null) {
            throw new IllegalStateException("⚠️ Ya registró su entrada hoy");
        }

        // Registrar en base de datos
        asistenciaDAO.marcarEntrada(hoy, idEmpleado, ahora.toLocalTime());

        // Obtener sucursal para el registro
        int idSucursal = empleadoDAO.obtenerSucursalEmpleado(idEmpleado);

        // Crear registro para la interfaz
        RegistroAsistencia registro = new RegistroAsistencia(
                idEmpleado, nombreEmpleado, "ENTRADA", ahora, "REGISTRADO"
        );

        System.out.println("✅ Entrada registrada en BD para: " + idEmpleado + " - Sucursal: " + idSucursal);
        return registro;
    }

    public RegistroAsistencia registrarSalida(String idEmpleado, String nombreEmpleado) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        // Verificar si existe registro de entrada
        Optional<Asistencia> asistenciaExistente = asistenciaDAO.obtener(hoy, idEmpleado);
        if (asistenciaExistente.isEmpty() || asistenciaExistente.get().getHoraEntrada() == null) {
            throw new IllegalStateException("❌ Debe registrar entrada primero");
        }

        if (asistenciaExistente.get().getHoraSalida() != null) {
            throw new IllegalStateException("⚠️ Ya registró su salida hoy");
        }

        // Registrar salida en base de datos
        asistenciaDAO.marcarSalida(hoy, idEmpleado, ahora.toLocalTime());

        RegistroAsistencia registro = new RegistroAsistencia(
                idEmpleado, nombreEmpleado, "SALIDA", ahora, "REGISTRADO"
        );

        System.out.println("✅ Salida registrada en BD para: " + idEmpleado);
        return registro;
    }

    public boolean yaRegistroEntrada(String idEmpleado) {
        Optional<Asistencia> asistencia = asistenciaDAO.obtener(LocalDate.now(), idEmpleado);
        return asistencia.isPresent() && asistencia.get().getHoraEntrada() != null;
    }

    public boolean yaRegistroSalida(String idEmpleado) {
        Optional<Asistencia> asistencia = asistenciaDAO.obtener(LocalDate.now(), idEmpleado);
        return asistencia.isPresent() && asistencia.get().getHoraSalida() != null;
    }

    public String obtenerEstadoActual(String idEmpleado) {
        if (yaRegistroSalida(idEmpleado)) {
            return "SALIDA_REGISTRADA";
        } else if (yaRegistroEntrada(idEmpleado)) {
            return "ENTRADA_REGISTRADA";
        } else {
            return "PENDIENTE_ENTRADA";
        }
    }

    public Optional<Asistencia> obtenerAsistenciaHoy(String idEmpleado) {
        return asistenciaDAO.obtener(LocalDate.now(), idEmpleado);
    }
}
