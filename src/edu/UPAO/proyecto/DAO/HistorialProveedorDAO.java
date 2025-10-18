package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.HistorialProveedor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistorialProveedorDAO {
  private static final String ARCHIVO_CSV = "historial_proveedores.csv";
    private static final List<HistorialProveedor> historial = new ArrayList<>();
    private static int contadorId = 1;

    public HistorialProveedorDAO() {
        cargarDesdeCSV();
        historial.stream()
                .mapToInt(HistorialProveedor::getIdHistorial)
                .max()
                .ifPresent(maxId -> contadorId = maxId + 1);
    }

    /** Registra un nuevo evento para un proveedor
     * @param idProveedor
     * @param evento
     * @param descripcion */
    public void registrarEvento(int idProveedor, String evento, String descripcion) {
        HistorialProveedor nuevo = new HistorialProveedor(contadorId++, idProveedor, evento, descripcion, LocalDateTime.now());
        historial.add(nuevo);
        guardarEnCSV();
    }

    /** Devuelve el historial completo
     * @return  */
    public List<HistorialProveedor> listar() {
        return new ArrayList<>(historial);
    }

    /** Devuelve los eventos de un proveedor específico
     * @param idProveedor
     * @return  */
    public List<HistorialProveedor> listarPorProveedor(int idProveedor) {
        List<HistorialProveedor> lista = new ArrayList<>();
        for (HistorialProveedor h : historial) {
            if (h.getIdProveedor() == idProveedor) {
                lista.add(h);
            }
        }
        return lista;
    }

    // ==========================
    //  MÉTODOS PRIVADOS CSV
    // ==========================
    private void cargarDesdeCSV() {
        historial.clear();
        File archivo = new File(ARCHIVO_CSV);
        if (!archivo.exists()) {
            System.out.println("Archivo historial_proveedores.csv no encontrado, se creará uno nuevo.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine(); // Encabezado
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] c = linea.split(",", -1);
                if (c.length < 5) continue;

                int idHistorial = Integer.parseInt(c[0].trim());
                int idProveedor = Integer.parseInt(c[1].trim());
                String evento = c[2].trim();
                String descripcion = c[3].trim();
                LocalDateTime fecha = LocalDateTime.parse(c[4].trim());

                historial.add(new HistorialProveedor(idHistorial, idProveedor, evento, descripcion, fecha));
            }
        } catch (IOException e) {
            System.err.println("Error al leer historial CSV: " + e.getMessage());
        }
    }

    private void guardarEnCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_CSV))) {
            bw.write("idHistorial,idProveedor,evento,descripcion,fecha\n");
            for (HistorialProveedor h : historial) {
                bw.write(String.format("%d,%d,%s,%s,%s\n",
                        h.getIdHistorial(),
                        h.getIdProveedor(),
                        h.getEvento(),
                        h.getDescripcion(),
                        h.getFecha().toString()));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar historial CSV: " + e.getMessage());
        }
    }  
}
