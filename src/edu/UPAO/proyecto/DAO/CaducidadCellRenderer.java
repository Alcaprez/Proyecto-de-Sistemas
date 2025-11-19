
package edu.UPAO.proyecto.DAO;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CaducidadCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component cell = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        
        // No colorear si está seleccionada
        if (!isSelected) {
            // Obtener días restantes de la columna 3
            Object diasObj = table.getValueAt(row, 3);
            
            if (diasObj != null) {
                try {
                    long diasRestantes = Long.parseLong(diasObj.toString());
                    
                    // Aplicar colores según días restantes
                    if (diasRestantes < 0) {
                        cell.setBackground(new Color(220, 53, 69)); // Rojo
                        cell.setForeground(Color.WHITE);
                    } else if (diasRestantes <= 7) {
                        cell.setBackground(new Color(255, 193, 7)); // Amarillo
                        cell.setForeground(Color.BLACK);
                    } else if (diasRestantes <= 30) {
                        cell.setBackground(new Color(255, 235, 156)); // Beige
                        cell.setForeground(Color.BLACK);
                    } else {
                        cell.setBackground(Color.WHITE);
                        cell.setForeground(Color.BLACK);
                    }
                } catch (NumberFormatException e) {
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(Color.BLACK);
                }
            }
        }
        
        return cell;
    }
}
