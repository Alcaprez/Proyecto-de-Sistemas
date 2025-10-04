/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.UPAO.proyecto.DAO;
import edu.UPAO.proyecto.Modelo.RegistroAsistencia;
import java.util.ArrayList;
import java.util.List;

public class RegistroAsistenciaDAO {

    private static final List<RegistroAsistencia> registros = new ArrayList<>();

    // Guardar un nuevo registro
    public static void guardar(RegistroAsistencia registro) {
        registros.add(registro);
        System.out.println("✅ Registro guardado en memoria: " + registro);
    }

    // Listar todos los registros
    public static List<RegistroAsistencia> listarTodos() {
        return new ArrayList<>(registros); // copia para no modificar la lista original
    }

    // Buscar registros por ID de empleado
    public static List<RegistroAsistencia> buscarPorEmpleado(String idEmpleado) {
        List<RegistroAsistencia> resultados = new ArrayList<>();
        for (RegistroAsistencia r : registros) {
            if (r.getIdEmpleado().equalsIgnoreCase(idEmpleado)) {
                resultados.add(r);
            }
        }
        return resultados;
    }

    // Limpiar registros (ejemplo: para pruebas)
    public static void limpiar() {
        registros.clear();
    }
}