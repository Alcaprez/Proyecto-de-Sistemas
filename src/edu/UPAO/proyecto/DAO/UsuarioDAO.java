package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String RUTA = "data/empleados.csv";
    private Connection conexion;

    public UsuarioDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conectando UsuarioDAO: " + e.getMessage());
        }
    }

    // En UsuarioDAO - método de prueba
    public boolean probarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("✅ Conexión a BD establecida correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en conexión: " + e.getMessage());
        }
        return false;
    }

    public Usuario autenticar(String idEmpleado, String contrasena) {
        System.out.println("🔐 Intentando autenticar: " + idEmpleado);

        String sql = "SELECT u.id_usuario, u.id_empleado, u.contrasena, u.estado, "
                + "e.dni, e.rol, e.id_sucursal, "
                + "p.nombres, p.apellidos, "
                + "s.nombre_sucursal "
                + "FROM usuario u "
                + "INNER JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + // LEFT JOIN por si acaso
                "WHERE u.id_empleado = ? AND u.contrasena = ? AND u.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            stmt.setString(2, contrasena);

            System.out.println("📝 Ejecutando consulta: " + sql.replace("?", idEmpleado).replace("?", "***"));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ USUARIO AUTENTICADO CORRECTAMENTE");

                Usuario user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setUsuario(rs.getString("id_empleado"));
                user.setCargo(rs.getString("rol"));
                user.setNombreComp(rs.getString("nombres") + " " + rs.getString("apellidos"));
                user.setTienda(rs.getString("nombre_sucursal"));
                user.setContrasena(rs.getString("contrasena"));
                user.setDni(Integer.parseInt(rs.getString("dni")));
                user.setEstado(true);

                // Debug info
                System.out.println("📋 Datos del usuario:");
                System.out.println("   - ID: " + user.getId());
                System.out.println("   - Empleado: " + user.getUsuario());
                System.out.println("   - Nombre: " + user.getNombreComp());
                System.out.println("   - Cargo: " + user.getCargo());
                System.out.println("   - Sucursal: " + user.getTienda());

                return user;
            } else {
                System.out.println("❌ Credenciales incorrectas o usuario inactivo");
                // Verificar si el usuario existe pero la contraseña está mal
                verificarDatosUsuario(idEmpleado);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL en autenticación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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

    // ---------- API PRINCIPAL ----------
    /**
     * Lee usuarios desde data/empleados.csv. Si no existe, devuelve lista
     * vacía.
     */
    public List<Usuario> listar() {
        return new ArrayList<>(); // Temporal
    }

    /**
     * Reescribe el CSV completo (útil para exportar/sincronizar).
     */
    public void guardarTodos(List<Usuario> lista) {
        File f = new File(RUTA);
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            pw.println("id,dni,estado,tienda,nombreComp,cargo,usuario,contrasena,horaEntradaProg,horaSalidaProg");
            for (Usuario u : lista) {
                pw.println(aCsv(u));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Si no existe el CSV, lo crea con data demo (sin GERENTE).
     */
    public void seedIfMissing() {
        File f = new File(RUTA);
        if (f.exists()) {
            return;
        }
        List<Usuario> demo = Arrays.asList(
                new Usuario(1, 45678901, true, "Tienda Central", "Alberth", "CAJERO", "alberth", "123"),
                new Usuario(2, 56789012, true, "Tienda Central", "Jhosep", "CAJERO", "jhosep", "123"),
                new Usuario(3, 93832374, true, "Tienda Central", "Lucas", "CAJERO", "Lucas", "123"),
                new Usuario(4, 74398478, true, "Tienda Central", "karina", "CAJERO", "karina", "123")
        );
        guardarTodos(demo);
    }

    // ---------- HELPERS CSV ----------
    private Usuario parseLinea(String line) {
        // split básico con soporte de comillas dobles
        List<String> cols = splitCsv(line);
        // Esperamos 10 columnas
        if (cols.size() < 10) {
            return null;
        }

        try {
            int id = parseInt(cols.get(0));
            int dni = parseInt(cols.get(1));
            boolean estado = parseBool(cols.get(2));
            String tienda = cols.get(3);
            String nombre = cols.get(4);
            String cargo = cols.get(5);
            String usuario = cols.get(6);
            String pass = cols.get(7);
            LocalTime he = parseTime(cols.get(8)); // puede ser null
            LocalTime hs = parseTime(cols.get(9)); // puede ser null

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
                esc(u.getHoraSalidaProg() == null ? "" : u.getHoraSalidaProg().toString())
        );
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean parseBool(String s) {
        String v = s.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("activo") || v.equals("habilitado");
    }

    private LocalTime parseTime(String s) {
        s = s == null ? "" : s.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(s);
        } catch (Exception e) {
            return null;
        }
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
                    cur.append('\"');
                    i++; // escape "" -> "
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }

    public void verificarDatosUsuario(String idEmpleado) {
        String sql = "SELECT u.id_empleado, e.dni, p.nombres, p.apellidos, e.rol, s.nombre_sucursal "
                + "FROM usuario u "
                + "LEFT JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "LEFT JOIN persona p ON e.dni = p.dni "
                + "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE u.id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ USUARIO ENCONTRADO:");
                System.out.println("ID Empleado: " + rs.getString("id_empleado"));
                System.out.println("DNI: " + rs.getString("dni"));
                System.out.println("Nombre: " + rs.getString("nombres") + " " + rs.getString("apellidos"));
                System.out.println("Rol: " + rs.getString("rol"));
                System.out.println("Sucursal: " + rs.getString("nombre_sucursal"));
            } else {
                System.out.println("❌ NO SE ENCONTRÓ EL USUARIO: " + idEmpleado);
            }
        } catch (SQLException e) {
            System.err.println("Error en verificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // En UsuarioDAO.java - método opcional para validar sucursal
    public boolean verificarSucursalUsuario(String idEmpleado, String nombreSucursal) {
        String sql = "SELECT COUNT(*) as count "
                + "FROM usuario u "
                + "INNER JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE u.id_empleado = ? AND s.nombre_sucursal = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            stmt.setString(2, nombreSucursal);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando sucursal: " + e.getMessage());
        }
        return false;
    }
}
