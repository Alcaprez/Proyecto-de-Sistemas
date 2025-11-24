package edu.UPAO.proyecto.Util;

import java.io.FileWriter;
import javax.swing.JTable;
import javax.swing.JOptionPane;

public class  GeneradorExcelRk{

    // Este es el método que el botón está buscando.
    // Asegúrate de que se llame exactamente así: generarExcel
    public void generarExcel(JTable tabla, String mes) {
        try {
            // 1. Definir nombre del archivo
            String ruta = "Ranking_" + mes + ".csv";
            FileWriter fw = new FileWriter(ruta);
            
            // 2. Escribir Cabeceras
            fw.write("TOP;LOCALES;VENTAS;TRANSACCIONES\n");
            
            // 3. Escribir Datos (Recorriendo la tabla)
            for (int i = 0; i < tabla.getRowCount(); i++) {
                Object topObj = tabla.getValueAt(i, 0);
                Object localObj = tabla.getValueAt(i, 1);
                Object ventasObj = tabla.getValueAt(i, 2);
                Object transObj = tabla.getValueAt(i, 3);

                // Evitar error si alguna celda está vacía (null)
                String top = (topObj != null) ? topObj.toString() : "";
                String local = (localObj != null) ? localObj.toString() : "";
                
                // Limpiar símbolos de moneda "S/" para que Excel entienda los números
                String ventas = (ventasObj != null) ? ventasObj.toString().replace("S/", "").replace(",", "").trim() : "0";
                String trans = (transObj != null) ? transObj.toString().replace("S/", "").replace(",", "").trim() : "0";
                
                // Escribir fila en el archivo (separado por punto y coma)
                fw.write(top + ";" + local + ";" + ventas + ";" + trans + "\n");
            }
            
            // 4. Cerrar y avisar
            fw.close();
            JOptionPane.showMessageDialog(null, "Excel Exportado correctamente:\n" + ruta);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al exportar Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}