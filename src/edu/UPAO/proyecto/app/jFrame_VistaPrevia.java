/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Venta;
import java.awt.Font;
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

    // ✅ CONSTRUCTOR ORIGINAL (mántelo para compatibilidad)
    public jFrame_VistaPrevia() {
        initComponents();
        setTitle("Vista Previa - Comprobante de Pago");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public jFrame_VistaPrevia(Venta venta) {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Comprobante de Pago - Vista Previa");
        setLocationRelativeTo(null);

        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", Font.PLAIN, 12));
        area.setText(venta.generarComprobante());

        btn_imprimir.addActionListener(e -> {
            try {
                area.print();
                JOptionPane.showMessageDialog(this, "✅ Comprobante enviado a impresión");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error al imprimir: " + ex.getMessage());
            }
        });
    }

    // ✅ NUEVO CONSTRUCTOR que recibe DNI y observaciones
    public jFrame_VistaPrevia(Venta venta, String dniCliente, String observaciones) {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Comprobante de Pago - Vista Previa");
        setLocationRelativeTo(null);

        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", Font.PLAIN, 12));

        // ✅ GENERAR COMPROBANTE MODIFICADO CON DNI Y OBSERVACIONES
        String comprobanteModificado = modificarComprobante(venta.generarComprobante(), dniCliente, observaciones);
        area.setText(comprobanteModificado);

        btn_imprimir.addActionListener(e -> {
            try {
                area.print();
                JOptionPane.showMessageDialog(this, "✅ Comprobante enviado a impresión");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error al imprimir: " + ex.getMessage());
            }
        });
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

// ✅ MÉTODO PARA AGREGAR DNI Y OBSERVACIONES AL COMPROBANTE EXISTENTE
    private String generarComprobanteConExtras(Venta venta, String dniCliente, String observaciones) {
        // Obtener el comprobante base de la venta
        String comprobanteBase = venta.generarComprobante();

        StringBuilder sb = new StringBuilder();

        // Reconstruir el comprobante agregando DNI y observaciones
        String[] lineas = comprobanteBase.split("\n");

        for (int i = 0; i < lineas.length; i++) {
            sb.append(lineas[i]).append("\n");

            // ✅ INSERTAR DNI DESPUÉS DE LA LÍNEA DEL CAJERO
            if (lineas[i].contains("Cajero ID:")) {
                if (dniCliente != null && !dniCliente.isEmpty()) {
                    sb.append("DNI Cliente: ").append(dniCliente).append("\n");
                }
            }

            // ✅ INSERTAR OBSERVACIONES ANTES DE "¡GRACIAS POR SU COMPRA!"
            if (lineas[i].contains("¡GRACIAS POR SU COMPRA!")) {
                if (observaciones != null && !observaciones.trim().isEmpty()) {
                    sb.append("\nOBSERVACIONES:\n");
                    sb.append("-----------------------------------------\n");
                    // Dividir observaciones en líneas
                    String[] obsLineas = observaciones.split("\n");
                    for (String obsLinea : obsLineas) {
                        if (obsLinea.length() > 40) {
                            // Dividir líneas muy largas
                            for (int j = 0; j < obsLinea.length(); j += 40) {
                                sb.append(obsLinea.substring(j, Math.min(obsLinea.length(), j + 40))).append("\n");
                            }
                        } else {
                            sb.append(obsLinea).append("\n");
                        }
                    }
                    sb.append("=========================================\n\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_imprimir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_imprimir.setText("Imprimir");
        btn_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_imprimirActionPerformed(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel1)))
                .addContainerGap(24, Short.MAX_VALUE))
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
                    .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirActionPerformed
    }//GEN-LAST:event_btn_imprimirActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(jFrame_VistaPrevia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jFrame_VistaPrevia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jFrame_VistaPrevia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jFrame_VistaPrevia.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jFrame_VistaPrevia().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea area;
    private javax.swing.JButton btn_imprimir;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
