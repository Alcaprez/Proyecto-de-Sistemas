/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package edu.UPAO.proyecto.app;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ALBERTH
 */
public class jFrame_Asistncias extends javax.swing.JFrame {

    private DefaultTableModel modelo;
    private String usuarioNombre;
    private boolean yaRegistroEntradaHoy = false;
    private boolean yaRegistroSalidaHoy = false;

    /**
     * Creates new form jFrame_Asistncias
     *
     *
     *
     */
    // Constructor sin parámetros (para el main)
    public jFrame_Asistncias() {
        this("Usuario Demo"); // Valores por defecto para testing
    }

    public jFrame_Asistncias(String usuarioNombre) {
        initComponents();
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // CENTRAR VENTANA

        this.usuarioNombre = usuarioNombre;

        // Configurar tabla
        modelo = new DefaultTableModel(new Object[]{"Usuario", "Tipo", "Fecha", "Hora"}, 0);
        jTable3.setModel(modelo);

        // Mostrar nombre en título
        jLabel7.setText("Registro - " + usuarioNombre);

        // Cargar registros existentes
        cargarRegistros();

        // Verificar estado ACTUAL desde el archivo (no usar variables temporales)
        verificarEstadoActual();

        // Actualizar hora actual
        actualizarHoraActual();

        // Configurar estado de los botones basado en el archivo
        actualizarEstadoBotones();
    }

    private void verificarEstadoActual() {
        // Este método ya no usa variables booleanas, verifica directamente del archivo
        // No necesitamos almacenar el estado en variables, lo verificamos en tiempo real
    }

    private void verificarRegistrosHoy() {
        String fechaHoy = java.time.LocalDate.now().toString();

        File archivo = new File("registros_asistencia.txt");
        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4 && partes[0].equals(usuarioNombre) && partes[2].equals(fechaHoy)) {
                    if (partes[1].equals("ENTRADA")) {
                        yaRegistroEntradaHoy = true;
                    } else if (partes[1].equals("SALIDA")) {
                        yaRegistroSalidaHoy = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al verificar registros: " + e.getMessage());
        }
    }

    private void actualizarEstadoBotones() {
        // Verificar directamente del archivo cuál es el estado actual
        boolean tieneEntradaHoy = tieneRegistroHoy("ENTRADA");
        boolean tieneSalidaHoy = tieneRegistroHoy("SALIDA");

        if (tieneEntradaHoy && !tieneSalidaHoy) {
            // Ya registró entrada pero no salida - ESPERANDO SALIDA
            btn_entrada.setEnabled(false);
            btn_entrada.setText("✓ ENTRADA REGISTRADA");
            btn_salida.setEnabled(true);
            btn_salida.setText("MARCAR SALIDA");

        } else if (tieneEntradaHoy && tieneSalidaHoy) {
            // Ya completó el ciclo de hoy - FINALIZADO
            btn_entrada.setEnabled(false);
            btn_entrada.setText("✓ ENTRADA REGISTRADA");
            btn_salida.setEnabled(false);
            btn_salida.setText("✓ SALIDA REGISTRADA");

        } else {
            // No ha registrado entrada hoy - INICIO DEL DÍA
            btn_entrada.setEnabled(true);
            btn_entrada.setText("MARCAR ENTRADA");
            btn_salida.setEnabled(false);
            btn_salida.setText("SALIDA (Espere entrada)");
        }
    }

    private boolean tieneRegistroHoy(String tipo) {
        String fechaHoy = java.time.LocalDate.now().toString();

        File archivo = new File("registros_asistencia.txt");
        if (!archivo.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4
                        && partes[0].equals(usuarioNombre)
                        && partes[1].equals(tipo)
                        && partes[2].equals(fechaHoy)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al verificar registro: " + e.getMessage());
        }
        return false;
    }
    
    
    

    private void registrarAsistencia(String tipo) {
        try {
            LocalDateTime ahora = LocalDateTime.now();
            String fecha = ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String hora = ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Guardar en archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("registros_asistencia.txt", true))) {
                writer.write(usuarioNombre + "|" + tipo + "|" + fecha + "|" + hora);
                writer.newLine();
            }

            // Agregar a la tabla
            modelo.addRow(new Object[]{usuarioNombre, tipo, fecha, hora});

            JOptionPane.showMessageDialog(this, 
                "✅ " + tipo + " registrada exitosamente\n" +
                "🕒 Hora: " + hora, 
                tipo + " Registrada", 
                JOptionPane.INFORMATION_MESSAGE);

            // Actualizar estado de los botones BASADO EN EL ARCHIVO
            actualizarEstadoBotones();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al guardar: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarRegistros() {
        modelo.setRowCount(0);

        File archivo = new File("registros_asistencia.txt");
        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4 && partes[0].equals(usuarioNombre)) {
                    modelo.addRow(new Object[]{partes[0], partes[1], partes[2], partes[3]});
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar registros: " + e.getMessage());
        }
    }

    private void actualizarHoraActual() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            lblHoraActual.setText(ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            lblFechaActual.setText(ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });
        timer.start();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btn_entrada = new javax.swing.JButton();
        btn_salida = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        lblHoraActual = new javax.swing.JLabel();
        lblFechaActual = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        jButton1.setText("MARCAR SALIDA");

        jButton2.setText("MARCAR ENTRADA");

        jLabel3.setText("Ultimos registros:");

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

        jLabel2.setText("Registro de entradas y salidas");

        jButton3.setText("MARCAR SALIDA");

        jButton4.setText("MARCAR ENTRADA");

        jLabel4.setText("Ultimos registros:");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        jLabel5.setText("Registro de entradas y salidas");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_entrada.setText("MARCAR ENTRADA");
        btn_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_entradaActionPerformed(evt);
            }
        });

        btn_salida.setText("MARCAR SALIDA");
        btn_salida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salidaActionPerformed(evt);
            }
        });

        jLabel6.setText("Ultimos registros:");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setResizable(false);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(100);
        }

        jLabel7.setText("Registro de entradas y salidas");

        lblHoraActual.setText("jLabel1");

        lblFechaActual.setText("jLabel1");

        jLabel1.setText("HORA:");

        jLabel8.setText("FECHA:");

        jButton5.setText("Limpiar registros");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(34, 34, 34)
                        .addComponent(lblHoraActual)
                        .addGap(91, 91, 91)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(lblFechaActual))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btn_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_salida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHoraActual)
                    .addComponent(lblFechaActual)
                    .addComponent(jLabel1)
                    .addComponent(jLabel8))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_salida, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_entradaActionPerformed
        registrarAsistencia("ENTRADA");
    }//GEN-LAST:event_btn_entradaActionPerformed

    private void btn_salidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salidaActionPerformed
        registrarAsistencia("SALIDA");
    }//GEN-LAST:event_btn_salidaActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar TODOS sus registros de asistencia?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                File archivo = new File("registros_asistencia.txt");
                File tempFile = new File("registros_temp.txt");

                if (!archivo.exists()) {
                    JOptionPane.showMessageDialog(this,
                            "No hay registros para eliminar",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                boolean encontrado = false;
                try (BufferedReader reader = new BufferedReader(new FileReader(archivo)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        String[] partes = linea.split("\\|");
                        if (partes.length == 4 && partes[0].equals(usuarioNombre)) {
                            encontrado = true; // Este registro será eliminado (no lo escribimos)
                        } else {
                            writer.write(linea);
                            writer.newLine();
                        }
                    }
                }

                if (encontrado) {
                    // Reemplazar archivo original
                    if (archivo.delete() && tempFile.renameTo(archivo)) {
                        // Limpiar tabla y resetear estados
                        modelo.setRowCount(0);
                        yaRegistroEntradaHoy = false;
                        yaRegistroSalidaHoy = false;
                        actualizarEstadoBotones();

                        JOptionPane.showMessageDialog(this,
                                "✅ Sus registros han sido eliminados",
                                "Registros Limpiados",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    tempFile.delete(); // Eliminar archivo temporal
                    JOptionPane.showMessageDialog(this,
                            "No se encontraron registros para eliminar",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar registros: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(jFrame_Asistncias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jFrame_Asistncias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jFrame_Asistncias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jFrame_Asistncias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jFrame_Asistncias().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_entrada;
    private javax.swing.JButton btn_salida;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JLabel lblFechaActual;
    private javax.swing.JLabel lblHoraActual;
    // End of variables declaration//GEN-END:variables
}
