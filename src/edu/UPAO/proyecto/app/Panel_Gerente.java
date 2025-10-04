
package edu.UPAO.proyecto.app;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author WIN-10
 */
public class Panel_Gerente extends javax.swing.JFrame {

    public Panel_Gerente() {
        initComponents();  
      
getContentPane().setLayout(null);

// jPanel1 debe crecer siempre con la ventana
getContentPane().addComponentListener(new java.awt.event.ComponentAdapter() {
    @Override public void componentResized(java.awt.event.ComponentEvent e) {
        layoutRootAndChildren();
    }
});
        setExtendedState(getExtendedState() | javax.swing.JFrame.MAXIMIZED_BOTH);

    // aseguramos AbsoluteLayout en los paneles que vamos a posicionar
    panelTitle.setLayout(null);
    panelHeader.setLayout(null);
    panelTitle.setLayout(null);

    // recalcular cuando cambie el tamaño
    panelCenter.addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override public void componentResized(java.awt.event.ComponentEvent e) {
            layoutFullScreen();
        }
    });

    // primera vez
    layoutFullScreen();
   
}
private void layoutFullScreen() {
    int W = panelTitle.getWidth();
    int H = panelTitle.getHeight();
    if (W <= 0 || H <= 0) return;

    int gap = 16;     // margen general
    int hNaranja = 72; // alto barra naranja
    int hRojo    = 44; // alto franja roja

    // --- Barras superiores ---
    panelHeader.setBounds(gap, gap, W - 2*gap, hNaranja);                 // barra naranja
    panelTitle.setBounds(gap, gap + hNaranja, W - 2*gap, hRojo);         // franja roja

    // --- Zona de contenido (donde van las 6 tarjetas) ---
    int top = gap + hNaranja + hRojo + gap;
    int contentW = W - 2*gap;
    int contentH = H - top - gap;

    // Para que el fondo gris se vea como en tu mockup, si tienes un panel de fondo
    // reusamos jPanel1 mismo; si tienes otro panel para fondo, dale bounds aquí:
    // jPanelContenido.setBounds(gap, top, contentW, contentH);

    // ---- GRID de tarjetas 3×2 (se adapta a ancho) ----
    // cambia columnas según ancho
    int cols = contentW >= 1100 ? 3 : (contentW >= 760 ? 2 : 1);
    int cardW = (contentW - (cols + 1) * gap) / cols;
    int cardH = Math.min(220, (int)(cardW * 0.75)); // relación ~4:3

    javax.swing.JButton[] cards = new javax.swing.JButton[]{
        jButton3,       // R. VENTAS
        jButton5,  // EMPLEADOS
        jButton2,       // PROMOCIONES
        jButton4,       // LOCALES
        jButton6        // CUENTA
    };

    for (int i = 0; i < cards.length; i++) {
        int col = i % cols;
        int row = i / cols;

        int x = gap + col * (cardW + gap);
        int y = top + gap + row * (cardH + gap);

        cards[i].setBounds(x, y, cardW, cardH);
    }

   if (btnSalir != null) {
    int bw = 140;  // ancho del botón
    int bh = 40;   // alto del botón
    int margen = 20; // margen derecho

    // Lo colocamos pegado al lado derecho de la barra naranja
    btnSalir.setBounds(
        panelHeader.getWidth() - bw - margen, // X (derecha)
        (panelHeader.getHeight() - bh) / 2,   // Y (centrado vertical)
        bw, bh
    );
}
}

private void layoutRootAndChildren() {
    // 1) Estirar jPanel1 al tamaño del contentPane
    int W = getContentPane().getWidth();
    int H = getContentPane().getHeight();
    if (W <= 0 || H <= 0) return;

    panelTitle.setBounds(0, 0, W, H);

    // 2) Ahora sí, reposiciona barras y tarjetas según jPanel1
    layoutFullScreen();   // este es el método que ya te pasé antes
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
        palenTiles = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
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

        logoLabel.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Imágenes\\Logo_kuyay-convertido-a-pequeño-removebg-preview.png")); // NOI18N
        panelHeader.add(logoLabel, java.awt.BorderLayout.WEST);

        btnSalir.setBackground(new java.awt.Color(255, 255, 255));
        btnSalir.setFont(new java.awt.Font("Leelawadee UI", 1, 14)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
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

        palenTiles.setOpaque(false);
        palenTiles.setLayout(new java.awt.GridLayout(2, 2, 48, 48));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Documentos\\reporte ventas.png")); // NOI18N
        jButton3.setText("R. VENTAS");
        jButton3.setContentAreaFilled(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        palenTiles.add(jButton3);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Documentos\\personal.jpg")); // NOI18N
        jButton5.setText("EMPLEADOS");
        jButton5.setContentAreaFilled(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        palenTiles.add(jButton5);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Documentos\\promociones.png")); // NOI18N
        jButton2.setText("PROMOCIONES");
        jButton2.setContentAreaFilled(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setPreferredSize(new java.awt.Dimension(150, 150));
        jButton2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        palenTiles.add(jButton2);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Documentos\\locales.jpg")); // NOI18N
        jButton4.setText("LOCALES");
        jButton4.setContentAreaFilled(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setPreferredSize(new java.awt.Dimension(150, 150));
        jButton4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        palenTiles.add(jButton4);

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon("C:\\Users\\WIN-10\\OneDrive\\Documentos\\USUARIO.png")); // NOI18N
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
        palenTiles.add(jButton6);

        panelCenter.add(palenTiles, new java.awt.GridBagConstraints());

        getContentPane().add(panelCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        Panel_Gerente g = new Panel_Gerente(); // cambia por el nombre de tu JFrame de Gerente
        g.setVisible(true);
        g.setLocationRelativeTo(null); // centrar la ventana en la pantalla

        // Cerrar esta ventana (PERSONAL)
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Panel_Gerente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Panel_Gerente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel palenTiles;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel panelTop;
    // End of variables declaration//GEN-END:variables
}
