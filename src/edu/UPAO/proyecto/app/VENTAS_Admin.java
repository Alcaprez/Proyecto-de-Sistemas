package edu.UPAO.proyecto.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.*;
import javax.swing.JPanel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
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
        
        // 1. Llenar el Combo de Tipos manualmente
           cboTipoMovimiento.removeAllItems();
            cboTipoMovimiento.addItem("Todos");
            cboTipoMovimiento.addItem("VENTA");       // Ingresos por venta
            cboTipoMovimiento.addItem("COMPRA");      // Salidas por compra (Monto 0.00)
            cboTipoMovimiento.addItem("GASTO");       // Gastos operativos (Monto 0.00)
            cboTipoMovimiento.addItem("INVENTARIO");
        
        // 2. Cargar los datos
        cargarKPIs(); 
        cargarGrafico();
        cargarDevoluciones("");
        cargarHistorialCaja(""); // <--- ¡ESTO ES LO QUE FALTABA!
        
        // 3. Evento para el Combo (Para que filtre al cambiar)
        cboTipoMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarHistorialCaja("");
            }
        });
       
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
    private void cargarHistorialCaja(String string) {
        DefaultTableModel modelo = (DefaultTableModel) tblFacturas.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"FECHA", "HORA", "CONCEPTO / DESCRIPCIÓN", "TIPO", "MONTO"});
        
        tblFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblFacturas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblFacturas.getColumnModel().getColumn(2).setPreferredWidth(350);
        tblFacturas.getColumnModel().getColumn(4).setPreferredWidth(100);

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        // 1. CONSULTA
        String sql = "SELECT * FROM (" +
                     "   SELECT fecha_hora, descripcion, tipo, monto, id_sucursal " +
                     "   FROM movimiento_caja " +
                     
                     "   UNION ALL " +
                     
                     "   SELECT mi.fecha_hora, " +
                     "          CONCAT('MOV. STOCK: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, " +
                     "          CASE " +
                     "             WHEN mi.tipo LIKE '%COMPRA%' THEN 'COMPRA_INVENTARIO' " +
                     "             ELSE 'INVENTARIO' " +
                     "          END as tipo, " +
                     "          0.00 as monto, " +
                     "          mi.id_sucursal " +
                     "   FROM movimiento_inventario mi " +
                     "   INNER JOIN producto p ON mi.id_producto = p.id_producto " +
                     ") AS historial " +
                     "WHERE id_sucursal = 1 ";

        // 2. FILTROS
        if (cboTipoMovimiento != null && cboTipoMovimiento.getSelectedItem() != null) {
            String tipoSel = cboTipoMovimiento.getSelectedItem().toString();
            if (!"Todos".equalsIgnoreCase(tipoSel)) {
                sql += " AND tipo LIKE '%" + tipoSel + "%' ";
            }
        }

        sql += " ORDER BY fecha_hora DESC LIMIT 100";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                String hora = new java.text.SimpleDateFormat("HH:mm").format(ts);
                String desc = rs.getString("descripcion");
                String tipo = rs.getString("tipo");
                double montoReal = rs.getDouble("monto");
                
                // --- CORRECCIÓN: LÓGICA VISUAL ---
                String textoMonto = "0.00";
                
                // AHORA SÍ: Solo mostramos dinero si es VENTA genuina o un INGRESO externo.
                // Apertura y Reposición son traslados internos -> Se muestran como 0.00 en este reporte de Ventas.
                if (tipo.equals("VENTA") || tipo.equals("INGRESO")) {
                     textoMonto = String.format("S/ %.2f", montoReal);
                }
                
                String tipoVisual = tipo.replace("_INVENTARIO", "");

                modelo.addRow(new Object[]{fecha, hora, desc, tipoVisual, textoMonto});
            }
        } catch (SQLException e) {
            System.out.println("Error historial ventas: " + e);
        }
    }
    private void exportarPDF(JTable tabla, String tituloReporte) {
        // 1. Configurar el selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        // Filtro para que solo muestre PDFs
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
        
        // Sugerir un nombre por defecto (Ej: Reporte_Ventas.pdf)
        fileChooser.setSelectedFile(new java.io.File("Reporte_" + System.currentTimeMillis() + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Obtener la ruta elegida por el usuario
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            
            // Asegurar que termine en .pdf
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            try {
                // 2. Crear el documento PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
                document.open();

                // 3. Agregar Título
                com.itextpdf.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph titulo = new Paragraph(tituloReporte, fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                document.add(titulo);

                // 4. Agregar Tabla
                PdfPTable tablePDF = new PdfPTable(tabla.getColumnCount());
                tablePDF.setWidthPercentage(100); // Ancho completo

                // A. Encabezados
                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    cell.setPadding(8);
                    tablePDF.addCell(cell);
                }

                // B. Datos
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    for (int j = 0; j < tabla.getColumnCount(); j++) {
                        Object valor = tabla.getValueAt(i, j);
                        String texto = (valor == null) ? "" : valor.toString();
                        
                        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 10)));
                        cell.setPadding(5);
                        // Alinear a la derecha si es dinero
                        if (texto.contains("S/") || texto.contains("$")) {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                        tablePDF.addCell(cell);
                    }
                }

                document.add(tablePDF);
                document.close();

                // 5. MENSAJE DE ÉXITO CON LA RUTA
                JOptionPane.showMessageDialog(this, 
                    "¡Reporte generado exitosamente!\n\nGuardado en:\n" + rutaArchivo, 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
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
        jButton1 = new javax.swing.JButton();
        cboTipoMovimiento = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();

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

        jButton1.setText("Exportar en pdf");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cboTipoMovimiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Facturas");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboTipoMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(jButton1)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jLabel3)
                .addGap(45, 45, 45)
                .addComponent(cboTipoMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(153, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(274, 274, 274))))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // Llamamos al método pasándole la tabla y un título bonito
        exportarPDF(tblFacturas, "Reporte Detallado de Ventas e Ingresos");       // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarDev;
    private javax.swing.JComboBox<String> cboTipoMovimiento;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    // End of variables declaration//GEN-END:variables
}
