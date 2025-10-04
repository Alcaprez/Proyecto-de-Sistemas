package edu.UPAO.proyecto.app;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import edu.UPAO.proyecto.app.Menu2;
import edu.UPAO.proyecto.ProductoController;
import edu.UPAO.proyecto.Modelo.Producto;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import edu.UPAO.proyecto.VentasController;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.VentaItem;

public class jFrame_GenerarBoleta extends javax.swing.JFrame {
    private Menu2 owner;                // referencia a la ventana principal
    private DefaultTableModel modeloBoleta;     // modelo que mostrará la boleta (copia del carrito)
    private jFram_MaquinaDePAgo maquina;
    // Constructor que recibe carrito + totales listos
    public jFrame_GenerarBoleta(Menu2 owner, DefaultTableModel carritoClonado,
            String subtotal, String descuento, String total) {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        bg_TipoPago = new ButtonGroup();
        bg_TipoPago.add(rb_efectivo);
        bg_TipoPago.add(rb_digital);
        bg_TipoPago.add(rb_mixto);

        btn_mostrarMaquina.setEnabled(false);

        this.owner = owner;
        this.modeloBoleta = carritoClonado;

        // Mostrar tabla
        jTable1.setModel(modeloBoleta);
        jTable1.setEnabled(false);

        // Mostrar valores recibidos
        lbl_subtotal.setText("Subtotal: " + subtotal);
        lbl_descueto.setText("Descuento: " + descuento);
        lbl_total.setText("Total: " + total);

        // Si quieres, también puedes recalcular IGV
        try {
            double totalNum = Double.parseDouble(total.replace("S/", "").trim());
            double subtotalNum = Double.parseDouble(subtotal.replace("S/", "").trim());
            double igv = totalNum - subtotalNum;
            lbl_igv.setText("IGV: " + String.format("%.2f", igv));
        } catch (Exception e) {
            lbl_igv.setText("IGV: -");
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    // Constructor vacío (para pruebas desde NetBeans). No inicializa owner ni modeloBoleta.
    public jFrame_GenerarBoleta() {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        bg_TipoPago = new ButtonGroup();
        bg_TipoPago.add(rb_efectivo);
        bg_TipoPago.add(rb_digital);
        bg_TipoPago.add(rb_mixto);
        btn_mostrarMaquina.setEnabled(false);

        this.owner = null;
        this.modeloBoleta = new DefaultTableModel(); // tabla vacía por defecto
        jTable1.setModel(modeloBoleta);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    }

    // Constructor que recibe la ventana que llamó y el carrito clonado
    public jFrame_GenerarBoleta(Menu2 owner, DefaultTableModel carritoClonado) {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        btn_mostrarMaquina.setEnabled(false);

        bg_TipoPago = new ButtonGroup();
        bg_TipoPago.add(rb_efectivo);
        bg_TipoPago.add(rb_digital);
        bg_TipoPago.add(rb_mixto);

        this.owner = owner;
        this.modeloBoleta = carritoClonado;

        // Mostrar modelo clonado en la tabla de la boleta
        jTable1.setModel(modeloBoleta);
        jTable1.setEnabled(false);

        // Calcular totales sobre la copia
        calcularTotales();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    }

    @SuppressWarnings("unchecked")

    private void calcularTotales() {
        double total = 0.0;
        for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
            Object val = modeloBoleta.getValueAt(i, 2); // Columna "Subtotal"
            if (val != null) {
                total += Double.parseDouble(String.valueOf(val));
            }
        }

        double subtotal = total / 1.18;
        double igv = subtotal * 0.18;

        lbl_subtotal.setText("Subtotal: " + String.format("%.2f", subtotal));
        lbl_igv.setText("IGV (18%): " + String.format("%.2f", igv));
        lbl_total.setText("Total: " + String.format("%.2f", total));
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_boletaOfactura = new javax.swing.ButtonGroup();
        bg_TipoPago = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rb_boleta = new javax.swing.JRadioButton();
        rb_factura = new javax.swing.JRadioButton();
        cb_id = new javax.swing.JComboBox<>();
        tf_nombre = new javax.swing.JTextField();
        tf_id = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rb_digital = new javax.swing.JRadioButton();
        rb_efectivo = new javax.swing.JRadioButton();
        rb_mixto = new javax.swing.JRadioButton();
        btn_mostrarMaquina = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btn_imprimir = new javax.swing.JButton();
        lbl_subtotal = new javax.swing.JLabel();
        lbl_igv = new javax.swing.JLabel();
        lbl_total = new javax.swing.JLabel();
        lbl_descueto = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("Generar comprobante de pago");

        rb_boleta.setText("Boleta");
        rb_boleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_boletaActionPerformed(evt);
            }
        });

        rb_factura.setText("Factura");
        rb_factura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_facturaActionPerformed(evt);
            }
        });

        cb_id.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DNI", "RUC", " " }));
        cb_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_idActionPerformed(evt);
            }
        });

        tf_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_idActionPerformed(evt);
            }
        });

        jLabel2.setText("Nombre:");

        rb_digital.setText("Pago digital");
        rb_digital.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_digitalActionPerformed(evt);
            }
        });

        rb_efectivo.setText("Pago efectivo");
        rb_efectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_efectivoActionPerformed(evt);
            }
        });

        rb_mixto.setText("Pago mixto");
        rb_mixto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_mixtoActionPerformed(evt);
            }
        });

        btn_mostrarMaquina.setText("Mostrar maquina");
        btn_mostrarMaquina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_mostrarMaquinaActionPerformed(evt);
            }
        });

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

        btn_imprimir.setText("IMPRIMIR");
        btn_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_imprimirActionPerformed(evt);
            }
        });

        lbl_subtotal.setText("Subtoal:");

        lbl_igv.setText("IGV:");

        lbl_total.setText("Total:");

        lbl_descueto.setText("Descuento:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rb_boleta)
                                    .addComponent(rb_efectivo))
                                .addGap(47, 47, 47)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addComponent(jLabel2))
                                            .addComponent(cb_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tf_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tf_id, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(rb_factura)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(rb_digital)
                                        .addGap(56, 56, 56)
                                        .addComponent(rb_mixto))
                                    .addComponent(btn_mostrarMaquina, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbl_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lbl_total, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lbl_igv, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 828, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lbl_descueto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(17, 17, 17)))))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_boleta)
                    .addComponent(rb_factura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tf_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_digital)
                    .addComponent(rb_efectivo)
                    .addComponent(rb_mixto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_mostrarMaquina)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl_subtotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(btn_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl_descueto)
                        .addGap(4, 4, 4)
                        .addComponent(lbl_igv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_total)))
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rb_boletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_boletaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rb_boletaActionPerformed

    private void rb_facturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_facturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rb_facturaActionPerformed

    private void cb_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_idActionPerformed

    private void tf_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_idActionPerformed

    private void btn_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_imprimirActionPerformed
      // Construir lista de DetalleVenta y calcular total
    ProductoController pc = new ProductoController();
    List<Producto> productos = pc.cargarProductos();

    List<DetalleVenta> detalles = new ArrayList<>();

    for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
        String nombreFila = String.valueOf(modeloBoleta.getValueAt(i, 0));
        int cantidadVendida = 0;
        double subtotal = 0.0;
        try {
            Object valCant = modeloBoleta.getValueAt(i, 1); // columna cantidad
            if (valCant instanceof Number number) {
                cantidadVendida = number.intValue();
            } else {
                cantidadVendida = Integer.parseInt(String.valueOf(valCant));
            }
        } catch (Exception ex) {
            cantidadVendida = 0;
        }
        try {
            Object valSub = modeloBoleta.getValueAt(i, 2); // columna subtotal
            subtotal = Double.parseDouble(String.valueOf(valSub));
        } catch (Exception ex) {
            subtotal = 0.0;
        }

        double precioUnitario = (cantidadVendida > 0) ? (subtotal / cantidadVendida) : 0.0;

        // Buscar producto existente por nombre en la lista de productos cargada
        Producto productoExistente = null;
        for (Producto p : productos) {
            if (p.getNombre() != null && p.getNombre().equalsIgnoreCase(nombreFila)) {
                productoExistente = p;
                break;
            }
        }

        Producto productoParaDetalle;
        if (productoExistente != null) {
            productoParaDetalle = productoExistente;
        } else {
            // Producto temporal (si no se encuentra en productos.txt)
            productoParaDetalle = new Producto(); // usa constructor vacío y setters
            productoParaDetalle.setNombre(nombreFila);
            productoParaDetalle.setPrecioVenta(precioUnitario);
            productoParaDetalle.setCodigo(""); // o algún valor por defecto
            productoParaDetalle.setStock(0);
        }

        DetalleVenta detalle = new DetalleVenta(productoParaDetalle, cantidadVendida, precioUnitario);
        detalles.add(detalle);
    }

    // Actualizar inventario (se actualiza sobre la lista 'productos' cargada)
    for (DetalleVenta d : detalles) {
        for (Producto p : productos) {
            if (p.getNombre() != null && p.getNombre().equalsIgnoreCase(d.getProducto().getNombre())) {
                p.setStock(Math.max(0, p.getStock() - d.getCantidad()));
                p.setVendidos(p.getVendidos() + d.getCantidad());
                break;
            }
        }
    }

    // Guardar cambios en productos.txt
    pc.guardarProductos(productos);

    // Crear objeto Venta con id (simple autogeneración por tamaño actual)
    int idVenta = VentasController.getVentas().size() + 1; // si prefieres, implementa generarIdVenta() en VentasController
    int cajeroId = 0;
    try {
        cajeroId = Integer.parseInt(tf_id.getText().trim());
    } catch (Exception e) {
        // si tf_id no es numérico, dejamos 0 (o ajusta según tu lógica)
    }

    String metodoPago = rb_efectivo.isSelected() ? "Efectivo"
                      : rb_digital.isSelected() ? "Digital"
                      : "Mixto";

    Venta venta = new Venta(idVenta, cajeroId, metodoPago, detalles);
    VentasController.registrarVenta(venta);
    }//GEN-LAST:event_btn_imprimirActionPerformed

    private void rb_mixtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_mixtoActionPerformed
        btn_mostrarMaquina.setEnabled(true);

    }//GEN-LAST:event_rb_mixtoActionPerformed

    private void rb_digitalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_digitalActionPerformed
        btn_mostrarMaquina.setEnabled(true);

    }//GEN-LAST:event_rb_digitalActionPerformed

    private void btn_mostrarMaquinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_mostrarMaquinaActionPerformed
       if (maquina == null || !maquina.isDisplayable()) {
        // Si no existe o ya fue cerrada, la creas
        maquina = new jFram_MaquinaDePAgo();
        maquina.setLocationRelativeTo(this); // la centras respecto al JFrame actual
        maquina.setVisible(true);
    } else {
        // Si ya existe, solo la traes al frente
        maquina.toFront();
        maquina.requestFocus();
    }
    }//GEN-LAST:event_btn_mostrarMaquinaActionPerformed

    private void rb_efectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_efectivoActionPerformed
        btn_mostrarMaquina.setEnabled(false);
    }//GEN-LAST:event_rb_efectivoActionPerformed

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
            java.util.logging.Logger.getLogger(jFrame_GenerarBoleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jFrame_GenerarBoleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jFrame_GenerarBoleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jFrame_GenerarBoleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jFrame_GenerarBoleta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_TipoPago;
    private javax.swing.ButtonGroup bg_boletaOfactura;
    private javax.swing.JButton btn_imprimir;
    private javax.swing.JButton btn_mostrarMaquina;
    private javax.swing.JComboBox<String> cb_id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbl_descueto;
    private javax.swing.JLabel lbl_igv;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JRadioButton rb_boleta;
    private javax.swing.JRadioButton rb_digital;
    private javax.swing.JRadioButton rb_efectivo;
    private javax.swing.JRadioButton rb_factura;
    private javax.swing.JRadioButton rb_mixto;
    private javax.swing.JTextField tf_id;
    private javax.swing.JTextField tf_nombre;
    // End of variables declaration//GEN-END:variables
}
