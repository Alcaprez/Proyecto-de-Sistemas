package edu.UPAO.proyecto.Util;

import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class GeneradorExcel {
    
    public static void exportarExcel(JTable tabla, String nombreBase) {
        try {
            String nombreArchivo = nombreBase + "_" + System.currentTimeMillis() + ".csv";
            File file = new File(nombreArchivo);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            // Para que Excel reconozca bien las columnas en español, usamos ";" como separador
            // y agregamos el BOM (Byte Order Mark) para tildes.
            bw.write("\ufeff"); 

            // 1. Cabeceras
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                bw.write(tabla.getColumnName(i));
                if (i < tabla.getColumnCount() - 1) bw.write(";");
            }
            bw.newLine();

            // 2. Datos
            for (int i = 0; i < tabla.getRowCount(); i++) {
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    Object val = tabla.getValueAt(i, j);
                    String dato = (val == null) ? "" : val.toString();
                    // Limpiamos saltos de línea o punto y coma para no romper el CSV
                    dato = dato.replace(";", ",").replace("\n", " ");
                    
                    bw.write(dato);
                    if (j < tabla.getColumnCount() - 1) bw.write(";");
                }
                bw.newLine();
            }

            bw.close();
            JOptionPane.showMessageDialog(null, "¡Exportado a Excel (CSV) exitosamente!\nArchivo: " + nombreArchivo);
            
            // Intentar abrir
            java.awt.Desktop.getDesktop().open(file);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al exportar: " + e.getMessage());
        }
    }
}