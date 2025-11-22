package edu.UPAO.proyecto.app;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PrincipalAdministrador extends javax.swing.JFrame {

    private String idEmpleado;
    private String nombreUsuario;

    public PrincipalAdministrador() {
        initComponents();

    }

    public PrincipalAdministrador(String idEmpleado, String nombreUsuario) {
        // Inicializar componentes visuales primero
        initComponents();

        // Guardar los datos recibidos
        this.idEmpleado = idEmpleado;
        this.nombreUsuario = nombreUsuario;

        // Opcional: Poner el nombre en el título de la ventana o en una etiqueta
        this.setTitle("Panel Administrador - Usuario: " + nombreUsuario);

        verificarStockSucursal();
    }
    
    private void verificarStockSucursal() {
        new Thread(() -> {
            try {
                // 1. Obtener la sucursal de ESTE administrador
                edu.UPAO.proyecto.DAO.EmpleadoDAO empleadoDAO = new edu.UPAO.proyecto.DAO.EmpleadoDAO();
                int idSucursal = empleadoDAO.obtenerSucursalEmpleado(this.idEmpleado);
                
                // 2. Buscar alertas SOLO de esa sucursal (pasamos el ID)
                edu.UPAO.proyecto.DAO.InventarioSucursalDAO inventarioDAO = new edu.UPAO.proyecto.DAO.InventarioSucursalDAO();
                java.util.List<String> alertas = inventarioDAO.obtenerAlertasBajoStock(idSucursal);
                
                if (!alertas.isEmpty()) {
                    StringBuilder mensaje = new StringBuilder("⚠️ ALERTA DE STOCK (Tu Sucursal)\n\n");
                    
                    int limite = Math.min(alertas.size(), 15);
                    for (int i = 0; i < limite; i++) {
                        mensaje.append(alertas.get(i)).append("\n");
                    }
                    
                    if (alertas.size() > 15) {
                        mensaje.append("\n... y ").append(alertas.size() - 15).append(" más.");
                    }
                    
                    mensaje.append("\n\nSe requiere reposición urgente en esta sede.");

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(this, 
                            new javax.swing.JScrollPane(new javax.swing.JTextArea(mensaje.toString(), 15, 40)), 
                            "Gestión de Inventario - Local", 
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    });
                }
            } catch (Exception e) {
                System.err.println("Error stock admin: " + e.getMessage());
            }
        }).start();
    }

    private void MostrarPanel(JPanel p) {
// Configurar el panel nuevo
        p.setOpaque(true);

        // Limpiar el contenedor
        content.removeAll();

        // Agregar y estirar automáticamente
        content.add(p, java.awt.BorderLayout.CENTER);

        // Refrescar visualización
        content.revalidate();
        content.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btn_venta = new javax.swing.JButton();
        btn_almacenes = new javax.swing.JButton();
        btn_compras = new javax.swing.JButton();
        btn_personal = new javax.swing.JButton();
        btn_cuenta = new javax.swing.JButton();
        btn_tesoreria = new javax.swing.JButton();
        btn_cerrarSesion = new javax.swing.JButton();
        btnMarcarAsistencia = new javax.swing.JButton();
        content = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel4 = new javax.swing.JPanel();
        lblFrase = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(6);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jPanel1.setBackground(new java.awt.Color(0, 51, 29));

        btn_venta.setText("VENTAS");
        btn_venta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventaActionPerformed(evt);
            }
        });

        btn_almacenes.setText("ALMACENES");
        btn_almacenes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_almacenesActionPerformed(evt);
            }
        });

        btn_compras.setText("COMPRAS");
        btn_compras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_comprasActionPerformed(evt);
            }
        });

        btn_personal.setText("PERSONAL");
        btn_personal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_personalActionPerformed(evt);
            }
        });

        btn_cuenta.setText("CUENTA");
        btn_cuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cuentaActionPerformed(evt);
            }
        });

        btn_tesoreria.setText("TESORERIA");
        btn_tesoreria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tesoreriaActionPerformed(evt);
            }
        });

        btn_cerrarSesion.setText("Cerrar sesion");
        btn_cerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cerrarSesionActionPerformed(evt);
            }
        });

        btnMarcarAsistencia.setText("Mis asistencias");
        btnMarcarAsistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarAsistenciaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btn_cerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_venta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                            .addComponent(btn_almacenes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_compras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_personal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_cuenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_tesoreria, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                            .addComponent(btnMarcarAsistencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btn_tesoreria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_compras, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_almacenes, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_personal, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_cuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMarcarAsistencia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_cerrarSesion)
                .addGap(15, 15, 15))
        );

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1197, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 654, Short.MAX_VALUE)
        );

        content.add(jLayeredPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(255, 153, 0));

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 36)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/miniLogo.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addGap(77, 77, 77)
                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 894, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tesoreriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tesoreriaActionPerformed

    }//GEN-LAST:event_btn_tesoreriaActionPerformed

    private void btn_personalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_personalActionPerformed
        CONTROL_PERSONAL panelControl_Personal = new CONTROL_PERSONAL();
        MostrarPanel(panelControl_Personal);
    }//GEN-LAST:event_btn_personalActionPerformed

    private void btn_comprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_comprasActionPerformed
        // 1. Creamos la instancia de tu panel de pedidos
        // (Asegúrate de que 'FormularioPedidos' sea el nombre exacto de tu archivo Java del panel anterior)
        COMPRAS_Admin panelCompras = new COMPRAS_Admin();

        // 2. Llamamos a tu función para mostrarlo en el área blanca
        MostrarPanel(panelCompras);
    }//GEN-LAST:event_btn_comprasActionPerformed

    private void btn_ventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventaActionPerformed
        VENTAS_Admin panelventas = new VENTAS_Admin();
        MostrarPanel(panelventas);
    }//GEN-LAST:event_btn_ventaActionPerformed

    private void btn_almacenesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_almacenesActionPerformed
        ALMACEN_Admin panelAlmacen = new ALMACEN_Admin();
        MostrarPanel(panelAlmacen);
    }//GEN-LAST:event_btn_almacenesActionPerformed

    private void btn_cuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cuentaActionPerformed
        // 1. Validar que tenemos el ID del administrador (por si acaso)
        String idParaEnviar = (this.idEmpleado != null) ? this.idEmpleado : "12000001"; 
        
        // 2. Instanciar el panel pasándole el ID
        panel_Cuenta panelCuenta = new panel_Cuenta(idParaEnviar);
        
        // 3. Mostrarlo en el contenido
        MostrarPanel(panelCuenta);
    }//GEN-LAST:event_btn_cuentaActionPerformed

    private void btn_cerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cerrarSesionActionPerformed
// Cierra la ventana actual
        this.dispose();

        // Abre el login de nuevo
        LoginjFrame login = new LoginjFrame();
        login.setVisible(true);
    }//GEN-LAST:event_btn_cerrarSesionActionPerformed

    private void btnMarcarAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarAsistenciaActionPerformed
        String idAdmin = this.idEmpleado; // O la variable donde guardes el ID
        String nombreAdmin = this.nombreUsuario; // O la variable donde guardes el Nombre

        // 2. Validar que no sean nulos (por seguridad)
        if (idAdmin == null || idAdmin.isEmpty()) {
            // Fallback por si es una prueba rápida
            idAdmin = "ADMIN001";
            nombreAdmin = "Administrador";
        }

        // 3. Abrir la ventanita de asistencia usando tu método estático existente
        //
        jFrame_Asistncias.mostrarRegistroAsistencia(idAdmin, nombreAdmin);
    }//GEN-LAST:event_btnMarcarAsistenciaActionPerformed

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
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalAdministrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMarcarAsistencia;
    private javax.swing.JButton btn_almacenes;
    private javax.swing.JButton btn_cerrarSesion;
    private javax.swing.JButton btn_compras;
    private javax.swing.JButton btn_cuenta;
    private javax.swing.JButton btn_personal;
    private javax.swing.JButton btn_tesoreria;
    private javax.swing.JButton btn_venta;
    private javax.swing.JPanel content;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblFrase;
    // End of variables declaration//GEN-END:variables
}
