package edu.UPAO.proyecto.app;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.geom.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import edu.UPAO.proyecto.Service.DashboardVentasService;
import edu.UPAO.proyecto.Service.ProductoService;
import edu.UPAO.proyecto.Modelo.Producto;
import edu.UPAO.proyecto.DAO.*;
import edu.UPAO.proyecto.Modelo.PuntoDiario;
import edu.UPAO.proyecto.Modelo.PuntoMensual;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VENTAS extends javax.swing.JFrame {

    private YearMonth mesSeleccionado = YearMonth.now();     // FECHA filtro (mes)
    private Integer productoIdSeleccionado = null; // null = todos

// formatos
    private final NumberFormat NF_MONEY = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
    private final DateTimeFormatter DF_MES = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "PE"));
    private final DashboardVentasService dash = new DashboardVentasService();
    private final ProductoService productoService = new ProductoService();

    private Map<String, Object> ultimoResultado;
    private final java.util.Map<String, Integer> keyProductoId = new java.util.LinkedHashMap<>();
    private BarChartPanel chartMensual;
    private BarChartPanel chartProducto;
    private PieChartPanel chartMediosPago;
    private LineChartPanel chartDiario;

    public VENTAS() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        poblarCombosDesdeServicios();
        buildCharts();
        wireFilters();
        refrescarDashboard();
    }

    private void buildCharts() {
        // Construye una sola vez y añade a los paneles de tu GUI
        panelVentasMensuales.setLayout(new BorderLayout());
        panelVentasPorProducto.setLayout(new BorderLayout());
        panelMediosPago.setLayout(new BorderLayout());
        panelVentasDiarias.setLayout(new BorderLayout());

        chartMensual = new BarChartPanel("");
        chartProducto = new BarChartPanel("");
        chartMediosPago = new PieChartPanel("");
        chartDiario = new LineChartPanel("");

        panelVentasMensuales.add(chartMensual, BorderLayout.CENTER);
        panelVentasPorProducto.add(chartProducto, BorderLayout.CENTER);
        panelMediosPago.add(chartMediosPago, BorderLayout.CENTER);
        panelVentasDiarias.add(chartDiario, BorderLayout.CENTER);
    }

    private String formatoSoles(double v) {
        // Muestra S/ 12,345.67
        String s = NF_MONEY.format(v);
        // En algunas configuraciones sale "PEN" o "S/." – lo normalizamos:
        return s.replace("PEN", "S/").replace("S/.", "S/");
    }

    private void poblarCombosDesdeServicios() {
        try {
            // 1) Traer productos
            java.util.List<Producto> productos = productoService.listarProductos(); // ajusta el nombre si fuera listar()

            // 2) Modelo para JComboBox<String>
            DefaultComboBoxModel<String> mP = new DefaultComboBoxModel<>();
            mP.addElement("Todos");

            // 3) Limpiar y volver a llenar el índice etiqueta -> id
            keyProductoId.clear();
            for (Producto p : productos) {
                // Etiqueta visible en el combo. Usa código si lo tienes para evitar duplicados.
                String etiqueta;
                if (p.getCodigo() != null && !p.getCodigo().isBlank()) {
                    etiqueta = p.getCodigo() + " - " + p.getNombre();
                } else {
                    etiqueta = p.getNombre();
                }
                mP.addElement(etiqueta);
                keyProductoId.put(etiqueta, p.getIdProducto()); // vincula etiqueta con el ID
            }

            cmbProducto.setModel(mP);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar productos: " + ex.getMessage());
            DefaultComboBoxModel<String> mP = new DefaultComboBoxModel<>();
            mP.addElement("Todos");
            cmbProducto.setModel(mP);
            keyProductoId.clear();
        }

        // Si aún no manejas tiendas, deja solo "Todas"
        DefaultComboBoxModel<String> mT = new DefaultComboBoxModel<>();
        mT.addElement("Todas");
        cmbTienda.setModel(mT);
    }

    private void wireFilters() {
        cmbProducto.addActionListener(e -> {
            Object sel = cmbProducto.getSelectedItem();
            if (sel instanceof Producto p) {
                productoIdSeleccionado = p.getIdProducto();
            } else {
                productoIdSeleccionado = null;
            }
            refrescarDashboard();
        });

        btnFecha.addActionListener(e -> {
            String sel = (String) cmbProducto.getSelectedItem();
            if (sel == null || "Todos".equals(sel)) {
                productoIdSeleccionado = null;                 // sin filtro
            } else {
                productoIdSeleccionado = keyProductoId.get(sel); // ID por la etiqueta
            }
            refrescarDashboard(); // actualiza KPIs y gráficos
        });

        btnExportar.addActionListener(e -> {
            try {
                exportarCSV();
            } catch (IOException ex) {
                System.getLogger(VENTAS.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }); // nuevo export
    }

    private void refrescarDashboard() {
        LocalDate desde = mesSeleccionado.atDay(1);
        LocalDate hasta = mesSeleccionado.atEndOfMonth();

        ultimoResultado = dash.consultar(desde, hasta, productoIdSeleccionado);

        // KPIs
        KPI k = (KPI) ultimoResultado.get("kpis");
        lblVentasValor.setText(String.valueOf(k.totalVentas()));
        lblProdValor.setText(String.valueOf(k.productosVendidos()));
        lblGanTotalValor.setText(formatoSoles(k.gananciaTotal()));
        lblGanBrutaValor.setText(formatoSoles(k.gananciaBruta()));

        // Gráfico: Ventas mensuales
        List<PuntoMensual> mensuales = (List<PuntoMensual>) ultimoResultado.get("mensuales");
        Map<String, Double> mapMensual = new LinkedHashMap<>();
        for (PuntoMensual p : mensuales) {
            mapMensual.put(p.getMes().getMonth().name().substring(0, 3), p.getTotal()); // ENE, FEB...
        }
        chartMensual.setData(mapMensual);

        // Gráfico: Ventas por producto
        List<Barra> porProducto = (List<Barra>) ultimoResultado.get("porProducto");
        Map<String, Double> mapProd = new LinkedHashMap<>();
        for (Barra b : porProducto) {
            mapProd.put(b.etiqueta(), b.total());
        }
        chartProducto.setData(mapProd);

        // Gráfico: Medios de pago
        List<Slice> medios = (List<Slice>) ultimoResultado.get("mediosPago");
        Map<String, Double> mapMedios = new LinkedHashMap<>();
        for (Slice s : medios) {
            mapMedios.put(s.etiqueta(), s.valor());
        }
        chartMediosPago.setData(mapMedios);

        // Gráfico: Ventas diarias
        List<PuntoDiario> diarias = (List<PuntoDiario>) ultimoResultado.get("diarias");
        int last = mesSeleccionado.lengthOfMonth();
        double[] serie = new double[last];
        for (PuntoDiario d : diarias) {
            int idx = d.getdia().getDayOfMonth() - 1;
            if (idx >= 0 && idx < last) {
                serie[idx] = d.getTotal();
            }
        }
        chartDiario.setSeries(serie);
        chartDiario.repaint();
    }

    private void exportarCSV() throws IOException {
    try (FileWriter writer = new FileWriter("ventas_dashboard.csv")) {
        // Cabecera
        writer.write("Tipo,Etiqueta,Valor\n");

        // Productos (porProducto)
        Object o1 = ultimoResultado.get("porProducto");
        if (o1 instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Barra b) {
                    writer.write("Producto," + escapeCsv(b.etiqueta()) + "," + b.total() + "\n");
                }
            }
        }

        // Medios de pago (mediosPago)
        Object o2 = ultimoResultado.get("mediosPago");
        if (o2 instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Slice s) {
                    writer.write("MedioPago," + escapeCsv(s.etiqueta()) + "," + s.valor() + "\n");
                }
            }
        }

        // Ventas mensuales (mensuales)
        Object o3 = ultimoResultado.get("mensuales");
        if (o3 instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof PuntoMensual p) {
                    writer.write("Mensual," + p.getMes() + "," + p.getTotal() + "\n");
                }
            }
        }

        // Ventas diarias (diarias)
        Object o4 = ultimoResultado.get("diarias");
        if (o4 instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof PuntoDiario p) {
                    writer.write("Diario," + p.getdia() + "," + p.getTotal() + "\n");
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Datos exportados a ventas_dashboard.csv");
    }
}
    private static String escapeCsv(String s) {
    if (s == null) return "";
    String tmp = s.replace("\"", "\"\"");
    return "\"" + tmp + "\"";
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTop = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        lblFrase = new javax.swing.JLabel();
        panelTitle = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelBody = new javax.swing.JPanel();
        panelFiltros = new javax.swing.JPanel();
        lblFiltrar = new javax.swing.JLabel();
        cmbTienda = new javax.swing.JComboBox<>();
        btnFecha = new javax.swing.JButton();
        cmbProducto = new javax.swing.JComboBox<>();
        btnExportar = new javax.swing.JButton();
        panelContent = new javax.swing.JPanel();
        panelKPIs = new javax.swing.JPanel();
        kpiVentasTotales = new javax.swing.JPanel();
        lblVentasTitulo = new javax.swing.JLabel();
        lblVentasValor = new javax.swing.JLabel();
        kpiProductosVendidos = new javax.swing.JPanel();
        lblProdTitulo = new javax.swing.JLabel();
        lblProdValor = new javax.swing.JLabel();
        kpiGananciaTotal = new javax.swing.JPanel();
        lblGanTotalTitulo = new javax.swing.JLabel();
        lblGanTotalValor = new javax.swing.JLabel();
        kpiGananciaBruta = new javax.swing.JPanel();
        lblGanBrutaTitulo = new javax.swing.JLabel();
        lblGanBrutaValor = new javax.swing.JLabel();
        panelCharts = new javax.swing.JPanel();
        panelVentasMensuales = new javax.swing.JPanel();
        lblVentasMensuales = new javax.swing.JLabel();
        panelVentasPorProducto = new javax.swing.JPanel();
        lblVentasPorProductos = new javax.swing.JLabel();
        panelMediosPago = new javax.swing.JPanel();
        lblMediosPago = new javax.swing.JLabel();
        panelVentasDiarias = new javax.swing.JPanel();
        lblVentasDiarias = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelTop.setLayout(new java.awt.BorderLayout());

        panelHeader.setBackground(new java.awt.Color(255, 153, 0));
        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 16, 0, 16));
        panelHeader.setPreferredSize(new java.awt.Dimension(780, 70));
        panelHeader.setLayout(new java.awt.BorderLayout());
        panelHeader.add(logoLabel, java.awt.BorderLayout.WEST);

        btnSalir.setFont(new java.awt.Font("Leelawadee UI", 1, 12)); // NOI18N
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelHeader.add(btnSalir, java.awt.BorderLayout.EAST);

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 12)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");
        panelHeader.add(lblFrase, java.awt.BorderLayout.CENTER);

        panelTop.add(panelHeader, java.awt.BorderLayout.NORTH);

        panelTitle.setBackground(new java.awt.Color(153, 0, 0));
        panelTitle.setPreferredSize(new java.awt.Dimension(780, 40));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("VENTAS");
        panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);

        panelTop.add(panelTitle, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        panelBody.setBackground(new java.awt.Color(219, 236, 232));
        panelBody.setLayout(new java.awt.BorderLayout());

        panelFiltros.setBackground(new java.awt.Color(219, 236, 232));
        panelFiltros.setLayout(new java.awt.GridBagLayout());

        lblFiltrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFiltrar.setText("FILTRAR POR:");
        panelFiltros.add(lblFiltrar, new java.awt.GridBagConstraints());

        cmbTienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTienda.setPreferredSize(new java.awt.Dimension(160, 32));
        panelFiltros.add(cmbTienda, new java.awt.GridBagConstraints());

        btnFecha.setText("FECHA");
        btnFecha.setPreferredSize(new java.awt.Dimension(120, 32));
        panelFiltros.add(btnFecha, new java.awt.GridBagConstraints());

        cmbProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProducto.setPreferredSize(new java.awt.Dimension(160, 32));
        cmbProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProductoActionPerformed(evt);
            }
        });
        panelFiltros.add(cmbProducto, new java.awt.GridBagConstraints());

        btnExportar.setText("EXPORTAR");
        btnExportar.setPreferredSize(new java.awt.Dimension(120, 32));
        panelFiltros.add(btnExportar, new java.awt.GridBagConstraints());

        panelBody.add(panelFiltros, java.awt.BorderLayout.PAGE_START);

        panelContent.setLayout(new java.awt.BorderLayout());

        panelKPIs.setBackground(new java.awt.Color(153, 153, 153));
        panelKPIs.setPreferredSize(new java.awt.Dimension(330, 534));
        panelKPIs.setLayout(new java.awt.GridLayout(4, 1, 10, 14));

        kpiVentasTotales.setBackground(new java.awt.Color(32, 107, 170));
        kpiVentasTotales.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        kpiVentasTotales.setLayout(new java.awt.BorderLayout());

        lblVentasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblVentasTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblVentasTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasTitulo.setText("TOTAL DE VENTAS");
        kpiVentasTotales.add(lblVentasTitulo, java.awt.BorderLayout.PAGE_START);

        lblVentasValor.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        lblVentasValor.setForeground(new java.awt.Color(255, 255, 255));
        lblVentasValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasValor.setText("jLabel3");
        lblVentasValor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        kpiVentasTotales.add(lblVentasValor, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiVentasTotales);

        kpiProductosVendidos.setBackground(new java.awt.Color(148, 79, 209));
        kpiProductosVendidos.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        kpiProductosVendidos.setLayout(new java.awt.BorderLayout());

        lblProdTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblProdTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblProdTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProdTitulo.setText("PRODUCTOS VENDIDOS");
        kpiProductosVendidos.add(lblProdTitulo, java.awt.BorderLayout.PAGE_START);

        lblProdValor.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        lblProdValor.setForeground(new java.awt.Color(255, 255, 255));
        lblProdValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProdValor.setText("jLabel5");
        kpiProductosVendidos.add(lblProdValor, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiProductosVendidos);

        kpiGananciaTotal.setBackground(new java.awt.Color(76, 182, 80));
        kpiGananciaTotal.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        kpiGananciaTotal.setLayout(new java.awt.BorderLayout());

        lblGanTotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblGanTotalTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblGanTotalTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGanTotalTitulo.setText("GANANCIA TOTAL");
        kpiGananciaTotal.add(lblGanTotalTitulo, java.awt.BorderLayout.PAGE_START);

        lblGanTotalValor.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        lblGanTotalValor.setForeground(new java.awt.Color(255, 255, 255));
        lblGanTotalValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGanTotalValor.setText("jLabel7");
        kpiGananciaTotal.add(lblGanTotalValor, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiGananciaTotal);

        kpiGananciaBruta.setBackground(new java.awt.Color(247, 120, 48));
        kpiGananciaBruta.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        kpiGananciaBruta.setLayout(new java.awt.BorderLayout());

        lblGanBrutaTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblGanBrutaTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblGanBrutaTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGanBrutaTitulo.setText("GANANCIA BRUTA");
        kpiGananciaBruta.add(lblGanBrutaTitulo, java.awt.BorderLayout.PAGE_START);

        lblGanBrutaValor.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        lblGanBrutaValor.setForeground(new java.awt.Color(255, 255, 255));
        lblGanBrutaValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGanBrutaValor.setText("jLabel9");
        kpiGananciaBruta.add(lblGanBrutaValor, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiGananciaBruta);

        panelContent.add(panelKPIs, java.awt.BorderLayout.EAST);

        panelCharts.setBackground(new java.awt.Color(255, 255, 255));
        panelCharts.setLayout(new java.awt.GridLayout(2, 2, 12, 12));

        panelVentasMensuales.setBackground(new java.awt.Color(255, 255, 255));
        panelVentasMensuales.setLayout(new java.awt.BorderLayout());

        lblVentasMensuales.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasMensuales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasMensuales.setText("VENTAS MENSUALES");
        panelVentasMensuales.add(lblVentasMensuales, java.awt.BorderLayout.PAGE_START);

        panelCharts.add(panelVentasMensuales);

        panelVentasPorProducto.setBackground(new java.awt.Color(255, 255, 255));
        panelVentasPorProducto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelVentasPorProducto.setLayout(new java.awt.BorderLayout());

        lblVentasPorProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasPorProductos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasPorProductos.setText("VENTAS POR PRODUCTO");
        panelVentasPorProducto.add(lblVentasPorProductos, java.awt.BorderLayout.PAGE_START);

        panelCharts.add(panelVentasPorProducto);

        panelMediosPago.setBackground(new java.awt.Color(255, 255, 255));
        panelMediosPago.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelMediosPago.setLayout(new java.awt.BorderLayout());

        lblMediosPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMediosPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMediosPago.setText("MEDIOS DE PAGO");
        panelMediosPago.add(lblMediosPago, java.awt.BorderLayout.PAGE_START);

        panelCharts.add(panelMediosPago);

        panelVentasDiarias.setBackground(new java.awt.Color(255, 255, 255));
        panelVentasDiarias.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelVentasDiarias.setLayout(new java.awt.BorderLayout());

        lblVentasDiarias.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasDiarias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasDiarias.setText("VENTAS DIARIAS");
        panelVentasDiarias.add(lblVentasDiarias, java.awt.BorderLayout.PAGE_START);

        panelCharts.add(panelVentasDiarias);

        panelContent.add(panelCharts, java.awt.BorderLayout.CENTER);

        panelBody.add(panelContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelBody, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        Panel_Gerente g = new Panel_Gerente();
        g.setVisible(true);
        g.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void cmbProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProductoActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VENTAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VENTAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VENTAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VENTAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VENTAS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cmbProducto;
    private javax.swing.JComboBox<String> cmbTienda;
    private javax.swing.JPanel kpiGananciaBruta;
    private javax.swing.JPanel kpiGananciaTotal;
    private javax.swing.JPanel kpiProductosVendidos;
    private javax.swing.JPanel kpiVentasTotales;
    private javax.swing.JLabel lblFiltrar;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lblGanBrutaTitulo;
    private javax.swing.JLabel lblGanBrutaValor;
    private javax.swing.JLabel lblGanTotalTitulo;
    private javax.swing.JLabel lblGanTotalValor;
    private javax.swing.JLabel lblMediosPago;
    private javax.swing.JLabel lblProdTitulo;
    private javax.swing.JLabel lblProdValor;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblVentasDiarias;
    private javax.swing.JLabel lblVentasMensuales;
    private javax.swing.JLabel lblVentasPorProductos;
    private javax.swing.JLabel lblVentasTitulo;
    private javax.swing.JLabel lblVentasValor;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelCharts;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelKPIs;
    private javax.swing.JPanel panelMediosPago;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel panelTop;
    private javax.swing.JPanel panelVentasDiarias;
    private javax.swing.JPanel panelVentasMensuales;
    private javax.swing.JPanel panelVentasPorProducto;
    // End of variables declaration//GEN-END:variables
}

// ============================ CHARTS BASE =============================
class ChartBase extends JPanel {

    final String title;

    ChartBase(String title) {
        this.title = title;
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(420, 260);
    }

    void drawTitle(Graphics2D g, int w) {
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(30, 30, 30));
        g.drawString(title, 10, fm.getAscent() + 6);
    }
}

// ------------------------------- BARRAS -------------------------------
class BarChartPanel extends ChartBase {

    private Map<String, Double> data = new LinkedHashMap<>();

    BarChartPanel(String title) {
        super(title);
    }

    void setData(Map<String, Double> d) {
        data = d;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        drawTitle(g, w);

        int top = 26, left = 40, bottom = 28, right = 10;
        int gh = h - top - bottom, gw = w - left - right;

        // eje
        g.setColor(new Color(200, 200, 200));
        g.drawLine(left, h - bottom, w - right, h - bottom);
        g.drawLine(left, top, left, h - bottom);

        if (data == null || data.isEmpty()) {
            g.dispose();
            return;
        }

        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        int n = data.size();
        int bw = Math.max(8, gw / Math.max(1, n) - 8);
        int x = left + 6;

        int i = 0;
        for (Map.Entry<String, Double> e : data.entrySet()) {
            double v = e.getValue();
            int bh = (int) Math.round(v / max * (gh - 10));
            int y = h - bottom - bh;

            g.setColor(new Color(66, 135, 245));
            g.fillRect(x, y, bw, bh);
            g.setColor(new Color(66, 135, 245).darker());
            g.drawRect(x, y, bw, bh);

            // etiqueta x
            g.setFont(g.getFont().deriveFont(11f));
            g.setColor(new Color(60, 60, 60));
            String lab = e.getKey();
            int tw = g.getFontMetrics().stringWidth(lab);
            g.drawString(lab, x + (bw - tw) / 2, h - 10);

            x += bw + 8;
            i++;
        }

        g.dispose();
    }
}

// ------------------------------- TARTA --------------------------------
class PieChartPanel extends ChartBase {

    private Map<String, Double> data = new LinkedHashMap<>();

    PieChartPanel(String title) {
        super(title);
    }

    void setData(Map<String, Double> d) {
        data = d;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        drawTitle(g, w);

        if (data == null || data.isEmpty()) {
            g.dispose();
            return;
        }

        int top = 28;
        int size = Math.min(w, h - top) - 40;
        int cx = 18, cy = top + 10;

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double ang0 = 0;
        int i = 0;

        Color[] cols = {new Color(66, 135, 245), new Color(255, 153, 0), new Color(46, 204, 113),
            new Color(231, 76, 60), new Color(155, 89, 182), new Color(52, 73, 94)};

        for (Map.Entry<String, Double> e : data.entrySet()) {
            double frac = (total == 0) ? 0 : e.getValue() / total;
            int ang = (int) Math.round(frac * 360);

            g.setColor(cols[i % cols.length]);
            g.fillArc(cx, cy, size, size, (int) ang0, ang);
            g.setColor(Color.WHITE);
            g.drawArc(cx, cy, size, size, (int) ang0, ang);

            ang0 += ang;

            // leyenda
            g.setColor(cols[i % cols.length]);
            int ly = top + 10 + i * 18;
            g.fillRect(cx + size + 20, ly, 12, 12);
            g.setColor(new Color(40, 40, 40));
            g.setFont(g.getFont().deriveFont(12f));
            g.drawString(e.getKey(), cx + size + 38, ly + 11);
            i++;
        }
        g.dispose();
    }
}

// ------------------------------- LÍNEA --------------------------------
class LineChartPanel extends ChartBase {

    private double[] series = new double[0];

    LineChartPanel(String title) {
        super(title);
    }

    void setSeries(double[] s) {
        series = (s != null) ? s : new double[0];
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        drawTitle(g, w);

        int top = 28, left = 40, bottom = 26, right = 10;
        int gh = h - top - bottom, gw = w - left - right;

        // ejes
        g.setColor(new Color(200, 200, 200));
        g.drawLine(left, h - bottom, w - right, h - bottom);
        g.drawLine(left, top, left, h - bottom);

        if (series.length == 0) {
            g.dispose();
            return;
        }

        double max = Arrays.stream(series).max().orElse(1);
        int n = series.length;
        double step = gw / (double) Math.max(1, n - 1);

        Path2D path = new Path2D.Double();
        for (int i = 0; i < n; i++) {
            double v = series[i];
            int x = (int) Math.round(left + i * step);
            int y = (int) Math.round(h - bottom - (v / max) * (gh - 10));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(66, 135, 245));
        g.draw(path);

        g.dispose();
    }
}
