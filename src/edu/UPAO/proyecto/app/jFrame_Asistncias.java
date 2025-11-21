package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.Modelo.RegistroAsistencia;
import edu.UPAO.proyecto.Service.AsistenciaService;
import java.io.*;
import java.awt.Color;
import java.time.LocalDate;
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
    private String idEmpleado;
    private final AsistenciaService asistenciaService = new AsistenciaService();
    private LocalDate fechaHoy = LocalDate.now();
    // Variables para animación
    // Formatters
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public jFrame_Asistncias() {
        this("12000001", "Usuario Demo");
    }

// CONSTRUCTOR TEMPORAL - SOLUCIÓN INMEDIATA
    public jFrame_Asistncias(String usuarioNombre) {
        this("12000001", usuarioNombre); // ID por defecto
    }

    public jFrame_Asistncias(String idEmpleado, String usuarioNombre) {
        this.idEmpleado = idEmpleado;
        this.usuarioNombre = usuarioNombre;

        // ⚠️ QUITA ESTA LÍNEA: setOpacity(0.0f);
        initComponents();
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // CENTRAR VENTANA

        // Configurar tabla
        modelo = new DefaultTableModel(new Object[]{"Usuario", "Tipo", "Fecha", "Hora", "Estado"}, 0);
        jTable3.setModel(modelo);

        jLabel7.setText("Registro - " + usuarioNombre);
        cargarRegistros();
        actualizarEstadoBotones();
        actualizarHoraActual();

        //configurarAnimacion();
        //mostrarConAnimacion();
    }

    private void verificarEstadoActual() {

    }

    private void actualizarEstadoBotones() {
        String estado = asistenciaService.obtenerEstadoActual(idEmpleado);

        switch (estado) {
            case "PENDIENTE_ENTRADA":
                btn_entrada.setEnabled(true);
                btn_entrada.setText("MARCAR ENTRADA");
                btn_entrada.setBackground(new java.awt.Color(76, 175, 80));
                btn_salida.setEnabled(false);
                btn_salida.setText("SALIDA (Espere entrada)");
                btn_salida.setBackground(new java.awt.Color(200, 200, 200));
                break;

            case "ENTRADA_REGISTRADA":
                btn_entrada.setEnabled(false);
                btn_entrada.setText("✓ ENTRADA REGISTRADA");
                btn_entrada.setBackground(new java.awt.Color(46, 125, 50));
                btn_salida.setEnabled(true);
                btn_salida.setText("MARCAR SALIDA");
                btn_salida.setBackground(new java.awt.Color(244, 67, 54));
                break;

            case "SALIDA_REGISTRADA":
                btn_entrada.setEnabled(false);
                btn_entrada.setText("✓ ENTRADA REGISTRADA");
                btn_entrada.setBackground(new java.awt.Color(46, 125, 50));
                btn_salida.setEnabled(false);
                btn_salida.setText("✓ SALIDA REGISTRADA");
                btn_salida.setBackground(new java.awt.Color(183, 28, 28));
                break;
        }
    }

    private void actualizarMensajeEstado(String estado) {
        String mensaje = "";
        Color color = Color.BLACK;

        switch (estado) {
            case "PENDIENTE_ENTRADA":
                mensaje = "⏳ Esperando registro de entrada";
                color = Color.ORANGE;
                break;
            case "ENTRADA_REGISTRADA":
                mensaje = "✅ Entrada registrada - Puede registrar salida";
                color = Color.BLUE;
                break;
            case "SALIDA_REGISTRADA":
                mensaje = "✅ Jornada completada - Ventana se cerrará automáticamente";
                color = new Color(0, 100, 0); // Verde oscuro
                break;
        }

        // Si tienes un label para mensajes, actualízalo aquí
        // lblMensajeEstado.setText(mensaje);
        // lblMensajeEstado.setForeground(color);
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
            RegistroAsistencia registro = null;

            if (tipo.equals("ENTRADA")) {
                registro = asistenciaService.registrarEntrada(idEmpleado, usuarioNombre);
            } else if (tipo.equals("SALIDA")) {
                registro = asistenciaService.registrarSalida(idEmpleado, usuarioNombre);
            }

            if (registro != null) {
                // Agregar a la tabla
                String fecha = registro.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String hora = registro.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                modelo.addRow(new Object[]{
                    usuarioNombre,
                    tipo,
                    fecha,
                    hora,
                    registro.getEstado()
                });

                // Mostrar mensaje de confirmación
                String mensaje = String.format(
                        "✅ %s registrada exitosamente\n🕒 Hora: %s\n📊 Estado: %s",
                        tipo, hora, registro.getEstado()
                );

                JOptionPane.showMessageDialog(this,
                        mensaje,
                        tipo + " Registrada",
                        JOptionPane.INFORMATION_MESSAGE);

                // Actualizar estado de los botones
                actualizarEstadoBotones();

                // Scroll to the last row
                if (modelo.getRowCount() > 0) {
                    jTable3.scrollRectToVisible(jTable3.getCellRect(modelo.getRowCount() - 1, 0, true));
                }
            }

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error al registrar " + tipo + ": " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void actualizarHoraActual() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            lblHoraActual.setText(ahora.format(timeFormatter));
            lblFechaActual.setText(ahora.format(dateFormatter));
        });
        timer.start();
    }

    public void autoCompletarSiEsNecesario() {
        String estado = asistenciaService.obtenerEstadoActual(idEmpleado);

        if (estado.equals("ENTRADA_REGISTRADA")) {
            // El empleado registró entrada pero no salida
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "⚠️  Tiene entrada registrada pero no salida.\n¿Desea registrar salida automáticamente?",
                    "Registro de Salida Pendiente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RegistroAsistencia registro = asistenciaService.registrarSalida(idEmpleado, usuarioNombre);
                    JOptionPane.showMessageDialog(this,
                            "✅ Salida automática registrada a las: "
                            + registro.getFechaHora().format(timeFormatter),
                            "Salida Registrada",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Error al auto-completar salida: " + ex.getMessage());
                }
            }
        }
    }

    public static void mostrarRegistroAsistencia(String idEmpleado, String nombreEmpleado) {
        SwingUtilities.invokeLater(() -> {
            jFrame_Asistncias registro = new jFrame_Asistncias(idEmpleado, nombreEmpleado);
            registro.setVisible(true); // ✅ El setVisible debe estar AQUÍ, no en el constructor
        });
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
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        lblHoraActual = new javax.swing.JLabel();
        lblFechaActual = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btn_entrada = new javax.swing.JButton();
        btn_salida = new javax.swing.JButton();

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
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btn_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn_salida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHoraActual)
                    .addComponent(lblFechaActual)
                    .addComponent(jLabel1)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_salida, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(96, 96, 96))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_entradaActionPerformed
        registrarAsistencia("ENTRADA");
    }//GEN-LAST:event_btn_entradaActionPerformed

    private void btn_salidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salidaActionPerformed
        registrarAsistencia("SALIDA");
    }//GEN-LAST:event_btn_salidaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
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

    private void cargarRegistros() {
        // 1. Limpiar la tabla actual
        modelo.setRowCount(0);
        System.out.println("Cargando registros para: " + usuarioNombre);

        // 2. Obtener la asistencia desde la Base de Datos usando el Servicio
        java.util.Optional<edu.UPAO.proyecto.Modelo.Asistencia> asistenciaOpt = asistenciaService.obtenerAsistenciaHoy(idEmpleado);

        // 3. Si existe un registro para hoy, lo procesamos
        if (asistenciaOpt.isPresent()) {
            edu.UPAO.proyecto.Modelo.Asistencia asistencia = asistenciaOpt.get();

            // Formateadores de fecha y hora
            DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm:ss");

            // --- FILA DE ENTRADA ---
            if (asistencia.getHoraEntrada() != null) {
                // Calculamos el estado visualmente (puedes ajustar la lógica si lo deseas)
                String estado = "REGISTRADO";

                modelo.addRow(new Object[]{
                    usuarioNombre,
                    "ENTRADA",
                    asistencia.getFecha().format(fmtFecha),
                    asistencia.getHoraEntrada().format(fmtHora),
                    estado
                });
            }

            // --- FILA DE SALIDA ---
            if (asistencia.getHoraSalida() != null) {
                modelo.addRow(new Object[]{
                    usuarioNombre,
                    "SALIDA",
                    asistencia.getFecha().format(fmtFecha),
                    asistencia.getHoraSalida().format(fmtHora),
                    "FINALIZADO"
                });
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_entrada;
    private javax.swing.JButton btn_salida;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
