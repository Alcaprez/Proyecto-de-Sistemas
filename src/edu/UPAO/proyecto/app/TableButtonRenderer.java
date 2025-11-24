
package edu.UPAO.proyecto.app;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer para mostrar un botón en las celdas de la tabla
 */
public class TableButtonRenderer extends JButton implements TableCellRenderer {
    
    public TableButtonRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        
        setText((value == null) ? "" : value.toString());
        return this;
    }
}