package edu.UPAO.proyecto.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class AsistenciaTxtRepo {

    public static class RegistroDia {
        public LocalTime entrada; // puede ser null
        public LocalTime salida;  // puede ser null
    }

    private final File archivo;
    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter F_HORA  = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AsistenciaTxtRepo(String path) { this.archivo = new File(path); }

    /** Mapa: clave = usuarioLowerCase, valor = (entrada/salida) del día */
    public Map<String, RegistroDia> leerPorFecha(LocalDate fecha) {
        Map<String, RegistroDia> map = new LinkedHashMap<>();
        if (!archivo.exists()) return map;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(archivo), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|");
                if (p.length < 4) continue;

                String usuario, tipo, sFecha, sHora;

                if (p.length == 4) {
                    // usuario|tipo|fecha|hora
                    usuario = p[0].trim();
                    tipo    = p[1].trim().toUpperCase();
                    sFecha  = p[2].trim();
                    sHora   = p[3].trim();
                } else {
                    // 5 columnas: ignoramos la 1ra (id/etiqueta)
                    usuario = p[1].trim();
                    tipo    = p[2].trim().toUpperCase();
                    sFecha  = p[3].trim();
                    sHora   = p[4].trim();
                }

                if (!sFecha.equals(fecha.format(F_FECHA))) continue;

                LocalTime hora;
                try { hora = LocalTime.parse(sHora, F_HORA); }
                catch (Exception e) { continue; }

                String key = usuario.toLowerCase();
                RegistroDia r = map.computeIfAbsent(key, k -> new RegistroDia());

                if ("ENTRADA".equals(tipo)) {
                    // guardamos la PRIMERA entrada
                    if (r.entrada == null || hora.isBefore(r.entrada)) r.entrada = hora;
                } else if ("SALIDA".equals(tipo)) {
                    // guardamos la ÚLTIMA salida
                    if (r.salida == null || hora.isAfter(r.salida)) r.salida = hora;
                }
            }
        } catch (IOException ignored) {}

        return map;
    }

    public boolean tieneEntrada(String usuario, LocalDate fecha) {
        var r = leerPorFecha(fecha).get(usuario == null ? "" : usuario.toLowerCase());
        return r != null && r.entrada != null;
    }

    public boolean tieneSalida(String usuario, LocalDate fecha) {
        var r = leerPorFecha(fecha).get(usuario == null ? "" : usuario.toLowerCase());
        return r != null && r.salida != null;
    }
}