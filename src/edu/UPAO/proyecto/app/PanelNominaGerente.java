package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.NominaDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelNominaGerente extends JPanel {

    // --- COMPONENTES UI ---
    private JComboBox<String> cbSucursal;
    private JComboBox<String> cbMes;
    private JTable tablaNomina;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalPlanilla;
    private JLabel lblTotalPagado;
    private JLabel lblTotalPendiente;

    // --- DAOs ---
    private NominaDAO nominaDAO;
    private SucursalDAO sucursalDAO;

    public PanelNominaGerente() {
        nominaDAO = new NominaDAO();
        sucursalDAO = new SucursalDAO();

        configurarLayoutGeneral();
        initPanelSuperior(); // Filtros y KPIs
        initPanelCentral();  // Tabla
        initPanelInferior(); // Botones de Acción

        cargarDatos(); // Carga inicial
    }

    private void configurarLayoutGeneral() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 20, 30));
    }

    // --- 1. PANEL SUPERIOR (KPIs y Filtros) ---
    private void initPanelSuperior() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // 1.1 Título y Filtros
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Nómina de Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(33, 37, 41));

        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtrosPanel.setOpaque(false);

        // Combo Sucursal
        cbSucursal = new JComboBox<>();
        cargarSucursales();
        cbSucursal.setPreferredSize(new Dimension(200, 35));
        cbSucursal.addActionListener(e -> cargarDatos());

        // Combo Mes
        cbMes = new JComboBox<>();
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (String m : meses) {
            cbMes.addItem(m);
        }
        // Seleccionar mes actual
        int mesActual = LocalDate.now().getMonthValue() - 1;
        cbMes.setSelectedIndex(mesActual);
        cbMes.setPreferredSize(new Dimension(150, 35));
        cbMes.addActionListener(e -> cargarDatos());

        filtrosPanel.add(new JLabel("Tienda:"));
        filtrosPanel.add(cbSucursal);
        filtrosPanel.add(new JLabel("Mes:"));
        filtrosPanel.add(cbMes);

        // 1.2 Tarjetas de Resumen (KPIs)
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiPanel.setOpaque(false);

        lblTotalPlanilla = crearTarjetaKPI("Total Planilla", "S/ 0.00", new Color(13, 110, 253)); // Azul
        lblTotalPagado = crearTarjetaKPI("Pagado", "S/ 0.00", new Color(25, 135, 84));    // Verde
        lblTotalPendiente = crearTarjetaKPI("Pendiente", "S/ 0.00", new Color(220, 53, 69)); // Rojo

        kpiPanel.add(lblTotalPlanilla.getParent());
        kpiPanel.add(lblTotalPagado.getParent());
        kpiPanel.add(lblTotalPendiente.getParent());

        // Armar panel superior
        JPanel titleFilterContainer = new JPanel(new BorderLayout());
        titleFilterContainer.setOpaque(false);
        titleFilterContainer.add(lblTitulo, BorderLayout.WEST);
        titleFilterContainer.add(filtrosPanel, BorderLayout.EAST);

        topPanel.add(titleFilterContainer, BorderLayout.NORTH);
        topPanel.add(kpiPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
    }

    // --- 2. PANEL CENTRAL (Tabla) ---
    private void initPanelCentral() {
        tablaNomina = new JTable();
        tablaNomina.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaNomina.setRowHeight(30);
        tablaNomina.setSelectionBackground(new Color(231, 241, 255));
        tablaNomina.setSelectionForeground(Color.BLACK);
        tablaNomina.setShowVerticalLines(false);
        tablaNomina.setShowHorizontalLines(true);
        tablaNomina.setGridColor(new Color(230, 230, 230));

        // Header
        JTableHeader header = tablaNomina.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        // Modelo (Columnas)
        String[] columnas = {"ID", "Empleado", "Cargo", "Sueldo Base", "Estado", "Fecha Pago"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaNomina.setModel(modeloTabla);

        // Estilos de columna
        tablaNomina.getColumnModel().getColumn(0).setPreferredWidth(90);  // ID
        tablaNomina.getColumnModel().getColumn(1).setPreferredWidth(240); // Nombre
        tablaNomina.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer()); // Badge Estado

        JScrollPane scrollPane = new JScrollPane(tablaNomina);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel centralWrapper = new JPanel(new BorderLayout());
        centralWrapper.setOpaque(false);

        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        add(scrollPane, BorderLayout.CENTER);

    }

    // --- 3. PANEL INFERIOR (Botones) ---
    private void initPanelInferior() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);

        JButton btnPagar = crearBotonAccion("💰 Procesar Pago", new Color(25, 135, 84), Color.WHITE);
        JButton btnExportar = crearBotonAccion("📥 Exportar Planilla", new Color(248, 249, 250), Color.BLACK);

        btnPagar.addActionListener(e -> procesarPago());
        // btnExportar.addActionListener(e -> exportarExcel()); // Implementar si deseas

        bottomPanel.add(btnExportar);
        bottomPanel.add(btnPagar);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== LÓGICA ====================
    private void cargarSucursales() {
        cbSucursal.removeAllItems();
        try {
            List<String> sucursales = sucursalDAO.obtenerSucursalesActivas();
            for (String s : sucursales) {
                cbSucursal.addItem(s);
            }
        } catch (Exception e) {
            cbSucursal.addItem("Error cargar");
        }
    }

    private void cargarDatos() {
        String nombreSucursal = (String) cbSucursal.getSelectedItem();
        String mes = (String) cbMes.getSelectedItem();
        int anio = LocalDate.now().getYear();

        if (nombreSucursal == null || mes == null) {
            return;
        }

        // 1. Obtener ID Sucursal (Simulado o real según tu DAO)
        int idSucursal = obtenerIdSucursal(nombreSucursal);

        // 2. Llamar al DAO
        List<Object[]> datos = nominaDAO.listarNominaMes(idSucursal, mes, anio, "");

        modeloTabla.setRowCount(0);
        double totalPlanilla = 0;
        double totalPagado = 0;

        for (Object[] fila : datos) {
            // Adaptar los datos del DAO a nuestra tabla bonita
            // DAO devuelve: ID, Nombres, Apellidos, Cargo, Sueldo, Estado, Fecha
            String nombreCompleto = fila[1] + " " + fila[2];
            double sueldo = Double.parseDouble(fila[4].toString());
            String estado = fila[5].toString();

            modeloTabla.addRow(new Object[]{
                fila[0], // ID
                nombreCompleto,
                fila[3], // Cargo
                "S/ " + String.format("%.2f", sueldo),
                estado,
                fila[6] // Fecha
            });

            // Cálculos
            totalPlanilla += sueldo;
            if ("PAGADO".equalsIgnoreCase(estado)) {
                totalPagado += sueldo;
            }
        }

        // Actualizar tarjetas
        actualizarTarjeta(lblTotalPlanilla, totalPlanilla);
        actualizarTarjeta(lblTotalPagado, totalPagado);
        actualizarTarjeta(lblTotalPendiente, totalPlanilla - totalPagado);
    }

    private void procesarPago() {
        int fila = tablaNomina.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado para pagar.");
            return;
        }

        String estado = modeloTabla.getValueAt(fila, 4).toString();
        if ("PAGADO".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Este empleado ya está pagado.");
            return;
        }

        String idEmp = modeloTabla.getValueAt(fila, 0).toString();
        String nombre = modeloTabla.getValueAt(fila, 1).toString();
        // Limpiamos "S/ " para obtener el double
        double sueldo = Double.parseDouble(modeloTabla.getValueAt(fila, 3).toString().replace("S/ ", "").replace(",", "."));

        String mes = (String) cbMes.getSelectedItem();
        String nombreSucursal = (String) cbSucursal.getSelectedItem();
        int idSucursal = obtenerIdSucursal(nombreSucursal);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Pagar S/ " + sueldo + " a " + nombre + "?\nSe descontará de caja.",
                "Confirmar Pago", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = nominaDAO.pagarEmpleado(idEmp, sueldo, mes, LocalDate.now().getYear(), idSucursal);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Pago realizado con éxito.");
                cargarDatos(); // Recargar tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

// REEMPLAZAR EN PanelNominaGerente.java
    private int obtenerIdSucursal(String nombreSucursal) {
        // ANTES: return 1; (Esto causaba el error)
        // AHORA: Preguntamos a la base de datos cuál es el ID verdadero
        try {
            // Asegúrate de que sucursalDAO esté inicializado en el constructor
            if (sucursalDAO == null) {
                sucursalDAO = new SucursalDAO();
            }

            return sucursalDAO.obtenerIdPorNombre(nombreSucursal);
        } catch (Exception e) {
            System.err.println("Error buscando ID de sucursal: " + e.getMessage());
            return -1;
        }
    }

    // ==================== ESTILOS ====================
    private JLabel crearTarjetaKPI(String titulo, String valorInicial, Color colorBorde) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(new Color(250, 250, 250));
        tarjeta.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new javax.swing.border.MatteBorder(0, 5, 0, 0, colorBorde) // Borde izquierdo de color
        ));

        JLabel lblTitulo = new JLabel(" " + titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(Color.GRAY);

        JLabel lblValor = new JLabel(" " + valorInicial);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(Color.DARK_GRAY);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return lblValor; // Retornamos el label del valor para actualizarlo luego
    }

    private void actualizarTarjeta(JLabel label, double monto) {
        label.setText(" S/ " + String.format("%.2f", monto));
    }

    private JButton crearBotonAccion(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(1, 20, 1, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Renderizador para "Badges" de Estado (Pagado/Pendiente)
    private class EstadoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String estado = (String) value;
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            // Estilo Badge
            if ("PAGADO".equalsIgnoreCase(estado)) {
                label.setBackground(new Color(209, 231, 221)); // Verde claro
                label.setForeground(new Color(25, 135, 84));   // Verde oscuro
            } else {
                label.setBackground(new Color(255, 243, 205)); // Amarillo claro
                label.setForeground(new Color(133, 100, 4));   // Marrón/Naranja oscuro
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            return label;
        }
    }
}
