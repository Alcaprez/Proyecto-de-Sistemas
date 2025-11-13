package edu.UPAO.proyecto.app;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.DAO.VentaDAO;
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
import javax.swing.JFrame;
import java.sql.SQLException;
import edu.UPAO.proyecto.DAO.VentaDAO;
import edu.UPAO.proyecto.DAO.ClienteDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class jFrame_GenerarBoleta extends javax.swing.JFrame {

    private Menu2 owner;                        // referencia a la ventana principal
    private DefaultTableModel modeloBoleta;     // modelo que mostrará la boleta (copia del carrito)
    private jFram_MaquinaDePAgo maquina;
    private String idEmpleado;                  // AGREGAR este campo
    private String observaciones = "";

    // Constructor que recibe carrito + totales listos
    public jFrame_GenerarBoleta(Menu2 owner, DefaultTableModel carritoClonado, String subtotal, String descuento, String total) {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        btn_mostrarMaquina.setEnabled(false);

        this.owner = owner;
        this.modeloBoleta = carritoClonado;
        this.idEmpleado = idEmpleado;
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

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Constructor vacío (para pruebas desde NetBeans). No inicializa owner ni modeloBoleta.
    public jFrame_GenerarBoleta() {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        bg_TipoPago = new ButtonGroup();

        btn_mostrarMaquina.setEnabled(false);

        this.owner = null;
        this.modeloBoleta = new DefaultTableModel(); // tabla vacía por defecto
        jTable1.setModel(modeloBoleta);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    }

    // Constructor que recibe la ventana que llamó y el carrito clonado
    public jFrame_GenerarBoleta(Menu2 owner, DefaultTableModel carritoClonado, String subtotal, String descuento, String total, String idEmpleado) {

        initComponents();
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);

        bg_TipoPago = new ButtonGroup();

        btn_mostrarMaquina.setEnabled(false);

        this.owner = owner;
        this.modeloBoleta = carritoClonado;
        this.idEmpleado = idEmpleado; // CAPTURAR id_empleado

        // ✅ CARGAR MÉTODOS DE PAGO EN COMBOBOX
        cargarMetodosPago();

        // ✅ AGREGAR LISTENER PARA CONTROLAR BOTÓN DE MÁQUINA
        cb_metodoPago.addActionListener(e -> controlarBotonMaquina());

        // Mostrar tabla
        jTable1.setModel(modeloBoleta);
        jTable1.setEnabled(false);

        // Mostrar valores recibidos
        lbl_subtotal.setText("Subtotal: " + subtotal);
        lbl_descueto.setText("Descuento: " + descuento);
        lbl_total.setText("Total: " + total);

        // Calcular IGV
        try {
            double totalNum = Double.parseDouble(total.replace("S/", "").trim());
            double subtotalNum = Double.parseDouble(subtotal.replace("S/", "").trim());
            double igv = totalNum - subtotalNum;
            lbl_igv.setText("IGV: " + String.format("%.2f", igv));
        } catch (Exception e) {
            lbl_igv.setText("IGV: -");
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        tf_nombres = new javax.swing.JTextField();
        tf_dni = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btn_mostrarMaquina = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btn_pagado = new javax.swing.JButton();
        lbl_subtotal = new javax.swing.JLabel();
        lbl_igv = new javax.swing.JLabel();
        lbl_total = new javax.swing.JLabel();
        lbl_descueto = new javax.swing.JLabel();
        tf_apellidos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cb_metodoPago = new javax.swing.JComboBox<>();

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

        tf_dni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_dniActionPerformed(evt);
            }
        });

        jLabel2.setText("Nombres:");

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

        btn_pagado.setText("PAGADO");
        btn_pagado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_pagadoActionPerformed(evt);
            }
        });

        lbl_subtotal.setText("Subtoal:");

        lbl_igv.setText("IGV:");

        lbl_total.setText("Total:");

        lbl_descueto.setText("Descuento:");

        jLabel3.setText("Apellidos");

        cb_metodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                                .addComponent(rb_boleta)
                                .addGap(85, 85, 85)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rb_factura)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(cb_id, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel2))
                                            .addComponent(cb_metodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(tf_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(tf_nombres, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(44, 44, 44)
                                                        .addComponent(jLabel3)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(26, 26, 26)
                                                .addComponent(btn_mostrarMaquina, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(24, 24, 24))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbl_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btn_pagado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lbl_total, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lbl_igv, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 828, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lbl_descueto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(17, 17, 17)))))))
                .addContainerGap(12, Short.MAX_VALUE))
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
                    .addComponent(tf_dni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tf_nombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_mostrarMaquina)
                        .addGap(41, 41, 41))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(cb_metodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl_subtotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(btn_pagado, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void tf_dniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_dniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_dniActionPerformed

    private void btn_pagadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_pagadoActionPerformed
        if (!validarCampos()) {
            return;
        }

        try {
            // Calcular totales desde los labels
            String totalText = lbl_total.getText().replace("Total:", "").replace("S/", "").trim();
            String subtotalText = lbl_subtotal.getText().replace("Subtotal:", "").replace("S/", "").trim();
            String descuentoText = lbl_descueto.getText().replace("Descuento:", "").replace("S/", "").trim();

            double total = Double.parseDouble(totalText);
            double subtotal = Double.parseDouble(subtotalText);
            double descuento = Double.parseDouble(descuentoText);
            double igv = total - subtotal;

            // Obtener datos del formulario
            String dniCliente = tf_dni.getText().trim();
            String nombres = tf_nombres.getText().trim();
            String apellidos = tf_apellidos.getText().trim();

            // ✅ OBTENER MÉTODO DE PAGO DEL COMBOBOX (NO DE RADIOBUTTONS)
            String metodoPago = (String) cb_metodoPago.getSelectedItem();
            if (metodoPago == null || metodoPago.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Seleccione un método de pago");
                return;
            }

            // ✅ OBTENER EMPLEADO DE LA SESIÓN
            String idEmpleado = obtenerIdEmpleadoDeSesion();

            // DEBUG
            System.out.println("🔍 DEBUG - Datos de venta:");
            System.out.println("  - Empleado: " + idEmpleado);
            System.out.println("  - Método Pago: " + metodoPago);
            System.out.println("  - DNI Cliente: " + dniCliente);
            System.out.println("  - Total: " + total);

            // ✅ 1. REGISTRAR CLIENTE SI NO EXISTE
            try {
                ClienteDAO clienteDAO = new ClienteDAO();

                if (nombres.isEmpty()) {
                    nombres = "CLIENTE";
                }
                if (apellidos.isEmpty()) {
                    apellidos = "GENERICO";
                }

                clienteDAO.registrarClienteSiNoExiste(dniCliente, nombres, apellidos);
                clienteDAO.cerrarConexion();
            } catch (Exception e) {
                System.err.println("⚠️ Error al registrar cliente: " + e.getMessage());
            }

            // ✅ 2. CREAR DETALLES DE VENTA
            List<DetalleVenta> detalles = new ArrayList<>();
            ProductoDAO productoDAO = new ProductoDAO();

            for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
                String nombreProducto = modeloBoleta.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modeloBoleta.getValueAt(i, 1).toString());
                double precioUnitario = Double.parseDouble(modeloBoleta.getValueAt(i, 2).toString());
                double subtotalItem = Double.parseDouble(modeloBoleta.getValueAt(i, 3).toString());
                String codigoProducto = modeloBoleta.getValueAt(i, 4).toString();

                Producto producto = productoDAO.buscarPorCodigo(codigoProducto);

                if (producto != null) {
                    DetalleVenta detalle = new DetalleVenta(producto, cantidad, precioUnitario);
                    detalles.add(detalle);
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Producto no encontrado: " + nombreProducto);
                    return;
                }
            }

            // ✅ 3. CREAR OBJETO VENTA CON EMPLEADO REAL
            Venta venta = new Venta();
            venta.setIdEmpleado(idEmpleado);
            venta.setDniCliente(dniCliente);
            venta.setMetodoPago(metodoPago); // ✅ Usar método del ComboBox
            venta.setDetalleVenta(detalles);
            venta.setSubtotal(subtotal);
            venta.setIgv(igv);
            venta.setTotal(total);

            // ✅ 4. GUARDAR EN BD USANDO VentaDAO
            VentaDAO ventaDAO = new VentaDAO();
            int idVentaGenerada = ventaDAO.registrarVenta(venta);

            JOptionPane.showMessageDialog(this, "✅ Venta registrada correctamente!\nID Venta: " + idVentaGenerada);

            // ✅ 5. PASAR A VISTA PREVIA
            String observaciones = this.observaciones;
            jFrame_VistaPrevia vista = new jFrame_VistaPrevia(venta, dniCliente, observaciones);
            vista.setLocationRelativeTo(this);
            vista.setVisible(true);

            this.dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error al registrar venta: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_pagadoActionPerformed

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

    private String obtenerIdEmpleadoDeSesion() {
        if (this.idEmpleado != null && !this.idEmpleado.isEmpty()) {
            System.out.println("✅ Usando id_empleado de sesión: " + this.idEmpleado);
            return this.idEmpleado;
        }

        // Fallback seguro
        System.err.println("⚠️ No se pudo obtener id_empleado de sesión");
        JOptionPane.showMessageDialog(this,
                "⚠️ No se pudo obtener empleado de sesión. Contacte al administrador.");
        return "12000001"; // Valor temporal POR EMERGENCIA - usa uno válido de tu BD
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    private boolean validarCampos() {
// Validar que hay productos en la boleta
        if (modeloBoleta.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "❌ No hay productos en la boleta");
            return false;
        }

        // ✅ Validar DNI del cliente
        String dni = tf_dni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese DNI del cliente");
            tf_dni.requestFocus();
            return false;
        }

        // ✅ Validar formato DNI (8 dígitos)
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "❌ DNI debe tener 8 dígitos");
            tf_dni.requestFocus();
            return false;
        }

        // ✅ Validar nombres
        String nombres = tf_nombres.getText().trim();
        if (nombres.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese los nombres del cliente");
            tf_nombres.requestFocus();
            return false;
        }

        // ✅ Validar apellidos
        String apellidos = tf_apellidos.getText().trim();
        if (apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese los apellidos del cliente");
            tf_apellidos.requestFocus();
            return false;
        }

        // ✅ Validar método de pago seleccionado (COMBOBOX)
        if (cb_metodoPago.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "❌ Seleccione método de pago");
            cb_metodoPago.requestFocus();
            return false;
        }

        // Validar que las cantidades sean positivas
        for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
            Object cantidadObj = modeloBoleta.getValueAt(i, 1);
            try {
                int cantidad = Integer.parseInt(String.valueOf(cantidadObj));
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "❌ La cantidad debe ser mayor a 0");
                    return false;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "❌ Cantidad inválida en fila " + (i + 1));
                return false;
            }
        }

        return true;
    }

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

    private void cargarMetodosPago() {
        Connection conexion = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Conectar a la BD y obtener métodos de pago
            String sql = "SELECT id_metodo_pago, nombre FROM metodo_pago";
            conexion = new Conexion().establecerConexion();
            stmt = conexion.prepareStatement(sql);
            rs = stmt.executeQuery();

            cb_metodoPago.removeAllItems();

            while (rs.next()) {
                String nombreMetodo = rs.getString("nombre");
                cb_metodoPago.addItem(nombreMetodo);
            }

            System.out.println("✅ Métodos de pago cargados en ComboBox");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar métodos de pago: " + e.getMessage());
            e.printStackTrace();

            // Valores por defecto en caso de error
            cargarMetodosPagoPorDefecto();

        } finally {
            // ✅ Cerrar conexiones de forma segura
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando conexiones: " + e.getMessage());
            }
        }
    }

// ✅ MÉTODO AUXILIAR PARA CARGAR MÉTODOS POR DEFECTO
    private void cargarMetodosPagoPorDefecto() {
        cb_metodoPago.addItem("Efectivo");
        cb_metodoPago.addItem("Tarjeta Débito");
        cb_metodoPago.addItem("Tarjeta Crédito");
        cb_metodoPago.addItem("Transferencia");
        cb_metodoPago.addItem("Yape/Plin");
        cb_metodoPago.addItem("Mixto");

        System.out.println("⚠️ Cargados métodos de pago por defecto (modo offline)");
    }

    private void controlarBotonMaquina() {
        String metodoSeleccionado = (String) cb_metodoPago.getSelectedItem();
        if (metodoSeleccionado != null) {
            // Habilitar botón solo para métodos digitales
            boolean esDigital = metodoSeleccionado.contains("Tarjeta")
                    || metodoSeleccionado.contains("Transferencia")
                    || metodoSeleccionado.contains("Yape")
                    || metodoSeleccionado.contains("Plin");
            btn_mostrarMaquina.setEnabled(esDigital);

            System.out.println("🔍 Método seleccionado: " + metodoSeleccionado + " - Máquina habilitada: " + esDigital);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_TipoPago;
    private javax.swing.ButtonGroup bg_boletaOfactura;
    private javax.swing.JButton btn_mostrarMaquina;
    private javax.swing.JButton btn_pagado;
    private javax.swing.JComboBox<String> cb_id;
    private javax.swing.JComboBox<String> cb_metodoPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbl_descueto;
    private javax.swing.JLabel lbl_igv;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JRadioButton rb_boleta;
    private javax.swing.JRadioButton rb_factura;
    private javax.swing.JTextField tf_apellidos;
    private javax.swing.JTextField tf_dni;
    private javax.swing.JTextField tf_nombres;
    // End of variables declaration//GEN-END:variables
}
