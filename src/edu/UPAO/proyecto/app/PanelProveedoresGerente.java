package edu.UPAO.proyecto.app;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PanelProveedoresGerente extends JPanel {

    // --- COMPONENTES UI ---
    private JTextField txtBuscar;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JLabel lblPaginacionInfo;
    private JButton btnAnterior, btnSiguiente;

    // --- VARIABLES DE LÓGICA ---
    private int paginaActual = 1;
    private final int filasPorPagina = 15; // Ajustable según el tamaño de tu pantalla
    private int totalRegistros = 0;

    // --- DATOS DE CONEXIÓN (Ajusta si tienes una clase Conexion separada) ---
    private final String DB_URL = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    private final String DB_USER = "root";
    private final String DB_PASS = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

    public PanelProveedoresGerente() {
        configurarLayoutGeneral();
        initPanelSuperior();
        initPanelCentral();
        initPanelInferior();
        
        // Carga inicial de datos
        contarRegistrosTotal();
        cargarDatosTabla();
    }

    // ==================== SECCIÓN 1: CONFIGURACIÓN UI ====================

    private void configurarLayoutGeneral() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        // Padding general del panel entero
        setBorder(new EmptyBorder(20, 30, 20, 30));
    }

    // --- 1.1 PANEL SUPERIOR (Título y Buscador) ---
    private void initPanelSuperior() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Título
        JLabel lblTitulo = new JLabel("Proveedores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(33, 37, 41));

        // Panel del buscador (para alinearlo a la derecha)
        JPanel searchPanelWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanelWrapper.setOpaque(false);

        txtBuscar = new JTextField(20);
        txtBuscar.putClientProperty("JTextField.placeholderText", " Buscar..."); // Funciona en librerías modernas como FlatLaf
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(250, 35));
        // Borde redondeado con icono simulado (🔍)
        txtBuscar.setBorder(new CompoundBorder(
                new LineBorder(new Color(206, 212, 218), 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        // Listener para buscar al escribir
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                paginaActual = 1; // Resetear a la primera página al buscar
                contarRegistrosTotal();
                cargarDatosTabla();
            }
        });

        searchPanelWrapper.add(new JLabel("🔍 ")); // Icono simple
        searchPanelWrapper.add(txtBuscar);

        topPanel.add(lblTitulo, BorderLayout.WEST);
        topPanel.add(searchPanelWrapper, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    // --- 1.2 PANEL CENTRAL (Tabla) ---
    private void initPanelCentral() {
        tablaProveedores = new JTable();
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProveedores.setRowHeight(40); // Filas más altas como en la imagen
        tablaProveedores.setSelectionBackground(new Color(231, 241, 255));
        tablaProveedores.setSelectionForeground(Color.BLACK);
        tablaProveedores.setShowVerticalLines(false);
        tablaProveedores.setShowHorizontalLines(true);
        tablaProveedores.setGridColor(new Color(230, 230, 230));

        // Configuración del Header
        JTableHeader header = tablaProveedores.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        // Modelo de datos
        // Nombres de columna basados en la imagen (más ID oculto para lógica)
        String[] columnas = {"ID", "Nombre / Razón Social", "Contacto Principal", "Teléfono", "Correo Electrónico", "Estado"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                 return String.class;
            }
        };
        tablaProveedores.setModel(modeloTabla);

        // Ocultar columna ID
        tablaProveedores.getColumnModel().getColumn(0).setMinWidth(0);
        tablaProveedores.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaProveedores.getColumnModel().getColumn(0).setWidth(0);

        // Anchos preferidos
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(180); // Correo
        tablaProveedores.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado

        // Aplicar Renderizador personalizado para el Estado (Columna 5)
        tablaProveedores.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());

        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    // --- 1.3 PANEL INFERIOR (Paginación y Botones) ---
    private void initPanelInferior() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // -- Panel Izquierdo: Paginación --
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paginationPanel.setOpaque(false);

        btnAnterior = crearBotonPaginacion("Anterior");
        btnSiguiente = crearBotonPaginacion("Siguiente");
        lblPaginacionInfo = new JLabel("Mostrando 0-0 de 0");
        lblPaginacionInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPaginacionInfo.setBorder(new EmptyBorder(0, 10, 0, 10));

        btnAnterior.addActionListener(e -> cambiarPagina(-1));
        btnSiguiente.addActionListener(e -> cambiarPagina(1));

        // Nota: La imagen tiene números (1, 2, 3...), para simplificar usamos Anterior/Siguiente
        // pero con estilo moderno.
        paginationPanel.add(btnAnterior);
        paginationPanel.add(lblPaginacionInfo);
        paginationPanel.add(btnSiguiente);


        // -- Panel Derecho: Botones de Acción --
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonsPanel.setOpaque(false);

        // Usamos emojis como iconos temporales
        JButton btnDescargar = crearBotonAccion("📥 Descargar", new Color(248, 249, 250), Color.BLACK);
        JButton btnFiltrar = crearBotonAccion("⚡ Filtrar", new Color(248, 249, 250), Color.BLACK);
        JButton btnNuevo = crearBotonAccion("➕ Nuevo Proveedor", new Color(13, 110, 253), Color.WHITE); // Azul primario

        actionButtonsPanel.add(btnDescargar);
        actionButtonsPanel.add(btnFiltrar);
        actionButtonsPanel.add(btnNuevo);

        bottomPanel.add(paginationPanel, BorderLayout.WEST);
        bottomPanel.add(actionButtonsPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== SECCIÓN 2: LÓGICA Y BASE DE DATOS ====================

    private void contarRegistrosTotal() {
        String filtro = txtBuscar.getText().trim();
        String sql = "SELECT COUNT(*) FROM proveedor WHERE Razon_Social LIKE ? OR Contacto_Principal LIKE ?";
        
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ps.setString(2, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRegistros = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            totalRegistros = 0;
        }
        actualizarControlesPaginacion();
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        String filtro = txtBuscar.getText().trim();
        int offset = (paginaActual - 1) * filasPorPagina;

        // Asumo que tu tabla proveedor tiene estas columnas o similares
        String sql = "SELECT id_proveedor, Razon_Social, Contacto_Principal, Telefono, Correo, estado " +
                     "FROM proveedor " +
                     "WHERE Razon_Social LIKE ? OR Contacto_Principal LIKE ? " +
                     "ORDER BY Razon_Social ASC " +
                     "LIMIT ? OFFSET ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + filtro + "%");
            ps.setString(2, "%" + filtro + "%");
            ps.setInt(3, filasPorPagina);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id_proveedor"),
                        rs.getString("Razon_Social"),
                        rs.getString("Contacto_Principal"),
                        rs.getString("Telefono"),
                        rs.getString("Correo"),
                        rs.getString("estado") // Asegúrate que en BD sea "Activo" o "Inactivo"
                    };
                    modeloTabla.addRow(fila);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
        actualizarControlesPaginacion();
    }

    private void cambiarPagina(int direccion) {
        paginaActual += direccion;
        cargarDatosTabla();
    }

    private void actualizarControlesPaginacion() {
        int totalPaginas = (int) Math.ceil((double) totalRegistros / filasPorPagina);
        if (totalPaginas == 0) totalPaginas = 1;

        int inicio = (paginaActual - 1) * filasPorPagina + 1;
        int fin = Math.min(inicio + filasPorPagina - 1, totalRegistros);

        if (totalRegistros == 0) {
            lblPaginacionInfo.setText("Sin resultados");
        } else {
            lblPaginacionInfo.setText("Mostrando " + inicio + "-" + fin + " de " + totalRegistros);
        }
        
        btnAnterior.setEnabled(paginaActual > 1);
        btnSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ==================== SECCIÓN 3: UTILIDADES VISUALES ====================

    // Renderizador para los estados con "Badges" de color (Como en la imagen)
    private class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String estado = (String) value;
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true); // Necesario para ver el color de fondo

            // Estilo tipo "Badge" redondeado
            label.setBorder(new LineBorder(Color.WHITE, 8, true)); // Truco para margen interno redondeado
            
            if (estado != null && (estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Active"))) {
                label.setBackground(new Color(209, 231, 221)); // Verde claro fondo
                label.setForeground(new Color(25, 135, 84));   // Verde oscuro texto
            } else {
                label.setBackground(new Color(248, 215, 218)); // Rojo claro fondo
                label.setForeground(new Color(220, 53, 69));   // Rojo oscuro texto
            }
            
            if(isSelected) {
                 label.setBackground(table.getSelectionBackground());
                 label.setForeground(table.getSelectionForeground());
            }
            
            return label;
        }
    }

    private JButton crearBotonPaginacion(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(new Color(222, 226, 230), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 35));
        return btn;
    }
    
    private JButton crearBotonAccion(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Borde sutil si es blanco
        if(bg.equals(Color.WHITE) || bg.getRed() > 240) {
             btn.setBorder(new CompoundBorder(new LineBorder(new Color(222, 226, 230)), new EmptyBorder(10, 15, 10, 15)));
        }
        return btn;
    }
}