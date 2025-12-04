package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.HorarioDAO;
import edu.UPAO.proyecto.Modelo.HorarioEmpleado;
import java.util.HashMap;
import java.util.Map;


public class LoginController {

    private static Map<String, String> usuarios;
    private static Map<String, String> tiposUsuario; // ✅ NUEVO: Para almacenar tipos

    static {
        usuarios = new HashMap<>();
        tiposUsuario = new HashMap<>(); // ✅ NUEVO

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

    private static String obtenerDiaSemana(java.time.DayOfWeek dia) {
        switch (dia) {
            case MONDAY:
                return "Lunes";
            case TUESDAY:
                return "Martes";
            case WEDNESDAY:
                return "Miércoles";
            case THURSDAY:
                return "Jueves";
            case FRIDAY:
                return "Viernes";
            case SATURDAY:
                return "Sábado";
            case SUNDAY:
                return "Domingo";
            default:
                return "";
        }
    }

// En edu.UPAO.proyecto.LoginController

public static boolean esHorarioValido(String idEmpleado) {
    // A. Obtener fecha y hora actual
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    String diaActual = obtenerDiaSemana(ahora.getDayOfWeek());
    java.time.LocalTime horaActual = ahora.toLocalTime();

    // B. Consultar el horario en la BD
    edu.UPAO.proyecto.DAO.HorarioDAO horarioDAO = new edu.UPAO.proyecto.DAO.HorarioDAO();
    edu.UPAO.proyecto.Modelo.HorarioEmpleado horario = horarioDAO.obtenerHorarioPorDia(idEmpleado, diaActual);

    // C. Validar existencia
    if (horario == null) {
        System.out.println("⚠️ No se encontró horario para " + diaActual + ". (Política: Se permite acceso)");
        return true;
    }

    // D. Comprobar rango de horas (Con 15 min de tolerancia)
    java.time.LocalTime entradaOriginal = horario.getHoraEntrada();
    java.time.LocalTime salida = horario.getHoraSalida();
    
    // Calculamos la hora de entrada con tolerancia
    java.time.LocalTime entradaConTolerancia = entradaOriginal.minusMinutes(15);

    // ✅ CORRECCIÓN LÓGICA:
    // El turno cruza la medianoche si:
    // 1. La hora de salida es menor que la entrada original (Ej: 22:00 a 06:00)
    // 2. O la tolerancia hizo que la entrada retrocediera al día anterior (Ej: 00:05 - 15min = 23:50)
    boolean cruzaMedianoche = salida.isBefore(entradaOriginal) || entradaConTolerancia.isAfter(entradaOriginal);

    if (cruzaMedianoche) {
        // Caso especial: El rango válido "envuelve" la medianoche
        // Es válido si es "tarde en la noche" (después de 23:xx) O "temprano en la mañana" (antes de salida)
        if (horaActual.isAfter(entradaConTolerancia) || horaActual.isBefore(salida)) {
            return true;
        }
    } else {
        // Turno normal (todo en el mismo día)
        if (horaActual.isAfter(entradaConTolerancia) && horaActual.isBefore(salida)) {
            return true; 
        }
    }

    System.out.println("⛔ Fuera de horario. Hora actual: " + horaActual + " | Acceso válido desde: " + entradaConTolerancia + " hasta " + salida);
    return false;
}
}
