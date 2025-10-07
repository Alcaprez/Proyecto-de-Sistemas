package edu.UPAO.proyecto.app;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import edu.UPAO.proyecto.app.*;

public class Panel_Gerente extends javax.swing.JFrame {

    public Panel_Gerente() {
        initComponents();
        configurarGerente();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

    }

    private void configurarGerente() {
        getContentPane().setLayout(new java.awt.BorderLayout());
        setExtendedState(getExtendedState() | javax.swing.JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new java.awt.Dimension(1024, 620));
        setLocationRelativeTo(null);

        panelTop.setLayout(new java.awt.BorderLayout());
        panelHeader.setPreferredSize(new java.awt.Dimension(10, 72));
        panelTitle.setPreferredSize(new java.awt.Dimension(10, 44));
        if (panelHeader.getParent() != panelTop) {
            panelTop.add(panelHeader, java.awt.BorderLayout.NORTH);
        }
        if (panelTitle.getParent() != panelTop) {
            panelTop.add(panelTitle, java.awt.BorderLayout.SOUTH);
        }

        panelCenter.setLayout(new java.awt.BorderLayout());
        if (panelTiles.getParent() != panelCenter) {

            panelCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
            panelCenter.add(panelTiles, java.awt.BorderLayout.CENTER);
        }

        if (!(panelHeader.getLayout() instanceof java.awt.BorderLayout)) {
            panelHeader.setLayout(new java.awt.BorderLayout());
            panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 16, 8, 16));
            javax.swing.JPanel left = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
            left.setOpaque(false);
            if (logoLabel != null) {
                left.add(logoLabel);
            }
            if (lblFrase != null) {
                left.add(lblFrase);
            }
            panelHeader.add(left, java.awt.BorderLayout.WEST);
            if (btnSalir != null) {
                panelHeader.add(btnSalir, java.awt.BorderLayout.EAST);
            }
        }

        if (!(panelTitle.getLayout() instanceof java.awt.BorderLayout)) {
            panelTitle.setLayout(new java.awt.BorderLayout());
            panelTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 16, 4, 16));
            if (lblTitulo != null) {
                lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);
            }
        }

        if (panelTop.getParent() != getContentPane()) {
            getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);
        }
        if (panelCenter.getParent() != getContentPane()) {
            getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);
        }

        prepararGridAdaptable();
        getContentPane().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                actualizarColumnasGrid();
            }
        });

        pack();
    }

    private void prepararGridAdaptable() {

        int colsIniciales = 3;
        if (!(panelTiles.getLayout() instanceof java.awt.GridLayout)) {
            panelTiles.setLayout(new java.awt.GridLayout(0, colsIniciales, 16, 16));
        } else {
            java.awt.GridLayout g = (java.awt.GridLayout) panelTiles.getLayout();
            g.setHgap(16);
            g.setVgap(16);
            if (g.getColumns() == 0) {
                g.setColumns(colsIniciales);
            }
        }
    }

    private void actualizarColumnasGrid() {

        int ancho = panelCenter.getWidth();
        if (ancho <= 0) {
            return;
        }

        int cols;
        if (ancho >= 1200) {
            cols = 3;      // pantallas grandes
        } else if (ancho >= 800) {
            cols = 2;  // medianas
        } else {
            cols = 1;                    // chicas
        }
        java.awt.LayoutManager lm = panelTiles.getLayout();
        if (lm instanceof java.awt.GridLayout g) {
            if (g.getColumns() != cols) {
                panelTiles.setLayout(new java.awt.GridLayout(0, cols, 16, 16));
                panelTiles.revalidate();
                panelTiles.repaint();
            }
        }
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
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

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

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/personal.jpg"))); // NOI18N
        jButton5.setText("EMPLEADOS");
        jButton5.setContentAreaFilled(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTiles.add(jButton5);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/promociones.png"))); // NOI18N
        jButton2.setText("PROMOCIONES");
        jButton2.setContentAreaFilled(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setPreferredSize(new java.awt.Dimension(150, 150));
        jButton2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTiles.add(jButton2);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/locales.jpg"))); // NOI18N
        jButton4.setText("LOCALES");
        jButton4.setContentAreaFilled(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setPreferredSize(new java.awt.Dimension(150, 150));
        jButton4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        panelTiles.add(jButton4);

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/USUARIO_1.png"))); // NOI18N
        jButton6.setText("CUENTA");
        jButton6.setContentAreaFilled(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        panelTiles.add(jButton6);

        panelCenter.add(panelTiles, new java.awt.GridBagConstraints());

        getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        LoginjFrame login = new LoginjFrame();                           
        login.setLocationRelativeTo(null);                              
        login.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);       
        login.setVisible(true);                                          
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnReporteVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteVentasActionPerformed
        VENTAS ventanaVentas = new VENTAS();
        ventanaVentas.setVisible(true);
        ventanaVentas.setLocationRelativeTo(null); 
        this.dispose(); 
    }//GEN-LAST:event_btnReporteVentasActionPerformed

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

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Panel_Gerente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReporteVentas;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
