package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Asistencia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AsistenciaDAO {
    private static final String RUTA = "data/asistencias.csv";

    public List<Asistencia> listarPorFecha(LocalDate fecha) {
        List<Asistencia> out = new ArrayList<>();
        File f = new File(RUTA);
        if (!f.exists()) return out;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {

            String header = br.readLine(); // fecha,usuario,entrada,salida
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;

                LocalDate dia = LocalDate.parse(p[0].trim());
                if (!dia.equals(fecha)) continue;

                String usuario = p[1].trim();
                LocalTime ent = p[2].isBlank() ? null : LocalTime.parse(p[2].trim());
                LocalTime sal = p[3].isBlank() ? null : LocalTime.parse(p[3].trim());
                out.add(new Asistencia(dia, usuario, ent, sal));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }

    public Optional<Asistencia> obtener(LocalDate fecha, String usuario) {
        return listarPorFecha(fecha).stream()
                .filter(a -> a.getUsuario().equalsIgnoreCase(usuario))
                .findFirst();
    }

    /** Marca entrada si no existe; si existe, solo actualiza hora de entrada si está vacía. */
    public void marcarEntrada(LocalDate fecha, String usuario, LocalTime hora) {
        Map<String, Asistencia> map = mapByUsuario(listarPorFecha(fecha));
        Asistencia a = map.get(usuario.toLowerCase());
        if (a == null) a = new Asistencia(fecha, usuario, hora, null);
        else if (a.getHoraEntrada() == null) a.setHoraEntrada(hora);

        map.put(usuario.toLowerCase(), a);
        guardarFecha(fecha, new ArrayList<>(map.values()));
    }

    /** Marca salida (crea registro si no existía). */
    public void marcarSalida(LocalDate fecha, String usuario, LocalTime hora) {
        Map<String, Asistencia> map = mapByUsuario(listarPorFecha(fecha));
        Asistencia a = map.get(usuario.toLowerCase());
        if (a == null) a = new Asistencia(fecha, usuario, null, hora);
        else a.setHoraSalida(hora);

        map.put(usuario.toLowerCase(), a);
        guardarFecha(fecha, new ArrayList<>(map.values()));
    }

    // ---- helpers ----
    private Map<String, Asistencia> mapByUsuario(List<Asistencia> list) {
        Map<String, Asistencia> m = new LinkedHashMap<>();
        for (Asistencia a : list) m.put(a.getUsuario().toLowerCase(), a);
        return m;
    }

    private void guardarFecha(LocalDate fecha, List<Asistencia> registrosDelDia) {
        // leemos TODO el archivo y reescribimos solo líneas de ese día
        List<String> otrasLineas = new ArrayList<>();
        File f = new File(RUTA);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f), StandardCharsets.UTF_8))) {
                String header = br.readLine(); // guardaremos otro header luego
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;
                    String[] p = line.split(",", -1);
                    if (p.length < 4) continue;
                    LocalDate dia = LocalDate.parse(p[0].trim());
                    if (!dia.equals(fecha)) otrasLineas.add(line);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            pw.println("fecha,usuario,entrada,salida");
            for (String s : otrasLineas) pw.println(s);
            for (Asistencia a : registrosDelDia) {
                pw.println(String.join(",",
                        fecha.toString(),
                        a.getUsuario(),
                        a.getHoraEntrada() == null ? "" : a.getHoraEntrada().toString(),
                        a.getHoraSalida()  == null ? "" : a.getHoraSalida().toString()
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}