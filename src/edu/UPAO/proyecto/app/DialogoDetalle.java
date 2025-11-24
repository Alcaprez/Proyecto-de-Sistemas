package edu.UPAO.proyecto.app;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class DialogoDetalle extends javax.swing.JDialog {

    // Variable para guardar qué pedido estamos viendo
    String codigoPedido;

    // 1. MODIFICAMOS EL CONSTRUCTOR para que pida el código
    public DialogoDetalle(java.awt.Frame parent, boolean modal, String codigo) {
        super(parent, modal);
        initComponents();
        this.codigoPedido = codigo;
        this.setLocationRelativeTo(null); // Centrar ventana
        this.setTitle("Detalle del Pedido: " + codigo);
        
        cargarProductos(); // <--- Llamamos a la función al abrir
    }

    // 2. MÉTODO PARA CARGAR LOS PRODUCTOS DE ESTE PEDIDO
    private void cargarProductos() {
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; // Tu clave

        DefaultTableModel modelo = (DefaultTableModel) tblDetalle.getModel();
        modelo.setRowCount(0);
        
        // 1. AHORA TENEMOS 4 COLUMNAS
        modelo.setColumnIdentifiers(new Object[]{"Producto", "Cantidad", "P. Unitario", "Subtotal"});

        // 2. SQL ACTUALIZADO: Traemos también el precio_compra
        String sql = "SELECT pr.nombre, d.cantidad, pr.precio_compra " +
                     "FROM detalle_pedido d " +
                     "INNER JOIN producto pr ON d.id_producto = pr.id_producto " +
                     "INNER JOIN pedido p ON d.id_pedido = p.id_pedido " +
                     "WHERE p.codigo = ?";

        double totalGeneral = 0.0; // Acumulador para la suma final

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, codigoPedido);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio_compra"); // Precio de la base de datos
                
                // 3. CALCULAMOS EL SUBTOTAL
                double subtotal = cantidad * precio;
                
                // 4. SUMAMOS AL TOTAL GENERAL
                totalGeneral += subtotal;

                // Agregamos la fila con formato de moneda (2 decimales)
                modelo.addRow(new Object[]{
                    nombre,
                    cantidad,
                    String.format("S/ %.2f", precio),
                    String.format("S/ %.2f", subtotal)
                });
            }
            con.close();
            
            // 5. MOSTRAMOS EL TOTAL EN LA ETIQUETA (Asegúrate de haber creado lblTotal)
            if (lblTotal != null) { // Validación por si olvidaste crear el label
                lblTotal.setText("Monto Total: S/ " + String.format("%.2f", totalGeneral));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar detalle: " + e);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        BotonCerrar = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblDetalle);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 153, 51));
        jLabel1.setText("Detalle del pedido");

        BotonCerrar.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        BotonCerrar.setText("Cerrar");
        BotonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCerrarActionPerformed(evt);
            }
        });

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotal.setText("Total: S/ 0.00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(BotonCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(339, 339, 339))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(277, 277, 277)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(276, 276, 276)
                        .addComponent(lblTotal)))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addComponent(BotonCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCerrarActionPerformed
       this.dispose(); // TODO add your handling code here:
    }//GEN-LAST:event_BotonCerrarActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonCerrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetalle;
    // End of variables declaration//GEN-END:variables
}
