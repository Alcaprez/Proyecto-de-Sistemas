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
        usuarios.put("admin", "admin123");
        tiposUsuario.put("admin", "admin");
        
        usuarios.put("cajero1", "cajero123");
        tiposUsuario.put("cajero1", "cajero");
        
        usuarios.put("cajero2", "cajero456");
        tiposUsuario.put("cajero2", "cajero");
        
        usuarios.put("gerente", "gerente123");
        tiposUsuario.put("gerente", "gerente"); // ✅ GERENTE
    }
    
    public static boolean validarCredenciales(String identificacion, String password) {
        return usuarios.containsKey(identificacion) && 
               usuarios.get(identificacion).equals(password);
    }
    
    // ✅ NUEVO MÉTODO: Obtener tipo de usuario
    public static String getTipoUsuario(String identificacion) {
        return tiposUsuario.getOrDefault(identificacion, "cajero");
    }
    
    public static void agregarUsuario(String identificacion, String password, String tipo) {
        usuarios.put(identificacion, password);
        tiposUsuario.put(identificacion, tipo);
    }
    
    public static boolean usuarioExiste(String identificacion) {
        return usuarios.containsKey(identificacion);
    }
}