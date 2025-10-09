package edu.UPAO.proyecto;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private static Map<String, String> usuarios;
    private static Map<String, String> tiposUsuario; // ✅ NUEVO: Para almacenar tipos

    static {
        usuarios = new HashMap<>();
        tiposUsuario = new HashMap<>(); // ✅ NUEVO

        // Agregar usuarios de prueba con sus tipos
        usuarios.put("admin", "admin");
        tiposUsuario.put("admin", "admin");

        usuarios.put("lucas", "cajero");
        tiposUsuario.put("lucas", "cajero");

        usuarios.put("karina", "cajero");
        tiposUsuario.put("karina", "cajero");

        usuarios.put("alberth", "cajero");
        tiposUsuario.put("alberth", "cajero");

        usuarios.put("jhosep", "cajero");
        tiposUsuario.put("jhosep", "cajero");

        usuarios.put("lucas", "gerente");
        tiposUsuario.put("gerente", "gerente"); // ✅ GERENTE
    }

    public static boolean validarCredenciales(String identificacion, String password) {
        return usuarios.containsKey(identificacion)
                && usuarios.get(identificacion).equals(password);
    }

    // ✅ NUEVO MÉTODO: Obtener tipo de usuario
    public static String getTipoUsuario(String identificacion) {
        return tiposUsuario.getOrDefault(identificacion, "cajero");
    }

    public static void agregarUsuario(String identificacion, String password, String tipo) {
        usuarios.put(identificacion, password);
        tiposUsuario.put(identificacion, tipo);
    }

    public static String getNombreUsuario(String identificacion) {
        // Implementa según tu lógica
        // Ejemplo simple:
        if ("admin".equals(identificacion)) {
            return "Administrador";
        } else if ("gerente".equals(identificacion)) {
            return "Gerente";
        } else {
            return "Cajero " + identificacion;
        }
    }

    public static boolean usuarioExiste(String identificacion) {
        return usuarios.containsKey(identificacion);
    }

}
