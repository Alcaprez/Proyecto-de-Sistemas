package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.VentaDAO;
import edu.UPAO.proyecto.Modelo.Venta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField; // Usamos JTextField simple para fechas por simplicidad, idealmente usar JDateChooser
import javax.swing.table.DefaultTableModel;

public class PanelHistorialVentas extends javax.swing.JPanel {

    private VentaDAO ventaDAO;
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    
    // Componentes de filtro (usamos JFormattedTextField o similar en producción, aquí simulado)
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;

    public PanelHistorialVentas() {
        initComponentsPersonalizado();
        ventaDAO = new VentaDAO();
        cargarVentasMesActual();
    }

    private void initComponentsPersonalizado() {
        setLayout(new BorderLayout());
        
        // --- PANEL SUPERIOR (Filtros) ---
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBackground(new Color(240, 240, 240));
        
        txtFechaInicio = new JTextField(10);
        txtFechaFin = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar por Rango");
        
        // Pre-llenar fechas (Mes actual)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); // Primer día
        txtFechaInicio.setText(sdf.format(cal.getTime()));
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Último día
        txtFechaFin.setText(sdf.format(cal.getTime()));

        panelFiltros.add(new JLabel("Desde (dd/MM/yyyy):"));
        panelFiltros.add(txtFechaInicio);
        panelFiltros.add(new JLabel("Hasta (dd/MM/yyyy):"));
        panelFiltros.add(txtFechaFin);
        panelFiltros.add(btnBuscar);
        
        add(panelFiltros, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Tabla) ---
        String[] columnas = {"ID Venta", "Fecha y Hora", "Cliente", "Empleado", "Método Pago", "Total (S/)"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        add(scrollPane, BorderLayout.CENTER);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> buscarPorFechas());
    }

    private void cargarVentasMesActual() {
        try {
            // Calcular fechas del mes actual
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date inicio = cal.getTime();
            
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date fin = cal.getTime();
            
            llenarTabla(inicio, fin);
        } catch (Exception e) {
            System.err.println("Error calculando fechas: " + e.getMessage());
        }
    }

    private void buscarPorFechas() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date inicio = sdf.parse(txtFechaInicio.getText());
            Date fin = sdf.parse(txtFechaFin.getText());
            llenarTabla(inicio, fin);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use dd/MM/yyyy");
        }
    }

    private void llenarTabla(Date inicio, Date fin) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<Venta> lista = ventaDAO.listarHistorial(inicio, fin);
        
        for (Venta v : lista) {
            Object[] fila = {
                v.getIdVenta(),
                v.getFechaFormateada(), // Método que ya tienes en Venta.java
                v.getIdCliente(),
                v.getIdEmpleado(),
                v.getMetodoPago(),
                String.format("%.2f", v.getTotal())
            };
            modeloTabla.addRow(fila);
        }
    }
}