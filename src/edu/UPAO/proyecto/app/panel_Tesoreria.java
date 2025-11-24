package edu.UPAO.proyecto.app;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.DAO.RentabilidadDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import edu.UPAO.proyecto.DAO.VentasDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class panel_Tesoreria extends javax.swing.JPanel {


    private VentasDAO ventasDAO;
    private RentabilidadDAO rentabilidadDAO;
    private SucursalDAO sucursalDAO;

    private Date fechaInicioFiltro;
    private Date fechaFinFiltro;

    private List<Object[]> datosActualesParaExportar;

    public panel_Tesoreria() {
        
        initComponents();
        iniciarDashboard();
        configurarGraficos();
        
    }
    
    
    
private void configurarGraficos() {
        // 1. AJUSTAR EL CONTENEDOR PRINCIPAL DE GRÁFICOS (panelCentro3)
        // Le damos un margen interno grande para que los gráficos se vean más pequeños y centrados.
        // Formato: (Arriba, Izquierda, Abajo, Derecha)
        panelCentro3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // 2. AJUSTAR ESPACIADO ENTRE GRÁFICOS (Separación)
        // Cambiamos el GridLayout original (2,2,5,5) por uno con más separación (20px)
        panelCentro3.setLayout(new java.awt.GridLayout(2, 2, 20, 20));
        
        // 3. FORZAR ACTUALIZACIÓN
        panelCentro3.revalidate();
    }

    // ESTE MÉTODO ES OBLIGATORIO PARA QUE LOS GRÁFICOS NO SE CORTEN
    // Úsalo dentro de tus métodos 'actualizarGrafico...' en lugar de solo añadir el panel.
    private void renderizarGrafico(JPanel panelContenedor, JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        
        // Crear el panel del gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        
        // --- EL TRUCO PARA QUE NO SE CORTE NI SE VEA GIGANTE ---
        // 1. Quitamos restricciones de tamaño mínimo (para que pueda encogerse)
        chartPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        // 2. Damos un tamaño preferido base pequeño (el layout lo estirará lo necesario)
        chartPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        
        chartPanel.setPopupMenu(null);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        
        // Limpiar el panel contenedor (el gris) y agregar el gráfico
        panelContenedor.removeAll();
        panelContenedor.setLayout(new BorderLayout()); // Importante para que llene el hueco
        panelContenedor.add(chartPanel, BorderLayout.CENTER);
        
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    private void iniciarDashboard() {
       try {
            java.sql.Connection conexion = new Conexion().establecerConexion();
            ventasDAO = new VentasDAO(conexion);
            rentabilidadDAO = new RentabilidadDAO();
            sucursalDAO = new SucursalDAO();
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error crítico de conexión: " + e.getMessage());
             return;
        }

        configurarFiltrosUI();
        
        // Por defecto: Este mes
        calcularFechasDesdeCombo("Este mes");
        actualizarDashboardVentas();
    }

    
    private void actualizarDashboardVentas() {
        if (ventasDAO == null) return;

        String sucursalSeleccionada = (cb_sucursal.getSelectedItem() != null) ? cb_sucursal.getSelectedItem().toString() : "TODAS";

        try {
            // Guardamos los datos en la variable global para poder exportarlos luego
            datosActualesParaExportar = ventasDAO.obtenerVentas(fechaInicioFiltro, fechaFinFiltro, sucursalSeleccionada);
            
            // Actualizar interfaz
            actualizarKPIs(datosActualesParaExportar);
            actualizarGraficoVentasDiarias(datosActualesParaExportar);
            actualizarGraficoVentasMensuales(datosActualesParaExportar);
            actualizarGraficoVentasPorProducto(datosActualesParaExportar);
            actualizarGraficoMetodosPago(datosActualesParaExportar);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // ACTUALIZACIÓN DE KPIs (Cuadros de colores)
    // =========================================================================
    private void actualizarKPIs(List<Object[]> ventas) {
      double totalSoles = 0;
        int totalCantidadVentas = ventas.size();
        int totalProductosVendidos = 0;
        
        // Mapa para contar repeticiones de métodos de pago
        Map<String, Integer> conteoPagos = new HashMap<>();
        
        for (Object[] v : ventas) {
            // v[3] = cantidad, v[4] = ingreso (subtotal), v[8] = metodo_pago
            totalProductosVendidos += (int) v[3];
            totalSoles += (double) v[4];
            
            String metodo = (String) v[8]; 
            if(metodo != null) {
                conteoPagos.put(metodo, conteoPagos.getOrDefault(metodo, 0) + 1);
            }
        }

        // Buscar el método más usado
        String pagoMasUsado = "N/A";
        int maxRepeticiones = 0;
        for (Map.Entry<String, Integer> entry : conteoPagos.entrySet()) {
            if (entry.getValue() > maxRepeticiones) {
                maxRepeticiones = entry.getValue();
                pagoMasUsado = entry.getKey();
            }
        }

       // Asignar valores a los Labels (Asegúrate que los nombres coinciden con DESIGN)
        lbl_rentabilidad7.setText(String.format("S/ %.2f", totalSoles));
        lbl_totalVentas.setText(String.valueOf(totalCantidadVentas));
        lbl_productosVendidos.setText(String.valueOf(totalProductosVendidos));
        
        // KPI MORADO
        if(lbl_medioPagoMasUsado != null) {
            lbl_medioPagoMasUsado.setText(pagoMasUsado);
            // Reducir fuente si el texto es muy largo
            if(pagoMasUsado.length() > 10) lbl_medioPagoMasUsado.setFont(new java.awt.Font("Dialog", 1, 18));
            else lbl_medioPagoMasUsado.setFont(new java.awt.Font("Dialog", 1, 24));
        }
    }
private void exportarDatosCSV() {
        if (datosActualesParaExportar == null || datosActualesParaExportar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Ventas");
        fileChooser.setSelectedFile(new File("Reporte_Ventas_" + System.currentTimeMillis() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Asegurar extensión .csv
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                // Escribir cabecera (BOM para Excel en español)
                writer.write("\ufeff"); 
                writer.write("Fecha;Sucursal;Producto;Cantidad;Ingreso;Costo;Ganancia;Tipo;MetodoPago\n");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (Object[] row : datosActualesParaExportar) {
                    // Construir línea CSV
                    String fecha = sdf.format((Date) row[0]);
                    String sucursal = String.valueOf(row[1]);
                    String producto = String.valueOf(row[2]);
                    String cantidad = String.valueOf(row[3]);
                    String ingreso = String.valueOf(row[4]).replace(".", ","); // Formato decimal excel
                    String costo = String.valueOf(row[5]).replace(".", ",");
                    String ganancia = String.valueOf(row[6]).replace(".", ",");
                    String tipo = String.valueOf(row[7]);
                    String metodo = String.valueOf(row[8]);

                    writer.write(String.join(";", fecha, sucursal, producto, cantidad, ingreso, costo, ganancia, tipo, metodo));
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "¡Exportación exitosa!\nGuardado en: " + fileToSave.getAbsolutePath());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
            }
        }
    }
      private void actualizarGraficoVentasDiarias(List<Object[]> ventas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> porDia = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object[] v : ventas) {
            Timestamp ts = (Timestamp) v[0];
            String dia = sdf.format(ts);
            double monto = (double) v[4];
            porDia.put(dia, porDia.getOrDefault(dia, 0.0) + monto);
        }

        for (Map.Entry<String, Double> e : porDia.entrySet()) dataset.addValue(e.getValue(), "Ventas", e.getKey());
        
        JFreeChart chart = ChartFactory.createLineChart("Ventas Diarias", "Fecha", "Monto (S/)", dataset, PlotOrientation.VERTICAL, false, true, false);
        renderizarGrafico(grafico_ventasdiarias3, chart);
    }

    private void actualizarGraficoVentasMensuales(List<Object[]> ventas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> porMes = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        for (Object[] v : ventas) {
            Timestamp ts = (Timestamp) v[0];
            String mes = sdf.format(ts);
            double monto = (double) v[4];
            porMes.put(mes, porMes.getOrDefault(mes, 0.0) + monto);
        }
        for (Map.Entry<String, Double> e : porMes.entrySet()) dataset.addValue(e.getValue(), "Ventas", e.getKey());

        JFreeChart chart = ChartFactory.createBarChart("Ventas Mensuales", "Mes", "Monto (S/)", dataset, PlotOrientation.VERTICAL, false, true, false);
        renderizarGrafico(grafico_ventasMensuales, chart);
    }

    private void actualizarGraficoVentasPorProducto(List<Object[]> ventas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> porProducto = new HashMap<>();

        for (Object[] v : ventas) {
            String producto = (String) v[2];
            double monto = (double) v[4];
            porProducto.put(producto, porProducto.getOrDefault(producto, 0.0) + monto);
        }
        for (Map.Entry<String, Double> entry : porProducto.entrySet()) dataset.addValue(entry.getValue(), "Ventas", entry.getKey());

        JFreeChart chart = ChartFactory.createBarChart("Ventas por Producto (Top)", "Producto", "Monto (S/)", dataset, PlotOrientation.HORIZONTAL, false, true, false);
        renderizarGrafico(grafico_ventasXProducto, chart);
    }

    private void actualizarGraficoMetodosPago(List<Object[]> ventas) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> porMetodo = new HashMap<>();

        for (Object[] v : ventas) {
            String metodo = (String) v[8];
            double monto = (double) v[4];
            porMetodo.put(metodo, porMetodo.getOrDefault(metodo, 0.0) + monto);
        }
        for (Map.Entry<String, Double> entry : porMetodo.entrySet()) dataset.setValue(entry.getKey(), entry.getValue());

        JFreeChart chart = ChartFactory.createPieChart("Medios de Pago", dataset, true, true, false);
        renderizarGrafico(grafico_metodosPago, chart);
    }
    


    private void configurarFiltrosUI() {
        cb_sucursal.removeAllItems();
        cb_sucursal.addItem("TODAS");
        try {
            List<String> sucursales = sucursalDAO.obtenerSucursalesActivas();
            for (String s : sucursales) cb_sucursal.addItem(s);
        } catch (Exception e) {}
        
        cb_rangoFechas.removeAllItems();
        cb_rangoFechas.addItem("Hoy");
        cb_rangoFechas.addItem("Últimos 7 días");
        cb_rangoFechas.addItem("Este mes");
        cb_rangoFechas.addItem("Mes anterior");
        cb_rangoFechas.setSelectedItem("Este mes");

        // Eventos
        cb_sucursal.addActionListener(e -> actualizarDashboardVentas());
        cb_rangoFechas.addActionListener(e -> {
            calcularFechasDesdeCombo(cb_rangoFechas.getSelectedItem().toString());
            actualizarDashboardVentas();
        });
        
        // ACCIÓN DEL BOTÓN EXPORTAR
        btn_exportar.addActionListener(e -> exportarDatosCSV());
    }

    private void calcularFechasDesdeCombo(String rango) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date hoyInicio = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        Date hoyFin = cal.getTime();

        switch (rango) {
            case "Hoy": fechaInicioFiltro = hoyInicio; fechaFinFiltro = hoyFin; break;
            case "Últimos 7 días": cal.setTime(hoyInicio); cal.add(Calendar.DAY_OF_YEAR, -7); fechaInicioFiltro = cal.getTime(); fechaFinFiltro = hoyFin; break;
            case "Este mes": cal.setTime(hoyInicio); cal.set(Calendar.DAY_OF_MONTH, 1); fechaInicioFiltro = cal.getTime(); fechaFinFiltro = hoyFin; break;
            case "Mes anterior": cal.setTime(hoyInicio); cal.add(Calendar.MONTH, -1); cal.set(Calendar.DAY_OF_MONTH, 1); fechaInicioFiltro = cal.getTime(); cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); fechaFinFiltro = cal.getTime(); break;
            default: fechaInicioFiltro = hoyInicio; fechaFinFiltro = hoyFin;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbl_total2 = new javax.swing.JLabel();
        Tabbed_Tesoreria = new javax.swing.JTabbedPane();
        panel_Ventas = new javax.swing.JPanel();
        panelSuperiorNorte = new javax.swing.JPanel();
        Filtros = new javax.swing.JPanel();
        cb_sucursal = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cb_productos2 = new javax.swing.JComboBox<>();
        cb_rangoFechas = new javax.swing.JComboBox<>();
        btn_exportar = new javax.swing.JButton();
        panelKpis = new javax.swing.JPanel();
        panel_TotalenSoles = new javax.swing.JPanel();
        lbl_rentabilidad7 = new javax.swing.JLabel();
        lbl_totalSoles = new javax.swing.JLabel();
        panel_TotaldeVentas = new javax.swing.JPanel();
        lbl_rentabilidad3 = new javax.swing.JLabel();
        lbl_totalVentas = new javax.swing.JLabel();
        panelProductosVendidos = new javax.swing.JPanel();
        lbl_rentabilidad2 = new javax.swing.JLabel();
        lbl_productosVendidos = new javax.swing.JLabel();
        panelMedioDePagoUsado = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbl_medioPagoMasUsado = new javax.swing.JLabel();
        panelCentro3 = new javax.swing.JPanel();
        grafico_ventasdiarias3 = new javax.swing.JPanel();
        grafico_ventasXProducto = new javax.swing.JPanel();
        grafico_ventasMensuales = new javax.swing.JPanel();
        grafico_metodosPago = new javax.swing.JPanel();
        panel_COMPRAS = new javax.swing.JPanel();
        cb_sucursal1 = new javax.swing.JComboBox<>();
        cb_productos1 = new javax.swing.JComboBox<>();
        cb_rangoFechas1 = new javax.swing.JComboBox<>();
        btn_exportar1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        panel_Estadisticas = new javax.swing.JPanel();
        Tabbed_Estadisticas = new javax.swing.JTabbedPane();
        panel_Ranking = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        btn_gananciaNeta = new javax.swing.JButton();
        btn_rentabilidad = new javax.swing.JButton();
        cb_tipoRanking = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        lbl_totalVentas1 = new javax.swing.JLabel();
        lbl_totalVentas5 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        lbl_totalVentas2 = new javax.swing.JLabel();
        lbl_totalVentas4 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        lbl_totalVentas3 = new javax.swing.JLabel();
        lbl_promedioVentas = new javax.swing.JLabel();
        panel_rankingXVentas = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        panel_Rentabilidad = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        ganancia_bruta2 = new javax.swing.JPanel();
        lbl_gananciaBruta2 = new javax.swing.JLabel();
        costos_totales = new javax.swing.JPanel();
        lbl_costos = new javax.swing.JLabel();
        Ingresos_totales = new javax.swing.JPanel();
        lbl_totalIngresos = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        filtro_sucursal = new javax.swing.JComboBox<>();
        lbl_rentabilidad = new javax.swing.JLabel();
        ganancia_neta = new javax.swing.JPanel();
        lbl_gananciaNeta = new javax.swing.JLabel();
        panel_estadisticas = new javax.swing.JPanel();
        cb_rangoss = new javax.swing.JComboBox<>();

        lbl_total2.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_total2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total2.setText("0.00");
        lbl_total2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        setPreferredSize(new java.awt.Dimension(0, 0));

        panel_Ventas.setLayout(new java.awt.BorderLayout());

        panelSuperiorNorte.setLayout(new java.awt.BorderLayout());

        Filtros.setLayout(new java.awt.GridBagLayout());

        cb_sucursal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        Filtros.add(cb_sucursal, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText("Filtrar por datos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        Filtros.add(jLabel3, gridBagConstraints);

        cb_productos2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        Filtros.add(cb_productos2, new java.awt.GridBagConstraints());

        cb_rangoFechas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        Filtros.add(cb_rangoFechas, new java.awt.GridBagConstraints());

        btn_exportar.setText("Exportar");
        Filtros.add(btn_exportar, new java.awt.GridBagConstraints());

        panelSuperiorNorte.add(Filtros, java.awt.BorderLayout.WEST);

        panelKpis.setLayout(new java.awt.GridLayout(1, 0, 10, 10));

        panel_TotalenSoles.setBackground(new java.awt.Color(51, 153, 0));
        panel_TotalenSoles.setLayout(new java.awt.BorderLayout());

        lbl_rentabilidad7.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad7.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_rentabilidad7.setText("0.00");
        lbl_rentabilidad7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panel_TotalenSoles.add(lbl_rentabilidad7, java.awt.BorderLayout.CENTER);

        lbl_totalSoles.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_totalSoles.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalSoles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_totalSoles.setText("TOTAL EN SOLES");
        panel_TotalenSoles.add(lbl_totalSoles, java.awt.BorderLayout.PAGE_START);

        panelKpis.add(panel_TotalenSoles);

        panel_TotaldeVentas.setBackground(new java.awt.Color(255, 153, 0));
        panel_TotaldeVentas.setLayout(new java.awt.BorderLayout());

        lbl_rentabilidad3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_rentabilidad3.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_rentabilidad3.setText("TOTAL DE VENTAS");
        lbl_rentabilidad3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panel_TotaldeVentas.add(lbl_rentabilidad3, java.awt.BorderLayout.PAGE_START);

        lbl_totalVentas.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_totalVentas.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_totalVentas.setText("0");
        lbl_totalVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panel_TotaldeVentas.add(lbl_totalVentas, java.awt.BorderLayout.CENTER);

        panelKpis.add(panel_TotaldeVentas);

        panelProductosVendidos.setBackground(new java.awt.Color(0, 102, 204));
        panelProductosVendidos.setLayout(new java.awt.BorderLayout());

        lbl_rentabilidad2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_rentabilidad2.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_rentabilidad2.setText("PRODUCTOS VENDIDOS");
        lbl_rentabilidad2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panelProductosVendidos.add(lbl_rentabilidad2, java.awt.BorderLayout.PAGE_START);

        lbl_productosVendidos.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_productosVendidos.setForeground(new java.awt.Color(0, 0, 0));
        lbl_productosVendidos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_productosVendidos.setText("0");
        panelProductosVendidos.add(lbl_productosVendidos, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelProductosVendidos);

        panelMedioDePagoUsado.setBackground(new java.awt.Color(105, 102, 255));
        panelMedioDePagoUsado.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MEDIO DE PAGO MAS USADO");
        panelMedioDePagoUsado.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        lbl_medioPagoMasUsado.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_medioPagoMasUsado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_medioPagoMasUsado.setText("jLabel5");
        panelMedioDePagoUsado.add(lbl_medioPagoMasUsado, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelMedioDePagoUsado);

        panelSuperiorNorte.add(panelKpis, java.awt.BorderLayout.CENTER);

        panel_Ventas.add(panelSuperiorNorte, java.awt.BorderLayout.NORTH);

        panelCentro3.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        grafico_ventasdiarias3.setBackground(new java.awt.Color(255, 255, 255));
        grafico_ventasdiarias3.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout grafico_ventasdiarias3Layout = new javax.swing.GroupLayout(grafico_ventasdiarias3);
        grafico_ventasdiarias3.setLayout(grafico_ventasdiarias3Layout);
        grafico_ventasdiarias3Layout.setHorizontalGroup(
            grafico_ventasdiarias3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );
        grafico_ventasdiarias3Layout.setVerticalGroup(
            grafico_ventasdiarias3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelCentro3.add(grafico_ventasdiarias3);

        grafico_ventasXProducto.setBackground(new java.awt.Color(255, 255, 255));
        grafico_ventasXProducto.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout grafico_ventasXProductoLayout = new javax.swing.GroupLayout(grafico_ventasXProducto);
        grafico_ventasXProducto.setLayout(grafico_ventasXProductoLayout);
        grafico_ventasXProductoLayout.setHorizontalGroup(
            grafico_ventasXProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );
        grafico_ventasXProductoLayout.setVerticalGroup(
            grafico_ventasXProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelCentro3.add(grafico_ventasXProducto);

        grafico_ventasMensuales.setBackground(new java.awt.Color(255, 255, 255));
        grafico_ventasMensuales.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout grafico_ventasMensualesLayout = new javax.swing.GroupLayout(grafico_ventasMensuales);
        grafico_ventasMensuales.setLayout(grafico_ventasMensualesLayout);
        grafico_ventasMensualesLayout.setHorizontalGroup(
            grafico_ventasMensualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );
        grafico_ventasMensualesLayout.setVerticalGroup(
            grafico_ventasMensualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelCentro3.add(grafico_ventasMensuales);

        grafico_metodosPago.setBackground(new java.awt.Color(255, 255, 255));
        grafico_metodosPago.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout grafico_metodosPagoLayout = new javax.swing.GroupLayout(grafico_metodosPago);
        grafico_metodosPago.setLayout(grafico_metodosPagoLayout);
        grafico_metodosPagoLayout.setHorizontalGroup(
            grafico_metodosPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );
        grafico_metodosPagoLayout.setVerticalGroup(
            grafico_metodosPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelCentro3.add(grafico_metodosPago);

        panel_Ventas.add(panelCentro3, java.awt.BorderLayout.CENTER);

        Tabbed_Tesoreria.addTab("VENTAS", panel_Ventas);

        cb_sucursal1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_sucursal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_sucursal1ActionPerformed(evt);
            }
        });

        cb_productos1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cb_rangoFechas1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btn_exportar1.setText("Exportar");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTable4);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setText("Filtrar por datos:");

        javax.swing.GroupLayout panel_COMPRASLayout = new javax.swing.GroupLayout(panel_COMPRAS);
        panel_COMPRAS.setLayout(panel_COMPRASLayout);
        panel_COMPRASLayout.setHorizontalGroup(
            panel_COMPRASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_COMPRASLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panel_COMPRASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(panel_COMPRASLayout.createSequentialGroup()
                        .addComponent(cb_sucursal1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cb_productos1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cb_rangoFechas1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(btn_exportar1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 969, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(115, Short.MAX_VALUE))
        );
        panel_COMPRASLayout.setVerticalGroup(
            panel_COMPRASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_COMPRASLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_COMPRASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_sucursal1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_productos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_rangoFechas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_exportar1))
                .addGap(56, 56, 56)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(129, Short.MAX_VALUE))
        );

        Tabbed_Tesoreria.addTab("COMPRAS", panel_COMPRAS);

        btn_gananciaNeta.setText("GANANCCIA NETA");

        btn_rentabilidad.setText("RENTABILIDAD");

        cb_tipoRanking.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("RANKING POR :");

        jPanel16.setBackground(new java.awt.Color(204, 255, 204));

        lbl_totalVentas1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_totalVentas1.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas1.setText("0.00");

        lbl_totalVentas5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas5.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas5.setText("Ventas totales");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas1)
                .addGap(80, 80, 80))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(lbl_totalVentas5)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_totalVentas1)
                .addGap(18, 18, 18))
        );

        jPanel17.setBackground(new java.awt.Color(204, 204, 204));

        lbl_totalVentas2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_totalVentas2.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas2.setText("SUCURSAL A");

        lbl_totalVentas4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas4.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas4.setText("Tienda top");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_totalVentas4)
                    .addComponent(lbl_totalVentas2))
                .addGap(30, 30, 30))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas4)
                .addGap(18, 18, 18)
                .addComponent(lbl_totalVentas2)
                .addGap(24, 24, 24))
        );

        jPanel18.setBackground(new java.awt.Color(153, 204, 255));

        lbl_totalVentas3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas3.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas3.setText("Promedio ventas");

        lbl_promedioVentas.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_promedioVentas.setForeground(new java.awt.Color(0, 0, 0));
        lbl_promedioVentas.setText("SUCURSAL A");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbl_promedioVentas))
                    .addComponent(lbl_totalVentas3))
                .addGap(34, 34, 34))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lbl_totalVentas3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_promedioVentas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_rankingXVentas.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panel_rankingXVentasLayout = new javax.swing.GroupLayout(panel_rankingXVentas);
        panel_rankingXVentas.setLayout(panel_rankingXVentasLayout);
        panel_rankingXVentasLayout.setHorizontalGroup(
            panel_rankingXVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 351, Short.MAX_VALUE)
        );
        panel_rankingXVentasLayout.setVerticalGroup(
            panel_rankingXVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
        );

        jTable5.setBackground(new java.awt.Color(255, 255, 255));
        jTable5.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_tipoRanking, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_gananciaNeta, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_rentabilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(51, 51, 51)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5))
                        .addGap(39, 39, 39)
                        .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(151, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cb_tipoRanking)
                        .addComponent(jLabel6))
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_rentabilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_gananciaNeta, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_RankingLayout = new javax.swing.GroupLayout(panel_Ranking);
        panel_Ranking.setLayout(panel_RankingLayout);
        panel_RankingLayout.setHorizontalGroup(
            panel_RankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_RankingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_RankingLayout.setVerticalGroup(
            panel_RankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_RankingLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Tabbed_Estadisticas.addTab("RANKING", panel_Ranking);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTable3);

        ganancia_bruta2.setBackground(new java.awt.Color(54, 59, 105));

        lbl_gananciaBruta2.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_gananciaBruta2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_gananciaBruta2.setText("0.00");
        lbl_gananciaBruta2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout ganancia_bruta2Layout = new javax.swing.GroupLayout(ganancia_bruta2);
        ganancia_bruta2.setLayout(ganancia_bruta2Layout);
        ganancia_bruta2Layout.setHorizontalGroup(
            ganancia_bruta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ganancia_bruta2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_gananciaBruta2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );
        ganancia_bruta2Layout.setVerticalGroup(
            ganancia_bruta2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ganancia_bruta2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl_gananciaBruta2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        costos_totales.setBackground(new java.awt.Color(120, 51, 44));

        lbl_costos.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_costos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_costos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_costos.setText("0.00");
        lbl_costos.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout costos_totalesLayout = new javax.swing.GroupLayout(costos_totales);
        costos_totales.setLayout(costos_totalesLayout);
        costos_totalesLayout.setHorizontalGroup(
            costos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(costos_totalesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl_costos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        costos_totalesLayout.setVerticalGroup(
            costos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(costos_totalesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbl_costos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        Ingresos_totales.setBackground(new java.awt.Color(23, 87, 55));

        lbl_totalIngresos.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_totalIngresos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos.setText("0.00");
        lbl_totalIngresos.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout Ingresos_totalesLayout = new javax.swing.GroupLayout(Ingresos_totales);
        Ingresos_totales.setLayout(Ingresos_totalesLayout);
        Ingresos_totalesLayout.setHorizontalGroup(
            Ingresos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ingresos_totalesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Ingresos_totalesLayout.setVerticalGroup(
            Ingresos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Ingresos_totalesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setText("Filtrar por datos:");

        filtro_sucursal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filtro_sucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtro_sucursalActionPerformed(evt);
            }
        });

        lbl_rentabilidad.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad.setText("RENTABILIDAD");

        ganancia_neta.setBackground(new java.awt.Color(0, 153, 51));

        lbl_gananciaNeta.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_gananciaNeta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_gananciaNeta.setText("0.00");
        lbl_gananciaNeta.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout ganancia_netaLayout = new javax.swing.GroupLayout(ganancia_neta);
        ganancia_neta.setLayout(ganancia_netaLayout);
        ganancia_netaLayout.setHorizontalGroup(
            ganancia_netaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ganancia_netaLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lbl_gananciaNeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ganancia_netaLayout.setVerticalGroup(
            ganancia_netaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ganancia_netaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbl_gananciaNeta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        panel_estadisticas.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panel_estadisticasLayout = new javax.swing.GroupLayout(panel_estadisticas);
        panel_estadisticas.setLayout(panel_estadisticasLayout);
        panel_estadisticasLayout.setHorizontalGroup(
            panel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 285, Short.MAX_VALUE)
        );
        panel_estadisticasLayout.setVerticalGroup(
            panel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        cb_rangoss.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_rentabilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(479, 479, 479))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_rangoss, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(135, 135, 135)
                        .addComponent(filtro_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(Ingresos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(costos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(ganancia_bruta2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panel_estadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ganancia_neta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(420, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl_rentabilidad)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cb_rangoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(filtro_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Ingresos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(costos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ganancia_bruta2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ganancia_neta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_estadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_RentabilidadLayout = new javax.swing.GroupLayout(panel_Rentabilidad);
        panel_Rentabilidad.setLayout(panel_RentabilidadLayout);
        panel_RentabilidadLayout.setHorizontalGroup(
            panel_RentabilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_RentabilidadLayout.setVerticalGroup(
            panel_RentabilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_RentabilidadLayout.createSequentialGroup()
                .addGap(0, 104, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Tabbed_Estadisticas.addTab("RENTABILIDAD", panel_Rentabilidad);

        javax.swing.GroupLayout panel_EstadisticasLayout = new javax.swing.GroupLayout(panel_Estadisticas);
        panel_Estadisticas.setLayout(panel_EstadisticasLayout);
        panel_EstadisticasLayout.setHorizontalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, 1209, Short.MAX_VALUE)
        );
        panel_EstadisticasLayout.setVerticalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas)
        );

        Tabbed_Tesoreria.addTab("ESTADISTICAS", panel_Estadisticas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Tesoreria, javax.swing.GroupLayout.PREFERRED_SIZE, 1114, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Tesoreria)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cb_sucursal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursal1ActionPerformed

    private void filtro_sucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtro_sucursalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtro_sucursalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Filtros;
    private javax.swing.JPanel Ingresos_totales;
    private javax.swing.JTabbedPane Tabbed_Estadisticas;
    private javax.swing.JTabbedPane Tabbed_Tesoreria;
    private javax.swing.JButton btn_exportar;
    private javax.swing.JButton btn_exportar1;
    private javax.swing.JButton btn_gananciaNeta;
    private javax.swing.JButton btn_rentabilidad;
    private javax.swing.JComboBox<String> cb_productos1;
    private javax.swing.JComboBox<String> cb_productos2;
    private javax.swing.JComboBox<String> cb_rangoFechas;
    private javax.swing.JComboBox<String> cb_rangoFechas1;
    private javax.swing.JComboBox<String> cb_rangoss;
    private javax.swing.JComboBox<String> cb_sucursal;
    private javax.swing.JComboBox<String> cb_sucursal1;
    private javax.swing.JComboBox<String> cb_tipoRanking;
    private javax.swing.JPanel costos_totales;
    private javax.swing.JComboBox<String> filtro_sucursal;
    private javax.swing.JPanel ganancia_bruta2;
    private javax.swing.JPanel ganancia_neta;
    private javax.swing.JPanel grafico_metodosPago;
    private javax.swing.JPanel grafico_ventasMensuales;
    private javax.swing.JPanel grafico_ventasXProducto;
    private javax.swing.JPanel grafico_ventasdiarias3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JLabel lbl_costos;
    private javax.swing.JLabel lbl_gananciaBruta2;
    private javax.swing.JLabel lbl_gananciaNeta;
    private javax.swing.JLabel lbl_medioPagoMasUsado;
    private javax.swing.JLabel lbl_productosVendidos;
    private javax.swing.JLabel lbl_promedioVentas;
    private javax.swing.JLabel lbl_rentabilidad;
    private javax.swing.JLabel lbl_rentabilidad2;
    private javax.swing.JLabel lbl_rentabilidad3;
    private javax.swing.JLabel lbl_rentabilidad7;
    private javax.swing.JLabel lbl_total2;
    private javax.swing.JLabel lbl_totalIngresos;
    private javax.swing.JLabel lbl_totalSoles;
    private javax.swing.JLabel lbl_totalVentas;
    private javax.swing.JLabel lbl_totalVentas1;
    private javax.swing.JLabel lbl_totalVentas2;
    private javax.swing.JLabel lbl_totalVentas3;
    private javax.swing.JLabel lbl_totalVentas4;
    private javax.swing.JLabel lbl_totalVentas5;
    private javax.swing.JPanel panelCentro3;
    private javax.swing.JPanel panelKpis;
    private javax.swing.JPanel panelMedioDePagoUsado;
    private javax.swing.JPanel panelProductosVendidos;
    private javax.swing.JPanel panelSuperiorNorte;
    private javax.swing.JPanel panel_COMPRAS;
    private javax.swing.JPanel panel_Estadisticas;
    private javax.swing.JPanel panel_Ranking;
    private javax.swing.JPanel panel_Rentabilidad;
    private javax.swing.JPanel panel_TotaldeVentas;
    private javax.swing.JPanel panel_TotalenSoles;
    private javax.swing.JPanel panel_Ventas;
    private javax.swing.JPanel panel_estadisticas;
    private javax.swing.JPanel panel_rankingXVentas;
    // End of variables declaration//GEN-END:variables
}
