package edu.UPAO.proyecto.app;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import edu.UPAO.proyecto.app.*;

public class Panel_Gerente extends javax.swing.JFrame {

    public Panel_Gerente() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTop = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        lblFrase = new javax.swing.JLabel();
        panelTitle = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelCenter = new javax.swing.JPanel();
        panelTiles = new javax.swing.JPanel();
        btnReporteVentas = new javax.swing.JButton();
        btnEmpleados = new javax.swing.JButton();
        btnPromociones = new javax.swing.JButton();
        btnLocales = new javax.swing.JButton();
        btnCuenta = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelTop.setLayout(new java.awt.BorderLayout());

        panelHeader.setBackground(new java.awt.Color(255, 153, 0));
        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 16, 0, 16));
        panelHeader.setPreferredSize(new java.awt.Dimension(780, 70));
        panelHeader.setLayout(new java.awt.BorderLayout());
        panelHeader.add(logoLabel, java.awt.BorderLayout.WEST);

        btnSalir.setFont(new java.awt.Font("Leelawadee UI", 1, 14)); // NOI18N
        btnSalir.setText("CERRAR SESIÓN");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelHeader.add(btnSalir, java.awt.BorderLayout.EAST);

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 12)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");
        panelHeader.add(lblFrase, java.awt.BorderLayout.CENTER);

        panelTop.add(panelHeader, java.awt.BorderLayout.NORTH);

        panelTitle.setBackground(new java.awt.Color(153, 0, 0));
        panelTitle.setPreferredSize(new java.awt.Dimension(780, 40));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("BIENVENIDO GERENTE");
        panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);

        panelTop.add(panelTitle, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        panelCenter.setBackground(new java.awt.Color(219, 236, 232));
        panelCenter.setLayout(new java.awt.GridBagLayout());

        panelTiles.setOpaque(false);
        panelTiles.setLayout(new java.awt.GridLayout(2, 2, 48, 48));

        btnReporteVentas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnReporteVentas.setForeground(new java.awt.Color(255, 255, 255));
        btnReporteVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/reporte ventas.png"))); // NOI18N
        btnReporteVentas.setText("R. VENTAS");
        btnReporteVentas.setContentAreaFilled(false);
        btnReporteVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReporteVentas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReporteVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteVentasActionPerformed(evt);
            }
        });
        panelTiles.add(btnReporteVentas);

        btnEmpleados.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEmpleados.setForeground(new java.awt.Color(255, 255, 255));
        btnEmpleados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/personal.jpg"))); // NOI18N
        btnEmpleados.setText("EMPLEADOS");
        btnEmpleados.setContentAreaFilled(false);
        btnEmpleados.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEmpleados.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpleadosActionPerformed(evt);
            }
        });
        panelTiles.add(btnEmpleados);

        btnPromociones.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPromociones.setForeground(new java.awt.Color(255, 255, 255));
        btnPromociones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/promociones.png"))); // NOI18N
        btnPromociones.setText("PROMOCIONES");
        btnPromociones.setContentAreaFilled(false);
        btnPromociones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPromociones.setPreferredSize(new java.awt.Dimension(150, 150));
        btnPromociones.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnPromociones.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPromociones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPromocionesActionPerformed(evt);
            }
        });
        panelTiles.add(btnPromociones);

        btnLocales.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLocales.setForeground(new java.awt.Color(255, 255, 255));
        btnLocales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/locales.jpg"))); // NOI18N
        btnLocales.setText("LOCALES");
        btnLocales.setContentAreaFilled(false);
        btnLocales.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLocales.setPreferredSize(new java.awt.Dimension(150, 150));
        btnLocales.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnLocales.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLocales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocalesActionPerformed(evt);
            }
        });
        panelTiles.add(btnLocales);

        btnCuenta.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCuenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCuenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/Cuenta.png"))); // NOI18N
        btnCuenta.setText("CUENTA");
        btnCuenta.setContentAreaFilled(false);
        btnCuenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCuenta.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCuenta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuentaActionPerformed(evt);
            }
        });
        panelTiles.add(btnCuenta);

        panelCenter.add(panelTiles, new java.awt.GridBagConstraints());

        getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        LoginjFrame login = new LoginjFrame();                           
        login.setLocationRelativeTo(null);                                    
        login.setVisible(true);                                          
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuentaActionPerformed
        CUENTA_GERENTE cuenta = new CUENTA_GERENTE();
        cuenta.setLocationRelativeTo(null);
        cuenta.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCuentaActionPerformed

    private void btnReporteVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteVentasActionPerformed
        VENTAS ventanaVentas = new VENTAS();
        ventanaVentas.setVisible(true);
        ventanaVentas.setLocationRelativeTo(null); 
        this.dispose(); 
    }//GEN-LAST:event_btnReporteVentasActionPerformed

    private void btnEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpleadosActionPerformed
       PERSONAL empleados = new PERSONAL();
        empleados.setVisible(true);
       empleados.setLocationRelativeTo(null); 
        this.dispose(); 
    }//GEN-LAST:event_btnEmpleadosActionPerformed

    private void btnPromocionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromocionesActionPerformed
       PROMOCIONES promos = new PROMOCIONES();
       promos.setVisible(true);
       promos.setLocationRelativeTo(null); 
        this.dispose(); 
    }//GEN-LAST:event_btnPromocionesActionPerformed

    private void btnLocalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocalesActionPerformed
     
    }//GEN-LAST:event_btnLocalesActionPerformed

    public static void main(String args[]) {

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
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Panel_Gerente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCuenta;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnLocales;
    private javax.swing.JButton btnPromociones;
    private javax.swing.JButton btnReporteVentas;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelTiles;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel panelTop;
    // End of variables declaration//GEN-END:variables
}
