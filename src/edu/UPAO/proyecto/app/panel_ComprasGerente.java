package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.CompraGlobalDAO;
import edu.UPAO.proyecto.DAO.DevolucionCompraDAO;
import edu.UPAO.proyecto.DAO.ProveedorDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import edu.UPAO.proyecto.Modelo.Proveedor;
import edu.UPAO.proyecto.Util.GeneradorExcel;
import edu.UPAO.proyecto.Util.GeneradorPDF;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import edu.UPAO.proyecto.DAO.DevolucionCompraDAO; // Tu nuevo DAO
import java.awt.Font;
import org.jfree.data.general.DefaultPieDataset;

public class panel_ComprasGerente extends javax.swing.JPanel {

// Variables de control
    private ProveedorDAO proveedorDAO;
    private String idProveedorSeleccionado = null;
    private int idSucursalActual;
    private CompraGlobalDAO compraGlobalDAO; // <--- NUEVO
    private DevolucionCompraDAO devolucionDAO; // <--- NUEVO
    private boolean isLoading = false;

    public panel_ComprasGerente(int idSucursal) {
        this.idSucursalActual = idSucursal;
        initComponents();
        cargarGraficoRankingProveedores();

        // Inicializar DAOs
        proveedorDAO = new ProveedorDAO();
        compraGlobalDAO = new CompraGlobalDAO();
        devolucionDAO = new DevolucionCompraDAO(); // <--- Inicializar

        // --- CONFIGURACIONES PESTAÑA 1 (COMPRAS) ---
        configurarTablaProveedores();
        configurarTablaComprasGlobales();
        configurarBusquedaEnTiempoReal();
        cargarProveedoresEnTabla("");
        cargarComboSucursales();
        cargarComprasGlobales();
        limpiarFormulario();
        btn_exportarCompras.addActionListener(e -> exportarReporteCompras());

        // --- CONFIGURACIONES PESTAÑA 2 (DEVOLUCIONES) ---
        configurarPestanaDevoluciones(); // <--- Método nuevo que agrupa todo

        jTabbedPane1.addChangeListener(e -> {
            if (jTabbedPane1.getSelectedIndex() == 1) { // 1 es el índice de la pestaña DEVOLUCIONES
                System.out.println("🔄 Recargando pestaña de Devoluciones...");
                cargarTablaDevoluciones();
                cargarGraficosDevoluciones();
            }
        });
    }
    
    

    private void configurarPestanaDevoluciones() {
        // 1. Configurar Combo Proveedores
        cargarComboProveedoresDevolucion();

        // 2. Cargar Tabla Inicial
        cargarTablaDevoluciones();

        // 3. Cargar Gráficos
        cargarGraficosDevoluciones();
    }

    private void cargarComboProveedoresDevolucion() {
        cb_Proveedor.removeAllItems();
        cb_Proveedor.addItem("TODOS");
        try {
            // Reutilizamos tu proveedorDAO existente
            List<Proveedor> lista = proveedorDAO.listar("");
            for (Proveedor p : lista) {
                cb_Proveedor.addItem(p.getRazonSocial());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Evento (Lambda simplificada)
        cb_Proveedor.addActionListener(e -> cargarTablaDevoluciones());
    }

    private void cargarTablaDevoluciones() {
        DefaultTableModel modelo = (DefaultTableModel) tb_devoluciones.getModel();
        modelo.setRowCount(0);

        // Configurar columnas si no están bien en diseño
        if (modelo.getColumnCount() == 0) {
            modelo.setColumnIdentifiers(new Object[]{"ID", "Fecha", "Tienda", "Proveedor", "Items", "Unidades", "Total", "Estado"});
        }

        String proveedorSel = (cb_Proveedor.getSelectedItem() != null)
                ? cb_Proveedor.getSelectedItem().toString()
                : "TODOS";

        List<Object[]> datos = devolucionDAO.listarDevoluciones(proveedorSel);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }

    private void cargarGraficosDevoluciones() {
        // A. GRÁFICO CIRCULAR (Por Sucursal)
        Map<String, Integer> datosPie = devolucionDAO.obtenerDistribucionPorSucursal();
        DefaultPieDataset datasetPie = new DefaultPieDataset();

        for (Map.Entry<String, Integer> entry : datosPie.entrySet()) {
            datasetPie.setValue(entry.getKey(), entry.getValue());
        }

        // --- AGREGAR ESTA LÍNEA PARA ACHICAR EL GRÁFICO CIRCULAR ---
        GraficoCircular.setPreferredSize(new java.awt.Dimension(350, 250)); // (Ancho, Alto)
        // -----------------------------------------------------------

        JFreeChart chartPie = ChartFactory.createPieChart(
                "Devoluciones por Tienda", datasetPie, false, true, false);
        renderizarGrafico(GraficoCircular, chartPie);

        // B. GRÁFICO DE BARRAS (Monto por Proveedor)
        Map<String, Double> datosBar = devolucionDAO.obtenerMontoPorProveedor();
        DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : datosBar.entrySet()) {
            datasetBar.addValue(entry.getValue(), "Monto S/", entry.getKey());
        }

        // --- AGREGAR ESTA LÍNEA PARA ACHICAR EL GRÁFICO DE BARRAS ---
        GraficoBarras.setPreferredSize(new java.awt.Dimension(350, 250)); // (Ancho, Alto)
        // -----------------------------------------------------------

        JFreeChart chartBar = ChartFactory.createBarChart(
                "Top Devoluciones (S/)", "Proveedor", "Monto",
                datasetBar, PlotOrientation.HORIZONTAL, false, true, false);
        renderizarGrafico(GraficoBarras, chartBar);
    }

    private void renderizarGrafico(javax.swing.JPanel panel, JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        panel.setLayout(new BorderLayout());
        panel.removeAll();
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
        panel.repaint(); // Forzar repintado
    }

    private void configurarBusquedaEnTiempoReal() {
        tf_buscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = tf_buscar.getText().trim();
                cargarProveedoresEnTabla(texto);
            }
        });
    }

    private void configurarTablaProveedores() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelo.addColumn("ID");
        modelo.addColumn("RUC");
        modelo.addColumn("Razón Social");
        modelo.addColumn("DNI Contacto");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Dirección");
        modelo.addColumn("Estado");
        jTable2.setModel(modelo);

        // Ocultar columna ID
        jTable2.getColumnModel().getColumn(0).setMinWidth(0);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable2.getColumnModel().getColumn(0).setWidth(0);

        jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable2.getSelectedRow() != -1) {
                llenarFormularioDesdeTabla();
            }
        });
    }

    private void configurarTablaComprasGlobales() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        modelo.addColumn("ID");
        modelo.addColumn("Fecha");
        modelo.addColumn("Sucursal");
        modelo.addColumn("Proveedor");
        modelo.addColumn("Empleado (DNI)");
        modelo.addColumn("Total");
        modelo.addColumn("Estado");

        tb_ComprasGlobales.setModel(modelo);

        // Ajuste estético de anchos
        tb_ComprasGlobales.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tb_ComprasGlobales.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        tb_ComprasGlobales.getColumnModel().getColumn(3).setPreferredWidth(200); // Proveedor
    }

    private void cargarComboSucursales() {
        // ✅ Activamos la bandera para que el evento NO filtre mientras llenamos
        isLoading = true;

        cb_SucursalCompras.removeAllItems();
        cb_SucursalCompras.addItem("TODAS LAS TIENDAS");

        try {
            SucursalDAO sucDao = new SucursalDAO();
            List<String> lista = sucDao.obtenerSucursalesActivas();
            for (String s : lista) {
                cb_SucursalCompras.addItem(s);
            }
        } catch (Exception e) {
            System.err.println("Error cargando sucursales: " + e.getMessage());
        }

        // ✅ Desactivamos la bandera y cargamos la tabla inicial
        isLoading = false;
        cargarComprasGlobales();
    }

    private void cargarComprasGlobales() {
        DefaultTableModel modelo = (DefaultTableModel) tb_ComprasGlobales.getModel();
        modelo.setRowCount(0);

        String sucursalSeleccionada = "TODAS LAS TIENDAS";
        if (cb_SucursalCompras.getSelectedItem() != null) {
            sucursalSeleccionada = cb_SucursalCompras.getSelectedItem().toString();
        }

        System.out.println("🔍 Filtrando por: " + sucursalSeleccionada);

        List<Object[]> datos = compraGlobalDAO.listarComprasGlobales(sucursalSeleccionada);

        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }

    private void exportarReporteCompras() {
        if (tb_ComprasGlobales.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.", "Tabla vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Excel (.csv)", "PDF (.pdf)", "Cancelar"};
        int seleccion = JOptionPane.showOptionDialog(this,
                "¿En qué formato desea exportar el reporte de compras?",
                "Exportar Reporte",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion == 0) {
            // Opción Excel
            GeneradorExcel.exportarExcel(tb_ComprasGlobales, "Reporte_Compras_Globales");
        } else if (seleccion == 1) {
            // Opción PDF
            GeneradorPDF.generarReporteDesdeTabla(tb_ComprasGlobales, "Reporte de Compras Globales");
        }
    }

    private void cargarProveedoresEnTabla(String filtro) {
        DefaultTableModel modelo = (DefaultTableModel) jTable2.getModel();
        modelo.setRowCount(0);

        try {
            List<Proveedor> lista = proveedorDAO.listar(filtro);
            for (Proveedor p : lista) {
                modelo.addRow(new Object[]{
                    p.getIdProveedor(),
                    p.getRuc(),
                    p.getRazonSocial(),
                    p.getDniAsociado(),
                    p.getTelefonoContacto(),
                    p.getDireccion(),
                    p.getEstado()
                });
            }
        } catch (Exception e) {
            System.err.println("Error al cargar tabla proveedores: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        tf_razonSocial.setText("");
        tf_RUC.setText("");
        tf_dniAsosiado.setText("");
        tf_Direccion.setText("");
        tf_Telefono.setText("");
        jCheckBox1.setSelected(true);

        idProveedorSeleccionado = null;
        btn_GuardarActualizar.setText("Guardar");
        jTable2.clearSelection();

        // Permitir editar claves únicas solo al crear
        tf_dniAsosiado.setEditable(true);
        tf_RUC.setEditable(true);
    }

    private void llenarFormularioDesdeTabla() {
        int fila = jTable2.getSelectedRow();
        if (fila >= 0) {
            idProveedorSeleccionado = jTable2.getValueAt(fila, 0).toString();
            tf_RUC.setText(validarNulo(jTable2.getValueAt(fila, 1)));
            tf_razonSocial.setText(validarNulo(jTable2.getValueAt(fila, 2)));
            tf_dniAsosiado.setText(validarNulo(jTable2.getValueAt(fila, 3)));
            tf_Telefono.setText(validarNulo(jTable2.getValueAt(fila, 4)));
            tf_Direccion.setText(validarNulo(jTable2.getValueAt(fila, 5)));

            String estado = validarNulo(jTable2.getValueAt(fila, 6));
            jCheckBox1.setSelected("ACTIVO".equals(estado));

            btn_GuardarActualizar.setText("Actualizar");
            tf_dniAsosiado.setEditable(false);
            tf_RUC.setEditable(false);
        }
    }

    private String validarNulo(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    // Método auxiliar para evitar NullPointerException en la tabla
    private String objToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private void guardarOActualizar() {
        // 1. Capturar TODOS los datos del formulario
        String razonSocial = tf_razonSocial.getText().trim();
        String ruc = tf_RUC.getText().trim();
        String dni = tf_dniAsosiado.getText().trim();

        // Datos de PERSONA (que irán a los campos auxiliares)
        String nombres = tf_nombres.getText().trim();
        String apellidos = tf_apellidos.getText().trim();
        String correo = tf_correo.getText().trim(); // <--- Capturamos el correo

        String direccion = tf_Direccion.getText().trim();
        String telefono = tf_Telefono.getText().trim();
        String estado = jCheckBox1.isSelected() ? "ACTIVO" : "INACTIVO";

        // 2. Validaciones
        if (razonSocial.isEmpty() || ruc.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Razón Social, RUC y DNI son obligatorios.");
            return;
        }
        // Si es nuevo, exigimos nombre y apellido para poder crear la Persona
        if (idProveedorSeleccionado == null && (nombres.isEmpty() || apellidos.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Para un nuevo registro, Nombres y Apellidos son obligatorios.");
            return;
        }

        if (!ruc.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "El RUC debe tener 11 dígitos.");
            return;
        }
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener 8 dígitos.");
            return;
        }

        // 3. Llenar el Objeto Proveedor
        Proveedor p = new Proveedor();
        // Datos directos de Proveedor
        p.setRazonSocial(razonSocial);
        p.setRuc(ruc);
        p.setDniAsociado(dni);
        p.setDireccion(direccion);
        p.setEstado(estado);
        p.setIdSucursal(this.idSucursalActual);

        // --> AQUÍ USAMOS TUS CAMPOS AUXILIARES <--
        // Guardamos la info de persona dentro del objeto Proveedor temporalmente
        p.setNombresContacto(nombres);
        p.setApellidosContacto(apellidos);
        p.setCorreoContacto(correo);     // Nuevo campo
        p.setTelefonoContacto(telefono); // El teléfono suele ir en persona

        // 4. Enviar al DAO
        if (idProveedorSeleccionado == null) {
            if (proveedorDAO.existeRuc(ruc)) {
                JOptionPane.showMessageDialog(this, "El RUC ya existe.");
                return;
            }
            if (proveedorDAO.guardar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor y Asociado registrados correctamente.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en BD.");
            }
        } else {
            p.setIdProveedor(idProveedorSeleccionado);
            if (proveedorDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor actualizado.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.");
            }
        }
    }

  private void cargarGraficoRankingProveedores() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 1. Obtener datos (SQL igual que antes)
        String sql = "SELECT p.razon_social, SUM(c.total) as total_comprado " +
                     "FROM compra c " +
                     "INNER JOIN proveedor p ON c.id_proveedor = p.id_proveedor " +
                     "GROUP BY p.razon_social " + 
                     "ORDER BY total_comprado DESC LIMIT 5";

        try (java.sql.Connection con = new BaseDatos.Conexion().establecerConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String proveedor = rs.getString("razon_social"); 
                double monto = rs.getDouble("total_comprado");
                
                // Acortar nombre si es muy largo (vital para gráficos pequeños)
                if (proveedor.length() > 12) {
                    proveedor = proveedor.substring(0, 12) + "..";
                }
                dataset.addValue(monto, "Compras", proveedor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Crear el Gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Proveedores", // Título corto
                "", 
                "", 
                dataset,
                PlotOrientation.HORIZONTAL, 
                false, true, false 
        );

        // --- AJUSTES VISUALES PARA TAMAÑO MINI ---
        
        // Fondo blanco limpio
        chart.setBackgroundPaint(Color.WHITE);
        
        // Ajustar fuentes para que no se vean gigantes
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Ajustar fuente de los ejes
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10)); // Nombres proveedores
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));  // Números
        
        // 3. Asignar al panel con TAMAÑO CONTROLADO
        ChartPanel cp = new ChartPanel(chart);
        
        // CLAVE DEL ARREGLO: Forzar un tamaño preferido pequeño.
        // El BorderLayout luego lo estirará lo necesario, pero no "explotará" el diseño.
        cp.setPreferredSize(new java.awt.Dimension(300, 150)); 
        
        minipanel_rankingProveedores.removeAll();
        minipanel_rankingProveedores.setLayout(new java.awt.BorderLayout());
        minipanel_rankingProveedores.add(cp, java.awt.BorderLayout.CENTER);
        
        minipanel_rankingProveedores.revalidate();
        minipanel_rankingProveedores.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        ComprasGlobales = new javax.swing.JPanel();
        cb_SucursalCompras = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tb_ComprasGlobales = new javax.swing.JTable();
        btn_exportarCompras = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        DevolucionesPorTienda1 = new javax.swing.JLabel();
        Devoluciones = new javax.swing.JPanel();
        DevolucionesPorTienda = new javax.swing.JLabel();
        cb_Proveedor = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_devoluciones = new javax.swing.JTable();
        DetalleDevolucionesRecientes = new javax.swing.JLabel();
        GraficoBarras = new javax.swing.JPanel();
        GraficoCircular = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        tf_razonSocial = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tf_RUC = new javax.swing.JTextField();
        tf_dniAsosiado = new javax.swing.JTextField();
        tf_Direccion = new javax.swing.JTextField();
        tf_Telefono = new javax.swing.JTextField();
        btn_GuardarActualizar = new javax.swing.JToggleButton();
        btn_Cancerlarlimpiar = new javax.swing.JToggleButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        btn_AbrirHistorial = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        tf_apellidos = new javax.swing.JTextField();
        tf_nombres = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tf_correo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btn_Exportar = new javax.swing.JToggleButton();
        tf_buscar = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        minipanel_rankingProveedores = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 102, 0));

        ComprasGlobales.setBackground(new java.awt.Color(255, 255, 255));

        cb_SucursalCompras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIENDA" }));
        cb_SucursalCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_SucursalComprasActionPerformed(evt);
            }
        });

        tb_ComprasGlobales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tb_ComprasGlobales);

        btn_exportarCompras.setText("Exportar");

        jSeparator4.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator4.setForeground(new java.awt.Color(255, 255, 255));
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator5.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator5.setForeground(new java.awt.Color(255, 255, 255));

        DevolucionesPorTienda1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        DevolucionesPorTienda1.setForeground(new java.awt.Color(51, 51, 51));
        DevolucionesPorTienda1.setText("LISTA DE COMPRAS:");

        javax.swing.GroupLayout ComprasGlobalesLayout = new javax.swing.GroupLayout(ComprasGlobales);
        ComprasGlobales.setLayout(ComprasGlobalesLayout);
        ComprasGlobalesLayout.setHorizontalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator5)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DevolucionesPorTienda1)
                    .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                        .addGap(966, 966, 966)
                        .addComponent(btn_exportarCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1071, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_SucursalCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        ComprasGlobalesLayout.setVerticalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                        .addComponent(cb_SucursalCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(DevolucionesPorTienda1)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_exportarCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("COMPRAS GLOBALES", ComprasGlobales);

        Devoluciones.setBackground(new java.awt.Color(255, 255, 255));

        DevolucionesPorTienda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        DevolucionesPorTienda.setForeground(new java.awt.Color(51, 51, 51));
        DevolucionesPorTienda.setText("DEVOLUCIONES POR TIENDA:");

        cb_Proveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PROVEEDOR" }));
        cb_Proveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_ProveedorActionPerformed(evt);
            }
        });

        tb_devoluciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID PEDIDO", "FECHA", "TIENDA", "PROVEEDOR", "PRODUCTOS", "UNIDADES", "VALOR", "ESTADO"
            }
        ));
        jScrollPane1.setViewportView(tb_devoluciones);

        DetalleDevolucionesRecientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        DetalleDevolucionesRecientes.setForeground(new java.awt.Color(51, 51, 51));
        DetalleDevolucionesRecientes.setText("DETALLE DE DEVOLUCIONES RECIENTES:");

        GraficoBarras.setBackground(new java.awt.Color(204, 204, 204));
        GraficoBarras.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout GraficoBarrasLayout = new javax.swing.GroupLayout(GraficoBarras);
        GraficoBarras.setLayout(GraficoBarrasLayout);
        GraficoBarrasLayout.setHorizontalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 526, Short.MAX_VALUE)
        );
        GraficoBarrasLayout.setVerticalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        GraficoCircular.setBackground(new java.awt.Color(204, 204, 204));
        GraficoCircular.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout GraficoCircularLayout = new javax.swing.GroupLayout(GraficoCircular);
        GraficoCircular.setLayout(GraficoCircularLayout);
        GraficoCircularLayout.setHorizontalGroup(
            GraficoCircularLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
        );
        GraficoCircularLayout.setVerticalGroup(
            GraficoCircularLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout DevolucionesLayout = new javax.swing.GroupLayout(Devoluciones);
        Devoluciones.setLayout(DevolucionesLayout);
        DevolucionesLayout.setHorizontalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DevolucionesLayout.createSequentialGroup()
                        .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DevolucionesLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(DetalleDevolucionesRecientes)
                                    .addGroup(DevolucionesLayout.createSequentialGroup()
                                        .addGap(544, 544, 544)
                                        .addComponent(GraficoCircular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1)))
                            .addGroup(DevolucionesLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(DevolucionesPorTienda)
                                    .addComponent(cb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(GraficoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 56, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        DevolucionesLayout.setVerticalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DevolucionesPorTienda)
                .addGap(12, 12, 12)
                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GraficoCircular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GraficoBarras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(DetalleDevolucionesRecientes)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", Devoluciones);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1161, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 594, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("RECEPCION", jPanel4);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jPanel3.setBackground(new java.awt.Color(255, 204, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Razon social:");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("DNI del asosiado:");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("RUC:");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Telefono:");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Direccion:");

        tf_Direccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_DireccionActionPerformed(evt);
            }
        });

        btn_GuardarActualizar.setBackground(new java.awt.Color(0, 204, 51));
        btn_GuardarActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_GuardarActualizar.setText("GUARDAR/ACTUALIZAR");
        btn_GuardarActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActualizarActionPerformed(evt);
            }
        });

        btn_Cancerlarlimpiar.setBackground(new java.awt.Color(255, 51, 51));
        btn_Cancerlarlimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_Cancerlarlimpiar.setText("CANCELAR");
        btn_Cancerlarlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CancerlarlimpiarActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(0, 102, 102));
        jCheckBox1.setText("Activo");

        btn_AbrirHistorial.setBackground(new java.awt.Color(51, 102, 255));
        btn_AbrirHistorial.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_AbrirHistorial.setText("HISTORIAL");
        btn_AbrirHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirHistorialActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Datos del Proveedor ");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Nombres:");

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Apellidos:");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Correo:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(96, 96, 96))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tf_nombres)
                                    .addComponent(tf_dniAsosiado)
                                    .addComponent(tf_razonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tf_Telefono)
                                    .addComponent(tf_Direccion)
                                    .addComponent(tf_correo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                    .addComponent(tf_RUC, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(70, 70, 70))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_GuardarActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_razonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_dniAsosiado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_nombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_RUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel9))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_GuardarActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        btn_Exportar.setText("Exportar");
        btn_Exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExportarActionPerformed(evt);
            }
        });

        btn_buscar.setText("Buscar");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        jSeparator6.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator6.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout minipanel_rankingProveedoresLayout = new javax.swing.GroupLayout(minipanel_rankingProveedores);
        minipanel_rankingProveedores.setLayout(minipanel_rankingProveedoresLayout);
        minipanel_rankingProveedoresLayout.setHorizontalGroup(
            minipanel_rankingProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        minipanel_rankingProveedoresLayout.setVerticalGroup(
            minipanel_rankingProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btn_Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(minipanel_rankingProveedores, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
            .addComponent(jSeparator6)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addComponent(btn_Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(minipanel_rankingProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("PROVEEDORES", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cb_SucursalComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_SucursalComprasActionPerformed
        if (isLoading) {
            return;
        }

        cargarComprasGlobales();    }//GEN-LAST:event_cb_SucursalComprasActionPerformed

    private void cb_ProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_ProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_ProveedorActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String textoBusqueda = tf_buscar.getText().trim();
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void btn_AbrirHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirHistorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_AbrirHistorialActionPerformed

    private void btn_CancerlarlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CancerlarlimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btn_CancerlarlimpiarActionPerformed

    private void btn_GuardarActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActualizarActionPerformed
        guardarOActualizar();        // TODO add your handling code here:
    }//GEN-LAST:event_btn_GuardarActualizarActionPerformed

    private void tf_DireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_DireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_DireccionActionPerformed

    private void btn_ExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExportarActionPerformed
// Opción para elegir formato
        String[] opciones = {"Excel (Solo Tabla)", "PDF (Tabla + Gráfico)", "Cancelar"};
        int seleccion = JOptionPane.showOptionDialog(this,
                "¿Cómo desea exportar la lista de proveedores?",
                "Exportar Proveedores",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == 0) {
            // Excel: Solo exportamos la jTable2
            GeneradorExcel.exportarExcel(jTable2, "Listado_Proveedores");
        } else if (seleccion == 1) {
            // PDF: Exportamos Tabla + Imagen del Mini Panel
            try {
                // 1. Capturar imagen del gráfico
                java.awt.image.BufferedImage imagenGrafico = new java.awt.image.BufferedImage(
                        minipanel_rankingProveedores.getWidth(),
                        minipanel_rankingProveedores.getHeight(),
                        java.awt.image.BufferedImage.TYPE_INT_RGB);
                minipanel_rankingProveedores.paint(imagenGrafico.getGraphics());

                // 2. Generar PDF (Usando tu utilidad existente o lógica iText)
                // Si tu GeneradorPDF no soporta imágenes directas, puedes guardarla temporalmente
                String rutaTemp = "temp_chart_prov.png";
                javax.imageio.ImageIO.write(imagenGrafico, "png", new java.io.File(rutaTemp));

                // Llamamos a tu generador (Asumiendo que tienes un método flexible, si no, aquí tienes la lógica rápida con iText)
                crearReportePDFConGrafico(rutaTemp);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
            }
        }    }//GEN-LAST:event_btn_ExportarActionPerformed

    private void crearReportePDFConGrafico(String rutaImagen) {
        try {
            String nombreArchivo = "Reporte_Proveedores_" + System.currentTimeMillis() + ".pdf";
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(nombreArchivo));
            doc.open();

            // Título
            doc.add(new com.itextpdf.text.Paragraph("REPORTE DE PROVEEDORES Y RANKING", 
                    com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18)));
            doc.add(new com.itextpdf.text.Paragraph(" ")); // Espacio

            // Agregar Imagen del Gráfico
            com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(rutaImagen);
            img.scaleToFit(500, 300); // Ajustar tamaño
            img.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            doc.add(img);
            doc.add(new com.itextpdf.text.Paragraph(" ")); // Espacio

            // Agregar Tabla (Convirtiendo jTable a PdfPTable)
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(jTable2.getColumnCount());
            // Headers
            for (int i = 0; i < jTable2.getColumnCount(); i++) {
                pdfTable.addCell(new com.itextpdf.text.Phrase(jTable2.getColumnName(i)));
            }
            // Data
            for (int rows = 0; rows < jTable2.getRowCount(); rows++) {
                for (int cols = 0; cols < jTable2.getColumnCount(); cols++) {
                    Object val = jTable2.getModel().getValueAt(rows, cols);
                    pdfTable.addCell(val != null ? val.toString() : "");
                }
            }
            doc.add(pdfTable);

            doc.close();
            JOptionPane.showMessageDialog(this, "Reporte generado: " + nombreArchivo);
            
            // Abrir archivo automáticamente
            java.awt.Desktop.getDesktop().open(new java.io.File(nombreArchivo));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ComprasGlobales;
    private javax.swing.JLabel DetalleDevolucionesRecientes;
    private javax.swing.JPanel Devoluciones;
    private javax.swing.JLabel DevolucionesPorTienda;
    private javax.swing.JLabel DevolucionesPorTienda1;
    private javax.swing.JPanel GraficoBarras;
    private javax.swing.JPanel GraficoCircular;
    private javax.swing.JToggleButton btn_AbrirHistorial;
    private javax.swing.JToggleButton btn_Cancerlarlimpiar;
    private javax.swing.JToggleButton btn_Exportar;
    private javax.swing.JToggleButton btn_GuardarActualizar;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_exportarCompras;
    private javax.swing.JComboBox<String> cb_Proveedor;
    private javax.swing.JComboBox<String> cb_SucursalCompras;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JPanel minipanel_rankingProveedores;
    private javax.swing.JTable tb_ComprasGlobales;
    private javax.swing.JTable tb_devoluciones;
    private javax.swing.JTextField tf_Direccion;
    private javax.swing.JTextField tf_RUC;
    private javax.swing.JTextField tf_Telefono;
    private javax.swing.JTextField tf_apellidos;
    private javax.swing.JTextField tf_buscar;
    private javax.swing.JTextField tf_correo;
    private javax.swing.JTextField tf_dniAsosiado;
    private javax.swing.JTextField tf_nombres;
    private javax.swing.JTextField tf_razonSocial;
    // End of variables declaration//GEN-END:variables
}
