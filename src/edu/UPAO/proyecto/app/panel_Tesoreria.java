package edu.UPAO.proyecto.app;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Util.GeneradorExcelRk;
import edu.UPAO.proyecto.Util.GeneradorPDFRk;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class panel_Tesoreria extends javax.swing.JPanel {

    private Connection conn;
    private JFreeChart chartVentasPorProducto;

    private LocalDate fechaDesde = null;
    private LocalDate fechaHasta = null;

    public panel_Tesoreria() {
        initComponents();
        envolverEnScroll();
        inicializar();
    }

    private void inicializar() {
        try {
            conn = new Conexion().establecerConexion();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error de conexión a BD: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        cargarComboTiendas();
        cargarComboProductos();

        // Listeners de filtros
        cmbTienda.addActionListener(e -> actualizarDashboard());
        cmbProducto.addActionListener(e -> actualizarDashboard());
        btnFecha.addActionListener(e -> seleccionarRangoFechas());
        btnExportar.addActionListener(e -> exportarResumen());

        // Carga inicial (todo)
        actualizarDashboard();
    }

    private void envolverEnScroll() {
        // Creamos el scroll que envuelve al tabbed pane
        JScrollPane scroll = new JScrollPane(Tabbed_Tesoreria);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // velocidad del scroll

        // Este panel (panel_Tesoreria) ya tiene BorderLayout (lo veo en el Navigator)
        // Quitamos el Tabbed_Tesoreria directo y ponemos el ScrollPane en su lugar
        this.remove(Tabbed_Tesoreria);
        this.add(scroll, BorderLayout.CENTER);

        // Por si NetBeans no refresca solo
        this.revalidate();
        this.repaint();
    }
    // =====================  CARGA DE COMBOS  =====================

    private void cargarComboTiendas() {
        cmbTienda.removeAllItems();
        cmbTienda.addItem("Todas las tiendas");

        String sql = "SELECT id_sucursal, nombre_sucursal FROM sucursal ORDER BY nombre_sucursal";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_sucursal");
                String nombre = rs.getString("nombre_sucursal");
                cmbTienda.addItem(id + " - " + nombre);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarComboProductos() {
        cmbProducto.removeAllItems();
        cmbProducto.addItem("Todos los productos");

        String sql = "SELECT id_producto, nombre FROM producto ORDER BY nombre";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                cmbProducto.addItem(id + " - " + nombre);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Integer getIdSucursalSeleccionada() {
        int idx = cmbTienda.getSelectedIndex();
        if (idx <= 0) {
            return null; // "Todas las tiendas"
        }
        String item = (String) cmbTienda.getSelectedItem();
        try {
            return Integer.parseInt(item.split(" - ")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIdProductoSeleccionado() {
        int idx = cmbProducto.getSelectedIndex();
        if (idx <= 0) {
            return null; // "Todos"
        }
        String item = (String) cmbProducto.getSelectedItem();
        try {
            return Integer.parseInt(item.split(" - ")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================  FILTRO DE FECHAS  =====================
    private void seleccionarRangoFechas() {
        String[] opciones = {"Hoy", "Últimos 7 días", "Últimos 30 días", "Todo"};
        int op = JOptionPane.showOptionDialog(this,
                "Selecciona el rango de fechas:",
                "Filtro de fecha",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        LocalDate hoy = LocalDate.now();

        switch (op) {
            case 0: // Hoy
                fechaDesde = hoy;
                fechaHasta = hoy;
                break;
            case 1: // 7 días
                fechaDesde = hoy.minusDays(6);
                fechaHasta = hoy;
                break;
            case 2: // 30 días
                fechaDesde = hoy.minusDays(29);
                fechaHasta = hoy;
                break;
            default: // Todo
                fechaDesde = null;
                fechaHasta = null;
        }

        actualizarDashboard();
    }

    // Construye la parte dinámica del WHERE
    private String construirWhereVentas(Integer idSucursal,
            Integer idProducto,
            List<Object> params,
            boolean usarJoinProducto) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");

        if (idSucursal != null) {
            where.append(" AND v.id_sucursal = ? ");
            params.add(idSucursal);
        }

        if (idProducto != null && usarJoinProducto) {
            where.append(" AND dv.id_producto = ? ");
            params.add(idProducto);
        }

        if (fechaDesde != null) {
            where.append(" AND v.fecha_hora >= ? ");
            params.add(Timestamp.valueOf(fechaDesde.atStartOfDay()));
        }
        if (fechaHasta != null) {
            // < día siguiente
            where.append(" AND v.fecha_hora < ? ");
            params.add(Timestamp.valueOf(fechaHasta.plusDays(1).atStartOfDay()));
        }

        return where.toString();
    }

    // =====================  ACTUALIZAR TODO  =====================
    private void actualizarDashboard() {
        if (conn == null) {
            return;
        }

        Integer idSucursal = getIdSucursalSeleccionada();
        Integer idProducto = getIdProductoSeleccionado();

        actualizarKPIs(idSucursal, idProducto);
        cargarGraficoVentasDiarias(idSucursal, idProducto);
        cargarGraficoVentasMensuales(idSucursal, idProducto);
        cargarGraficoVentasPorProducto(idSucursal, idProducto);
        cargarGraficoMediosPago(idSucursal, idProducto);
    }

    // =====================  KPIs  =====================
    private void actualizarKPIs(Integer idSucursal, Integer idProducto) {
        // Ventas totales
        BigDecimal totalVentas = obtenerTotalVentas(idSucursal, idProducto);
        long numeroVentas = obtenerNumeroVentas(idSucursal, idProducto);
        long productosVendidos = obtenerTotalProductosVendidos(idSucursal, idProducto);
        String medioPagoMasUsado = obtenerMedioPagoMasUsado(idSucursal, idProducto);

        lblValorVentasTotales.setText("S/" + (totalVentas != null ? totalVentas : BigDecimal.ZERO));
        lblValorNumeroVentas.setText(String.valueOf(numeroVentas));
        lblValorProductosVendidos.setText(String.valueOf(productosVendidos));
        lblValorMedioPago.setText(medioPagoMasUsado != null ? medioPagoMasUsado : "—");
    }

    private BigDecimal obtenerTotalVentas(Integer idSucursal, Integer idProducto) {
        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(v.total) AS total "
                + "FROM venta v ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private long obtenerNumeroVentas(Integer idSucursal, Integer idProducto) {
        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS cant FROM venta v ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("cant");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private long obtenerTotalProductosVendidos(Integer idSucursal, Integer idProducto) {
        List<Object> params = new ArrayList<>();
        boolean joinProducto = true; // siempre necesitamos detalle_venta
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(dv.cantidad) AS total_prod "
                + "FROM venta v "
                + "JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("total_prod");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private String obtenerMedioPagoMasUsado(Integer idSucursal, Integer idProducto) {
        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);

        StringBuilder sql = new StringBuilder(
                "SELECT mp.nombre, COUNT(*) AS cant "
                + "FROM venta v "
                + "JOIN metodo_pago mp ON mp.id_metodo_pago = v.id_metodo_pago ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY mp.nombre ORDER BY cant DESC LIMIT 1 ");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    // =====================  GRÁFICOS  =====================
    private void cargarGraficoVentasDiarias(Integer idSucursal, Integer idProducto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);

        StringBuilder sql = new StringBuilder(
                "SELECT DATE(v.fecha_hora) AS dia, SUM(v.total) AS total "
                + "FROM venta v ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY DATE(v.fecha_hora) ORDER BY DATE(v.fecha_hora)");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date fecha = rs.getDate("dia");
                BigDecimal total = rs.getBigDecimal("total");
                String etiqueta = fecha.toLocalDate().toString();
                dataset.addValue(total, "Ventas", etiqueta);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createAreaChart(
                "Ventas diarias",
                "Día",
                "Total (S/)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        mostrarChartEnPanel(panel_VentasDiarias, chart);
    }

    private void cargarGraficoVentasMensuales(Integer idSucursal, Integer idProducto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);

        StringBuilder sql = new StringBuilder(
                "SELECT DATE_FORMAT(v.fecha_hora, '%Y-%m') AS mes, SUM(v.total) AS total "
                + "FROM venta v ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY DATE_FORMAT(v.fecha_hora, '%Y-%m') ORDER BY mes");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String mes = rs.getString("mes");
                BigDecimal total = rs.getBigDecimal("total");
                dataset.addValue(total, "Ventas", mes);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Ventas mensuales",
                "Mes",
                "Total (S/)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        mostrarChartEnPanel(panel_VentasMensuales, chart);
    }

    private void cargarGraficoVentasPorProducto(Integer idSucursal, Integer idProducto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object> params = new ArrayList<>();
        boolean joinProducto = true; // siempre usamos detalle_venta

        StringBuilder sql = new StringBuilder(
                "SELECT p.nombre, SUM(dv.cantidad * dv.precio_unitario) AS total "
                + "FROM venta v "
                + "JOIN detalle_venta dv ON dv.id_venta = v.id_venta "
                + "JOIN producto p ON p.id_producto = dv.id_producto ");

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY p.nombre ORDER BY total DESC LIMIT 5");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                BigDecimal total = rs.getBigDecimal("total");
                dataset.addValue(total, "Producto", nombre);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

       chartVentasPorProducto = ChartFactory.createBarChart(
            "Ventas por producto (Top 5)",
            "Producto",
            "Total (S/)",
            dataset,
            PlotOrientation.HORIZONTAL,
            false, true, false);

    mostrarChartEnPanel(panel_VentasPorProducto, chartVentasPorProducto);
    }

    private void cargarGraficoMediosPago(Integer idSucursal, Integer idProducto) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);

        StringBuilder sql = new StringBuilder(
                "SELECT mp.nombre, COUNT(*) AS cant "
                + "FROM venta v "
                + "JOIN metodo_pago mp ON mp.id_metodo_pago = v.id_metodo_pago ");

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY mp.nombre");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                long cant = rs.getLong("cant");
                dataset.setValue(nombre, cant);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Medios de pago",
                dataset,
                false, true, false);

        mostrarChartEnPanel(panel_MediosPago, chart);
    }

    // =====================  UTILIDADES  =====================
    private PreparedStatement preparar(String sql, List<Object> params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof LocalDate) {
                ps.setDate(i + 1, Date.valueOf((LocalDate) p));
            } else {
                ps.setObject(i + 1, p);
            }
        }
        return ps;
    }

    private void mostrarChartEnPanel(JPanel contenedor, JFreeChart chart) {
        contenedor.removeAll();
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        contenedor.setLayout(new BorderLayout());
        contenedor.add(cp, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    private JTable construirTablaResumenDashboard() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"TOP", "LOCALES", "VENTAS", "TRANSACCIONES"}, 0
        );
        JTable tabla = new JTable(modelo);

        if (conn == null) {
            return tabla;
        }

        // Filtros actuales
        Integer idSucursal = getIdSucursalSeleccionada();
        Integer idProducto = getIdProductoSeleccionado(); // si quieres que sea general de TODO, pon null aquí

        // ====== 1) FILA DE RESUMEN: KPIs ======
        BigDecimal totalVentas = obtenerTotalVentas(idSucursal, idProducto);
        long numeroVentas = obtenerNumeroVentas(idSucursal, idProducto);
        long productosVendidos = obtenerTotalProductosVendidos(idSucursal, idProducto);
        String medioPagoMasUsado = obtenerMedioPagoMasUsado(idSucursal, idProducto);

        // OJO: usamos las columnas así:
        // TOP      → lo dejamos vacío
        // LOCALES  → nombre de la métrica
        // VENTAS   → valor de la métrica
        // TRANSACCIONES → lo dejamos vacío (o info extra si quieres)
        modelo.addRow(new Object[]{"", "VENTAS TOTALES (S/)", totalVentas, ""});
        modelo.addRow(new Object[]{"", "NÚMERO DE VENTAS", numeroVentas, ""});
        modelo.addRow(new Object[]{"", "PRODUCTOS VENDIDOS", productosVendidos, ""});
        modelo.addRow(new Object[]{"", "MEDIO DE PAGO MÁS USADO", medioPagoMasUsado, ""});

        // ====== 2) BLOQUE: TOP 5 PRODUCTOS ======
        List<Object> params = new ArrayList<>();
        boolean joinProducto = true; // siempre usamos detalle_venta

        StringBuilder sql = new StringBuilder(
                "SELECT p.nombre AS producto, "
                + "       SUM(dv.cantidad * dv.precio_unitario) AS ventas, "
                + "       COUNT(DISTINCT v.id_venta) AS transacciones "
                + "FROM venta v "
                + "JOIN detalle_venta dv ON dv.id_venta = v.id_venta "
                + "JOIN producto p ON p.id_producto = dv.id_producto ");

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));
        sql.append(" GROUP BY p.nombre ORDER BY ventas DESC LIMIT 5");

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {

            int top = 1;
            while (rs.next()) {
                String producto = rs.getString("producto");
                BigDecimal ventas = rs.getBigDecimal("ventas");
                long trans = rs.getLong("transacciones");

                modelo.addRow(new Object[]{
                    top, // TOP
                    producto, // LOCALES
                    ventas, // VENTAS
                    trans // TRANSACCIONES
                });
                top++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tabla;
    }

    private void exportarResumen() {
        try {
            JTable tablaRanking = construirTablaResumenDashboard();

            if (tablaRanking.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay datos para exportar con los filtros actuales.",
                        "Sin datos",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

         String mes = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm")
                 .format(new java.util.Date());

            String[] opciones = {"PDF", "Excel", "Cancelar"};
            int op = JOptionPane.showOptionDialog(this,
                    "¿Cómo deseas exportar el reporte general?",
                    "Exportar",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (op == 0) {
                // PDF
                if (chartVentasPorProducto == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontró el gráfico de productos para el PDF.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                GeneradorPDFRk genPdf = new GeneradorPDFRk();
                genPdf.generarReporte(tablaRanking, chartVentasPorProducto, mes);

            } else if (op == 1) {
                // Excel
                GeneradorExcelRk genExcel = new GeneradorExcelRk();
                genExcel.generarExcel(tablaRanking, mes);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbl_total2 = new javax.swing.JLabel();
        Tabbed_Tesoreria = new javax.swing.JTabbedPane();
        panel_Ventas = new javax.swing.JPanel();
        panel_Filtros = new javax.swing.JPanel();
        lblFiltrarPor = new javax.swing.JLabel();
        cmbTienda = new javax.swing.JComboBox<>();
        cmbProducto = new javax.swing.JComboBox<>();
        btnFecha = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        panel_KPIs = new javax.swing.JPanel();
        panel_KpiVentasTotales = new javax.swing.JPanel();
        lblTituloVentasTotales = new javax.swing.JLabel();
        lblValorVentasTotales = new javax.swing.JLabel();
        panel_KpiNumeroVentas = new javax.swing.JPanel();
        lblTituloNumeroVentas = new javax.swing.JLabel();
        lblValorNumeroVentas = new javax.swing.JLabel();
        panel_KpiProductosVendidos = new javax.swing.JPanel();
        lblTituloProductosVendidos = new javax.swing.JLabel();
        lblValorProductosVendidos = new javax.swing.JLabel();
        panel_KpiMedioPago = new javax.swing.JPanel();
        lblTituloMedioPago = new javax.swing.JLabel();
        lblValorMedioPago = new javax.swing.JLabel();
        panel_Graficos = new javax.swing.JPanel();
        panel_VentasDiarias = new javax.swing.JPanel();
        lblVentasDiarias = new javax.swing.JLabel();
        panel_VentasMensuales = new javax.swing.JPanel();
        lblVentasMensuales = new javax.swing.JLabel();
        panel_VentasPorProducto = new javax.swing.JPanel();
        lblVentasPorProducto = new javax.swing.JLabel();
        panel_MediosPago = new javax.swing.JPanel();
        lblMediosPago = new javax.swing.JLabel();
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
        jPanelVentasTotales = new javax.swing.JPanel();
        lbl_TotalVenta = new javax.swing.JLabel();
        lbl_totalVentas5 = new javax.swing.JLabel();
        jPanelTiendaTOP = new javax.swing.JPanel();
        lbl_TiendaTOP = new javax.swing.JLabel();
        lbl_totalVentas4 = new javax.swing.JLabel();
        jPanelPromedioVentas = new javax.swing.JPanel();
        lbl_totalVentas3 = new javax.swing.JLabel();
        lbl_promedioVentas = new javax.swing.JLabel();
        panel_rankingXVentas = new javax.swing.JPanel();
        cb_tipoRanking = new javax.swing.JComboBox<>();
        GraficoBarras = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TablaRanking = new javax.swing.JTable();
        FiltrarporFecha = new javax.swing.JLabel();
        FiltrarporMes = new javax.swing.JComboBox<>();
        jSplitPane1 = new javax.swing.JSplitPane();
        PDF = new javax.swing.JButton();
        Excel = new javax.swing.JButton();
        Exportar = new javax.swing.JLabel();
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
        setLayout(new java.awt.BorderLayout());

        Tabbed_Tesoreria.setPreferredSize(new java.awt.Dimension(0, 0));

        panel_Ventas.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel_Ventas.setPreferredSize(new java.awt.Dimension(0, 0));
        panel_Ventas.setLayout(new java.awt.BorderLayout());

        panel_Filtros.setBackground(new java.awt.Color(255, 255, 255));
        panel_Filtros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        lblFiltrarPor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFiltrarPor.setForeground(new java.awt.Color(0, 0, 0));
        lblFiltrarPor.setText("Filtrar por:");
        panel_Filtros.add(lblFiltrarPor);

        cmbTienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTienda.setPreferredSize(new java.awt.Dimension(140, 30));
        panel_Filtros.add(cmbTienda);

        cmbProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProducto.setPreferredSize(new java.awt.Dimension(120, 32));
        panel_Filtros.add(cmbProducto);

        btnFecha.setText("FECHA");
        btnFecha.setPreferredSize(new java.awt.Dimension(120, 30));
        btnFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechaActionPerformed(evt);
            }
        });
        panel_Filtros.add(btnFecha);

        btnExportar.setText("EXPORTAR");
        btnExportar.setPreferredSize(new java.awt.Dimension(120, 32));
        panel_Filtros.add(btnExportar);

        panel_Ventas.add(panel_Filtros, java.awt.BorderLayout.NORTH);

        panel_KPIs.setBackground(new java.awt.Color(204, 204, 204));
        panel_KPIs.setPreferredSize(new java.awt.Dimension(260, 607));
        panel_KPIs.setLayout(new java.awt.GridLayout(4, 1, 0, 10));

        panel_KpiVentasTotales.setBackground(new java.awt.Color(76, 175, 80));
        panel_KpiVentasTotales.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel_KpiVentasTotales.setLayout(new java.awt.BorderLayout());

        lblTituloVentasTotales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloVentasTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloVentasTotales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloVentasTotales.setText("VENTAS TOTALES");
        panel_KpiVentasTotales.add(lblTituloVentasTotales, java.awt.BorderLayout.PAGE_START);

        lblValorVentasTotales.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblValorVentasTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblValorVentasTotales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblValorVentasTotales.setText("jLabel3");
        panel_KpiVentasTotales.add(lblValorVentasTotales, java.awt.BorderLayout.CENTER);

        panel_KPIs.add(panel_KpiVentasTotales);

        panel_KpiNumeroVentas.setBackground(new java.awt.Color(255, 152, 0));
        panel_KpiNumeroVentas.setLayout(new java.awt.BorderLayout());

        lblTituloNumeroVentas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloNumeroVentas.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloNumeroVentas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloNumeroVentas.setText("NUMERO DE VENTAS");
        panel_KpiNumeroVentas.add(lblTituloNumeroVentas, java.awt.BorderLayout.PAGE_START);

        lblValorNumeroVentas.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblValorNumeroVentas.setForeground(new java.awt.Color(255, 255, 255));
        lblValorNumeroVentas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblValorNumeroVentas.setText("jLabel8");
        panel_KpiNumeroVentas.add(lblValorNumeroVentas, java.awt.BorderLayout.CENTER);

        panel_KPIs.add(panel_KpiNumeroVentas);

        panel_KpiProductosVendidos.setBackground(new java.awt.Color(33, 150, 243));
        panel_KpiProductosVendidos.setLayout(new java.awt.BorderLayout());

        lblTituloProductosVendidos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloProductosVendidos.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloProductosVendidos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloProductosVendidos.setText("PRODUCTOS VENDIDOS");
        panel_KpiProductosVendidos.add(lblTituloProductosVendidos, java.awt.BorderLayout.PAGE_START);

        lblValorProductosVendidos.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblValorProductosVendidos.setForeground(new java.awt.Color(255, 255, 255));
        lblValorProductosVendidos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblValorProductosVendidos.setText("jLabel9");
        panel_KpiProductosVendidos.add(lblValorProductosVendidos, java.awt.BorderLayout.CENTER);

        panel_KPIs.add(panel_KpiProductosVendidos);

        panel_KpiMedioPago.setBackground(new java.awt.Color(156, 39, 176));
        panel_KpiMedioPago.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel_KpiMedioPago.setLayout(new java.awt.BorderLayout());

        lblTituloMedioPago.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloMedioPago.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloMedioPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloMedioPago.setText("MEDIO DE PAGO MAS USADO");
        panel_KpiMedioPago.add(lblTituloMedioPago, java.awt.BorderLayout.PAGE_START);

        lblValorMedioPago.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblValorMedioPago.setForeground(new java.awt.Color(255, 255, 255));
        lblValorMedioPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblValorMedioPago.setText("jLabel10");
        panel_KpiMedioPago.add(lblValorMedioPago, java.awt.BorderLayout.CENTER);

        panel_KPIs.add(panel_KpiMedioPago);

        panel_Ventas.add(panel_KPIs, java.awt.BorderLayout.EAST);

        panel_Graficos.setPreferredSize(new java.awt.Dimension(0, 0));
        panel_Graficos.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        panel_VentasDiarias.setBackground(new java.awt.Color(255, 255, 255));
        panel_VentasDiarias.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        panel_VentasDiarias.setLayout(new java.awt.BorderLayout());

        lblVentasDiarias.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasDiarias.setForeground(new java.awt.Color(0, 0, 0));
        lblVentasDiarias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasDiarias.setText("VENTAS DIARIAS");
        panel_VentasDiarias.add(lblVentasDiarias, java.awt.BorderLayout.PAGE_START);

        panel_Graficos.add(panel_VentasDiarias);

        panel_VentasMensuales.setBackground(new java.awt.Color(255, 255, 255));
        panel_VentasMensuales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        panel_VentasMensuales.setLayout(new java.awt.BorderLayout());

        lblVentasMensuales.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasMensuales.setForeground(new java.awt.Color(0, 0, 0));
        lblVentasMensuales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasMensuales.setText("VENTAS MENSUALES");
        panel_VentasMensuales.add(lblVentasMensuales, java.awt.BorderLayout.PAGE_START);

        panel_Graficos.add(panel_VentasMensuales);

        panel_VentasPorProducto.setBackground(new java.awt.Color(255, 255, 255));
        panel_VentasPorProducto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        panel_VentasPorProducto.setLayout(new java.awt.BorderLayout());

        lblVentasPorProducto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasPorProducto.setForeground(new java.awt.Color(0, 0, 0));
        lblVentasPorProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasPorProducto.setText("VENTAS POR PRODUCTO");
        panel_VentasPorProducto.add(lblVentasPorProducto, java.awt.BorderLayout.PAGE_START);

        panel_Graficos.add(panel_VentasPorProducto);

        panel_MediosPago.setBackground(new java.awt.Color(255, 255, 255));
        panel_MediosPago.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        panel_MediosPago.setLayout(new java.awt.BorderLayout());

        lblMediosPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMediosPago.setForeground(new java.awt.Color(0, 0, 0));
        lblMediosPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMediosPago.setText("MEDIOS DE PAGO");
        panel_MediosPago.add(lblMediosPago, java.awt.BorderLayout.PAGE_START);

        panel_Graficos.add(panel_MediosPago);

        panel_Ventas.add(panel_Graficos, java.awt.BorderLayout.CENTER);

        Tabbed_Tesoreria.addTab("tab3", panel_Ventas);

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
                .addContainerGap(171, Short.MAX_VALUE))
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
                .addContainerGap(403, Short.MAX_VALUE))
        );

        Tabbed_Tesoreria.addTab("COMPRAS", panel_COMPRAS);

        jPanelVentasTotales.setBackground(new java.awt.Color(204, 255, 204));

        lbl_TotalVenta.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_TotalVenta.setForeground(new java.awt.Color(0, 0, 0));
        lbl_TotalVenta.setText("0.00");

        lbl_totalVentas5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas5.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas5.setText("Ventas totales");

        javax.swing.GroupLayout jPanelVentasTotalesLayout = new javax.swing.GroupLayout(jPanelVentasTotales);
        jPanelVentasTotales.setLayout(jPanelVentasTotalesLayout);
        jPanelVentasTotalesLayout.setHorizontalGroup(
            jPanelVentasTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentasTotalesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_TotalVenta)
                .addGap(80, 80, 80))
            .addGroup(jPanelVentasTotalesLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(lbl_totalVentas5)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanelVentasTotalesLayout.setVerticalGroup(
            jPanelVentasTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentasTotalesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_TotalVenta)
                .addGap(18, 18, 18))
        );

        jPanelTiendaTOP.setBackground(new java.awt.Color(204, 204, 204));

        lbl_TiendaTOP.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_TiendaTOP.setForeground(new java.awt.Color(0, 0, 0));
        lbl_TiendaTOP.setText("SUCURSAL A");

        lbl_totalVentas4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas4.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas4.setText("Tienda TOP");

        javax.swing.GroupLayout jPanelTiendaTOPLayout = new javax.swing.GroupLayout(jPanelTiendaTOP);
        jPanelTiendaTOP.setLayout(jPanelTiendaTOPLayout);
        jPanelTiendaTOPLayout.setHorizontalGroup(
            jPanelTiendaTOPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTiendaTOPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas4)
                .addGap(30, 30, 30))
            .addGroup(jPanelTiendaTOPLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lbl_TiendaTOP)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanelTiendaTOPLayout.setVerticalGroup(
            jPanelTiendaTOPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTiendaTOPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas4)
                .addGap(18, 18, 18)
                .addComponent(lbl_TiendaTOP)
                .addGap(24, 24, 24))
        );

        jPanelPromedioVentas.setBackground(new java.awt.Color(153, 204, 255));

        lbl_totalVentas3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbl_totalVentas3.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas3.setText("Promedio ventas");

        lbl_promedioVentas.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_promedioVentas.setForeground(new java.awt.Color(0, 0, 0));
        lbl_promedioVentas.setText("SUCURSAL A");

        javax.swing.GroupLayout jPanelPromedioVentasLayout = new javax.swing.GroupLayout(jPanelPromedioVentas);
        jPanelPromedioVentas.setLayout(jPanelPromedioVentasLayout);
        jPanelPromedioVentasLayout.setHorizontalGroup(
            jPanelPromedioVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromedioVentasLayout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanelPromedioVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPromedioVentasLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbl_promedioVentas))
                    .addComponent(lbl_totalVentas3))
                .addGap(34, 34, 34))
        );
        jPanelPromedioVentasLayout.setVerticalGroup(
            jPanelPromedioVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPromedioVentasLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lbl_totalVentas3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_promedioVentas)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        panel_rankingXVentas.setBackground(new java.awt.Color(255, 255, 255));
        panel_rankingXVentas.setLayout(new java.awt.BorderLayout());

        cb_tipoRanking.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ventas", "Transacciones" }));
        cb_tipoRanking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_tipoRankingActionPerformed(evt);
            }
        });
        panel_rankingXVentas.add(cb_tipoRanking, java.awt.BorderLayout.PAGE_START);

        GraficoBarras.setPreferredSize(new java.awt.Dimension(200, 415));

        javax.swing.GroupLayout GraficoBarrasLayout = new javax.swing.GroupLayout(GraficoBarras);
        GraficoBarras.setLayout(GraficoBarrasLayout);
        GraficoBarrasLayout.setHorizontalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 443, Short.MAX_VALUE)
        );
        GraficoBarrasLayout.setVerticalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );

        panel_rankingXVentas.add(GraficoBarras, java.awt.BorderLayout.CENTER);

        TablaRanking.setBackground(new java.awt.Color(255, 255, 255));
        TablaRanking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "TOP", "LOCALES", "VENTAS", "TRANSACCIONES"
            }
        ));
        jScrollPane5.setViewportView(TablaRanking);

        FiltrarporFecha.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        FiltrarporFecha.setText("FILTRAR POR FECHA:");

        FiltrarporMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Anual", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        FiltrarporMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FiltrarporMesActionPerformed(evt);
            }
        });

        PDF.setText("PDF");
        PDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFActionPerformed(evt);
            }
        });
        jSplitPane1.setLeftComponent(PDF);

        Excel.setText("Excel");
        Excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExcelActionPerformed(evt);
            }
        });
        jSplitPane1.setRightComponent(Excel);

        Exportar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Exportar.setText("EXPORTAR:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jPanelVentasTotales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jPanelTiendaTOP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jPanelPromedioVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(67, 67, 67)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel15Layout.createSequentialGroup()
                                        .addComponent(FiltrarporFecha)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(FiltrarporMes, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(Exportar)))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(68, 68, 68))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelVentasTotales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(FiltrarporFecha)
                                    .addComponent(FiltrarporMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Exportar)
                                    .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanelPromedioVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addComponent(jPanelTiendaTOP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5)
                    .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(399, 399, 399))
        );

        javax.swing.GroupLayout panel_RankingLayout = new javax.swing.GroupLayout(panel_Ranking);
        panel_Ranking.setLayout(panel_RankingLayout);
        panel_RankingLayout.setHorizontalGroup(
            panel_RankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_RankingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
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
                .addContainerGap(381, Short.MAX_VALUE))
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
                .addGap(0, 378, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Tabbed_Estadisticas.addTab("RENTABILIDAD", panel_Rentabilidad);

        javax.swing.GroupLayout panel_EstadisticasLayout = new javax.swing.GroupLayout(panel_Estadisticas);
        panel_Estadisticas.setLayout(panel_EstadisticasLayout);
        panel_EstadisticasLayout.setHorizontalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas)
        );
        panel_EstadisticasLayout.setVerticalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas)
        );

        Tabbed_Tesoreria.addTab("ESTADISTICAS", panel_Estadisticas);

        add(Tabbed_Tesoreria, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cb_sucursal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursal1ActionPerformed

    private void filtro_sucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtro_sucursalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtro_sucursalActionPerformed

    private void cb_tipoRankingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_tipoRankingActionPerformed

    }//GEN-LAST:event_cb_tipoRankingActionPerformed

    private void FiltrarporMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltrarporMesActionPerformed

    }//GEN-LAST:event_FiltrarporMesActionPerformed

    private void PDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFActionPerformed

    }//GEN-LAST:event_PDFActionPerformed

    private void ExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExcelActionPerformed

    }//GEN-LAST:event_ExcelActionPerformed

    private void btnFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFechaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Excel;
    private javax.swing.JLabel Exportar;
    private javax.swing.JLabel FiltrarporFecha;
    private javax.swing.JComboBox<String> FiltrarporMes;
    private javax.swing.JPanel GraficoBarras;
    private javax.swing.JPanel Ingresos_totales;
    private javax.swing.JButton PDF;
    private javax.swing.JTabbedPane Tabbed_Estadisticas;
    private javax.swing.JTabbedPane Tabbed_Tesoreria;
    private javax.swing.JTable TablaRanking;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btn_exportar1;
    private javax.swing.JComboBox<String> cb_productos1;
    private javax.swing.JComboBox<String> cb_rangoFechas1;
    private javax.swing.JComboBox<String> cb_rangoss;
    private javax.swing.JComboBox<String> cb_sucursal1;
    private javax.swing.JComboBox<String> cb_tipoRanking;
    private javax.swing.JComboBox<String> cmbProducto;
    private javax.swing.JComboBox<String> cmbTienda;
    private javax.swing.JPanel costos_totales;
    private javax.swing.JComboBox<String> filtro_sucursal;
    private javax.swing.JPanel ganancia_bruta2;
    private javax.swing.JPanel ganancia_neta;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelPromedioVentas;
    private javax.swing.JPanel jPanelTiendaTOP;
    private javax.swing.JPanel jPanelVentasTotales;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JLabel lblFiltrarPor;
    private javax.swing.JLabel lblMediosPago;
    private javax.swing.JLabel lblTituloMedioPago;
    private javax.swing.JLabel lblTituloNumeroVentas;
    private javax.swing.JLabel lblTituloProductosVendidos;
    private javax.swing.JLabel lblTituloVentasTotales;
    private javax.swing.JLabel lblValorMedioPago;
    private javax.swing.JLabel lblValorNumeroVentas;
    private javax.swing.JLabel lblValorProductosVendidos;
    private javax.swing.JLabel lblValorVentasTotales;
    private javax.swing.JLabel lblVentasDiarias;
    private javax.swing.JLabel lblVentasMensuales;
    private javax.swing.JLabel lblVentasPorProducto;
    private javax.swing.JLabel lbl_TiendaTOP;
    private javax.swing.JLabel lbl_TotalVenta;
    private javax.swing.JLabel lbl_costos;
    private javax.swing.JLabel lbl_gananciaBruta2;
    private javax.swing.JLabel lbl_gananciaNeta;
    private javax.swing.JLabel lbl_promedioVentas;
    private javax.swing.JLabel lbl_rentabilidad;
    private javax.swing.JLabel lbl_total2;
    private javax.swing.JLabel lbl_totalIngresos;
    private javax.swing.JLabel lbl_totalVentas3;
    private javax.swing.JLabel lbl_totalVentas4;
    private javax.swing.JLabel lbl_totalVentas5;
    private javax.swing.JPanel panel_COMPRAS;
    private javax.swing.JPanel panel_Estadisticas;
    private javax.swing.JPanel panel_Filtros;
    private javax.swing.JPanel panel_Graficos;
    private javax.swing.JPanel panel_KPIs;
    private javax.swing.JPanel panel_KpiMedioPago;
    private javax.swing.JPanel panel_KpiNumeroVentas;
    private javax.swing.JPanel panel_KpiProductosVendidos;
    private javax.swing.JPanel panel_KpiVentasTotales;
    private javax.swing.JPanel panel_MediosPago;
    private javax.swing.JPanel panel_Ranking;
    private javax.swing.JPanel panel_Rentabilidad;
    private javax.swing.JPanel panel_Ventas;
    private javax.swing.JPanel panel_VentasDiarias;
    private javax.swing.JPanel panel_VentasMensuales;
    private javax.swing.JPanel panel_VentasPorProducto;
    private javax.swing.JPanel panel_estadisticas;
    private javax.swing.JPanel panel_rankingXVentas;
    // End of variables declaration//GEN-END:variables
}
