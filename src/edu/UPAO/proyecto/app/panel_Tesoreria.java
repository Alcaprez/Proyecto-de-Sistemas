package edu.UPAO.proyecto.app;

import java.awt.Color;
import edu.UPAO.proyecto.DAO.RentabilidadDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import java.awt.Color;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author ALBERTH
 */
public class panel_Tesoreria extends javax.swing.JPanel {

    private RentabilidadDAO rentabilidadDAO;
    private SucursalDAO sucursalDAO;
    private Date fechaInicioActual;
    private Date fechaFinActual;

    private void verificarJFreeChart() {
        try {
            System.out.println("=== VERIFICACIÓN JFreeChart ===");

            // Verificar que las clases existen
            Class<?> chartClass = Class.forName("org.jfree.chart.JFreeChart");
            System.out.println("✅ JFreeChart cargado: " + chartClass);

            Class<?> datasetClass = Class.forName("org.jfree.data.category.DefaultCategoryDataset");
            System.out.println("✅ DefaultCategoryDataset cargado: " + datasetClass);

            Class<?> chartPanelClass = Class.forName("org.jfree.chart.ChartPanel");
            System.out.println("✅ ChartPanel cargado: " + chartPanelClass);

            System.out.println("✅ Todas las clases de JFreeChart están disponibles");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Clase no encontrada: " + e.getMessage());
            System.err.println("⚠️ Verifica que los JARs de JFreeChart 1.5.3 estén en el classpath");
        }
    }

    public panel_Tesoreria() {

        initComponents();
       
        inicializarDAOs();
        verificarJFreeChart();
        inicializarFechasPorDefecto();
        verificarMovimientosRecientes();
        diagnosticarVentas();
        configurarComponentes();
        cargarSucursales();
        actualizarDatos();
        diagnosticoCompleto();
    }

    private void inicializarFechasPorDefecto() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30); // Últimos 30 días
        this.fechaInicioActual = cal.getTime();
        this.fechaFinActual = new Date();

        System.out.println("✅ Fechas inicializadas por defecto:");
        System.out.println("   - Inicio: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaInicioActual));
        System.out.println("   - Fin: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaFinActual));
    }

    private void diagnosticarVentas() {
        try {
            // Usar la conexión del RentabilidadDAO
            String sql = "SELECT id_venta, fecha_hora, total, id_sucursal FROM venta ORDER BY fecha_hora DESC";
            java.sql.Connection conexion = rentabilidadDAO.getConexion(); // Necesitas este método en RentabilidadDAO

            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("=== DIAGNÓSTICO DE VENTAS EN BD ===");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Venta " + count + ": ID=" + rs.getInt("id_venta")
                        + ", Fecha=" + rs.getTimestamp("fecha_hora")
                        + ", Total=" + rs.getDouble("total")
                        + ", Sucursal=" + rs.getInt("id_sucursal"));
            }
            System.out.println("Total de ventas en BD: " + count);

        } catch (SQLException e) {
            System.err.println("❌ Error en diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void diagnosticoCompleto() {
        try {
            System.out.println("=== DIAGNÓSTICO COMPLETO ===");

            // Verificar conexión
            if (rentabilidadDAO == null) {
                System.err.println("❌ RentabilidadDAO es null");
                return;
            }

            // Verificar sucursales en ComboBox
            System.out.println("📋 Sucursales en ComboBox:");
            for (int i = 0; i < filtro_sucursal.getItemCount(); i++) {
                System.out.println("   - " + filtro_sucursal.getItemAt(i));
            }
            System.out.println("   - Seleccionada: " + filtro_sucursal.getSelectedItem());

            // Verificar rangos en ComboBox  
            System.out.println("📋 Rangos en ComboBox:");
            for (int i = 0; i < cb_rangos.getItemCount(); i++) {
                System.out.println("   - " + cb_rangos.getItemAt(i));
            }
            System.out.println("   - Seleccionado: " + cb_rangos.getSelectedItem());

            // Verificar fechas actuales
            System.out.println("📅 Fechas actuales:");
            System.out.println("   - Inicio: " + fechaInicioActual);
            System.out.println("   - Fin: " + fechaFinActual);

        } catch (Exception e) {
            System.err.println("❌ Error en diagnóstico: " + e.getMessage());
        }
    }

    private void inicializarDAOs() {
        try {
            this.rentabilidadDAO = new RentabilidadDAO();
            this.sucursalDAO = new SucursalDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al inicializar conexiones: " + e.getMessage());
        }
    }

    private void configurarComponentes() {
        configurarTabla();
        configurarComboRangos();

        // ✅ AGREGAR ACTION LISTENER PARA COMBOBOX DE SUCURSAL
        filtro_sucursal.addActionListener(e -> {
            System.out.println("🔄 Cambio de sucursal detectado: " + filtro_sucursal.getSelectedItem());
            actualizarDatos();
        });

        // También agregar para el ComboBox de rangos si no está
        cb_rangos.addActionListener(e -> {
            String rangoSeleccionado = (String) cb_rangos.getSelectedItem();
            System.out.println("🔄 Cambio de rango detectado: " + rangoSeleccionado);
            aplicarRangoPredefinido(rangoSeleccionado);
        });
    }

    private void configurarComboRangos() {
        // Limpiar y agregar opciones al ComboBox
        cb_rangos.removeAllItems();

        cb_rangos.addItem("Ultimos 7 dias");
        cb_rangos.addItem("Ultimos 30 dias");
        cb_rangos.addItem("Este mes");
        cb_rangos.addItem("Mes anterior");
        cb_rangos.addItem("Ultimos 3 meses");
        cb_rangos.addItem("Personalizado");

        // Establecer valor por defecto
        cb_rangos.setSelectedItem("Ultimos 30 dias");

        // Agregar ActionListener
        cb_rangos.addActionListener(e -> {
            String rangoSeleccionado = (String) cb_rangos.getSelectedItem();
            aplicarRangoPredefinido(rangoSeleccionado);
        });
    }

    private void aplicarRangoPredefinido(String rango) {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            Date fechaFin = new Date();
            Date fechaInicio;

            System.out.println("🔄 Aplicando rango: " + rango);

            // Reiniciar calendario
            cal.setTime(new Date());

            switch (rango) {
                case "Ultimos 7 dias":
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                    fechaInicio = cal.getTime();
                    break;

                case "Ultimos 30 dias":
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
                    fechaInicio = cal.getTime();
                    break;

                case "Este mes":
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
                    fechaInicio = cal.getTime();
                    break;

                case "Mes anterior":
                    cal.add(java.util.Calendar.MONTH, -1);
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
                    fechaInicio = cal.getTime();

                    Calendar calFin = Calendar.getInstance();
                    calFin.setTime(fechaInicio);
                    calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
                    fechaFin = calFin.getTime();
                    break;

                case "Ultimos 3 meses":
                    cal.add(java.util.Calendar.MONTH, -3);
                    fechaInicio = cal.getTime();
                    break;

                case "Personalizado":
                    cal.add(java.util.Calendar.MONTH, -1);
                    fechaInicio = cal.getTime();
                    System.out.println("⚠️ Modo personalizado no implementado, usando ultimo mes");
                    break;

                default:
                    // Por defecto, últimos 30 días
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
                    fechaInicio = cal.getTime();
                    break;
            }

            // ✅ ASIGNAR FECHAS CON VALIDACIÓN
            if (fechaInicio != null && fechaFin != null) {
                this.fechaInicioActual = fechaInicio;
                this.fechaFinActual = fechaFin;
            } else {
                // Fallback seguro
                inicializarFechasPorDefecto();
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            System.out.println("📅 Rango aplicado: " + rango);
            System.out.println("   - Fecha inicio: " + sdf.format(fechaInicioActual));
            System.out.println("   - Fecha fin: " + sdf.format(fechaFinActual));

            // Actualizar datos
            actualizarDatos();

        } catch (Exception e) {
            System.err.println("❌ Error en aplicarRangoPredefinido: " + e.getMessage());
            e.printStackTrace();
            // Asegurar que las fechas tengan valores
            inicializarFechasPorDefecto();
        }
    }

    private void verificarDatosEnRango(Date fechaInicio, Date fechaFin) {
        try {
            String sql = "SELECT "
                    + "SUM(CASE WHEN tipo = 'VENTA' THEN 1 ELSE 0 END) as ventas, "
                    + "SUM(CASE WHEN tipo = 'COMPRA' THEN 1 ELSE 0 END) as compras, "
                    + "SUM(CASE WHEN tipo IN ('INGRESO', 'GASTO') THEN 1 ELSE 0 END) as movimientos_caja "
                    + "FROM movimiento_caja "
                    + "WHERE DATE(fecha_hora) BETWEEN DATE(?) AND DATE(?)";

            java.sql.Connection conn = rentabilidadDAO.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("📊 DATOS EN RANGO:");
                System.out.println("   - Ventas: " + rs.getInt("ventas"));
                System.out.println("   - Compras: " + rs.getInt("compras"));
                System.out.println("   - Movimientos Caja: " + rs.getInt("movimientos_caja"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error verificando datos en rango: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        String[] columnNames = {"Fecha", "Sucursal", "Descripción", "Cantidad", "Ingreso", "Costo", "Ganancia", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Todas las columnas como String para formato consistente
            }
        };
        jTable3.setModel(model);

        // ✅ MEJORAR ANCHOS DE COLUMNAS
        jTable3.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable3.getColumnModel().getColumn(0).setPreferredWidth(100); // Fecha
        jTable3.getColumnModel().getColumn(1).setPreferredWidth(120); // Sucursal
        jTable3.getColumnModel().getColumn(2).setPreferredWidth(250); // Descripción
        jTable3.getColumnModel().getColumn(3).setPreferredWidth(70);  // Cantidad
        jTable3.getColumnModel().getColumn(4).setPreferredWidth(90);  // Ingreso
        jTable3.getColumnModel().getColumn(5).setPreferredWidth(90);  // Costo
        jTable3.getColumnModel().getColumn(6).setPreferredWidth(90);  // Ganancia
        jTable3.getColumnModel().getColumn(7).setPreferredWidth(80);  // Tipo

        jTable3.setFillsViewportHeight(true);
    }

    private void cargarSucursales() {
        try {
            List<String> sucursales = sucursalDAO.obtenerSucursalesActivas();
            filtro_sucursal.removeAllItems();

            // ✅ AGREGAR "TODAS" COMO PRIMERA OPCIÓN
            filtro_sucursal.addItem("TODAS");

            for (String sucursal : sucursales) {
                filtro_sucursal.addItem(sucursal);
            }

            // ✅ ESTABLECER "TODAS" COMO SELECCIÓN POR DEFECTO
            filtro_sucursal.setSelectedItem("TODAS");

            System.out.println("✅ Sucursales cargadas: " + (sucursales.size() + 1) + " (incluyendo TODAS)");

            // ✅ DIAGNÓSTICO DETALLADO DE SUCURSALES
            System.out.println("📋 LISTA COMPLETA DE SUCURSALES:");
            for (int i = 0; i < filtro_sucursal.getItemCount(); i++) {
                System.out.println("   - " + filtro_sucursal.getItemAt(i));
            }

        } catch (Exception e) {
            System.err.println("❌ Error al cargar sucursales: " + e.getMessage());
            e.printStackTrace();

            // ✅ CARGAR OPCIONES POR DEFECTO EN CASO DE ERROR
            filtro_sucursal.removeAllItems();
            filtro_sucursal.addItem("TODAS");
            filtro_sucursal.addItem("Tienda Central");
            filtro_sucursal.addItem("Sucursal Norte");
            filtro_sucursal.addItem("Sucursal Sur");

            filtro_sucursal.setSelectedItem("TODAS");
        }
    }

    private void actualizarPanelesRentabilidad(Map<String, Double> datos) {
        // Actualizar los JLabels EXISTENTES con los valores calculados
        double ingresos = datos.getOrDefault("ingresos_totales", 0.0);
        double costos = datos.getOrDefault("costos_totales", 0.0);
        double gastosCompras = datos.getOrDefault("gastos_compras", 0.0);
        double otrosGastos = datos.getOrDefault("otros_gastos", 0.0);
        double gananciaNeta = datos.getOrDefault("ganancia_neta", 0.0);

        // Calcular total de gastos
        double totalGastos = costos + gastosCompras + otrosGastos;

        // Actualizar labels
        lbl_totalIngresos.setText(String.format("S/ %.2f", ingresos));
        lbl_total2.setText(String.format("S/ %.2f", totalGastos));
        lbl_gananciaNeta.setText(String.format("S/ %.2f", gananciaNeta));

        // Color según ganancia/pérdida
        lbl_gananciaNeta.setForeground(gananciaNeta >= 0 ? Color.WHITE : Color.RED);

        System.out.println("📊 RESUMEN EN PANTALLA:");
        System.out.println("   - Ingresos: " + ingresos);
        System.out.println("   - Gastos totales: " + totalGastos);
        System.out.println("   - Ganancia Neta: " + gananciaNeta);
    }

// ✅ AGREGAR MÉTODO PARA VERIFICAR FILTROS
    private void verificarFiltros(Date fechaInicio, Date fechaFin) {
        System.out.println("🔍 VERIFICANDO FILTROS:");
        System.out.println("   - Fecha inicio: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaInicio));
        System.out.println("   - Fecha fin: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaFin));
        System.out.println("   - Días de diferencia: "
                + ((fechaFin.getTime() - fechaInicio.getTime()) / (1000 * 60 * 60 * 24)) + " días");

        // Verificar datos en cada tabla
        verificarDatosTablas(fechaInicio, fechaFin);
    }

// ✅ NUEVO MÉTODO: VERIFICAR DATOS EN TABLAS
    private void verificarDatosTablas(Date fechaInicio, Date fechaFin) {
        try {
            String[] tablas = {"venta", "compra", "movimiento_caja"};

            for (String tabla : tablas) {
                String sql = "SELECT COUNT(*) as total FROM " + tabla
                        + " WHERE DATE(fecha_hora) BETWEEN DATE(?) AND DATE(?)";

                PreparedStatement stmt = rentabilidadDAO.getConexion().prepareStatement(sql);
                stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
                stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("   - " + tabla.toUpperCase() + ": " + rs.getInt("total") + " registros");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error verificando tablas: " + e.getMessage());
        }
    }

    private void actualizarDatos() {
        if (rentabilidadDAO == null) {
            JOptionPane.showMessageDialog(this, "Error: No hay conexión a la base de datos");
            return;
        }

        try {
            if (fechaInicioActual == null || fechaFinActual == null) {
                System.err.println("❌ Fechas son null, inicializando por defecto");
                inicializarFechasPorDefecto();
            }

            Date fechaInicio = this.fechaInicioActual;
            Date fechaFin = this.fechaFinActual;

            String sucursalSeleccionada = "TODAS";
            if (filtro_sucursal.getSelectedItem() != null) {
                sucursalSeleccionada = filtro_sucursal.getSelectedItem().toString();
            }

            String rangoSeleccionado = (String) cb_rangos.getSelectedItem();

            System.out.println("🔄 ACTUALIZANDO DATOS...");
            System.out.println("   - Rango: " + rangoSeleccionado);
            System.out.println("   - Fechas: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaInicio)
                    + " a " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaFin));
            System.out.println("   - Sucursal: " + sucursalSeleccionada);

            // 1. Obtener datos de rentabilidad
            Map<String, Double> datosRentabilidad = rentabilidadDAO.calcularRentabilidadReal(
                    fechaInicio, fechaFin, sucursalSeleccionada
            );

            // 2. Actualizar paneles de resumen
            actualizarPanelesRentabilidad(datosRentabilidad);

            // 3. Actualizar tabla de movimientos
            actualizarTablaMovimientos(fechaInicio, fechaFin, sucursalSeleccionada);

            // 4. Actualizar gráfico
            actualizarGraficoBarrasSimple(fechaInicio, fechaFin);

            System.out.println("✅ DATOS ACTUALIZADOS CORRECTAMENTE");

        } catch (Exception e) {
            System.err.println("❌ Error en actualizarDatos: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar datos: " + e.getMessage());
        }
    }

    private void actualizarTablaMovimientos(Date fechaInicio, Date fechaFin, String sucursal) {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0); // Limpiar tabla

        try {
            // Obtener movimientos desde el DAO
            List<Object[]> movimientos = rentabilidadDAO.obtenerMovimientosFinancieros(
                    fechaInicio, fechaFin, sucursal
            );

            for (Object[] movimiento : movimientos) {
                model.addRow(movimiento);
            }

            // Mostrar resumen en consola
            System.out.println("=== MOVIMIENTOS FINANCIEROS ===");
            System.out.println("Período: " + fechaInicio + " a " + fechaFin);
            System.out.println("Sucursal: " + sucursal);
            System.out.println("Total de movimientos: " + movimientos.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar movimientos: " + e.getMessage());
        }
    }

    private void actualizarGraficoBarrasSimple(Date fechaInicio, Date fechaFin) {
        try {
            // Obtener datos REALES de la base de datos
            Map<String, Double> ventasSucursal = rentabilidadDAO.obtenerVentasPorSucursal(fechaInicio, fechaFin);

            System.out.println("📊 DATOS PARA GRÁFICO: " + ventasSucursal);

            // Crear dataset
            DefaultCategoryDataset datos = new DefaultCategoryDataset();

            // Agregar datos reales
            for (Map.Entry<String, Double> entry : ventasSucursal.entrySet()) {
                String sucursal = entry.getKey();
                Double ventas = entry.getValue();
                datos.setValue(ventas, "Ventas", sucursal);
                System.out.println("   - " + sucursal + ": S/ " + ventas);
            }

            // Si no hay datos, mostrar mensaje
            if (datos.getRowCount() == 0) {
                System.out.println("⚠️ No hay datos de ventas para el gráfico");
                datos.setValue(0, "Ventas", "Sin datos");
            }

            // Crear gráfico
            JFreeChart grafico_barras = ChartFactory.createBarChart(
                    "VENTAS POR SUCURSAL (" + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaInicio)
                    + " a " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaFin) + ")",
                    "Sucursales",
                    "Ventas (S/)",
                    datos,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // Configurar panel
            ChartPanel panel = new ChartPanel(grafico_barras);
            panel.setMouseWheelEnabled(true);
            panel.setPreferredSize(new java.awt.Dimension(320, 350)); // Tamaño optimizado

            // Actualizar interfaz
            panel_estadisticas.removeAll();
            panel_estadisticas.setLayout(new java.awt.BorderLayout());
            panel_estadisticas.add(panel, java.awt.BorderLayout.CENTER);
            panel_estadisticas.revalidate();
            panel_estadisticas.repaint();

            System.out.println("✅ Gráfico actualizado correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al crear gráfico: " + e.getMessage());
            e.printStackTrace();

            // Mostrar mensaje de error
            panel_estadisticas.removeAll();
            panel_estadisticas.setLayout(new java.awt.BorderLayout());
            JLabel errorLabel = new JLabel("<html><center>Error al cargar gráfico<br>"
                    + e.getMessage() + "</center></html>", JLabel.CENTER);
            errorLabel.setForeground(Color.RED);
            panel_estadisticas.add(errorLabel, java.awt.BorderLayout.CENTER);
            panel_estadisticas.revalidate();
        }
    }

    private void verificarDatosEnTiempoReal(Date fechaInicio, Date fechaFin, String sucursal) {
        try {
            String sql = "SELECT COUNT(*) as total_ventas, COALESCE(SUM(total), 0) as total_ingresos "
                    + "FROM venta v "
                    + "JOIN sucursal s ON v.id_sucursal = s.id_sucursal "
                    + "WHERE DATE(v.fecha_hora) BETWEEN DATE(?) AND DATE(?) "
                    + "AND (? = 'TODAS' OR s.nombre_sucursal = ?)";

            java.sql.Connection conn = rentabilidadDAO.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, sucursal);
            stmt.setString(4, sucursal);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("🔍 VERIFICACIÓN EN TIEMPO REAL:");
                System.out.println("   - Ventas en rango: " + rs.getInt("total_ventas"));
                System.out.println("   - Ingresos totales: " + rs.getDouble("total_ingresos"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en verificación: " + e.getMessage());
        }
    }

    private void verificarMovimientosRecientes() {
        try {
            String sql = "SELECT COUNT(*) as total, "
                    + "SUM(CASE WHEN tipo = 'VENTA' THEN 1 ELSE 0 END) as ventas, "
                    + "SUM(CASE WHEN tipo = 'COMPRA' THEN 1 ELSE 0 END) as compras "
                    + "FROM movimiento_caja "
                    + "WHERE fecha_hora >= DATE_SUB(NOW(), INTERVAL 7 DAY)";

            java.sql.Connection conn = rentabilidadDAO.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("🔍 MOVIMIENTOS RECIENTES (7 días):");
                System.out.println("   - Total: " + rs.getInt("total"));
                System.out.println("   - Ventas: " + rs.getInt("ventas"));
                System.out.println("   - Compras: " + rs.getInt("compras"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error verificando movimientos: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_total2 = new javax.swing.JLabel();
        Tabbed_Tesoreria = new javax.swing.JTabbedPane();
        panel_Ventas = new javax.swing.JPanel();
        cb_sucursal = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cb_productos = new javax.swing.JComboBox<>();
        cb_rangoFechas = new javax.swing.JComboBox<>();
        btn_exportar = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        lbl_rentabilidad1 = new javax.swing.JLabel();
        lbl_rentabilidad4 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lbl_rentabilidad3 = new javax.swing.JLabel();
        lbl_totalVentas = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lbl_rentabilidad2 = new javax.swing.JLabel();
        lbl_productosVendidos = new javax.swing.JLabel();
        grafico_ventasdiarias = new javax.swing.JPanel();
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
        cb_rangos = new javax.swing.JComboBox<>();

        lbl_total2.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_total2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total2.setText("0.00");
        lbl_total2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        cb_sucursal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_sucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_sucursalActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText("Filtrar por datos:");

        cb_productos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cb_rangoFechas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btn_exportar.setText("Exportar");

        jPanel11.setBackground(new java.awt.Color(51, 153, 0));

        lbl_rentabilidad1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad1.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad1.setText("0.00");

        lbl_rentabilidad4.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad4.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad4.setText("TOTAL EN SOLES");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_rentabilidad4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(lbl_rentabilidad1)
                        .addGap(77, 77, 77)))
                .addGap(19, 19, 19))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lbl_rentabilidad4)
                .addGap(18, 18, 18)
                .addComponent(lbl_rentabilidad1)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jPanel12.setBackground(new java.awt.Color(255, 153, 0));

        lbl_rentabilidad3.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad3.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad3.setText("TOTAL DE VENTAS");

        lbl_totalVentas.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_totalVentas.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalVentas.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(lbl_totalVentas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_rentabilidad3)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lbl_rentabilidad3)
                .addGap(18, 18, 18)
                .addComponent(lbl_totalVentas)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(0, 102, 204));

        lbl_rentabilidad2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_rentabilidad2.setForeground(new java.awt.Color(0, 0, 0));
        lbl_rentabilidad2.setText("PRODUCTOS VEND");

        lbl_productosVendidos.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lbl_productosVendidos.setForeground(new java.awt.Color(0, 0, 0));
        lbl_productosVendidos.setText("0");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addComponent(lbl_rentabilidad2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addComponent(lbl_productosVendidos)
                        .addGap(21, 21, 21))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lbl_rentabilidad2)
                .addGap(18, 18, 18)
                .addComponent(lbl_productosVendidos)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        grafico_ventasdiarias.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout grafico_ventasdiariasLayout = new javax.swing.GroupLayout(grafico_ventasdiarias);
        grafico_ventasdiarias.setLayout(grafico_ventasdiariasLayout);
        grafico_ventasdiariasLayout.setHorizontalGroup(
            grafico_ventasdiariasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 366, Short.MAX_VALUE)
        );
        grafico_ventasdiariasLayout.setVerticalGroup(
            grafico_ventasdiariasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        grafico_ventasXProducto.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout grafico_ventasXProductoLayout = new javax.swing.GroupLayout(grafico_ventasXProducto);
        grafico_ventasXProducto.setLayout(grafico_ventasXProductoLayout);
        grafico_ventasXProductoLayout.setHorizontalGroup(
            grafico_ventasXProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 367, Short.MAX_VALUE)
        );
        grafico_ventasXProductoLayout.setVerticalGroup(
            grafico_ventasXProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        grafico_ventasMensuales.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout grafico_ventasMensualesLayout = new javax.swing.GroupLayout(grafico_ventasMensuales);
        grafico_ventasMensuales.setLayout(grafico_ventasMensualesLayout);
        grafico_ventasMensualesLayout.setHorizontalGroup(
            grafico_ventasMensualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
        );
        grafico_ventasMensualesLayout.setVerticalGroup(
            grafico_ventasMensualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        grafico_metodosPago.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout grafico_metodosPagoLayout = new javax.swing.GroupLayout(grafico_metodosPago);
        grafico_metodosPago.setLayout(grafico_metodosPagoLayout);
        grafico_metodosPagoLayout.setHorizontalGroup(
            grafico_metodosPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );
        grafico_metodosPagoLayout.setVerticalGroup(
            grafico_metodosPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panel_VentasLayout = new javax.swing.GroupLayout(panel_Ventas);
        panel_Ventas.setLayout(panel_VentasLayout);
        panel_VentasLayout.setHorizontalGroup(
            panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_VentasLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(panel_VentasLayout.createSequentialGroup()
                        .addComponent(cb_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_productos, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cb_rangoFechas, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btn_exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_VentasLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(grafico_ventasdiarias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grafico_ventasXProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grafico_ventasMensuales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grafico_metodosPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        panel_VentasLayout.setVerticalGroup(
            panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_VentasLayout.createSequentialGroup()
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_VentasLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cb_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_rangoFechas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_VentasLayout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))
                            .addGroup(panel_VentasLayout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(grafico_ventasMensuales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(panel_VentasLayout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(grafico_ventasdiarias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(panel_VentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_VentasLayout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 190, Short.MAX_VALUE))
                    .addComponent(grafico_ventasXProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grafico_metodosPago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

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
                .addContainerGap(82, Short.MAX_VALUE))
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
                .addContainerGap(115, Short.MAX_VALUE))
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
                .addContainerGap(26, Short.MAX_VALUE))
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

        cb_rangos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                        .addComponent(cb_rangos, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(8, Short.MAX_VALUE))
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
                            .addComponent(cb_rangos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGap(0, 90, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Tabbed_Estadisticas.addTab("RENTABILIDAD", panel_Rentabilidad);

        javax.swing.GroupLayout panel_EstadisticasLayout = new javax.swing.GroupLayout(panel_Estadisticas);
        panel_Estadisticas.setLayout(panel_EstadisticasLayout);
        panel_EstadisticasLayout.setHorizontalGroup(
            panel_EstadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Estadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, 1081, Short.MAX_VALUE)
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
            .addComponent(Tabbed_Tesoreria)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabbed_Tesoreria)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cb_sucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursalActionPerformed

    private void cb_sucursal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursal1ActionPerformed

    private void filtro_sucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtro_sucursalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtro_sucursalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Ingresos_totales;
    private javax.swing.JTabbedPane Tabbed_Estadisticas;
    private javax.swing.JTabbedPane Tabbed_Tesoreria;
    private javax.swing.JButton btn_exportar;
    private javax.swing.JButton btn_exportar1;
    private javax.swing.JButton btn_gananciaNeta;
    private javax.swing.JButton btn_rentabilidad;
    private javax.swing.JComboBox<String> cb_productos;
    private javax.swing.JComboBox<String> cb_productos1;
    private javax.swing.JComboBox<String> cb_rangoFechas;
    private javax.swing.JComboBox<String> cb_rangoFechas1;
    private javax.swing.JComboBox<String> cb_rangos;
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
    private javax.swing.JPanel grafico_ventasdiarias;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
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
    private javax.swing.JLabel lbl_productosVendidos;
    private javax.swing.JLabel lbl_promedioVentas;
    private javax.swing.JLabel lbl_rentabilidad;
    private javax.swing.JLabel lbl_rentabilidad1;
    private javax.swing.JLabel lbl_rentabilidad2;
    private javax.swing.JLabel lbl_rentabilidad3;
    private javax.swing.JLabel lbl_rentabilidad4;
    private javax.swing.JLabel lbl_total2;
    private javax.swing.JLabel lbl_totalIngresos;
    private javax.swing.JLabel lbl_totalVentas;
    private javax.swing.JLabel lbl_totalVentas1;
    private javax.swing.JLabel lbl_totalVentas2;
    private javax.swing.JLabel lbl_totalVentas3;
    private javax.swing.JLabel lbl_totalVentas4;
    private javax.swing.JLabel lbl_totalVentas5;
    private javax.swing.JPanel panel_COMPRAS;
    private javax.swing.JPanel panel_Estadisticas;
    private javax.swing.JPanel panel_Ranking;
    private javax.swing.JPanel panel_Rentabilidad;
    private javax.swing.JPanel panel_Ventas;
    private javax.swing.JPanel panel_estadisticas;
    private javax.swing.JPanel panel_rankingXVentas;
    // End of variables declaration//GEN-END:variables
}
