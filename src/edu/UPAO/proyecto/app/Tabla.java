

package proyectosistemasempresariales.vista;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Tabla extends JTable {

    private DefaultTableModel modelo;

    public Tabla(String[] columnas) {
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desactivar edición
            }
        };
        this.setModel(modelo);
    }

    
    public Tabla() {
    modelo = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    this.setModel(modelo);
}

    public void agregarFila(Object[] fila) {
        modelo.addRow(fila);
    }

    public void eliminarFila(int fila) {
        if (fila >= 0 && fila < modelo.getRowCount()) {
            modelo.removeRow(fila);
        }
    }

    public void limpiarTabla() {
        modelo.setRowCount(0);
    }

    public DefaultTableModel getModelo() {
        return modelo;
    }
}
