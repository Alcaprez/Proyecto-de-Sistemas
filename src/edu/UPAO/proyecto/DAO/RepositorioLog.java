
package edu.UPAO.proyecto.dao;

import edu.UPAO.proyecto.modelo.HistorialCambio;
import java.util.ArrayList;
import java.util.List;

public class RepositorioLog {
    // Memoria compartida estática
    private static List<HistorialCambio> listaGlobal = new ArrayList<>();
    
    public static void agregarLog(HistorialCambio log) {
        listaGlobal.add(0, log); // Insertar al inicio
    }
    
    public static List<HistorialCambio> obtenerLogs() {
        return listaGlobal;
    }
}
