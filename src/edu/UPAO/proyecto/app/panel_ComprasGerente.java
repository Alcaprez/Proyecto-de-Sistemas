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
        } catch (Exception e) { e.printStackTrace(); }
        
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
        String razonSocial = tf_razonSocial.getText().trim();
        String ruc = tf_RUC.getText().trim();
        String dni = tf_dniAsosiado.getText().trim();
        String direccion = tf_Direccion.getText().trim();
        String telefono = tf_Telefono.getText().trim();
        String estado = jCheckBox1.isSelected() ? "ACTIVO" : "INACTIVO";

        if (razonSocial.isEmpty() || ruc.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete Razón Social, RUC y DNI.", "Datos faltantes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ruc.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "El RUC debe tener 11 dígitos numéricos.", "Error RUC", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener 8 dígitos numéricos.", "Error DNI", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor p = new Proveedor();
        p.setRazonSocial(razonSocial);
        p.setRuc(ruc);
        p.setDniAsociado(dni);
        p.setDireccion(direccion);
        p.setTelefonoContacto(telefono);
        p.setEstado(estado);
        p.setIdSucursal(this.idSucursalActual);

        if (idProveedorSeleccionado == null) {
            if (proveedorDAO.existeRuc(ruc)) {
                JOptionPane.showMessageDialog(this, "El RUC ya está registrado.", "Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (proveedorDAO.guardar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor registrado con éxito.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            p.setIdProveedor(idProveedorSeleccionado);
            if (proveedorDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor actualizado con éxito.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        Devoluciones = new javax.swing.JPanel();
        DevolucionesPorTienda = new javax.swing.JLabel();
        cb_Proveedor = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_devoluciones = new javax.swing.JTable();
        DetalleDevolucionesRecientes = new javax.swing.JLabel();
        GraficoBarras = new javax.swing.JPanel();
        GraficoCircular = new javax.swing.JPanel();
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
        btn_Exportar = new javax.swing.JToggleButton();
        tf_buscar = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 102, 0));

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

        javax.swing.GroupLayout ComprasGlobalesLayout = new javax.swing.GroupLayout(ComprasGlobales);
        ComprasGlobales.setLayout(ComprasGlobalesLayout);
        ComprasGlobalesLayout.setHorizontalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_exportarCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cb_SucursalCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(676, Short.MAX_VALUE))
        );
        ComprasGlobalesLayout.setVerticalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(cb_SucursalCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_exportarCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(92, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("COMPRAS GLOBALES", ComprasGlobales);

        DevolucionesPorTienda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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
        DetalleDevolucionesRecientes.setText("DETALLE DE DEVOLUCIONES RECIENTES:");

        GraficoBarras.setBackground(new java.awt.Color(204, 204, 204));

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

        javax.swing.GroupLayout DevolucionesLayout = new javax.swing.GroupLayout(Devoluciones);
        Devoluciones.setLayout(DevolucionesLayout);
        DevolucionesLayout.setHorizontalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1081, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DetalleDevolucionesRecientes)
                    .addGroup(DevolucionesLayout.createSequentialGroup()
                        .addComponent(GraficoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(GraficoCircular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(DevolucionesPorTienda)
                    .addComponent(cb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(723, Short.MAX_VALUE))
        );
        DevolucionesLayout.setVerticalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
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
                .addContainerGap(81, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", Devoluciones);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1831, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 654, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("RECEPCION", jPanel4);

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

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Razon social:");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("DNI del asosiado:");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("RUC:");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Telefono:");

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Direccion:");

        tf_Direccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_DireccionActionPerformed(evt);
            }
        });

        btn_GuardarActualizar.setBackground(new java.awt.Color(0, 153, 102));
        btn_GuardarActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_GuardarActualizar.setText("Guardar/Actualizar");
        btn_GuardarActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActualizarActionPerformed(evt);
            }
        });

        btn_Cancerlarlimpiar.setBackground(new java.awt.Color(255, 102, 102));
        btn_Cancerlarlimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_Cancerlarlimpiar.setText("Cancelar");
        btn_Cancerlarlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CancerlarlimpiarActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(0, 102, 102));
        jCheckBox1.setText("Activo");

        btn_AbrirHistorial.setBackground(new java.awt.Color(102, 153, 255));
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_GuardarActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tf_RUC, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                        .addComponent(tf_dniAsosiado, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tf_razonSocial, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tf_Direccion)
                        .addComponent(tf_Telefono)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_razonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_dniAsosiado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tf_RUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tf_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tf_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_GuardarActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        btn_Exportar.setText("Exportar");

        btn_buscar.setText("Buscar");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2))
                    .addComponent(btn_Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(708, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(74, Short.MAX_VALUE))
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
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cb_SucursalComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_SucursalComprasActionPerformed
        if (isLoading) {
            return;
        }

        cargarComprasGlobales();    }//GEN-LAST:event_cb_SucursalComprasActionPerformed

    private void btn_AbrirHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirHistorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_AbrirHistorialActionPerformed

    private void btn_GuardarActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActualizarActionPerformed
        guardarOActualizar();        // TODO add your handling code here:
    }//GEN-LAST:event_btn_GuardarActualizarActionPerformed

    private void btn_CancerlarlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CancerlarlimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btn_CancerlarlimpiarActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String textoBusqueda = tf_buscar.getText().trim();
        cargarProveedoresEnTabla(textoBusqueda);    }//GEN-LAST:event_btn_buscarActionPerformed

    private void cb_ProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_ProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_ProveedorActionPerformed

    private void tf_DireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_DireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_DireccionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ComprasGlobales;
    private javax.swing.JLabel DetalleDevolucionesRecientes;
    private javax.swing.JPanel Devoluciones;
    private javax.swing.JLabel DevolucionesPorTienda;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable tb_ComprasGlobales;
    private javax.swing.JTable tb_devoluciones;
    private javax.swing.JTextField tf_Direccion;
    private javax.swing.JTextField tf_RUC;
    private javax.swing.JTextField tf_Telefono;
    private javax.swing.JTextField tf_buscar;
    private javax.swing.JTextField tf_dniAsosiado;
    private javax.swing.JTextField tf_razonSocial;
    // End of variables declaration//GEN-END:variables
}
