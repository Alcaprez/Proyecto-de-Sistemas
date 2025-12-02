package edu.UPAO.proyecto.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GeneradorExcelRentabilidad {

    public static void generarExcel(JTable table, String nombreReporte) {
        // 1. Selector de archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Compatible con Excel");
        fileChooser.setSelectedFile(new File(nombreReporte + ".csv")); // Usamos .csv que Excel lee nativo
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo CSV (Excel) (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                
                // --- 2. GUARDAR ENCABEZADOS ---
                for (int i = 0; i < table.getColumnCount(); i++) {
                    bw.write(table.getColumnName(i));
                    // Usamos punto y coma (;) como separador porque en español Excel 
                    // a veces confunde la coma (,) con los decimales.
                    if (i < table.getColumnCount() - 1) {
                        bw.write(";"); 
                    }
                }
                bw.newLine(); // Salto de línea después de encabezados

                // --- 3. GUARDAR DATOS ---
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object valor = table.getValueAt(i, j);
                        if (valor != null) {
                            String texto = valor.toString();
                            // Limpiamos saltos de línea internos para no romper el CSV
                            texto = texto.replaceAll("\n", " ").replaceAll("\r", " ");
                            bw.write(texto);
                        } else {
                            bw.write("");
                        }
                        
                        if (j < table.getColumnCount() - 1) {
                            bw.write(";"); // Separador de columnas
                        }
                    }
                    bw.newLine(); // Fin de la fila
                }

                JOptionPane.showMessageDialog(null, "Reporte exportado correctamente.\nPuedes abrirlo con Excel.\nUbicación: " + filePath);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al guardar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}