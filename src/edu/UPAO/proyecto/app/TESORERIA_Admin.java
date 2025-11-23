package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
public class TESORERIA_Admin extends javax.swing.JPanel {
// --- 1. TUS CREDENCIALES DE RAILWAY ---
    private final String URL = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    private final String USUARIO = "root";
    private final String PASSWORD = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

    // ID de Sucursal (Fijo en 1 para este ejemplo)
    private final int ID_SUCURSAL = 1;

    // Componentes de la Interfaz
    private JLabel lblIngresosVal, lblGastosVal, lblSaldoVal;
    private JTable tablaMovimientos;
    public TESORERIA_Admin() {
       initComponents();
        cargarDatosDesdeBD();
    }
    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color bg, Color fg, String emoji) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titulo pequeño arriba
        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(fg);
        
        // Icono
        JLabel lblIcon = new JLabel(emoji);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(lblTit, BorderLayout.WEST);
        topPanel.add(lblIcon, BorderLayout.EAST);

        // Valor grande en el centro
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(fg.darker());

        card.add(topPanel, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }
// ========================================================================
    // PARTE B: BACKEND (CONEXIÓN A TU BASE DE DATOS)
    // ========================================================================
    private void cargarDatosDesdeBD() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. Conectar
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);

            if (con != null) {
                
                // --- CONSULTA 1: INGRESOS HOY (Tabla VENTA) ---
                // Usamos la tabla 'venta' que dijiste que facilitaba todo
                String sqlVentas = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ? AND DATE(fecha_hora) = CURDATE()";
                ps = con.prepareStatement(sqlVentas);
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();
                double ventasHoy = 0;
                if (rs.next()) ventasHoy = rs.getDouble(1);
                
                // Sumamos también 'INGRESO' manual de movimiento_caja si hubiera
                ps = con.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja WHERE id_sucursal = ? AND tipo = 'INGRESO' AND DATE(fecha_hora) = CURDATE()");
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();
                double otrosIngresos = 0;
                if (rs.next()) otrosIngresos = rs.getDouble(1);

                lblIngresosVal.setText(String.format("S/ %.2f", ventasHoy + otrosIngresos));

                // --- CONSULTA 2: GASTOS HOY (Tabla COMPRA + Caja Chica) ---
                ps = con.prepareStatement("SELECT COALESCE(SUM(total), 0) FROM compra WHERE id_sucursal = ? AND DATE(fecha_hora) = CURDATE()");
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();
                double comprasHoy = 0;
                if (rs.next()) comprasHoy = rs.getDouble(1);

                ps = con.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja WHERE id_sucursal = ? AND tipo = 'GASTO' AND DATE(fecha_hora) = CURDATE()");
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();
                double gastosCaja = 0;
                if (rs.next()) gastosCaja = rs.getDouble(1);

                lblGastosVal.setText(String.format("S/ %.2f", comprasHoy + gastosCaja));

                // --- CONSULTA 3: SALDO (Tabla SUCURSAL) ---
                ps = con.prepareStatement("SELECT presupuesto FROM sucursal WHERE id_sucursal = ?");
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();
                if (rs.next()) {
                    double saldo = rs.getDouble("presupuesto");
                    lblSaldoVal.setText(String.format("S/ %.2f", saldo));
                }

                // --- CONSULTA 4: LLENAR TABLA (Historial movimiento_caja) ---
                DefaultTableModel modelo = (DefaultTableModel) tablaMovimientos.getModel();
                modelo.setRowCount(0); // Limpiar tabla

                String sqlTabla = "SELECT fecha_hora, descripcion, tipo, monto FROM movimiento_caja WHERE id_sucursal = ? ORDER BY fecha_hora DESC LIMIT 50";
                ps = con.prepareStatement(sqlTabla);
                ps.setInt(1, ID_SUCURSAL);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fecha_hora");
                    String fecha = (ts != null) ? ts.toString().substring(0, 10) : "";
                    String hora = (ts != null) ? ts.toString().substring(11, 16) : "";
                    String desc = rs.getString("descripcion");
                    String tipo = rs.getString("tipo");
                    double monto = rs.getDouble("monto");

                    String montoStr = String.format("S/ %.2f", monto);
                    
                    // Poner signo negativo visualmente a los gastos
                    if (tipo.equals("GASTO") || tipo.equals("COMPRA")) {
                        montoStr = "- " + montoStr;
                    } else {
                        montoStr = "+ " + montoStr;
                    }

                    modelo.addRow(new Object[]{fecha, hora, desc, tipo, montoStr});
                }
            }

        } catch (Exception e) {
            System.err.println("Error BD Tesoreria: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage());
        } finally {
            // Cerrar recursos
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        dateChooserFiltro = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblIngresosTotal = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Detalle de ingresos por ventas ");

        jPanel5.setBackground(new java.awt.Color(0, 153, 0));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("INGRESO");

        lblIngresosTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblIngresosTotal.setText("$000");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIngresosTotal)
                    .addComponent(jLabel2))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addGap(28, 28, 28)
                .addComponent(lblIngresosTotal)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 153, 0));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setText("TICKET PROMEDIO");

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel5.setText("000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateChooserFiltro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(dateChooserFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(176, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("REPORTE DE VENTAS", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1037, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("REPORTE DE GASTOS", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1037, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1037, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("GESTION DE CAJA", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser dateChooserFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblIngresosTotal;
    // End of variables declaration//GEN-END:variables
}
