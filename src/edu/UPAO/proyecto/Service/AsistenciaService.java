package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.AsistenciaDAO;
import edu.UPAO.proyecto.Modelo.EventoAsistencia;
import edu.UPAO.proyecto.Modelo.Usuario;
import edu.UPAO.proyecto.Modelo.ResumenDiario;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class AsistenciaService {

    private final UsuarioService usuarioService = new UsuarioService();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    /** Calcula el resumen del día para la tabla/KPIs. */
    public List<ResumenDiario> resumirDia(LocalDate fecha) {
        List<Usuario> usuarios = usuarioService.listarUsuarios(); // ajusta al nombre real
        List<EventoAsistencia> eventos = asistenciaDAO.listarPorFecha(fecha);

        // Agrupo eventos por usuario y los ordeno por hora
        Map<String, List<EventoAsistencia>> porUsu = eventos.stream()
                .collect(Collectors.groupingBy(EventoAsistencia::getUsuarioId));
        porUsu.values().forEach(l -> l.sort(Comparator.comparing(EventoAsistencia::getFechaHora)));

        List<ResumenDiario> salida = new ArrayList<>();

        for (Usuario u : usuarios) {
            List<EventoAsistencia> l = porUsu.getOrDefault(u.getId(), Collections.emptyList());

            ResumenDiario r = new ResumenDiario();
            r.setUsuario(u);
            r.setFecha(fecha);

            if (l.isEmpty()) {
                r.setEstado("Ausente");
            } else {
                LocalDateTime firstIn = null, lastOut = null;
                for (EventoAsistencia ev : l) {
                    if (ev.getTipo() == EventoAsistencia.Tipo.IN && firstIn == null) firstIn = ev.getFechaHora();
                    if (ev.getTipo() == EventoAsistencia.Tipo.OUT) lastOut = ev.getFechaHora();
                }
                if (firstIn == null || lastOut == null || !lastOut.isAfter(firstIn)) {
                    r.setEstado("Incompleto");
                } else {
                    r.setEntrada(firstIn.toLocalTime());
                    r.setSalida(lastOut.toLocalTime());
                    long minutosTrab = Duration.between(firstIn, lastOut).toMinutes();
                    r.setMinutosTrab(minutosTrab);

                    // Horario programado (si tu Usuario no lo tiene, mira §3.1)
                    LocalTime inProg = u.getHoraEntradaProg();
                    long minTarde = Math.max(0, Duration.between(inProg, r.getEntrada()).toMinutes() - 10);
                    r.setMinTarde(minTarde);
                    r.setEstado(minTarde > 0 ? "Tarde" : "Responsable");
                }
            }
            salida.add(r);
        }
        salida.sort(Comparator.comparing(a -> a.getUsuario().getNombreComp()));
        return salida;
    }

    /** Permite a la pantalla de Cajero/Marcador registrar IN/OUT */
    public void marcar(String usuarioId, LocalDateTime fechaHora, EventoAsistencia.Tipo tipo) {
        asistenciaDAO.registrarEvento(new EventoAsistencia(usuarioId, fechaHora, tipo));
    }
}
