package edu.UPAO.proyecto.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// ESTA CLASE ES EL PANEL COMPLETO DE LA PESTAÑA "INVENTARIO GLOBAL"
public class PanelInventarioGlobal extends JPanel {

    // Componentes gráficos
    private JTextField txtBuscar;
    private JComboBox<String> cboCategoria;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JButton btnAnterior, btnSiguiente;
    private JLabel lblPaginacion;

    // Variables de datos y paginación
    private int paginaActual = 1;
    private final int filasPorPagina = 20;
    private int totalFilas = 0;

    // Constructor: Aquí se construye toda la interfaz
    public PanelInventarioGlobal() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Fondo gris claro
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margen externo

        // 1. PANEL SUPERIOR (Filtros)
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 0));
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Buscador
        JPanel panelBuscar = new JPanel(new BorderLayout(5, 0));
        panelBuscar.setOpaque(false);
        JLabel lblBuscar = new JLabel("🔍");
        lblBuscar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setPreferredSize(new Dimension(300, 35));
        // Evento al escribir
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paginaActual = 1; // Resetear a página 1
                cargarDatos();
            }
        });
        panelBuscar.add(lblBuscar, BorderLayout.WEST);
        panelBuscar.add(txtBuscar, BorderLayout.CENTER);

        // Combo Categoría
        cboCategoria = new JComboBox<>();
        cboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboCategoria.setBackground(Color.WHITE);
        cboCategoria.setPreferredSize(new Dimension(200, 35));
        cargarCategoriasCombo();
        cboCategoria.addActionListener(e -> {
            paginaActual = 1;
            cargarDatos();
        });

        // Botones de Acción (Solo visuales por ahora)
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesAccion.setOpaque(false);
        JButton btnEditar = crearBotonAccion("✏️ Editar", new Color(255, 193, 7));
        JButton btnEliminar = crearBotonAccion("🗑️ Eliminar", new Color(220, 53, 69));
        panelBotonesAccion.add(btnEditar);
        panelBotonesAccion.add(btnEliminar);

        // Armar panel superior
        JPanel panelFiltrosIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltrosIzquierda.setOpaque(false);
        panelFiltrosIzquierda.add(panelBuscar);
        panelFiltrosIzquierda.add(new JLabel("Categoría:"));
        panelFiltrosIzquierda.add(cboCategoria);

        panelSuperior.add(panelFiltrosIzquierda, BorderLayout.CENTER);
        panelSuperior.add(panelBotonesAccion, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // 2. PANEL CENTRAL (Tabla)
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablaInventario = new JTable();
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaInventario.setRowHeight(30);
        tablaInventario.setSelectionBackground(new Color(232, 240, 254));
        tablaInventario.setSelectionForeground(Color.BLACK);
        tablaInventario.setShowVerticalLines(false);
        tablaInventario.setIntercellSpacing(new Dimension(0, 0));

        // Encabezado de tabla
        JTableHeader header = tablaInventario.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Prod", "Código", "Nombre Producto", "Categoría", "Stock Total (Global)", "Precio Venta", "Estado"}
        ) {
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        tablaInventario.setModel(modeloTabla);
        scrollPane.setViewportView(tablaInventario);

        // Configurar anchos de columna
        tablaInventario.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tablaInventario.getColumnModel().getColumn(1).setPreferredWidth(100); // Código
        tablaInventario.getColumnModel().getColumn(2).setPreferredWidth(250); // Nombre
        tablaInventario.getColumnModel().getColumn(4).setPreferredWidth(120); // Stock
        tablaInventario.getColumnModel().getColumn(6).setPreferredWidth(80);  // Estado

        add(scrollPane, BorderLayout.CENTER);

        // 3. PANEL INFERIOR (Paginación)
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelPaginacion.setOpaque(false);

        btnAnterior = crearBotonPaginacion("<");
        btnSiguiente = crearBotonPaginacion(">");
        lblPaginacion = new JLabel("Página 1 de 1");
        lblPaginacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnAnterior.addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                cargarDatos();
            }
        });

        btnSiguiente.addActionListener(e -> {
            int totalPaginas = (int) Math.ceil((double) totalFilas / filasPorPagina);
            if (paginaActual < totalPaginas) {
                paginaActual++;
                cargarDatos();
            }
        });

        panelPaginacion.add(lblPaginacion);
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(btnSiguiente);

        add(panelPaginacion, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarDatos();
    }

    // --- MÉTODOS AUXILIARES DE DATOS ---
    private void cargarCategoriasCombo() {
        cboCategoria.removeAllItems();
        cboCategoria.addItem("Todas las Categorías");
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";
        try (Connection con = DriverManager.getConnection(url, usuario, password);
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cboCategoria.addItem(rs.getString("nombre"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

        String filtroTexto = txtBuscar.getText().trim();
        String filtroCat = cboCategoria.getSelectedItem() != null ? cboCategoria.getSelectedItem().toString() : "Todas las Categorías";

        // 1. Contar total de filas para paginación
        String sqlCount = "SELECT COUNT(DISTINCT p.id_producto) FROM producto p " +
                          "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                          "WHERE (p.nombre LIKE ? OR p.codigo LIKE ?) ";
        if (!filtroCat.equals("Todas las Categorías")) {
            sqlCount += " AND c.nombre = ?";
        }

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement psCount = con.prepareStatement(sqlCount);
            psCount.setString(1, "%" + filtroTexto + "%");
            psCount.setString(2, "%" + filtroTexto + "%");
            if (!filtroCat.equals("Todas las Categorías")) {
                psCount.setString(3, filtroCat);
            }
            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) totalFilas = rsCount.getInt(1);

            // 2. Consulta de datos paginados (Stock sumado de todas las sucursales)
            String sqlData = "SELECT p.id_producto, p.codigo, p.nombre, c.nombre as categoria, " +
                             "COALESCE(SUM(i.stock_actual), 0) as stock_global, " +
                             "p.precio_venta, p.estado " +
                             "FROM producto p " +
                             "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                             "LEFT JOIN inventario_sucursal i ON p.id_producto = i.id_producto " +
                             "WHERE (p.nombre LIKE ? OR p.codigo LIKE ?) ";
            
            if (!filtroCat.equals("Todas las Categorías")) {
                sqlData += " AND c.nombre = ? ";
            }
            
            sqlData += "GROUP BY p.id_producto, p.codigo, p.nombre, c.nombre, p.precio_venta, p.estado " +
                       "ORDER BY p.nombre ASC LIMIT ? OFFSET ?";

            PreparedStatement psData = con.prepareStatement(sqlData);
            int paramIndex = 1;
            psData.setString(paramIndex++, "%" + filtroTexto + "%");
            psData.setString(paramIndex++, "%" + filtroTexto + "%");
            if (!filtroCat.equals("Todas las Categorías")) {
                psData.setString(paramIndex++, filtroCat);
            }
            psData.setInt(paramIndex++, filasPorPagina);
            psData.setInt(paramIndex++, (paginaActual - 1) * filasPorPagina);

            ResultSet rsData = psData.executeQuery();
            while (rsData.next()) {
                modeloTabla.addRow(new Object[]{
                        rsData.getInt("id_producto"),
                        rsData.getString("codigo"),
                        rsData.getString("nombre"),
                        rsData.getString("categoria"),
                        rsData.getInt("stock_global"), // Stock total de todas las tiendas
                        "S/ " + String.format("%.2f", rsData.getDouble("precio_venta")),
                        rsData.getString("estado")
                });
            }

            actualizarControlesPaginacion();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void actualizarControlesPaginacion() {
        int totalPaginas = (int) Math.ceil((double) totalFilas / filasPorPagina);
        if (totalPaginas == 0) totalPaginas = 1;
        lblPaginacion.setText("Página " + paginaActual + " de " + totalPaginas);
        btnAnterior.setEnabled(paginaActual > 1);
        btnSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // --- MÉTODOS DE ESTILO VISUAL ---
    private JButton crearBotonAccion(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBotonPaginacion(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(100, 100, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}