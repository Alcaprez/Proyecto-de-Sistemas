package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.HorarioDAO;
import edu.UPAO.proyecto.Modelo.HorarioEmpleado;
import java.time.LocalTime;

public class CargarHorarioTest {
    public static void main(String[] args) {
        HorarioDAO dao = new HorarioDAO();
        
        // Horario super flexible para pruebas
        HorarioEmpleado horarioPrueba = new HorarioEmpleado(
            "12000002", // ID del usuario que usas para probar
            "Usuario Demo",
            LocalTime.of(12, 0),  // 12:00 PM
            LocalTime.of(23, 59), // 11:59 PM
            600 // Mucha tolerancia
        );

        dao.guardarOActualizarHorario(horarioPrueba);
        System.out.println("🚀 Horario de prueba cargado exitosamente en MySQL");
    }
}