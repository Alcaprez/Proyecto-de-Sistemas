package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.AsistenciaDAO;
import edu.UPAO.proyecto.Modelo.Asistencia;
import edu.UPAO.proyecto.Modelo.Usuario;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AsistenciaService {
    private final AsistenciaDAO dao = new AsistenciaDAO();

    public void marcarEntrada(String usuario, LocalDate fecha, LocalTime hora) {
        dao.marcarEntrada(fecha, usuario, hora);
    }

    public void marcarSalida(String usuario, LocalDate fecha, LocalTime hora) {
        dao.marcarSalida(fecha, usuario, hora);
    }

    public Map<String, Asistencia> mapaDelDia(LocalDate fecha) {
        Map<String, Asistencia> m = new HashMap<>();
        for (Asistencia a : dao.listarPorFecha(fecha)) {
            m.put(a.getUsuario().toLowerCase(), a);
        }
        return m;
    }

    public long tardanzaMin(Usuario u, Asistencia a) {
        if (u.getHoraEntradaProg() == null || a == null || a.getHoraEntrada() == null) return 0;
        long min = ChronoUnit.MINUTES.between(u.getHoraEntradaProg(), a.getHoraEntrada());
        return Math.max(0, min);
    }

    public String totalHoras(Asistencia a) {
        if (a == null || a.getHoraEntrada() == null || a.getHoraSalida() == null) return "0 h 0 m";
        long min = ChronoUnit.MINUTES.between(a.getHoraEntrada(), a.getHoraSalida());
        if (min < 0) min = 0;
        return (min / 60) + " h " + (min % 60) + " m";
    }
}