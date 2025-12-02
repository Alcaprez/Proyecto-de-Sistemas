package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.ProductoCaducidad;
import edu.UPAO.proyecto.DAO.CaducidadCellRenderer;
import edu.UPAO.proyecto.DAO.Categoria;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.BorderLayout;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.table.DefaultTableModel;
import static edu.UPAO.proyecto.DAO.ColorADM.*;
import javax.swing.*;

public class ALMACEN_Admin extends javax.swing.JPanel {

    private DefaultTableModel modeloTabla;
    private List<ProductoCaducidad> listaProductos;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Categoria> listaCategorias;
    private Categoria categoriaSeleccionada;
    private JPanel panelCategorias;

    // DATOS DE CONEXIÓN
    String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    String usuario = "root";
    String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";
    private int idSucursalUsuario;

    // ---------------------------------------------------------
    // ✅ SOLUCIÓN AL ERROR: CONSTRUCTOR VACÍO (POR DEFECTO)
    // ---------------------------------------------------------
    public ALMACEN_Admin() {
        // Si no le pasas ID, asume la sucursal 1 por defecto
        this(1);
    }

    public ALMACEN_Admin(int idSucursal) {
        this.idSucursalUsuario = idSucursal;
        initComponents();

        // --- IMPORTANTE: ESTO VA PRIMERO ---
        // Inicializamos la lista antes de que el ComboBox intente usarla
        listaProductos = new ArrayList<>();
        // -----------------------------------

        inicializarTabla();
        configurarComboBoxes();
        // Carga inicial de datos reales
        cargarDatosDesdeBD();
        actualizarMetricasBD();
        configurarBuscador();
        inicializarInventarioTienda();
    }
// /////////////////////////////////////////////////////
    //  1. GESTIÓN DE CADUCIDAD (TABLA Y FILTROS)
    // /////////////////////////////////////////////////////

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"Producto (Lote/SKU)", "Proveedor", "Fecha de Caducidad", "Días Restantes", "Cantidad"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(modeloTabla);

        // Aplicamos tu renderizador personalizado
        try {
            CaducidadCellRenderer renderer = new CaducidadCellRenderer();
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                jTable1.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        } catch (Exception e) {
            System.out.println("Nota: No se encontró CaducidadCellRenderer o dio error, usando default.");
        }

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
        jTable1.setRowHeight(30);
    }

    private void configurarComboBoxes() {
        Estado.removeAllItems();
        Estado.addItem("Todos");
        Estado.addItem("Vencidos");
        Estado.addItem("No Vencidos"); // <--- NUEVA OPCIÓN
        Estado.addItem("Vencen en 7 días");
        Estado.addItem("Vencen en 30 días");
    }

    // MÉTODO PRINCIPAL PARA CARGAR TABLA DE CADUCIDAD
    private void cargarDatosDesdeBD() {
        listaProductos.clear();
        modeloTabla.setRowCount(0);

        // SQL: Unimos inventario + producto. 
        // Nota: Como proveedor no está directo en producto, usamos 'Varios' o hacemos join complejo.
        // Aquí asumiremos que quieres ver el stock general.
        String sql = "SELECT p.nombre, p.codigo, i.stock_actual, i.fecha_caducidad "
                + "FROM inventario_sucursal i "
                + "INNER JOIN producto p ON i.id_producto = p.id_producto "
                + "WHERE i.id_sucursal = ? AND i.stock_actual > 0 "
                + // CAMBIO AQUÍ
                "ORDER BY i.fecha_caducidad ASC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSucursalUsuario); // USAMOS LA VARIABLE
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String codigo = rs.getString("codigo");
                int stock = rs.getInt("stock_actual");
                Date fechaSQL = rs.getDate("fecha_caducidad");
                LocalDate fecha = (fechaSQL != null) ? fechaSQL.toLocalDate() : LocalDate.now().plusYears(1);

                // Agregamos a la lista en memoria para filtrar luego
                listaProductos.add(new ProductoCaducidad(
                        codigo, nombre, "General", fecha, stock));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando inventario: " + e.getMessage());
        }

        filtrarProductos(); // Mostramos la lista
    }

    // /////////////////////////////////////////////////////
    //  2. DASHBOARD DE MÉTRICAS (KPIs) - SQL PURO
    // /////////////////////////////////////////////////////
    private void actualizarMetricasBD() {
        // Consultas directas a la BD para velocidad y precisión
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            // 1. Vencidos
            String sqlVencidos = "SELECT SUM(stock_actual) FROM inventario_sucursal WHERE fecha_caducidad < CURDATE() AND id_sucursal=?";
            PreparedStatement ps1 = con.prepareStatement(sqlVencidos);
            ps1.setInt(1, idSucursalUsuario);
            ResultSet rs1 = ps1.executeQuery();

            // --- ESTA ES LA LÍNEA QUE FALTABA ---
            int vencidos = rs1.next() ? rs1.getInt(1) : 0;
            // ------------------------------------

            // 2. Vencen en 7 días
            String sql7 = "SELECT SUM(stock_actual) FROM inventario_sucursal WHERE fecha_caducidad BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) AND id_sucursal=?";
            PreparedStatement ps2 = con.prepareStatement(sql7);
            ps2.setInt(1, idSucursalUsuario);
            ResultSet rs2 = ps2.executeQuery();
            int en7dias = rs2.next() ? rs2.getInt(1) : 0;

            // 3. Vencen en 30 días
            String sql30 = "SELECT SUM(stock_actual) FROM inventario_sucursal WHERE fecha_caducidad BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND id_sucursal=?";
            PreparedStatement ps3 = con.prepareStatement(sql30);
            ps3.setInt(1, idSucursalUsuario);
            ResultSet rs3 = ps3.executeQuery();
            int en30dias = rs3.next() ? rs3.getInt(1) : 0;

            // 4. Valor en Riesgo
            String sqlRiesgo = "SELECT SUM(i.stock_actual * p.precio_compra) "
                    + "FROM inventario_sucursal i "
                    + "INNER JOIN producto p ON i.id_producto = p.id_producto "
                    + "WHERE i.fecha_caducidad <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) AND i.id_sucursal=?";
            PreparedStatement ps4 = con.prepareStatement(sqlRiesgo);
            ps4.setInt(1, idSucursalUsuario);
            ResultSet rs4 = ps4.executeQuery();
            double riesgo = rs4.next() ? rs4.getDouble(1) : 0.0;

            // Actualizar Labels
            jLabel1.setText("<html><center>PRODUCTOS VENCIDOS<br><b style='font-size:18px'>" + vencidos + " UNDS</b></center></html>");
            jLabel2.setText("<html><center>VENCEN EN 7 DÍAS<br><b style='font-size:18px'>" + en7dias + " UNDS</b></center></html>");
            jLabel3.setText("<html><center>VENCEN EN 30 DÍAS<br><b style='font-size:18px'>" + en30dias + " UNDS</b></center></html>");
            jLabel4.setText("<html><center>VALOR EN RIESGO<br><b style='font-size:18px'>S/ " + String.format("%.2f", riesgo) + "</b></center></html>");

            // Colores
            jPanel6.setBackground(new Color(220, 53, 69));
            jPanel5.setBackground(new Color(255, 193, 7));
            jPanel4.setBackground(new Color(255, 235, 156));
            jPanel7.setBackground(new Color(173, 216, 230));
            jLabel1.setForeground(Color.WHITE);

        } catch (SQLException e) {
            System.out.println("Error en métricas: " + e);
        }
    }

    // Lógica de Filtros en Memoria (Para la tabla)
    private void filtrarProductos() {
        if (listaProductos == null) return;
        
        String estadoSel = (String) Estado.getSelectedItem();

        // Limpieza del buscador
        String busqueda = jTextField1.getText().trim().toLowerCase();
        String placeholder = "buscar producto por nombre/sku.....";
        if (busqueda.equals(placeholder) || busqueda.isEmpty()) {
            busqueda = "";
        }

        modeloTabla.setRowCount(0);

        for (ProductoCaducidad p : listaProductos) {
            long dias = p.getDiasRestantes();
            boolean cumpleEstado = true;

            // Lógica de Filtros
            if ("Vencidos".equals(estadoSel)) {
                cumpleEstado = dias < 0;
            } else if ("No Vencidos".equals(estadoSel)) { // <--- NUEVA LÓGICA
                cumpleEstado = dias >= 0; // Muestra todo lo que NO esté vencido (incluye por vencer)
            } else if ("Vencen en 7 días".equals(estadoSel)) {
                cumpleEstado = dias >= 0 && dias <= 7;
            } else if ("Vencen en 30 días".equals(estadoSel)) {
                cumpleEstado = dias > 7 && dias <= 30;
            }

            // Filtro de Texto
            boolean cumpleBusqueda = true;
            if (!busqueda.isEmpty()) {
                cumpleBusqueda = p.getNombreProducto().toLowerCase().contains(busqueda)
                        || p.getLote().toLowerCase().contains(busqueda);
            }

            if (cumpleEstado && cumpleBusqueda) {
                modeloTabla.addRow(new Object[]{
                    p.getNombreProducto() + " (" + p.getLote() + ")",
                    p.getProveedor(),
                    p.getFechaCaducidad().format(formatter),
                    dias,
                    p.getCantidad()
                });
            }
        }
    }

    // /////////////////////////////////////////////////////
    //  3. INVENTARIO DE TIENDA (CATEGORÍAS Y PRODUCTOS)
    // /////////////////////////////////////////////////////
    private void inicializarInventarioTienda() {
        listaCategorias = new ArrayList<>();
        configurarPanelCategorias();
        jPanel9.setLayout(new BoxLayout(jPanel9, BoxLayout.Y_AXIS));
        configurarPanelProductos();

        cargarCategoriasBD(); // Carga real
        actualizarVistaCategorias();
    }

    private void configurarPanelProductos() {
        jPanel9.setBackground(Color.WHITE);
        jScrollPane3.getViewport().setBackground(Color.WHITE);
    }

    private void configurarPanelCategorias() {
        panelCategorias = new JPanel();
        panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));
        panelCategorias.setBackground(Color.WHITE);
        jScrollPane2.setViewportView(panelCategorias);
    }

    // Carga Categorías que tengan productos con stock
    private void cargarCategoriasBD() {
        listaCategorias.clear();
        String sql = "SELECT DISTINCT c.id_categoria, c.nombre "
                + "FROM categoria c "
                + "INNER JOIN producto p ON c.id_categoria = p.id_categoria "
                + "INNER JOIN inventario_sucursal i ON p.id_producto = i.id_producto "
                + "WHERE i.stock_actual > 0 AND i.id_sucursal = ?"; // CAMBIO AQUÍ

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSucursalUsuario); // USAMOS LA VARIABLE
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_categoria");
                String nombre = rs.getString("nombre");
                // Creamos el objeto Categoria (Ajusta el constructor según tu clase DAO)
                // Asumimos: id, nombre, descripción
                listaCategorias.add(new Categoria(id, nombre, "Categoría de " + nombre));
            }
        } catch (SQLException e) {
            System.out.println("Error categorías: " + e);
        }
    }

    private void actualizarVistaCategorias() {
        panelCategorias.removeAll();
        panelCategorias.add(Box.createVerticalStrut(10));

        for (Categoria categoria : listaCategorias) {
            JPanel cardCategoria = crearCardCategoria(categoria);
            panelCategorias.add(cardCategoria);
            panelCategorias.add(Box.createVerticalStrut(10));
        }

        panelCategorias.revalidate();
        panelCategorias.repaint();
    }

    // Diseño de la tarjeta de Categoría (SIN BOTONES EDITAR/BORRAR)
    private JPanel crearCardCategoria(Categoria categoria) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(350, 70));
        card.setPreferredSize(new Dimension(350, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Colores (Usando tus constantes o defaults)
        if (categoria.equals(categoriaSeleccionada)) {
            card.setBackground(new Color(255, 248, 225)); // Color seleccionado suave
        } else {
            card.setBackground(Color.WHITE);
        }

        JLabel lblNombre = new JLabel(categoria.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));

        card.add(lblNombre, BorderLayout.CENTER);

        // Evento Clic
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarCategoria(categoria);
            }
        });

        return card;
    }

    private void seleccionarCategoria(Categoria categoria) {
        categoriaSeleccionada = categoria;
        actualizarVistaCategorias(); // Para repintar la selección
        actualizarVistaProductosBD(categoria.getId()); // Cargar productos de la BD
    }

    // Carga productos de la derecha según la categoría seleccionada
    private void actualizarVistaProductosBD(int idCategoria) {
        jPanel9.removeAll();

        jLabel6.setText("Productos: " + categoriaSeleccionada.getNombre());
        jPanel9.add(Box.createVerticalStrut(10));

        String sql = "SELECT p.nombre, i.stock_actual "
                + "FROM producto p "
                + "INNER JOIN inventario_sucursal i ON p.id_producto = i.id_producto "
                + "WHERE p.id_categoria = ? AND i.id_sucursal = ? AND i.stock_actual > 0"; // CAMBIO AQUÍ

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            ps.setInt(2, idSucursalUsuario); // USAMOS LA VARIABLE
            ResultSet rs = ps.executeQuery();

            boolean hayProductos = false;
            while (rs.next()) {
                hayProductos = true;
                String nombre = rs.getString("nombre");
                int stock = rs.getInt("stock_actual");

                JPanel cardProducto = crearCardProducto(nombre, stock);
                jPanel9.add(cardProducto);
                jPanel9.add(Box.createVerticalStrut(8));
            }

            if (!hayProductos) {
                jPanel9.add(new JLabel("  No hay stock en esta categoría."));
            }

        } catch (SQLException e) {
            System.out.println("Error productos: " + e);
        }

        jPanel9.revalidate();
        jPanel9.repaint();
    }

    // Diseño de tarjeta de Producto (SIN BOTONES DE BORRAR)
    private JPanel crearCardProducto(String nombreProducto, int stock) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setMaximumSize(new Dimension(350, 45));
        card.setPreferredSize(new Dimension(350, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setBackground(new Color(250, 250, 250));

        JLabel lblNombre = new JLabel(nombreProducto);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblCantidad = new JLabel("(" + stock + " unid.)");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCantidad.setForeground(new Color(0, 100, 0)); // Verde oscuro

        card.add(lblNombre, BorderLayout.CENTER);
        card.add(lblCantidad, BorderLayout.EAST);

        return card;
    }

    private void configurarBuscador() {
        // 1. Configuración Visual (Texto Fantasma)
        String placeholder = "Buscar producto por Nombre/SKU.....";
        jTextField1.setText(placeholder);
        jTextField1.setForeground(Color.GRAY);

        // Eventos de Foco (Para borrar/poner el texto gris)
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().equals(placeholder)) {
                    jTextField1.setText("");
                    jTextField1.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText(placeholder);
                    jTextField1.setForeground(Color.GRAY);
                }
            }
        });

        // 2. Configuración Funcional (Filtro al escribir)
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Cada vez que sueltas una tecla, llamamos al filtro
                filtrarProductos();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        Estado = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setBackground(new java.awt.Color(153, 0, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 102, 0));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("VENCEN EN 30 DÍAS");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 60, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 51, 51));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("VENCEN EN 7 DÍAS");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 153, 51));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PRODUCTOS VENDIDOS");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(153, 0, 0));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("VALOR EN RIESGO");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(39, 39, 39))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextField1.setText("Buscar producto por Nombre/SKU.....");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        Estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        Estado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EstadoActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producto (Lote/SKU)", "Provvedor", "Fecha de Caducidad", "Días Restantes", "Cantidad"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 991, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 51, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Estado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(50, 50, 50))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("GESTIÓN DE CADUCIDAD", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 102, 0));
        jLabel5.setText("Categoria del Productos");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 153, 102));
        jLabel6.setText("Productos");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 435, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel8);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 457, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(jPanel9);

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Nueva Categoria");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 51, 51));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Mermas");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("INVENTARIO DE TIENDA", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void EstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EstadoActionPerformed
        filtrarProductos();
    }//GEN-LAST:event_EstadoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// 1. Crear y mostrar el diálogo
        JFrame marcoPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogoNuevaCategoria dialogo = new DialogoNuevaCategoria(marcoPadre, true);
        dialogo.setVisible(true);

        // 2. Si se guardó algo, recargamos la lista de categorías
        if (dialogo.isGuardadoExitoso()) {
            System.out.println("Recargando categorías...");
            // Limpiamos la lista actual
            listaCategorias.clear();
            // Volvemos a cargar de la BD
            cargarCategoriasBD();
            // Repintamos la interfaz gráfica
            actualizarVistaCategorias();
        }    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFrame marcoPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogoNuevaMerma dialogo = new DialogoNuevaMerma(marcoPadre, true);
        dialogo.setVisible(true);

        // Opcional: Al cerrar, recargar las métricas por si bajó el stock de algo crítico
        cargarDatosDesdeBD();
        actualizarMetricasBD();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Estado;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
