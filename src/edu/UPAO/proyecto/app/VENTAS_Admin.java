package edu.UPAO.proyecto.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class VENTAS_Admin extends javax.swing.JPanel {
    // DATOS DE CONEXIÓN
    String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    String usuario = "root";
    String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";
    public VENTAS_Admin() {
       initComponents();
        cargarKPIs(); 
        cargarGrafico();
        cargarDevoluciones("");
        cargarHistorialCaja("");
    }
    private void cargarKPIs() {
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            
            // A. PRODUCTOS DEVUELTOS
            // Sumamos los productos de las compras que marcamos como 'DEVUELTA' o 'ANULADA'
            String sqlDev = "SELECT SUM(dc.cantidad) FROM detalle_compra dc " +
                            "INNER JOIN compra c ON dc.id_compra = c.id_compra " +
                            "WHERE c.estado = 'DEVUELTA' OR c.estado = 'ANULADA'";
            PreparedStatement ps1 = con.prepareStatement(sqlDev);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                // Si es null (nadie ha devuelto nada), ponemos 0
                int devueltos = rs1.getInt(1);
                lblDevueltos.setText(devueltos + ""); 
            }

            // B. CANTIDAD DE VENTAS (Transacciones)
            String sqlVentas = "SELECT COUNT(*) FROM venta";
            PreparedStatement ps2 = con.prepareStatement(sqlVentas);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) lblCantVentas.setText(rs2.getInt(1) + "");

            // C. UNIDADES VENDIDAS (Suma total de productos salidos)
            String sqlUnidades = "SELECT SUM(cantidad) FROM detalle_venta";
            PreparedStatement ps3 = con.prepareStatement(sqlUnidades);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                int unidades = rs3.getInt(1);
                lblUnidades.setText(unidades + "");
            }

            // D. PRODUCTO ESTRELLA (Más vendido)
            String sqlTop = "SELECT p.nombre FROM detalle_venta dv " +
                            "JOIN producto p ON dv.id_producto = p.id_producto " +
                            "GROUP BY p.nombre ORDER BY SUM(dv.cantidad) DESC LIMIT 1";
            PreparedStatement ps4 = con.prepareStatement(sqlTop);
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) {
                // Usamos HTML para que si el nombre es largo se centre bonito
                lblTopProducto.setText("<html><center>" + rs4.getString(1) + "</center></html>");
            } else {
                lblTopProducto.setText("---");
            }

        } catch (SQLException e) {
            System.out.println("Error cargando KPIs: " + e);
        }
    }
    // --- MÉTODO 2: DIBUJAR EL GRÁFICO ---
    private void cargarGrafico() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Consulta: Suma de ventas agrupadas por mes (Formato Año-Mes)
        String sql = "SELECT DATE_FORMAT(fecha_hora, '%Y-%m') as mes, SUM(total) as total " +
                     "FROM venta " +
                     "GROUP BY mes ORDER BY mes ASC LIMIT 12"; // Últimos 12 meses

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String mes = rs.getString("mes"); 
                double monto = rs.getDouble("total");
                dataset.addValue(monto, "Ventas", mes);
            }

        } catch (SQLException e) {
            System.out.println("Error grafico: " + e);
        }

        // Crear el objeto gráfico (Línea)
        JFreeChart chart = ChartFactory.createLineChart(
                "Evolución de Ventas", // Título
                "Mes",                 // Eje X
                "Dinero (S/)",         // Eje Y
                dataset,
                PlotOrientation.VERTICAL,
                false, // Leyenda (false para ganar espacio)
                true,
                false
        );

        // Personalización Visual
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);     // Fondo blanco limpio
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY); // Líneas guía grises
        
        // Personalizar la línea (Puntos visibles)
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(46, 139, 87)); // Color verde corporativo
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f)); // Línea más gruesa
        plot.setRenderer(renderer);

        // INYECTAR EL GRÁFICO EN TU PANEL
        // Importante: Tu 'panelGrafico' debe tener Layout BorderLayout o esto no se verá bien.
        ChartPanel chartPanel = new ChartPanel(chart);
        
        // Nos aseguramos que el panelGrafico acepte el relleno completo
        panelGrafico.setLayout(new java.awt.BorderLayout());
        panelGrafico.removeAll(); // Limpiamos por si acaso
        panelGrafico.add(chartPanel, BorderLayout.CENTER);
        panelGrafico.validate();
    }
    
    private void cargarDevoluciones(String filtro) {
        DefaultTableModel modelo = (DefaultTableModel) tblDevolucionesVentas.getModel();
        modelo.setRowCount(0);
        // Ajustamos columnas para que se vean bien los datos
        modelo.setColumnIdentifiers(new Object[]{"ID Dev.", "Producto", "Fecha", "Cliente", "Importe", "Motivo"});
        
        // Anchos visuales
        tblDevolucionesVentas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblDevolucionesVentas.getColumnModel().getColumn(1).setPreferredWidth(200); 
        tblDevolucionesVentas.getColumnModel().getColumn(5).setPreferredWidth(150);

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        String sql = "SELECT d.id_devolucion, p.nombre, d.fecha_hora, " +
                     "CONCAT(per.nombres, ' ', per.apellidos) as cliente_nombre, " +
                     "dd.subtotal, d.motivo " +
                     "FROM devolucion d " +
                     "INNER JOIN detalle_devolucion dd ON d.id_devolucion = dd.id_devolucion " +
                     "INNER JOIN producto p ON dd.id_producto = p.id_producto " +
                     "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                     "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN persona per ON c.dni = per.dni " +
                     "WHERE 1=1 ";

        // --- CAMBIO AQUÍ: FILTRO POR NOMBRE DEL PRODUCTO ---
        if (filtro != null && !filtro.isEmpty()) {
            sql += " AND p.nombre LIKE '%" + filtro + "%'"; 
        }
        
        sql += " ORDER BY d.fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("id_devolucion"),
                    rs.getString("nombre"), // Muestra el nombre encontrado
                    rs.getTimestamp("fecha_hora"),
                    rs.getString("cliente_nombre"),
                    "S/ " + rs.getDouble("subtotal"),
                    rs.getString("motivo")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error buscando devoluciones: " + e);
        }
    }
    private void cargarHistorialCaja(String filtro) {
        // 1. Configurar Modelo de Tabla
        DefaultTableModel modelo = (DefaultTableModel) tblFacturas.getModel();
        modelo.setRowCount(0);
        // Columnas idénticas a tu imagen de referencia
        modelo.setColumnIdentifiers(new Object[]{"FECHA", "HORA", "CONCEPTO / DESCRIPCIÓN", "TIPO", "MONTO"});
        
        // Ajustes visuales de ancho
        tblFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);  // Fecha
        tblFacturas.getColumnModel().getColumn(1).setPreferredWidth(60);  // Hora
        tblFacturas.getColumnModel().getColumn(2).setPreferredWidth(350); // Concepto ancho
        tblFacturas.getColumnModel().getColumn(3).setPreferredWidth(100); // Tipo (Ingreso/Egreso)

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        // 2. Consulta SQL a movimiento_caja
        String sql = "SELECT fecha_hora, descripcion, tipo, monto " +
                     "FROM movimiento_caja " +
                     "WHERE id_sucursal = 1 "; // Asumiendo Sucursal 1

        // Filtro dinámico (por descripción o tipo)
        if (filtro != null && !filtro.isEmpty()) {
            sql += " AND (descripcion LIKE '%" + filtro + "%' OR tipo LIKE '%" + filtro + "%')";
        }
        
        sql += " ORDER BY fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Separamos Fecha y Hora del Timestamp para que se vea como tu diseño
                Timestamp ts = rs.getTimestamp("fecha_hora");
                String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                String hora = new java.text.SimpleDateFormat("HH:mm").format(ts);
                
                String descripcion = rs.getString("descripcion");
                String tipo = rs.getString("tipo");
                double monto = rs.getDouble("monto");
                
                // (Opcional) Formato visual para el monto según si es Ingreso o Egreso
                // Si quieres que las salidas salgan en negativo o rojo, este es el lugar.
                String montoTexto = String.format("S/ %.2f", monto);

                modelo.addRow(new Object[]{
                    fecha,
                    hora,
                    descripcion,
                    tipo,
                    montoTexto
                });
            }
        } catch (SQLException e) {
            System.out.println("Error cargando facturas: " + e);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblDevueltos = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblCantVentas = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblUnidades = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblTopProducto = new javax.swing.JLabel();
        panelGrafico = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDevolucionesVentas = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarDev = new javax.swing.JTextField();
        btnBuscarDev = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFacturas = new javax.swing.JTable();
        btnBuscarFactura = new javax.swing.JButton();
        txtBuscarFactura = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(51, 153, 0));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Productos devueltos");

        lblDevueltos.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblDevueltos.setForeground(new java.awt.Color(255, 255, 255));
        lblDevueltos.setText("jLabel3");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lblDevueltos, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblDevueltos)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(51, 153, 0));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Cantidad de ventas");

        lblCantVentas.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblCantVentas.setForeground(new java.awt.Color(255, 255, 255));
        lblCantVentas.setText("jLabel5");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lblCantVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(lblCantVentas)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(51, 153, 0));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Cantidad de unidades vendidas");

        lblUnidades.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblUnidades.setForeground(new java.awt.Color(255, 255, 255));
        lblUnidades.setText("jLabel7");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblUnidades, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 4, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(lblUnidades)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(51, 153, 0));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Producto más vendido");

        lblTopProducto.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblTopProducto.setForeground(new java.awt.Color(255, 255, 255));
        lblTopProducto.setText("jLabel9");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lblTopProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(lblTopProducto)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        panelGrafico.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(83, 83, 83)
                .addComponent(panelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(panelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("VENTAS", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tblDevolucionesVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Devolucion", "Producto", "Fecha", "Cliente", "Importe", "Motivo"
            }
        ));
        jScrollPane1.setViewportView(tblDevolucionesVentas);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("PRODUCTOS DEVUELTOS");

        txtBuscarDev.setForeground(new java.awt.Color(153, 153, 153));
        txtBuscarDev.setText("Coloque el  nombre del producto");
        txtBuscarDev.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBuscarDevFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBuscarDevFocusLost(evt);
            }
        });

        btnBuscarDev.setText("Buscar");
        btnBuscarDev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarDevActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtBuscarDev, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscarDev, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(123, 123, 123)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarDev, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarDev, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tblFacturas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblFacturas);

        btnBuscarFactura.setText("Buscar factura/ refresh");

        txtBuscarFactura.setText("jTextField1");
        txtBuscarFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarFacturaKeyReleased(evt);
            }
        });

        jButton1.setText("Ver pdf");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtBuscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(214, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(99, 99, 99))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarFactura))
                .addGap(44, 44, 44)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(55, 55, 55))
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

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

        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarDevFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBuscarDevFocusGained
    if (txtBuscarDev.getText().equals("Coloque el  nombre del producto")) {
        txtBuscarDev.setText(""); // Borra el texto automáticamente
        txtBuscarDev.setForeground(new java.awt.Color(0, 0, 0)); // Pone la letra negra normal
    }
    }//GEN-LAST:event_txtBuscarDevFocusGained

    private void btnBuscarDevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarDevActionPerformed
       String texto = txtBuscarDev.getText().trim();
        String placeholder = "Coloque el  nombre del producto"; 
        
        // Si está vacío o tiene el texto de ayuda, carga todo
        if (texto.isEmpty() || texto.equals(placeholder)) {
            cargarDevoluciones(""); 
        } else {
            cargarDevoluciones(texto); // Busca por el nombre escrito
        }
    }//GEN-LAST:event_btnBuscarDevActionPerformed

    private void txtBuscarDevFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBuscarDevFocusLost
    if (txtBuscarDev.getText().isEmpty()) {
        txtBuscarDev.setText("Coloque el  nombre del producto"); // Restaura el mensaje si no escribiste nada
        txtBuscarDev.setForeground(new java.awt.Color(153, 153, 153)); // Lo pone gris clarito
    }        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarDevFocusLost

    private void txtBuscarFacturaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarFacturaKeyReleased
     String texto = txtBuscarFactura.getText().trim();
        cargarHistorialCaja(texto);   // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarFacturaKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarDev;
    private javax.swing.JButton btnBuscarFactura;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCantVentas;
    private javax.swing.JLabel lblDevueltos;
    private javax.swing.JLabel lblTopProducto;
    private javax.swing.JLabel lblUnidades;
    private javax.swing.JPanel panelGrafico;
    private javax.swing.JTable tblDevolucionesVentas;
    private javax.swing.JTable tblFacturas;
    private javax.swing.JTextField txtBuscarDev;
    private javax.swing.JTextField txtBuscarFactura;
    // End of variables declaration//GEN-END:variables
}
