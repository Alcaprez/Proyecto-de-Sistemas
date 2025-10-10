package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

public class UsuarioDAO {

    private static final String RUTA = "data/empleados.csv";

    // ---------- API PRINCIPAL ----------
    /** Lee usuarios desde data/empleados.csv. Si no existe, devuelve lista vacía. */
    public List<Usuario> listar() {
        List<Usuario> out = new ArrayList<>();
        File f = new File(RUTA);
        if (!f.exists()) return out;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String header = br.readLine(); // ignorar encabezado
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                Usuario u = parseLinea(line);
                if (u != null) out.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /** Reescribe el CSV completo (útil para exportar/sincronizar). */
    public void guardarTodos(List<Usuario> lista) {
        File f = new File(RUTA);
        if (f.getParentFile() != null) f.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            pw.println("id,dni,estado,tienda,nombreComp,cargo,usuario,contrasena,horaEntradaProg,horaSalidaProg");
            for (Usuario u : lista) pw.println(aCsv(u));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Si no existe el CSV, lo crea con data demo (sin GERENTE). */
    public void seedIfMissing() {
        File f = new File(RUTA);
        if (f.exists()) return;
        List<Usuario> demo = Arrays.asList(
          
            new Usuario(1, 45678901, true,  "Tienda Central", "Alberth",     "CAJERO", "alberth", "123"),
            new Usuario(2, 56789012, true,  "Tienda Central", "Jhosep",      "CAJERO", "jhosep",  "123"),
           new Usuario(3, 93832374, true,  "Tienda Central", "Lucas",     "CAJERO", "Lucas", "123"),
            new Usuario(4, 74398478, true,  "Tienda Central", "karina",      "CAJERO", "karina",  "123")
        );
        guardarTodos(demo);
    }

    // ---------- HELPERS CSV ----------
    private Usuario parseLinea(String line) {
        // split básico con soporte de comillas dobles
        List<String> cols = splitCsv(line);
        // Esperamos 10 columnas
        if (cols.size() < 10) return null;

        try {
            int id           = parseInt(cols.get(0));
            int dni          = parseInt(cols.get(1));
            boolean estado   = parseBool(cols.get(2));
            String tienda    = cols.get(3);
            String nombre    = cols.get(4);
            String cargo     = cols.get(5);
            String usuario   = cols.get(6);
            String pass      = cols.get(7);
            LocalTime he     = parseTime(cols.get(8)); // puede ser null
            LocalTime hs     = parseTime(cols.get(9)); // puede ser null

            Usuario u = new Usuario(id, dni, estado, tienda, nombre, cargo, usuario, pass);
            u.setHoraEntradaProg(he);
            u.setHoraSalidaProg(hs);
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String aCsv(Usuario u) {
        return String.join(",",
                esc(String.valueOf(u.getId())),
                esc(String.valueOf(u.getDni())),
                esc(String.valueOf(u.isEstado())),
                esc(u.getTienda()),
                esc(u.getNombreComp()),
                esc(u.getCargo()),
                esc(u.getUsuario()),
                esc(u.getContrasena()),
                esc(u.getHoraEntradaProg() == null ? "" : u.getHoraEntradaProg().toString()),
                esc(u.getHoraSalidaProg()  == null ? "" : u.getHoraSalidaProg().toString())
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"","\"\"") + "\"";
        return s;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private boolean parseBool(String s) {
        String v = s.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("activo") || v.equals("habilitado");
    }

    private LocalTime parseTime(String s) {
        s = s == null ? "" : s.trim();
        if (s.isEmpty()) return null;
        try { return LocalTime.parse(s); } catch (Exception e) { return null; }
    }

    // split CSV simple con comillas dobles
    private List<String> splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"'); i++; // escape "" -> "
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString()); cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }
}
