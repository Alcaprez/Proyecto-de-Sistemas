package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.DevolucionDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogoDevolucionVenta extends javax.swing.JDialog {

    // Componentes visuales
    private JTextField tfIdVenta;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    private JSpinner spinnerCantidad;
    private JTextArea taMotivo;
    private JComboBox<String> cbEstadoProducto;
    private JLabel lblProductoSeleccionado;
    private JLabel lblMontoReembolso;
    
    // Lógica y datos de sesión
    private DevolucionDAO devolucionDAO;
    private int idCajaActual; 
    private int idSucursalActual;
    private String idEmpleadoActual;

    public DialogoDevolucionVenta(java.awt.Frame parent, boolean modal, int idCaja, int idSucursal, String idEmpleado) {
        super(parent, modal);
        this.idCajaActual = idCaja;
        this.idSucursalActual = idSucursal;
        this.idEmpleadoActual = idEmpleado;
        
        devolucionDAO = new DevolucionDAO();
        construirInterfaz(); // Método que dibuja la ventana
        
        this.setTitle("Procesar Devolución de Cliente");
        this.setSize(750, 550);
        this.setLocationRelativeTo(parent);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));

        // 1. Panel Superior: Búsqueda
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfIdVenta = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar Venta");
        pnlNorte.add(new JLabel("ID Venta (Ticket):"));
        pnlNorte.add(tfIdVenta);
        pnlNorte.add(btnBuscar);
        add(pnlNorte, BorderLayout.NORTH);

        // 2. Panel Central: Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Producto", "Cant. Comprada", "Precio Unit.", "Subtotal"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaDetalles = new JTable(modeloTabla);
        add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        // 3. Panel Inferior: Formulario
        JPanel pnlSur = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlSur.setBorder(BorderFactory.createTitledBorder("Detalles de la Devolución"));
        
        lblProductoSeleccionado = new JLabel("- Seleccione un producto arriba -");
        lblProductoSeleccionado.setForeground(Color.BLUE);
        
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        cbEstadoProducto = new JComboBox<>(new String[]{"BUEN ESTADO (Revender)", "DAÑADO (Merma/Basura)"});
        taMotivo = new JTextArea(); taMotivo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        lblMontoReembolso = new JLabel("S/ 0.00");
        lblMontoReembolso.setForeground(Color.RED);
        lblMontoReembolso.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton btnConfirmar = new JButton("CONFIRMAR DEVOLUCIÓN");
        btnConfirmar.setBackground(new Color(255, 100, 100));
        btnConfirmar.setForeground(Color.WHITE);

        pnlSur.add(new JLabel("Producto:")); pnlSur.add(lblProductoSeleccionado);
        pnlSur.add(new JLabel("Cantidad a devolver:")); pnlSur.add(spinnerCantidad);
        pnlSur.add(new JLabel("Estado físico:")); pnlSur.add(cbEstadoProducto);
        pnlSur.add(new JLabel("Motivo:")); pnlSur.add(new JScrollPane(taMotivo));
        pnlSur.add(new JLabel("Dinero a devolver:")); pnlSur.add(lblMontoReembolso);
        pnlSur.add(new JLabel("")); pnlSur.add(btnConfirmar); // Espacio vacío a la izquierda
        
        add(pnlSur, BorderLayout.SOUTH);

        // --- EVENTOS ---
        
        // Buscar Venta
        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(tfIdVenta.getText());
                modeloTabla.setRowCount(0); // Limpiar
                var lista = devolucionDAO.buscarDetallesVenta(id);
                if(lista.isEmpty()) JOptionPane.showMessageDialog(this, "Venta no encontrada.");
                for(Object[] fila : lista) modeloTabla.addRow(fila);
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Ingrese un ID numérico válido."); }
        });

        // Seleccionar producto de la tabla
        tablaDetalles.getSelectionModel().addListSelectionListener(e -> {
            int row = tablaDetalles.getSelectedRow();
            if(row != -1) {
                lblProductoSeleccionado.setText(modeloTabla.getValueAt(row, 1).toString());
                int maxCant = Integer.parseInt(modeloTabla.getValueAt(row, 2).toString());
                spinnerCantidad.setModel(new SpinnerNumberModel(1, 1, maxCant, 1)); // Limitar cantidad
                calcularMonto();
            }
        });

        // Calcular monto al cambiar cantidad
        spinnerCantidad.addChangeListener(e -> calcularMonto());

        // Botón Confirmar
        btnConfirmar.addActionListener(e -> procesar());
    }

    private void calcularMonto() {
        int row = tablaDetalles.getSelectedRow();
        if(row != -1) {
            double precio = Double.parseDouble(modeloTabla.getValueAt(row, 3).toString());
            int cant = (int) spinnerCantidad.getValue();
            lblMontoReembolso.setText("S/ " + String.format("%.2f", precio * cant));
        }
    }

    private void procesar() {
        int row = tablaDetalles.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un producto."); return; }
        
        int idVenta = Integer.parseInt(tfIdVenta.getText());
        int idProd = Integer.parseInt(modeloTabla.getValueAt(row, 0).toString());
        int cant = (int) spinnerCantidad.getValue();
        double monto = Double.parseDouble(modeloTabla.getValueAt(row, 3).toString()) * cant;
        String motivo = taMotivo.getText();
        boolean stock = cbEstadoProducto.getSelectedIndex() == 0; // 0 es Buen Estado

        if(devolucionDAO.procesarDevolucion(idVenta, idProd, cant, monto, motivo, stock, idCajaActual, idSucursalActual, idEmpleadoActual)) {
            JOptionPane.showMessageDialog(this, "¡Devolución Exitosa!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al procesar.");
        }
    }
}