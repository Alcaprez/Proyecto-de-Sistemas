package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.CuponDAO;
import edu.UPAO.proyecto.DAO.DevolucionEstadisticasDAO;
import edu.UPAO.proyecto.Modelo.Cupon;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import edu.UPAO.proyecto.DAO.DevolucionEstadisticasDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO; // Si la tienes, para llenar el combo
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;


public class panel_VentaGerente extends javax.swing.JPanel {

    private ButtonGroup bgTipoCupon;
    private DevolucionEstadisticasDAO statsDAO = new DevolucionEstadisticasDAO();

    public panel_VentaGerente() {
        initComponents();
        initLogic();
        initDevoluciones();

    }

    private void initLogic() {
        bgTipoCupon = new ButtonGroup();
        bgTipoCupon.add(jRadioButton3);
        bgTipoCupon.add(jRadioButton4);
        jRadioButton3.setText("Porcentaje (%)");
        jRadioButton4.setText("Monto Fijo (S/.)");
        jRadioButton3.setSelected(true);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Código", "Tipo", "Valor", "Descripción", "Inicio", "Fin", "Estado", "Usos"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb_cupones.setModel(model); // Asegúrate que tu tabla en diseño se llame tb_cupones (o jTable2 según tu código anterior)

        cargarCupones();

        // Listener de selección
        tb_cupones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCuponSeleccionado();
            }
        });

        // Botones
        btn_guardar.addActionListener(e -> guardarCupon());
        btn_cancelar.addActionListener(e -> limpiarFormulario());

        jButton4.addActionListener(e -> {
            LocalDate hoy = LocalDate.now();
            tf_fechainicio.setText(hoy.toString());
            tf_fechafin.setText(hoy.plusDays(30).toString());
        });

        btn_eliminar.addActionListener(e -> eliminarCupon());
        btn_desactivar.addActionListener(e -> cambiarEstadoCupon(false));
        btn_activar.addActionListener(e -> cambiarEstadoCupon(true));
    }

    private void cargarCupones() {
        DefaultTableModel model = (DefaultTableModel) tb_cupones.getModel();
        model.setRowCount(0);

        List<Cupon> lista = CuponDAO.listar();
        for (Cupon c : lista) {
            model.addRow(new Object[]{
                c.getCodigo(),
                c.getTipo(),
                c.getValor(),
                // ✅ CORREGIDO: Usamos getDescripcion()
                (c.getDescripcion() == null || c.getDescripcion().isEmpty()) ? "GENERAL" : c.getDescripcion(),
                c.getInicio(),
                c.getFin(),
                c.isActivo() ? "ACTIVO" : "INACTIVO",
                c.getUsos() + " / " + (c.getMaxUsos() == 0 ? "∞" : c.getMaxUsos())
            });
        }
    }

    private void guardarCupon() {
        try {
            String codigo = tf_codigo.getText().trim().toUpperCase();
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El código es obligatorio.");
                return;
            }

            double valor = Double.parseDouble(tf_valor.getText().trim());
            String descripcion = tf_descripcion.getText().trim();

            LocalDate inicio = LocalDate.parse(tf_fechainicio.getText().trim());
            LocalDate fin = LocalDate.parse(tf_fechafin.getText().trim());

            int maxUsos = 0;
            try {
                maxUsos = Integer.parseInt(tf_usosMaximos.getText().trim());
            } catch (Exception e) {
            }

            int usosPrevios = 0;
            Optional<Cupon> anterior = CuponDAO.buscarPorCodigo(codigo);
            if (anterior.isPresent()) {
                usosPrevios = anterior.get().getUsos();
            }

            Cupon c = new Cupon(
                    codigo,
                    jRadioButton3.isSelected() ? Cupon.TipoDescuento.PERCENT : Cupon.TipoDescuento.FLAT,
                    valor,
                    descripcion, // Pasamos la descripción
                    0.0,
                    inicio, fin,
                    jCheckBox2.isSelected(),
                    maxUsos,
                    usosPrevios
            );

            CuponDAO.upsert(c);
            cargarCupones();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cupón guardado correctamente.");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (Use YYYY-MM-DD).");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Revise los campos numéricos.");
        }
    }

    private void cargarCuponSeleccionado() {
        int fila = tb_cupones.getSelectedRow();
        if (fila == -1) {
            return;
        }

        String codigo = tb_cupones.getValueAt(fila, 0).toString();
        Optional<Cupon> opt = CuponDAO.buscarPorCodigo(codigo);

        if (opt.isPresent()) {
            Cupon c = opt.get();
            tf_codigo.setText(c.getCodigo());

            if (c.getTipo() == Cupon.TipoDescuento.PERCENT) {
                jRadioButton3.setSelected(true);
            } else {
                jRadioButton4.setSelected(true);
            }

            // ✅ CORREGIDO: Usamos getDescripcion()
            tf_descripcion.setText(c.getDescripcion() == null ? "" : c.getDescripcion());
            tf_valor.setText(String.valueOf(c.getValor()));
            tf_fechainicio.setText(c.getInicio().toString());
            tf_fechafin.setText(c.getFin().toString());
            jCheckBox2.setSelected(c.isActivo());
            tf_usosMaximos.setText(String.valueOf(c.getMaxUsos()));
        }
    }

    private void eliminarCupon() {
        int fila = tb_cupones.getSelectedRow();
        if (fila == -1) {
            return;
        }
        String codigo = tb_cupones.getValueAt(fila, 0).toString();

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar cupón " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            CuponDAO.eliminar(codigo);
            cargarCupones();
            limpiarFormulario();
        }
    }

    private void cambiarEstadoCupon(boolean activo) {
        int fila = tb_cupones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cupón.");
            return;
        }
        String codigo = tb_cupones.getValueAt(fila, 0).toString();
        Optional<Cupon> opt = CuponDAO.buscarPorCodigo(codigo);
        if (opt.isPresent()) {
            Cupon c = opt.get();
            c.setActivo(activo);
            CuponDAO.upsert(c);
            cargarCupones();
            if (tf_codigo.getText().equals(codigo)) {
                jCheckBox2.setSelected(activo);
            }
        }
    }

    private void limpiarFormulario() {
        tf_codigo.setText("");
        tf_codigo.setEditable(true);
        tf_descripcion.setText("");
        tf_valor.setText("");
        tf_fechainicio.setText("");
        tf_fechafin.setText("");
        tf_usosMaximos.setText("0");
        jCheckBox2.setSelected(true);
        tb_cupones.clearSelection();
    }

    // 1. Método para Inicializar la Pestaña de Devoluciones (LLAMAR EN EL CONSTRUCTOR)
    private void initDevoluciones() {
        // Llenar combo de sucursales (Si ya tienes un método para esto, úsalo)
        cb_sucursal.removeAllItems();
        cb_sucursal.addItem("TODAS");
        try {
            // Instanciamos el DAO
            edu.UPAO.proyecto.DAO.SucursalDAO daoSuc = new edu.UPAO.proyecto.DAO.SucursalDAO();

            // Llamamos al método que ya tienes
            List<String> lista = daoSuc.obtenerSucursalesActivas();

            // VERIFICACIÓN EN CONSOLA (Para depurar)
            System.out.println("Sucursales encontradas: " + lista.size());

            for (String s : lista) {
                cb_sucursal.addItem(s);
                System.out.println("Agregada al combo: " + s);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar sucursales en el combo: " + e.getMessage());
            e.printStackTrace();
        }

        // Evento al cambiar sucursal (Evita añadirlo múltiples veces)
        for (java.awt.event.ActionListener al : cb_sucursal.getActionListeners()) {
            cb_sucursal.removeActionListener(al);
        }
        cb_sucursal.addActionListener(e -> cargarDatosDevoluciones());

        // Cargar datos iniciales
        cargarDatosDevoluciones();
    }

    // 2. Método Maestro de Carga de Datos
    private void cargarDatosDevoluciones() {
        String sucursal = cb_sucursal.getSelectedItem() != null ? cb_sucursal.getSelectedItem().toString() : "TODAS";
        //if (sucursal.equals("Sucursal Principal")) sucursal = "1"; // Ajuste si usas nombres

        // A. Actualizar KPIs (Etiquetas)
        int cantidad = statsDAO.obtenerCantidadTotal(sucursal);
        double monto = statsDAO.obtenerMontoTotal(sucursal);
        double tasa = statsDAO.obtenerTasaDevolucion(sucursal);

        lbl_Devoluciones.setText(String.valueOf(cantidad));
        lbl_DevolucionesSoles.setText("S/ " + String.format("%.2f", monto));
        lbl_PorcentajeDevoluciones3.setText(String.format("%.1f%%", tasa));

        // Color de alerta si la tasa es alta (> 5%)
        if (tasa > 5.0) {
            lbl_PorcentajeDevoluciones3.setForeground(Color.RED);
        } else {
            lbl_PorcentajeDevoluciones3.setForeground(new Color(51, 51, 51));
        }

        // B. Actualizar Gráficos
        actualizarGraficoPastel(sucursal);
        actualizarGraficoTendencia(sucursal);
    }

    // 3. Gráfico de Pastel (Motivos)
    private void actualizarGraficoPastel(String sucursal) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> data = statsDAO.obtenerMotivosFrecuentes(sucursal);

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
            // Opcional: Llenar tu lista lateral también

        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Motivos de Devolución",
                dataset,
                false, // Leyenda (La quitamos porque tenemos la lista al lado o el panel es chico)
                true,
                false
        );

        renderizarGrafico(panel_graficoPastelMotivo, chart);
    }

    // 4. Gráfico de Líneas (Tendencia)
    private void actualizarGraficoTendencia(String sucursal) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> data = statsDAO.obtenerTendenciaFechas(sucursal);

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Monto Devuelto", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Tendencia de Devoluciones (Últimos 30 días)",
                "Fecha",
                "Monto (S/)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        renderizarGrafico(panel_tendenciaDevoluciones, chart);
    }

    // 5. Método Auxiliar para pintar en el Panel sin deformarse
    private void renderizarGrafico(javax.swing.JPanel panelContenedor, JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(chart);

        // Ajuste crítico para layouts
        chartPanel.setMinimumSize(new java.awt.Dimension(479, 284));
        chartPanel.setPreferredSize(new java.awt.Dimension(479, 284));

        panelContenedor.removeAll();
        panelContenedor.setLayout(new BorderLayout());
        panelContenedor.add(chartPanel, BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PROMOCIONES = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbl_Devoluciones = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lbl_PorcentajeDevoluciones3 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lbl_DevolucionesSoles = new javax.swing.JLabel();
        cb_sucursal = new javax.swing.JComboBox<>();
        panel_tendenciaDevoluciones = new javax.swing.JPanel();
        panel_graficoPastelMotivo = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btn_activar = new javax.swing.JToggleButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        tf_codigo = new javax.swing.JTextField();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        tf_descripcion = new javax.swing.JTextField();
        tf_valor = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tf_fechainicio = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tf_fechafin = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        btn_cancelar = new javax.swing.JButton();
        btn_guardar = new javax.swing.JButton();
        tf_usosMaximos = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb_cupones = new javax.swing.JTable();
        btn_eliminar = new javax.swing.JToggleButton();
        btn_desactivar = new javax.swing.JToggleButton();
        btn_exportar = new javax.swing.JToggleButton();

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PROMOCIONES.addTab("ROTACION DE PRODUCTOS", jPanel1);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("Devoluciones totales");

        lbl_Devoluciones.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_Devoluciones.setForeground(new java.awt.Color(51, 51, 51));
        lbl_Devoluciones.setText("216");
        lbl_Devoluciones.setAlignmentX(0.5F);
        lbl_Devoluciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(lbl_Devoluciones))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lbl_Devoluciones, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("Porcentaje de devoluciones");

        lbl_PorcentajeDevoluciones3.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_PorcentajeDevoluciones3.setForeground(new java.awt.Color(51, 51, 51));
        lbl_PorcentajeDevoluciones3.setText("17.5%");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(lbl_PorcentajeDevoluciones3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(lbl_PorcentajeDevoluciones3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Devoluciones en soles");

        lbl_DevolucionesSoles.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_DevolucionesSoles.setForeground(new java.awt.Color(51, 51, 51));
        lbl_DevolucionesSoles.setText("S/.6,254");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel8)
                .addContainerGap(31, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_DevolucionesSoles, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_DevolucionesSoles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(14, 14, 14))
        );

        cb_sucursal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_sucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_sucursalActionPerformed(evt);
            }
        });

        panel_tendenciaDevoluciones.setBackground(new java.awt.Color(255, 255, 255));
        panel_tendenciaDevoluciones.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panel_tendenciaDevoluciones.setForeground(new java.awt.Color(255, 255, 204));

        javax.swing.GroupLayout panel_tendenciaDevolucionesLayout = new javax.swing.GroupLayout(panel_tendenciaDevoluciones);
        panel_tendenciaDevoluciones.setLayout(panel_tendenciaDevolucionesLayout);
        panel_tendenciaDevolucionesLayout.setHorizontalGroup(
            panel_tendenciaDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );
        panel_tendenciaDevolucionesLayout.setVerticalGroup(
            panel_tendenciaDevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panel_graficoPastelMotivo.setBackground(new java.awt.Color(255, 255, 255));
        panel_graficoPastelMotivo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panel_graficoPastelMotivo.setForeground(new java.awt.Color(255, 255, 204));

        javax.swing.GroupLayout panel_graficoPastelMotivoLayout = new javax.swing.GroupLayout(panel_graficoPastelMotivo);
        panel_graficoPastelMotivo.setLayout(panel_graficoPastelMotivoLayout);
        panel_graficoPastelMotivoLayout.setHorizontalGroup(
            panel_graficoPastelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );
        panel_graficoPastelMotivoLayout.setVerticalGroup(
            panel_graficoPastelMotivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 282, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cb_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(panel_tendenciaDevoluciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(panel_graficoPastelMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(174, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(cb_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_graficoPastelMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_tendenciaDevoluciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(110, 110, 110))
        );

        PROMOCIONES.addTab("DEVOLUCIONES", jPanel6);

        btn_activar.setText("Activar");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setForeground(new java.awt.Color(204, 204, 204));

        tf_codigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_codigoActionPerformed(evt);
            }
        });

        jRadioButton3.setText("jRadioButton1");

        jRadioButton4.setText("jRadioButton2");

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Codigo:");

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Tipo");

        jLabel17.setBackground(new java.awt.Color(0, 0, 0));
        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Valor");

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Descripcion");

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Fecha Inicio:");

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Fecha fin:");

        jCheckBox2.setText("Habilitado");

        jLabel21.setBackground(new java.awt.Color(0, 0, 0));
        jLabel21.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("Activo:");

        jButton4.setText("Fecha hoy +30");

        btn_cancelar.setText("Cancelar");

        btn_guardar.setText("Guardar/Actualizar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(0, 0, 0));
        jLabel22.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("Usos maximos:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(jRadioButton4))
                            .addComponent(tf_codigo)
                            .addComponent(tf_descripcion)
                            .addComponent(tf_valor, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tf_fechainicio, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tf_fechafin, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tf_usosMaximos)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)))
                .addGap(26, 26, 26))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(tf_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(tf_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_fechainicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_fechafin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(27, 27, 27)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_usosMaximos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(44, 44, 44)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );

        tb_cupones.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tb_cupones);

        btn_eliminar.setText("Eliminar");

        btn_desactivar.setText("Desactivar");

        btn_exportar.setText("Exportar");
        btn_exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(btn_desactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btn_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btn_exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_desactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(484, 484, 484)
                .addComponent(btn_activar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(560, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(520, Short.MAX_VALUE)
                .addComponent(btn_activar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        PROMOCIONES.addTab("CUPONES", jPanel10);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PROMOCIONES)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PROMOCIONES)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void cb_sucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursalActionPerformed

    private void tf_codigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_codigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_codigoActionPerformed

    private void btn_exportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exportarActionPerformed

        btn_exportar.addActionListener(e -> {
            if (tb_cupones.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.");
                return;
            }

            String[] opciones = {"PDF", "Excel (CSV)", "Cancelar"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "¿En qué formato desea exportar la lista de cupones?",
                    "Exportar Cupones",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == 0) {
                // Opción PDF - Usamos tu GeneradorPDF actualizado
                edu.UPAO.proyecto.Util.GeneradorPDF.generarReporteDesdeTabla(tb_cupones, "Reporte de Cupones");
            } else if (seleccion == 1) {
                // Opción Excel - Usamos la nueva clase
                edu.UPAO.proyecto.Util.GeneradorExcel.exportarExcel(tb_cupones, "Reporte_Cupones");
            }
        });
    }//GEN-LAST:event_btn_exportarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane PROMOCIONES;
    private javax.swing.JToggleButton btn_activar;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JToggleButton btn_desactivar;
    private javax.swing.JToggleButton btn_eliminar;
    private javax.swing.JToggleButton btn_exportar;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JComboBox<String> cb_sucursal;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbl_Devoluciones;
    private javax.swing.JLabel lbl_DevolucionesSoles;
    private javax.swing.JLabel lbl_PorcentajeDevoluciones3;
    private javax.swing.JPanel panel_graficoPastelMotivo;
    private javax.swing.JPanel panel_tendenciaDevoluciones;
    private javax.swing.JTable tb_cupones;
    private javax.swing.JTextField tf_codigo;
    private javax.swing.JTextField tf_descripcion;
    private javax.swing.JTextField tf_fechafin;
    private javax.swing.JTextField tf_fechainicio;
    private javax.swing.JTextField tf_usosMaximos;
    private javax.swing.JTextField tf_valor;
    // End of variables declaration//GEN-END:variables
}
