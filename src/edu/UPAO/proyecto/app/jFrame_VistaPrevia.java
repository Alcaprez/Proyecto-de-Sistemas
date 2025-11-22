package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Venta;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author ALBERTH
 */
public class jFrame_VistaPrevia extends javax.swing.JFrame {

    private String rutaPDF; // Variable para almacenar la ruta del PDF
    private Menu2 menuPrincipal;

    public jFrame_VistaPrevia() {
        initComponents();
        setTitle("Vista Previa - Comprobante de Pago");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
// ✅ Referencia para actualizar

    // Constructor Principal
    public jFrame_VistaPrevia(Menu2 menu, Venta venta, String dni, String obs, String rutaPDF) {
        this.menuPrincipal = menu;
        this.rutaPDF = rutaPDF;
        initComponents();

        setTitle("Vista Previa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Modificar texto del comprobante
        String texto = venta.generarComprobante();
        if (dni != null && !dni.isEmpty()) {
            texto = texto.replace("Cajero ID:", "Cajero ID:\nClient DNI: " + dni);
        }
        if (obs != null && !obs.isEmpty()) {
            texto += "\n[OBSERVACIONES]\n" + obs;
        }

        area.setText(texto);
    }

    // ✅ CONSTRUCTOR PARA COMPATIBILIDAD (si aún lo necesitas)
    public jFrame_VistaPrevia(Venta venta, String dniCliente, String observaciones) {
        // Este constructor NO tiene rutaPDF, el botón no abrirá PDF
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Comprobante de Pago - Vista Previa");
        setLocationRelativeTo(null);

        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", Font.PLAIN, 12));

        String comprobanteModificado = modificarComprobante(venta.generarComprobante(), dniCliente, observaciones);
        area.setText(comprobanteModificado);

        // ✅ CONFIGURAR BOTÓN PARA IMPRIMIR (comportamiento original)
        btn_AbrirPDF.addActionListener(e -> {
            try {
                area.print();
                JOptionPane.showMessageDialog(this, "✅ Comprobante enviado a impresión");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error al imprimir: " + ex.getMessage());
            }
        });
    }

    private void configurarBotonPDF() {
        btn_AbrirPDF.addActionListener(e -> {
            abrirPDF();
        });

        // ✅ CAMBIAR EL TEXTO DEL BOTÓN PARA INDICAR QUE ABRE PDF
        btn_AbrirPDF.setText("📂 Abrir PDF");
    }

    private String modificarComprobante(String comprobanteOriginal, String dniCliente, String observaciones) {
        StringBuilder sb = new StringBuilder();
        String[] lineas = comprobanteOriginal.split("\n");

        boolean dniAgregado = false;
        boolean observacionesAgregadas = false;

        for (String linea : lineas) {
            sb.append(linea).append("\n");

            // ✅ AGREGAR DNI DESPUÉS DE "Cajero ID:"
            if (!dniAgregado && linea.contains("Cajero ID:")) {
                if (dniCliente != null && !dniCliente.trim().isEmpty()) {
                    sb.append("DNI Cliente: ").append(dniCliente.trim()).append("\n");
                }
                dniAgregado = true;
            }

            // ✅ AGREGAR OBSERVACIONES ANTES DE "¡GRACIAS POR SU COMPRA!"
            if (!observacionesAgregadas && linea.contains("¡GRACIAS POR SU COMPRA!")) {
                if (observaciones != null && !observaciones.trim().isEmpty()) {
                    sb.append("\nOBSERVACIONES:\n");
                    sb.append("-----------------------------------------\n");

                    // Procesar observaciones (dividir si son muy largas)
                    String obs = observaciones.trim();
                    int maxLength = 40;
                    for (int i = 0; i < obs.length(); i += maxLength) {
                        int end = Math.min(obs.length(), i + maxLength);
                        sb.append(obs.substring(i, end)).append("\n");
                    }

                    sb.append("=========================================\n");
                }
                observacionesAgregadas = true;
            }
        }

        return sb.toString();
    }

    private void abrirPDF() {
        try {
            if (rutaPDF != null && !rutaPDF.isEmpty()) {
                File f = new File(rutaPDF);
                if (f.exists()) {
                    Desktop.getDesktop().open(f);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error abriendo PDF: " + e.getMessage());
        }
    }

    private void cerrarYActualizar() {
        // ✅ Actualizar Menu2 también desde aquí por si acaso
        if (menuPrincipal != null) {
            menuPrincipal.finalizarVenta();
        }
        this.dispose();
    }

    private void abrirCarpetaContenedora(File archivo) {
        try {
            // Abrir la carpeta que contiene el archivo
            Desktop.getDesktop().open(archivo.getParentFile());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir la carpeta.\n"
                    + "Ruta del archivo: " + archivo.getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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

        btn_AbrirPDF = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_AbrirPDF.setText("Abrir PDF");
        btn_AbrirPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirPDFActionPerformed(evt);
            }
        });

        area.setColumns(20);
        area.setRows(5);
        jScrollPane1.setViewportView(area);

        jButton1.setText("Listo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("VISTA DE IMPRESION DEL COMPROBANTE DE PAGO");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btn_AbrirPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(31, 31, 31)
                            .addComponent(jLabel1))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_AbrirPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AbrirPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirPDFActionPerformed
        abrirPDF();
    }//GEN-LAST:event_btn_AbrirPDFActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jFrame_VistaPrevia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jFrame_VistaPrevia().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea area;
    private javax.swing.JButton btn_AbrirPDF;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
