package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.EventoAsistencia;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AsistenciaDAO {

    private static final List<EventoAsistencia> eventos = new ArrayList<>();

    public void registrarEvento(EventoAsistencia e) {
        eventos.add(e);
    }

    public List<EventoAsistencia> listarPorFecha(LocalDate dia) {
        return eventos.stream()
                .filter(e -> e.getFechaHora().toLocalDate().equals(dia))
                .collect(Collectors.toList());
    }

    public List<EventoAsistencia> listarTodos() {
        return new ArrayList<>(eventos);
    }
}
