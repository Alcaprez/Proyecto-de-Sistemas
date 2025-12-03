package edu.UPAO.proyecto.app;

import BaseDatos.Conexion;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Image;
import edu.UPAO.proyecto.DAO.ReporteComprasDAO;
import edu.UPAO.proyecto.DAO.ReporteComprasDAO.ComboItem;
import edu.UPAO.proyecto.Util.GeneradorExcelRk;
import edu.UPAO.proyecto.Util.GeneradorPDFRk;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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

import org.jfree.chart.renderer.category.StandardBarPainter;
import java.awt.Color;
import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;

// --- IMPORTS DE BASE DE DATOS Y UTILIDADES ---
import BaseDatos.Conexion;
import edu.UPAO.proyecto.Util.GeneradorExcelRentabilidad;
import edu.UPAO.proyecto.Util.GeneradorPDFRentabilidad;

// --- IMPORTS DE JAVA ---
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout; // Importante para el tamaño del gráfico
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

// --- IMPORTS DE JFREECHART (GRÁFICOS) ---
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class panel_Tesoreria extends javax.swing.JPanel {

    private edu.UPAO.proyecto.DAO.CajaDAO cajaDAO = new edu.UPAO.proyecto.DAO.CajaDAO();
    private Connection conn;
    private JFreeChart chartVentasPorProducto;
    private ReporteComprasDAO reporteComprasDAO;
    private JFreeChart chartVentasDiarias;
    private JFreeChart chartVentasMensuales;
    private JFreeChart chartMediosPago;
    private JFreeChart chartRanking;
    private JFreeChart chartRentabilidad;

    private LocalDate fechaDesde = null;
    private LocalDate fechaHasta = null;

    public panel_Tesoreria() {
        initComponents();
        reporteComprasDAO = new ReporteComprasDAO();
        envolverEnScroll();
        inicializar();
        configurarModelosTablasCompras();
        configurarCombosCompras();
        configurarEventosTablasCompras();
        aplicarFiltrosCompras();
        initGestionCaja();
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
        iniciarSistemaRanking();
        initRentabilidad();
    }

    private void initGestionCaja() {
        // 1. Configurar Tablas
        configurarTablaSaldosSucursal();
        configurarTablaMovimientos();

        // 2. Cargar Combos
        cargarCombosTransferencia();

        // 3. Listener Botón
        for (java.awt.event.ActionListener al : btn_transferir.getActionListeners()) {
            btn_transferir.removeActionListener(al);
        }
        btn_transferir.addActionListener(e -> realizarTransferencia());

        // 4. Cargar Datos Iniciales
        actualizarTablasGestionCaja();
    }


        

private void cargarCombosTransferencia() {
        // TRUCO: Convertimos a JComboBox genérico para poder agregar objetos 'ComboItem'
        // aunque NetBeans los haya definido como <String>
        javax.swing.JComboBox comboOrigen = (javax.swing.JComboBox) CB_SURUSAL1;
        javax.swing.JComboBox comboDestino = (javax.swing.JComboBox) cb_sucursal1;

        comboOrigen.removeAllItems(); 
        comboDestino.removeAllItems(); 

        // Ahora sí aceptará el objeto 'suc' (ComboItem) sin dar error rojo
        for (ComboItem suc : reporteComprasDAO.listarSucursales()) {
            comboOrigen.addItem(suc);   
            comboDestino.addItem(suc);  
        }
    }
    private void configurarTablaMovimientos() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Fecha", "Sucursal", "Tipo", "Monto", "Detalle"}, 0
        );
        tb_movimientos.setModel(model);
        tb_movimientos.setRowHeight(25);
        // Anchos
        tb_movimientos.getColumnModel().getColumn(0).setPreferredWidth(130); // Fecha
        tb_movimientos.getColumnModel().getColumn(4).setPreferredWidth(250); // Detalle
    }

private void configurarTablaSaldosSucursal() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Sucursal", "Saldo Disponible"}, 0
        );
        tb_cajasGrandesPorSucursal.setModel(model);
        tb_cajasGrandesPorSucursal.setRowHeight(30);
        
        // CORRECCIÓN: Usamos "java.awt.Font" completo para evitar error de "Cannot find symbol"
        tb_cajasGrandesPorSucursal.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    }

    private void realizarTransferencia() {
        // 1. Validaciones
        ComboItem origenItem = (ComboItem) CB_SURUSAL1.getSelectedItem();
        ComboItem destinoItem = (ComboItem) cb_sucursal1.getSelectedItem();

        if (origenItem == null || destinoItem == null) {
            return;
        }

        int idOrigen = (Integer) origenItem.getId();
        int idDestino = (Integer) destinoItem.getId();

        if (idOrigen == idDestino) {
            JOptionPane.showMessageDialog(this, "La sucursal de origen y destino no pueden ser la misma.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(tf_montoAtransferir.getText());
            if (monto <= 0) {
                throw new NumberFormatException();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido mayor a 0.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validar Saldo Disponible
        double saldoOrigen = cajaDAO.obtenerSaldoAcumuladoHistorico(idOrigen);
        if (saldoOrigen < monto) {
            JOptionPane.showMessageDialog(this, "Saldo insuficiente en " + origenItem.toString() + ".\nDisponible: S/ " + saldoOrigen, "Fondos Insuficientes", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Ejecutar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Transferir S/ " + monto + " de " + origenItem + " a " + destinoItem + "?",
                "Confirmar Transferencia", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = cajaDAO.registrarTransferencia(idOrigen, idDestino, monto, "Transferencia Gerencia");
            if (exito) {
                JOptionPane.showMessageDialog(this, "Transferencia realizada con éxito.");
                tf_montoAtransferir.setText("0");
                actualizarTablasGestionCaja(); // Refrescar datos visuales
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la transferencia.", "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private void actualizarTablasGestionCaja() {
        // 1. Tabla Saldos (Izquierda)
        DefaultTableModel modelSaldos = (DefaultTableModel) tb_cajasGrandesPorSucursal.getModel();
        modelSaldos.setRowCount(0);

        // Recorremos las sucursales para ver cuánto tienen
        // Nota: Esto usa el método obtenerSaldoAcumuladoHistorico del DAO de Caja
        List<ComboItem> sucursales = reporteComprasDAO.listarSucursales();
        for (ComboItem suc : sucursales) {
            if (suc.getId() instanceof Integer) {
                int idSuc = (Integer) suc.getId();
                double saldo = cajaDAO.obtenerSaldoAcumuladoHistorico(idSuc);
                modelSaldos.addRow(new Object[]{suc.toString(), String.format("S/ %.2f", saldo)});
            }
        }

        // 2. Tabla Movimientos (Derecha)
        DefaultTableModel modelMovs = (DefaultTableModel) tb_movimientos.getModel();
        modelMovs.setRowCount(0);

        List<Object[]> transferencias = cajaDAO.listarTransferencias();
        for (Object[] row : transferencias) {
            modelMovs.addRow(row);
        }
    }

    private void configurarModelosTablasCompras() {
        // Tabla RESUMEN (arriba)
        DefaultTableModel modeloResumen = new DefaultTableModel(
                new Object[]{
                    "ID Proveedor", "Proveedor", "Total",
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResumenCompras.setModel(modeloResumen);

        // Ocultar columna ID Proveedor
        tblResumenCompras.getColumnModel().getColumn(0).setMinWidth(0);
        tblResumenCompras.getColumnModel().getColumn(0).setMaxWidth(0);
        tblResumenCompras.getColumnModel().getColumn(0).setWidth(0);

        // Tabla DETALLE (abajo)
        DefaultTableModel modeloDetalle = new DefaultTableModel(
                new Object[]{"ID Compra", "Fecha", "Tienda", "Total", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDetalleCompras.setModel(modeloDetalle);

        // Opcional: inicialmente ocultar detalle
        panelDetalleCompras.setVisible(false);
        lblDetalleProveedor.setText("Detalle del proveedor:");
    }

    private void configurarEventosTablasCompras() {
        tblResumenCompras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDetalleProveedorSeleccionado();
            }
        });
    }

    private void cargarDetalleProveedorSeleccionado() {
        int fila = tblResumenCompras.getSelectedRow();
        if (fila < 0) {
            return;
        }

        String idProveedor = (String) tblResumenCompras.getValueAt(fila, 0); // col 0 oculto
        String nombreProveedor = (String) tblResumenCompras.getValueAt(fila, 1);
        javax.swing.JComboBox comboTienda = (javax.swing.JComboBox) cbTiendaCompras;
        // Sucursal actual (para mantener filtro)
        ComboItem sucItem = (ComboItem) comboTienda.getSelectedItem();
        Integer idSucursal = null;
        if (sucItem != null && sucItem.getId() instanceof Integer) {
            int id = (Integer) sucItem.getId();
            idSucursal = (id == 0) ? null : id;
        }

        // Fechas actuales
        Date fDesde = null;
        Date fHasta = null;
        if (dcFechaInicioCompras.getDate() != null) {
            fDesde = new Date(dcFechaInicioCompras.getDate().getTime());
        }
        if (dcFechaFinCompras.getDate() != null) {
            fHasta = new Date(dcFechaFinCompras.getDate().getTime());
        }

        reporteComprasDAO.llenarTablaDetalle(
                tblDetalleCompras,
                idProveedor,
                idSucursal,
                fDesde,
                fHasta
        );

        lblDetalleProveedor.setText("Detalle del proveedor: " + nombreProveedor);
        panelDetalleCompras.setVisible(true);
    }

    private void configurarCombosCompras() {
        // Trabajamos con los combos como "raw" para saltarnos el <String>
        javax.swing.JComboBox comboTienda = (javax.swing.JComboBox) cbTiendaCompras;
        javax.swing.JComboBox comboProveedor = (javax.swing.JComboBox) cbProveedorCompras;

        // TIENDAS
        comboTienda.removeAllItems();
        comboTienda.addItem(new ComboItem(0, "Todas"));

        for (ComboItem suc : reporteComprasDAO.listarSucursales()) {
            comboTienda.addItem(suc);
        }

        // PROVEEDORES
        comboProveedor.removeAllItems();
        comboProveedor.addItem(new ComboItem("", "Todos"));

        for (ComboItem prov : reporteComprasDAO.listarProveedores()) {
            comboProveedor.addItem(prov);
        }

    }

    private void exportarTablaCSV(JTable tabla) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte de compras");

        int seleccion = chooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            java.io.File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(file)) {
                DefaultTableModel model = (DefaultTableModel) tabla.getModel();

                // Encabezados (omitimos columna ID proveedor si quieres)
                for (int col = 1; col < model.getColumnCount(); col++) {
                    fw.write(model.getColumnName(col));
                    if (col < model.getColumnCount() - 1) {
                        fw.write(";");
                    }
                }
                fw.write("\n");

                // Filas
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 1; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        fw.write(value == null ? "" : value.toString());
                        if (col < model.getColumnCount() - 1) {
                            fw.write(";");
                        }
                    }
                    fw.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Reporte exportado correctamente.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
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
        String[] opciones = {"Hoy", "Últimos 7 días", "Últimos 30 días", "Por mes", "Todo"};

        int op = JOptionPane.showOptionDialog(
                this,
                "Selecciona el rango de fechas:",
                "Filtro de fecha",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (op == JOptionPane.CLOSED_OPTION) {
            return;
        }

        LocalDate hoy = LocalDate.now();

        switch (op) {
            case 0: // Hoy
                fechaDesde = hoy;
                fechaHasta = hoy;
                break;

            case 1: // Últimos 7 días
                fechaHasta = hoy;
                fechaDesde = hoy.minusDays(6);
                break;

            case 2: // Últimos 30 días
                fechaHasta = hoy;
                fechaDesde = hoy.minusDays(29);
                break;

            case 3: // Por mes
                seleccionarMes();   // 👉 nuevo método de abajo
                break;

            case 4: // Todo
                fechaDesde = null;
                fechaHasta = null;
                break;
        }

        actualizarDashboard();  // recarga gráficos y KPIs
    }

    private void seleccionarMes() {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"};

        JComboBox<String> cmbMes = new JComboBox<>(meses);
        int anioActual = LocalDate.now().getYear();
        JSpinner spAnio = new JSpinner(new SpinnerNumberModel(anioActual, 2000, 2100, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Mes:"));
        panel.add(cmbMes);
        panel.add(new JLabel("Año:"));
        panel.add(spAnio);

        int res = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Seleccionar mes",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        int mesSeleccionado = cmbMes.getSelectedIndex() + 1; // 1–12
        int anioSeleccionado = (int) spAnio.getValue();

        fechaDesde = LocalDate.of(anioSeleccionado, mesSeleccionado, 1);
        fechaHasta = fechaDesde.with(TemporalAdjusters.lastDayOfMonth());
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

    private void cargarGraficoVentasDiarias(Integer idSucursal, Integer idProducto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Object> params = new ArrayList<>();
        boolean joinProducto = (idProducto != null);

        // Lógica inteligente: Si el rango es grande (o nulo = "Todo"), agrupamos por MES.
        // Si es pequeño (ej: un mes o una semana), agrupamos por DÍA.
        boolean agruparPorMes = (fechaDesde == null || fechaHasta == null
                || java.time.temporal.ChronoUnit.DAYS.between(fechaDesde, fechaHasta) > 35);

        StringBuilder sql = new StringBuilder();

        if (agruparPorMes) {
            // Consulta para rango largo: Agrupa por Año-Mes (Ej: "2025-01")
            sql.append("SELECT DATE_FORMAT(v.fecha_hora, '%Y-%m') AS fecha_group, SUM(v.total) AS total ")
                    .append("FROM venta v ");
        } else {
            // Consulta normal para rango corto: Agrupa por Día (Ej: "2025-01-15")
            sql.append("SELECT DATE(v.fecha_hora) AS fecha_group, SUM(v.total) AS total ")
                    .append("FROM venta v ");
        }

        if (joinProducto) {
            sql.append("JOIN detalle_venta dv ON dv.id_venta = v.id_venta ");
        }

        sql.append(construirWhereVentas(idSucursal, idProducto, params, joinProducto));

        if (agruparPorMes) {
            sql.append(" GROUP BY DATE_FORMAT(v.fecha_hora, '%Y-%m') ORDER BY fecha_group");
        } else {
            sql.append(" GROUP BY DATE(v.fecha_hora) ORDER BY fecha_group");
        }

        try (PreparedStatement ps = preparar(sql.toString(), params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                String etiqueta;

                if (agruparPorMes) {
                    // Formato Mes: "2025-11" -> "Nov-25" o "11/2025"
                    String fechaGroup = rs.getString("fecha_group"); // Viene como "YYYY-MM"
                    String[] partes = fechaGroup.split("-");
                    etiqueta = obtenerNombreMes(Integer.parseInt(partes[1])) + "-" + partes[0];
                } else {
                    // Formato Día: "2025-11-25" -> "25"
                    Date fecha = rs.getDate("fecha_group");
                    LocalDate localDate = fecha.toLocalDate();
                    etiqueta = String.format("%02d", localDate.getDayOfMonth());
                }

                dataset.addValue(total, "Ventas", etiqueta);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Crear gráfico
        JFreeChart chart = ChartFactory.createAreaChart(
                agruparPorMes ? "Evolución Mensual" : "Ventas Diarias", // Título dinámico
                agruparPorMes ? "Mes" : "Día",
                "Total (S/)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Estilo
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Rotar etiquetas si son muchas (solo si no agrupamos por mes, aunque por mes suelen caber bien)
        if (!agruparPorMes && dataset.getColumnCount() > 10) {
            plot.getDomainAxis().setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_90);
        }

        chartVentasDiarias = chart;
        mostrarChartEnPanel(panel_VentasDiarias, chart);

        ChartPanel panel = new ChartPanel(chart);
        panel_VentasDiarias.removeAll();
        panel_VentasDiarias.setLayout(new BorderLayout());
        panel_VentasDiarias.add(panel, BorderLayout.CENTER);
        panel_VentasDiarias.revalidate();
    }

    // Helper pequeño para nombres de mes
    private String obtenerNombreMes(int mes) {
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        if (mes >= 1 && mes <= 12) {
            return meses[mes - 1];
        }
        return String.valueOf(mes);
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
        chartVentasMensuales = chart; // 👉 guardar

        ChartPanel panel = new ChartPanel(chart);
        panel_VentasMensuales.removeAll();
        panel_VentasMensuales.setLayout(new BorderLayout());
        panel_VentasMensuales.add(panel, BorderLayout.CENTER);
        panel_VentasMensuales.revalidate();
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

        JFreeChart chart = ChartFactory.createBarChart(
                "Ventas por producto (Top 5)",
                "Producto",
                "Total (S/)",
                dataset,
                PlotOrientation.HORIZONTAL,
                false, true, false);

        mostrarChartEnPanel(panel_VentasPorProducto, chartVentasPorProducto);
        chartVentasPorProducto = chart; // 👉 ya lo usas para exportar

        ChartPanel panel = new ChartPanel(chart);
        panel_VentasPorProducto.removeAll();
        panel_VentasPorProducto.setLayout(new BorderLayout());
        panel_VentasPorProducto.add(panel, BorderLayout.CENTER);
        panel_VentasPorProducto.revalidate();
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

        chartMediosPago = chart;

        ChartPanel panel = new ChartPanel(chart);
        panel_MediosPago.removeAll();
        panel_MediosPago.setLayout(new BorderLayout());
        panel_MediosPago.add(panel, BorderLayout.CENTER);
        panel_MediosPago.revalidate();
    }

    public void generarReporteCompleto(JTable tabla,
            JFreeChart chartDiario,
            JFreeChart chartMensual,
            JFreeChart chartProductos,
            JFreeChart chartMediosPago,
            String mes) {
        try {
            String ruta = "Reporte_Ranking_" + mes + ".pdf"; // mismo formato de nombre
            Document doc = new Document(PageSize.A4.rotate());
            {
            }; // horizontal para que entren los gráficos
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // ===== TÍTULO =====
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Reporte general de ventas: " + mes, fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(Chunk.NEWLINE);

            // ===== BLOQUE DE GRÁFICOS (2 x 2) =====
            PdfPTable tablaGraficos = new PdfPTable(2);
            tablaGraficos.setWidthPercentage(100);

            agregarGraficoACelda(tablaGraficos, chartDiario, "Ventas diarias");
            agregarGraficoACelda(tablaGraficos, chartMensual, "Ventas mensuales");
            agregarGraficoACelda(tablaGraficos, chartProductos, "Ventas por producto (Top 5)");
            agregarGraficoACelda(tablaGraficos, chartMediosPago, "Medios de pago");

            doc.add(tablaGraficos);
            doc.add(Chunk.NEWLINE);

            // ===== TABLA RESUMEN (KPI + ranking) =====
            PdfPTable pdfTable = new PdfPTable(4); // 4 columnas
            pdfTable.setWidthPercentage(100);

            String[] headers = {"TOP", "LOCALES", "VENTAS", "TRANSACCIONES"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD))
                );
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            for (int i = 0; i < tabla.getRowCount(); i++) {
                for (int j = 0; j < 4; j++) {
                    Object val = tabla.getValueAt(i, j);
                    pdfTable.addCell(val != null ? val.toString() : "");
                }
            }

            doc.add(pdfTable);
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF Exportado: " + ruta);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error PDF: " + e.getMessage());
        }
    }

// ==== helper privado ====
    private void agregarGraficoACelda(PdfPTable tabla, JFreeChart grafico, String titulo) throws Exception {
        PdfPCell cell;

        if (grafico == null) {
            cell = new PdfPCell(new Phrase("Sin datos para " + titulo));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            return;
        }

        java.awt.image.BufferedImage img = grafico.createBufferedImage(400, 250);
        Image pdfImg = Image.getInstance(img, null);

        cell = new PdfPCell();
        cell.setPadding(5f);

        Paragraph tituloPar = new Paragraph(
                titulo,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
        );
        tituloPar.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(tituloPar);
        pdfImg.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pdfImg);

        tabla.addCell(cell);
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

                genPdf.generarReporteCompleto(
                        tablaRanking,
                        chartVentasDiarias,
                        chartVentasMensuales,
                        chartVentasPorProducto,
                        chartMediosPago,
                        mes
                );

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

    // =======================================================================
    //  MÓDULO DE RANKING (VENTAS vs TRANSACCIONES)
    // =======================================================================
    private void iniciarSistemaRanking() {
        // 1. Limpiar listeners previos por seguridad
        for (java.awt.event.ActionListener al : FiltrarporMes.getActionListeners()) {
            FiltrarporMes.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : cb_tipoRanking.getActionListeners()) {
            cb_tipoRanking.removeActionListener(al);
        }

        // 2. Activar recarga automática
        FiltrarporMes.addActionListener(e -> cargarDatosRanking());
        cb_tipoRanking.addActionListener(e -> cargarDatosRanking());

        // 3. Carga inicial
        cargarDatosRanking();
    }

    private void cargarDatosRanking() {
        // Validación de conexión
        if (this.conn == null) {
            return;
        }

        // A. Obtener datos de la Interfaz
        int mesIndex = FiltrarporMes.getSelectedIndex(); // 0=Anual
        String tipoRanking = cb_tipoRanking.getSelectedItem().toString();

        // B. Filtros SQL
        String filtroV = (mesIndex == 0) ? "" : " AND MONTH(v.fecha_hora) = " + mesIndex;
        String filtroC = (mesIndex == 0) ? "" : " AND MONTH(mc.fecha_hora) = " + mesIndex;

        // C. Consulta SQL (Ventas Suma, Transacciones Conteo)
        String sql = "SELECT s.nombre_sucursal, "
                + "COALESCE(v.total_v, 0) as ventas, "
                + "COALESCE(mc.total_count, 0) as transacciones, "
                + "(COALESCE(v.total_v, 0) + COALESCE(mc.total_count, 0))/2 as puntaje "
                + "FROM sucursal s "
                + "LEFT JOIN (SELECT id_sucursal, SUM(total) as total_v FROM venta v WHERE 1=1 " + filtroV + " GROUP BY id_sucursal) v ON s.id_sucursal=v.id_sucursal "
                + "LEFT JOIN (SELECT id_sucursal, COUNT(*) as total_count FROM movimiento_caja mc WHERE 1=1 " + filtroC + " GROUP BY id_sucursal) mc ON s.id_sucursal=mc.id_sucursal "
                + "ORDER BY puntaje DESC";

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            // 1. Preparar Tabla y Dataset
            DefaultTableModel modelo = (DefaultTableModel) TablaRanking.getModel();
            modelo.setRowCount(0);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Variables para Labels
            int puesto = 1;
            double sumaTotalVentas = 0;
            String topLocal = "-";
            String mejorPromedioLocal = "-";

            // 2. Recorrer Datos
            while (rs.next()) {
                String nom = rs.getString("nombre_sucursal");
                double vtas = rs.getDouble("ventas");
                int trxs = rs.getInt("transacciones");

                // -> Llenar Tabla
                modelo.addRow(new Object[]{
                    puesto, nom, "S/ " + String.format("%,.2f", vtas), trxs
                });

                // -> Llenar Gráfico
                double valorG = (tipoRanking.equalsIgnoreCase("Ventas")) ? vtas : trxs;
                dataset.addValue(valorG, tipoRanking, nom);

                // -> Calcular Labels
                sumaTotalVentas += vtas;
                if (puesto == 1) {
                    topLocal = nom;
                    mejorPromedioLocal = nom;
                }
                puesto++;
            }

            // 3. Actualizar UI
            TablaRanking.setModel(modelo);
            lbl_TotalVenta.setText("S/ " + String.format("%,.2f", sumaTotalVentas));
            lbl_TiendaTOP.setText(topLocal);
            lbl_promedioVentas.setText(mejorPromedioLocal);

            // Pintar Gráfico (Dinámico)
            String labelY = tipoRanking.equalsIgnoreCase("Ventas") ? "Monto (S/)" : "N° Transacciones";
            pintarGraficoRanking(dataset, labelY);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Ranking: " + e.getMessage());
        }
    }

    private void aplicarFiltrosCompras() {
        javax.swing.JComboBox comboTienda = (javax.swing.JComboBox) cbTiendaCompras;
        javax.swing.JComboBox comboProveedor = (javax.swing.JComboBox) cbProveedorCompras;

        // Sucursal
        ComboItem sucItem = (ComboItem) comboTienda.getSelectedItem();
        Integer idSucursal = null;
        if (sucItem != null && sucItem.getId() instanceof Integer) {
            int id = (Integer) sucItem.getId();
            idSucursal = (id == 0) ? null : id;
        }

        // Proveedor
        ComboItem provItem = (ComboItem) comboProveedor.getSelectedItem();
        String idProveedor = null;
        if (provItem != null && provItem.getId() instanceof String) {
            String id = (String) provItem.getId();
            idProveedor = id.isEmpty() ? null : id;
        }

        // Fechas
        Date fDesde = null;
        Date fHasta = null;
        if (dcFechaInicioCompras.getDate() != null) {
            fDesde = new Date(dcFechaInicioCompras.getDate().getTime());
        }
        if (dcFechaFinCompras.getDate() != null) {
            fHasta = new Date(dcFechaFinCompras.getDate().getTime());
        }

        // Llenar tabla de resumen
        reporteComprasDAO.llenarTablaResumen(
                tblResumenCompras,
                idSucursal,
                idProveedor,
                fDesde,
                fHasta
        );

        // Limpiar detalle
        ((DefaultTableModel) tblDetalleCompras.getModel()).setRowCount(0);
        panelDetalleCompras.setVisible(false);
        lblDetalleProveedor.setText("Detalle del proveedor:");
    }

    private void pintarGraficoRanking(DefaultCategoryDataset dataset, String labelY) {
        chartRanking = ChartFactory.createBarChart(
                "", "", labelY, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        // Estilo Plano (Sin brillo excesivo)
        org.jfree.chart.plot.CategoryPlot plot = chartRanking.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(Color.GRAY);
        chartRanking.setBackgroundPaint(Color.WHITE);

        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, new Color(70, 130, 180)); // Azul acero

        ChartPanel cp = new ChartPanel(chartRanking);
        // Ajustar altura a 250px o lo que necesites
        if (GraficoBarras.getWidth() > 0) {
            cp.setPreferredSize(new java.awt.Dimension(GraficoBarras.getWidth(), 250));
        }

        GraficoBarras.removeAll();
        GraficoBarras.setLayout(new BorderLayout());
        GraficoBarras.add(cp, BorderLayout.CENTER);
        GraficoBarras.validate();
        GraficoBarras.repaint();
    }

    // --- MÉTODOS PARA CONECTAR TUS BOTONES ---
    public void accionBotonPDF() {
        GeneradorPDFRk pdf = new GeneradorPDFRk();
        // Asumo que tu método se llama generarReporte, si es otro cambialo aquí
        pdf.generarReporte(TablaRanking, chartRanking, FiltrarporMes.getSelectedItem().toString());
    }

    public void accionBotonExcel() {
        GeneradorExcelRk excel = new GeneradorExcelRk();
        // Asumo que tu método se llama generarExcel, si es otro cambialo aquí
        excel.generarExcel(TablaRanking, FiltrarporMes.getSelectedItem().toString());
    }

// =======================================================================
    //   MÓDULO RENTABILIDAD (FORMATO FINAL: S/ 18,142.25)
    // =======================================================================
    private void initRentabilidad() {
        // 1. Configurar Combo
        if (RangoFechas.getItemCount() == 0) {
            RangoFechas.addItem("Anual");
            String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            for (String m : meses) {
                RangoFechas.addItem(m);
            }
        }

        // 2. Listeners
        for (java.awt.event.ActionListener al : RangoFechas.getActionListeners()) {
            RangoFechas.removeActionListener(al);
        }
        RangoFechas.addActionListener(e -> {
            System.out.println("🔄 Cambio de mes en Rentabilidad...");
            cargarDatosRentabilidad();
        });

        // Botones
        for (java.awt.event.ActionListener al : PDFRentabilidad.getActionListeners()) {
            PDFRentabilidad.removeActionListener(al);
        }
        PDFRentabilidad.addActionListener(e
                -> GeneradorPDFRentabilidad.generarPDF(TablaRentabilidad, chartRentabilidad, "Rentabilidad_2025")
        );

        for (java.awt.event.ActionListener al : ExcelRentabilidad.getActionListeners()) {
            ExcelRentabilidad.removeActionListener(al);
        }
        ExcelRentabilidad.addActionListener(e
                -> GeneradorExcelRentabilidad.generarExcel(TablaRentabilidad, "Rentabilidad_2025")
        );

        // 3. Ejecución inicial
        cargarDatosRentabilidad();
    }

    private void cargarDatosRentabilidad() {
        if (RangoFechas.getItemCount() == 0) {
            return;
        }

        // --- A. DEFINIR FECHAS ---
        int mesIndex = RangoFechas.getSelectedIndex();
        String fechaInicio, fechaFin;
        int anioFijo = 2025;

        if (mesIndex <= 0) { // Anual
            fechaInicio = anioFijo + "-01-01 00:00:00";
            fechaFin = anioFijo + "-12-31 23:59:59";
        } else { // Mes específico
            int ultimoDia = 31;
            if (mesIndex == 2) {
                ultimoDia = (anioFijo % 4 == 0) ? 29 : 28;
            } else if (mesIndex == 4 || mesIndex == 6 || mesIndex == 9 || mesIndex == 11) {
                ultimoDia = 30;
            }
            String mesStr = String.format("%02d", mesIndex);
            fechaInicio = anioFijo + "-" + mesStr + "-01 00:00:00";
            fechaFin = anioFijo + "-" + mesStr + "-" + ultimoDia + " 23:59:59";
        }

        System.out.println("📅 Rentabilidad Fechas: " + fechaInicio + " al " + fechaFin);

        // --- B. CONSULTA SQL (TU LÓGICA DEFINIDA) ---
        DefaultTableModel modelo = new DefaultTableModel(null,
                new String[]{"LOCALES", "INGRESOS", "COSTOS", "GANANCIA NETA", "RENTABILIDAD"});

        String sql
                = "SELECT s.nombre_sucursal AS LOCALES, "
                + "   COALESCE(v_stats.ingresos, 0) AS INGRESOS, "
                + "   COALESCE(c_stats.costos, 0) AS COSTOS "
                + "FROM sucursal s "
                + // JOIN 1: INGRESOS (Lógica Ranking)
                "LEFT JOIN ( "
                + "   SELECT id_sucursal, SUM(total) as ingresos "
                + "   FROM venta "
                + "   WHERE fecha_hora BETWEEN ? AND ? "
                + "   GROUP BY id_sucursal "
                + ") v_stats ON s.id_sucursal = v_stats.id_sucursal "
                + // JOIN 2: COSTOS (Lógica Stock Min * Precio Compra)
                "LEFT JOIN ( "
                + "   SELECT v.id_sucursal, SUM(p.stock_minimo * p.precio_compra) as costos "
                + "   FROM venta v "
                + "   JOIN detalle_venta dv ON v.id_venta = dv.id_venta "
                + "   JOIN producto p ON dv.id_producto = p.id_producto "
                + "   WHERE v.fecha_hora BETWEEN ? AND ? "
                + "   GROUP BY v.id_sucursal "
                + ") c_stats ON s.id_sucursal = c_stats.id_sucursal";

        try (Connection cnRent = new Conexion().establecerConexion(); PreparedStatement ps = cnRent.prepareStatement(sql)) {

            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ps.setString(3, fechaInicio);
            ps.setString(4, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String local = rs.getString("LOCALES");
                    double ingresos = rs.getDouble("INGRESOS");
                    double costos = rs.getDouble("COSTOS");
                    double ganancia = ingresos - costos;
                    double rentabilidad = (ingresos > 0) ? (ganancia / ingresos) * 100 : 0;

                    // --- AQUÍ ESTÁ EL CAMBIO DE FORMATO (%,.2f) ---
                    modelo.addRow(new Object[]{
                        local,
                        "S/ " + String.format(Locale.US, "%,.2f", ingresos), // Ej: S/ 1,500.00
                        "S/ " + String.format(Locale.US, "%,.2f", costos),
                        "S/ " + String.format(Locale.US, "%,.2f", ganancia),
                        String.format(Locale.US, "%.2f", rentabilidad) + "%"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error SQL Rentabilidad: " + e.getMessage());
        }

        TablaRentabilidad.setModel(modelo);

        // --- C. ACTUALIZAR INTERFAZ ---
        actualizarKPIsYGraficoRentabilidad(modelo);
    }

    private void actualizarKPIsYGraficoRentabilidad(DefaultTableModel modelo) {
        double tIngresos = 0, tCostos = 0, tGanancia = 0;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                // Parseo limpio
                String ingStr = modelo.getValueAt(i, 1).toString().replace("S/ ", "").replace(",", "");
                String cosStr = modelo.getValueAt(i, 2).toString().replace("S/ ", "").replace(",", "");
                String ganStr = modelo.getValueAt(i, 3).toString().replace("S/ ", "").replace(",", "");

                tIngresos += Double.parseDouble(ingStr);
                tCostos += Double.parseDouble(cosStr);
                tGanancia += Double.parseDouble(ganStr);

                // Datos Gráfico
                String local = modelo.getValueAt(i, 0).toString();
                String rentStr = modelo.getValueAt(i, 4).toString().replace("%", "").replace(",", "");
                dataset.addValue(Double.parseDouble(rentStr), "Rentabilidad %", local);
            } catch (Exception e) {
                System.err.println("⚠️ Error fila " + i);
            }
        }

        // 1. ETIQUETAS KPI (FORMATO: S/ 18,142.25)
        if (lbl_totalIngresos != null) {
            lbl_totalIngresos.setText("S/ " + String.format(Locale.US, "%,.2f", tIngresos));
        }

        if (lbl_costos != null) {
            lbl_costos.setText("S/ " + String.format(Locale.US, "%,.2f", tCostos));
        }

        // --- AQUÍ ESTÁ LA CORRECCIÓN VISUAL ---
        if (lbl_gananciastotales != null) {
            lbl_gananciastotales.setText("S/ " + String.format(Locale.US, "%,.2f", tGanancia));
        }

        // 2. GRÁFICO
        chartRentabilidad = ChartFactory.createBarChart("", "LOCALES", "Margen Rentabilidad (%)",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = chartRentabilidad.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 139, 87));

        ChartPanel chartPanel = new ChartPanel(chartRentabilidad);
        chartPanel.setPreferredSize(new java.awt.Dimension(479, 320));

        if (GraficoBarrasRentabilidad != null) {
            GraficoBarrasRentabilidad.removeAll();
            GraficoBarrasRentabilidad.setLayout(new FlowLayout(FlowLayout.CENTER));
            GraficoBarrasRentabilidad.add(chartPanel);
            GraficoBarrasRentabilidad.revalidate();
            GraficoBarrasRentabilidad.repaint();
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
        panelFiltrosCompras = new javax.swing.JPanel();
        lblFiltrarPor_Pcompras = new javax.swing.JLabel();
        lblTienda_Pcompras = new javax.swing.JLabel();
        cbTiendaCompras = new javax.swing.JComboBox<>();
        lblProveedorCompras = new javax.swing.JLabel();
        cbProveedorCompras = new javax.swing.JComboBox<>();
        lblDesdeCompras = new javax.swing.JLabel();
        dcFechaInicioCompras = new com.toedter.calendar.JDateChooser();
        lblHastaCompras = new javax.swing.JLabel();
        dcFechaFinCompras = new com.toedter.calendar.JDateChooser();
        btnFiltrarCompras = new javax.swing.JButton();
        btnExportarCompras = new javax.swing.JButton();
        panelTablasCompras = new javax.swing.JPanel();
        scrollResumenCompras = new javax.swing.JScrollPane();
        tblResumenCompras = new javax.swing.JTable();
        panelDetalleCompras = new javax.swing.JPanel();
        lblDetalleProveedor = new javax.swing.JLabel();
        scrollDetalleCompras = new javax.swing.JScrollPane();
        tblDetalleCompras = new javax.swing.JTable();
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
        TablaRentabilidad = new javax.swing.JTable();
        costos_totales = new javax.swing.JPanel();
        lbl_costos = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Ingresos_totales = new javax.swing.JPanel();
        lbl_totalIngresos = new javax.swing.JLabel();
        lbl_Ingresos = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ganancia_neta = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbl_gananciastotales = new javax.swing.JLabel();
        GraficoBarrasRentabilidad = new javax.swing.JPanel();
        RangoFechas = new javax.swing.JComboBox<>();
        Exportar1 = new javax.swing.JLabel();
        PDFRentabilidad = new javax.swing.JButton();
        ExcelRentabilidad = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_cajasGrandesPorSucursal = new javax.swing.JTable();
        btn_transferir = new javax.swing.JButton();
        CB_SURUSAL1 = new javax.swing.JComboBox<>();
        cb_sucursal1 = new javax.swing.JComboBox<>();
        tf_montoAtransferir = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb_movimientos = new javax.swing.JTable();

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

        panel_KpiVentasTotales.setBackground(new java.awt.Color(51, 153, 0));
        panel_KpiVentasTotales.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel_KpiVentasTotales.setLayout(new java.awt.BorderLayout());

        lblTituloVentasTotales.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloVentasTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloVentasTotales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloVentasTotales.setText("VENTAS TOTALES (S/)");
        panel_KpiVentasTotales.add(lblTituloVentasTotales, java.awt.BorderLayout.PAGE_START);

        lblValorVentasTotales.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblValorVentasTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblValorVentasTotales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblValorVentasTotales.setText("jLabel3");
        panel_KpiVentasTotales.add(lblValorVentasTotales, java.awt.BorderLayout.CENTER);

        panel_KPIs.add(panel_KpiVentasTotales);

        panel_KpiNumeroVentas.setBackground(new java.awt.Color(255, 152, 0));
        panel_KpiNumeroVentas.setLayout(new java.awt.BorderLayout());

        lblTituloNumeroVentas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        lblTituloProductosVendidos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        lblTituloMedioPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        Tabbed_Tesoreria.addTab("REPORTE DE VENTAS", panel_Ventas);

        panel_COMPRAS.setLayout(new java.awt.BorderLayout());

        panelFiltrosCompras.setLayout(new java.awt.GridBagLayout());

        lblFiltrarPor_Pcompras.setText("FILTRAR POR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(lblFiltrarPor_Pcompras, gridBagConstraints);

        lblTienda_Pcompras.setText("TIENDA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(lblTienda_Pcompras, gridBagConstraints);

        cbTiendaCompras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(cbTiendaCompras, gridBagConstraints);

        lblProveedorCompras.setText("PROVEEDOR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(lblProveedorCompras, gridBagConstraints);

        cbProveedorCompras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(cbProveedorCompras, gridBagConstraints);

        lblDesdeCompras.setText("DESDE:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(lblDesdeCompras, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(dcFechaInicioCompras, gridBagConstraints);

        lblHastaCompras.setText("HASTA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(lblHastaCompras, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(dcFechaFinCompras, gridBagConstraints);

        btnFiltrarCompras.setText("APLICAR FILTROS");
        btnFiltrarCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarComprasActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(btnFiltrarCompras, gridBagConstraints);

        btnExportarCompras.setText("EXPORTAR");
        btnExportarCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarComprasActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFiltrosCompras.add(btnExportarCompras, gridBagConstraints);

        panel_COMPRAS.add(panelFiltrosCompras, java.awt.BorderLayout.NORTH);

        panelTablasCompras.setLayout(new java.awt.BorderLayout());

        tblResumenCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Proveedor", "Total", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            }
        ));
        scrollResumenCompras.setViewportView(tblResumenCompras);

        panelTablasCompras.add(scrollResumenCompras, java.awt.BorderLayout.NORTH);

        panelDetalleCompras.setLayout(new java.awt.BorderLayout());

        lblDetalleProveedor.setText("Detalle del Proveedor:");
        panelDetalleCompras.add(lblDetalleProveedor, java.awt.BorderLayout.NORTH);

        tblDetalleCompras.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollDetalleCompras.setViewportView(tblDetalleCompras);

        panelDetalleCompras.add(scrollDetalleCompras, java.awt.BorderLayout.CENTER);

        panelTablasCompras.add(panelDetalleCompras, java.awt.BorderLayout.CENTER);

        panel_COMPRAS.add(panelTablasCompras, java.awt.BorderLayout.CENTER);

        Tabbed_Tesoreria.addTab("REPORTE DE COMPRAS", panel_COMPRAS);

        jPanelVentasTotales.setBackground(new java.awt.Color(0, 153, 102));

        lbl_TotalVenta.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_TotalVenta.setForeground(new java.awt.Color(255, 255, 255));
        lbl_TotalVenta.setText("0.00");

        lbl_totalVentas5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_totalVentas5.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalVentas5.setText("Ventas totales");

        javax.swing.GroupLayout jPanelVentasTotalesLayout = new javax.swing.GroupLayout(jPanelVentasTotales);
        jPanelVentasTotales.setLayout(jPanelVentasTotalesLayout);
        jPanelVentasTotalesLayout.setHorizontalGroup(
            jPanelVentasTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentasTotalesLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(jPanelVentasTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentasTotalesLayout.createSequentialGroup()
                        .addComponent(lbl_TotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentasTotalesLayout.createSequentialGroup()
                        .addComponent(lbl_totalVentas5)
                        .addGap(44, 44, 44))))
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

        jPanelTiendaTOP.setBackground(new java.awt.Color(255, 153, 0));

        lbl_TiendaTOP.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        lbl_TiendaTOP.setForeground(new java.awt.Color(255, 255, 255));
        lbl_TiendaTOP.setText("SUCURSAL A");

        lbl_totalVentas4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_totalVentas4.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalVentas4.setText("Tienda TOP");

        javax.swing.GroupLayout jPanelTiendaTOPLayout = new javax.swing.GroupLayout(jPanelTiendaTOP);
        jPanelTiendaTOP.setLayout(jPanelTiendaTOPLayout);
        jPanelTiendaTOPLayout.setHorizontalGroup(
            jPanelTiendaTOPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTiendaTOPLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(lbl_totalVentas4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTiendaTOPLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(lbl_TiendaTOP, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanelTiendaTOPLayout.setVerticalGroup(
            jPanelTiendaTOPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTiendaTOPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_totalVentas4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_TiendaTOP)
                .addGap(24, 24, 24))
        );

        jPanelPromedioVentas.setBackground(new java.awt.Color(0, 153, 204));

        lbl_totalVentas3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_totalVentas3.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalVentas3.setText("Promedio ventas");

        lbl_promedioVentas.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        lbl_promedioVentas.setForeground(new java.awt.Color(255, 255, 255));
        lbl_promedioVentas.setText("SUCURSAL A");

        javax.swing.GroupLayout jPanelPromedioVentasLayout = new javax.swing.GroupLayout(jPanelPromedioVentas);
        jPanelPromedioVentas.setLayout(jPanelPromedioVentasLayout);
        jPanelPromedioVentasLayout.setHorizontalGroup(
            jPanelPromedioVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromedioVentasLayout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanelPromedioVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_promedioVentas)
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
            .addGap(0, 0, Short.MAX_VALUE)
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
                .addGap(25, 25, 25)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanelVentasTotales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanelTiendaTOP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanelPromedioVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanelTiendaTOP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_rankingXVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout panel_RankingLayout = new javax.swing.GroupLayout(panel_Ranking);
        panel_Ranking.setLayout(panel_RankingLayout);
        panel_RankingLayout.setHorizontalGroup(
            panel_RankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_RankingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        panel_RankingLayout.setVerticalGroup(
            panel_RankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_RankingLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        Tabbed_Estadisticas.addTab("RANKING", panel_Ranking);

        TablaRentabilidad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "LOCALES", "INGRESOS", "COSTOS", "GANANCIA NETA", "RENTABILIDAD"
            }
        ));
        jScrollPane3.setViewportView(TablaRentabilidad);

        costos_totales.setBackground(new java.awt.Color(153, 0, 0));

        lbl_costos.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_costos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_costos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_costos.setText("0.00");
        lbl_costos.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("COSTOS TOTALES");

        javax.swing.GroupLayout costos_totalesLayout = new javax.swing.GroupLayout(costos_totales);
        costos_totales.setLayout(costos_totalesLayout);
        costos_totalesLayout.setHorizontalGroup(
            costos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(costos_totalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_costos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
            .addGroup(costos_totalesLayout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        costos_totalesLayout.setVerticalGroup(
            costos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(costos_totalesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbl_costos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        Ingresos_totales.setBackground(new java.awt.Color(0, 51, 153));

        lbl_totalIngresos.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_totalIngresos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos.setText("0.00");
        lbl_totalIngresos.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        lbl_Ingresos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_Ingresos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_Ingresos.setText("INGRESOS TOTALES");

        javax.swing.GroupLayout Ingresos_totalesLayout = new javax.swing.GroupLayout(Ingresos_totales);
        Ingresos_totales.setLayout(Ingresos_totalesLayout);
        Ingresos_totalesLayout.setHorizontalGroup(
            Ingresos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Ingresos_totalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22))
            .addGroup(Ingresos_totalesLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(lbl_Ingresos)
                .addContainerGap(88, Short.MAX_VALUE))
        );
        Ingresos_totalesLayout.setVerticalGroup(
            Ingresos_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Ingresos_totalesLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_Ingresos)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("FILTRAR POR FECHA:");

        ganancia_neta.setBackground(new java.awt.Color(0, 204, 0));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("GANANCIAS TOTALES");

        lbl_gananciastotales.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        lbl_gananciastotales.setForeground(new java.awt.Color(255, 255, 255));
        lbl_gananciastotales.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_gananciastotales.setText("0.00");
        lbl_gananciastotales.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout ganancia_netaLayout = new javax.swing.GroupLayout(ganancia_neta);
        ganancia_neta.setLayout(ganancia_netaLayout);
        ganancia_netaLayout.setHorizontalGroup(
            ganancia_netaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ganancia_netaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_gananciastotales, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
            .addGroup(ganancia_netaLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );
        ganancia_netaLayout.setVerticalGroup(
            ganancia_netaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ganancia_netaLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lbl_gananciastotales, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        GraficoBarrasRentabilidad.setBackground(new java.awt.Color(255, 255, 255));
        GraficoBarrasRentabilidad.setPreferredSize(new java.awt.Dimension(479, 320));

        javax.swing.GroupLayout GraficoBarrasRentabilidadLayout = new javax.swing.GroupLayout(GraficoBarrasRentabilidad);
        GraficoBarrasRentabilidad.setLayout(GraficoBarrasRentabilidadLayout);
        GraficoBarrasRentabilidadLayout.setHorizontalGroup(
            GraficoBarrasRentabilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
        );
        GraficoBarrasRentabilidadLayout.setVerticalGroup(
            GraficoBarrasRentabilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        RangoFechas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Anual", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        RangoFechas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RangoFechasActionPerformed(evt);
            }
        });

        Exportar1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Exportar1.setText("EXPORTAR:");

        PDFRentabilidad.setText("PDF");
        PDFRentabilidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFRentabilidadActionPerformed(evt);
            }
        });

        ExcelRentabilidad.setText("Excel");
        ExcelRentabilidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExcelRentabilidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2)
                        .addGap(33, 33, 33)
                        .addComponent(RangoFechas, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Exportar1)
                        .addGap(29, 29, 29)
                        .addComponent(PDFRentabilidad)
                        .addGap(5, 5, 5)
                        .addComponent(ExcelRentabilidad)
                        .addGap(337, 337, 337))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(GraficoBarrasRentabilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(Ingresos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(costos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ganancia_neta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(157, Short.MAX_VALUE))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(RangoFechas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(Exportar1))
                    .addComponent(PDFRentabilidad)
                    .addComponent(ExcelRentabilidad))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(costos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Ingresos_totales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ganancia_neta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GraficoBarrasRentabilidad, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                .addContainerGap(130, Short.MAX_VALUE))
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
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Tabbed_Estadisticas.addTab("RENTABILIDAD", panel_Rentabilidad);

        javax.swing.GroupLayout panel_EstadisticasLayout = new javax.swing.GroupLayout(panel_Estadisticas);
        panel_Estadisticas.setLayout(panel_EstadisticasLayout);
        panel_EstadisticasLayout.setHorizontalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, 1157, Short.MAX_VALUE)
        );
        panel_EstadisticasLayout.setVerticalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas)
        );

        Tabbed_Tesoreria.addTab("ESTADISTICAS", panel_Estadisticas);

        tb_cajasGrandesPorSucursal.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tb_cajasGrandesPorSucursal);

        btn_transferir.setText("TRANSFERIR");

        CB_SURUSAL1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cb_sucursal1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tf_montoAtransferir.setText("0");

        tb_movimientos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tb_movimientos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CB_SURUSAL1, 0, 202, Short.MAX_VALUE)
                            .addComponent(cb_sucursal1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(83, 83, 83)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tf_montoAtransferir)
                            .addComponent(btn_transferir, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CB_SURUSAL1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tf_montoAtransferir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cb_sucursal1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                            .addComponent(btn_transferir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(284, 284, 284))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Tabbed_Tesoreria.addTab("GESTION DE CAJA", jPanel1);

        add(Tabbed_Tesoreria, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cb_tipoRankingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_tipoRankingActionPerformed
        cargarDatosRanking();
    }//GEN-LAST:event_cb_tipoRankingActionPerformed

    private void FiltrarporMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltrarporMesActionPerformed
        cargarDatosRanking();
    }//GEN-LAST:event_FiltrarporMesActionPerformed

    private void PDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFActionPerformed
        accionBotonPDF();
    }//GEN-LAST:event_PDFActionPerformed

    private void ExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExcelActionPerformed
        accionBotonExcel();
    }//GEN-LAST:event_ExcelActionPerformed

    private void btnFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFechaActionPerformed

    private void btnFiltrarComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarComprasActionPerformed
        aplicarFiltrosCompras();
    }//GEN-LAST:event_btnFiltrarComprasActionPerformed

    private void btnExportarComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarComprasActionPerformed
        exportarTablaCSV(tblResumenCompras);
    }//GEN-LAST:event_btnExportarComprasActionPerformed

    private void PDFRentabilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFRentabilidadActionPerformed
        if (chartRentabilidad != null) {
            GeneradorPDFRentabilidad.generarPDF(TablaRentabilidad, chartRentabilidad, "Rentabilidad_2025");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "El gráfico aún no se ha cargado. Intenta refrescar.");
        }
    }//GEN-LAST:event_PDFRentabilidadActionPerformed

    private void ExcelRentabilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExcelRentabilidadActionPerformed
        GeneradorExcelRentabilidad.generarExcel(TablaRentabilidad, "Rentabilidad_2025");
    }//GEN-LAST:event_ExcelRentabilidadActionPerformed

    private void RangoFechasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RangoFechasActionPerformed
        cargarDatosRentabilidad();
    }//GEN-LAST:event_RangoFechasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CB_SURUSAL1;
    private javax.swing.JButton Excel;
    private javax.swing.JButton ExcelRentabilidad;
    private javax.swing.JLabel Exportar;
    private javax.swing.JLabel Exportar1;
    private javax.swing.JLabel FiltrarporFecha;
    private javax.swing.JComboBox<String> FiltrarporMes;
    private javax.swing.JPanel GraficoBarras;
    private javax.swing.JPanel GraficoBarrasRentabilidad;
    private javax.swing.JPanel Ingresos_totales;
    private javax.swing.JButton PDF;
    private javax.swing.JButton PDFRentabilidad;
    private javax.swing.JComboBox<String> RangoFechas;
    private javax.swing.JTabbedPane Tabbed_Estadisticas;
    private javax.swing.JTabbedPane Tabbed_Tesoreria;
    private javax.swing.JTable TablaRanking;
    private javax.swing.JTable TablaRentabilidad;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnExportarCompras;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnFiltrarCompras;
    private javax.swing.JButton btn_transferir;
    private javax.swing.JComboBox<String> cbProveedorCompras;
    private javax.swing.JComboBox<String> cbTiendaCompras;
    private javax.swing.JComboBox<String> cb_sucursal1;
    private javax.swing.JComboBox<String> cb_tipoRanking;
    private javax.swing.JComboBox<String> cmbProducto;
    private javax.swing.JComboBox<String> cmbTienda;
    private javax.swing.JPanel costos_totales;
    private com.toedter.calendar.JDateChooser dcFechaFinCompras;
    private com.toedter.calendar.JDateChooser dcFechaInicioCompras;
    private javax.swing.JPanel ganancia_neta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelPromedioVentas;
    private javax.swing.JPanel jPanelTiendaTOP;
    private javax.swing.JPanel jPanelVentasTotales;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblDesdeCompras;
    private javax.swing.JLabel lblDetalleProveedor;
    private javax.swing.JLabel lblFiltrarPor;
    private javax.swing.JLabel lblFiltrarPor_Pcompras;
    private javax.swing.JLabel lblHastaCompras;
    private javax.swing.JLabel lblMediosPago;
    private javax.swing.JLabel lblProveedorCompras;
    private javax.swing.JLabel lblTienda_Pcompras;
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
    private javax.swing.JLabel lbl_Ingresos;
    private javax.swing.JLabel lbl_TiendaTOP;
    private javax.swing.JLabel lbl_TotalVenta;
    private javax.swing.JLabel lbl_costos;
    private javax.swing.JLabel lbl_gananciastotales;
    private javax.swing.JLabel lbl_promedioVentas;
    private javax.swing.JLabel lbl_total2;
    private javax.swing.JLabel lbl_totalIngresos;
    private javax.swing.JLabel lbl_totalVentas3;
    private javax.swing.JLabel lbl_totalVentas4;
    private javax.swing.JLabel lbl_totalVentas5;
    private javax.swing.JPanel panelDetalleCompras;
    private javax.swing.JPanel panelFiltrosCompras;
    private javax.swing.JPanel panelTablasCompras;
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
    private javax.swing.JPanel panel_rankingXVentas;
    private javax.swing.JScrollPane scrollDetalleCompras;
    private javax.swing.JScrollPane scrollResumenCompras;
    private javax.swing.JTable tb_cajasGrandesPorSucursal;
    private javax.swing.JTable tb_movimientos;
    private javax.swing.JTable tblDetalleCompras;
    private javax.swing.JTable tblResumenCompras;
    private javax.swing.JTextField tf_montoAtransferir;
    // End of variables declaration//GEN-END:variables
}
