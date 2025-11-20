package edu.UPAO.proyecto.app;
import java.sql.*; // Asegúrate de tener estos imports arriba
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
public class VENTAS_Admin extends javax.swing.JPanel {
    public VENTAS_Admin() {
        initComponents();
        buscarDevoluciones();
    }
private void buscarDevoluciones() {
    // 1. Tus credenciales de Railway (tal cual me las diste)
    String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    String usuario = "root";
    String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

    // 2. Limpiamos la tabla antes de buscar
    DefaultTableModel modelo = (DefaultTableModel) tblDevoluciones.getModel();
    modelo.setRowCount(0);

    // 3. Construimos la consulta SQL con los JOINS necesarios
    // Explicación: Unimos Devolucion -> Detalle -> Venta -> Cliente -> Persona
    String sql = "SELECT " +
                 "  d.id_devolucion, " +
                 "  dd.id_producto, " +
                 "  d.fecha_hora, " +
                 "  CONCAT(p.nombres, ' ', p.apellidos) AS nombre_cliente, " +
                 "  dd.subtotal, " +
                 "  d.motivo " +
                 "FROM devolucion d " +
                 "INNER JOIN detalle_devolucion dd ON d.id_devolucion = dd.id_devolucion " +
                 "INNER JOIN venta v ON d.id_venta = v.id_venta " +
                 "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                 "INNER JOIN persona p ON c.dni = p.dni " +
                 "WHERE 1=1 ";

    // 4. Filtro por TEXTO (ID Producto)
    // Usamos tu variable txtIdProducto (o como hayas llamado a la caja negra larga)
    String texto = txtIdProducto.getText().trim(); 
    
    if (!texto.isEmpty() && !texto.equals("Coloque el  id del producto")) {
        sql += " AND dd.id_producto LIKE '%" + texto + "%'";
    }

    // Ordenamos por fecha más reciente
    sql += " ORDER BY d.fecha_hora DESC";

    try {
        Connection con = DriverManager.getConnection(url, usuario, password);
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] fila = new Object[6];
            fila[0] = rs.getInt("id_devolucion");
            fila[1] = rs.getInt("id_producto");     
            fila[2] = rs.getTimestamp("fecha_hora");  
            fila[3] = rs.getString("nombre_cliente");
            fila[4] = rs.getBigDecimal("subtotal");   
            fila[5] = rs.getString("motivo");          
            
            modelo.addRow(fila);
        }
        
        con.close();
        // Si tienes un método para pintar colores como en tu ejemplo, llámalo aquí:
        // pintarColoresTabla(); 

    } catch (SQLException e) {
        System.out.println("Error buscando devoluciones: " + e);
        JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDevoluciones = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtIdProducto = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("VENTAS", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tblDevoluciones.setModel(new javax.swing.table.DefaultTableModel(
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
                "ID Devolucion", "ID Producto", "Fecha", "Cliente", "Importe", "Motivo"
            }
        ));
        jScrollPane1.setViewportView(tblDevoluciones);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("PRODUCTOS DEVUELTOS");

        txtIdProducto.setText("Coloque el  id del producto");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
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
                                .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(123, 123, 123)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", jPanel2);

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

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
    buscarDevoluciones();
    }//GEN-LAST:event_btnBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblDevoluciones;
    private javax.swing.JTextField txtIdProducto;
    // End of variables declaration//GEN-END:variables
}
