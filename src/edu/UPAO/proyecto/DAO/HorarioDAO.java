package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.HorarioEmpleado;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {
    private static final String ARCHIVO_HORARIOS = "horarios_empleados.csv";

    // ✅ GUARDAR HORARIO
    public static void guardarHorario(HorarioEmpleado horario) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_HORARIOS, true))) {
            bw.write(horario.toCSV());
            bw.newLine();
            System.out.println("✅ Horario guardado: " + horario.getIdEmpleado());
        } catch (IOException e) {
            System.err.println("❌ Error guardando horario: " + e.getMessage());
        }
    }

    // ✅ CARGAR TODOS LOS HORARIOS
    public static List<HorarioEmpleado> cargarHorarios() {
        List<HorarioEmpleado> horarios = new ArrayList<>();
        File archivo = new File(ARCHIVO_HORARIOS);
        
        if (!archivo.exists()) return horarios;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                HorarioEmpleado horario = HorarioEmpleado.fromCSV(linea);
                if (horario != null) {
                    horarios.add(horario);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error cargando horarios: " + e.getMessage());
        }
        return horarios;
    }

    // ✅ OBTENER HORARIO POR EMPLEADO
    public static HorarioEmpleado obtenerHorarioPorEmpleado(String idEmpleado) {
        List<HorarioEmpleado> horarios = cargarHorarios();
        return horarios.stream()
                .filter(h -> h.getIdEmpleado().equals(idEmpleado))
                .findFirst()
                .orElse(null);
    }

    // ✅ ACTUALIZAR HORARIO EXISTENTE
    public static void actualizarHorario(HorarioEmpleado horarioActualizado) {
        List<HorarioEmpleado> horarios = cargarHorarios();
        
        // Eliminar el horario antiguo
        horarios.removeIf(h -> h.getIdEmpleado().equals(horarioActualizado.getIdEmpleado()));
        
        // Agregar el nuevo
        horarios.add(horarioActualizado);
        
        // Guardar toda la lista
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_HORARIOS))) {
            for (HorarioEmpleado h : horarios) {
                bw.write(h.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ Error actualizando horario: " + e.getMessage());
        }
    }
}