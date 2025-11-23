package edu.UPAO.proyecto.app;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.DAO.VentaDAO;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import edu.UPAO.proyecto.app.Menu2;
import edu.UPAO.proyecto.Modelo.Producto;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Venta;
import javax.swing.JFrame;
import java.sql.SQLException;
import edu.UPAO.proyecto.DAO.VentaDAO;
import edu.UPAO.proyecto.DAO.ClienteDAO;
import edu.UPAO.proyecto.DAO.ComprobanteDAO;
import edu.UPAO.proyecto.util.GeneradorPDF;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.Timer;

public class jFrame_GenerarBoleta extends javax.swing.JFrame {

    // Constructor que recibe carrito + totales listos
    private Menu2 menuPrincipal;  // ✅ Referencia a la ventana del cajero
    private DefaultTableModel modeloBoleta;
    private jFram_MaquinaDePAgo maquina;
    private String idEmpleado;
    private int idSucursal;
    private String observaciones = "";

    // ✅ CONSTRUCTOR PRINCIPAL (El que usas desde Menu2)
    public jFrame_GenerarBoleta(Menu2 menu, DefaultTableModel carritoClonado,
            String subtotal, String descuento, String total,
            String idEmpleado, int idSucursal, String observaciones) {

        initComponents();
        this.menuPrincipal = menu; // ✅ Guardamos referencia para actualizar stock luego
        this.modeloBoleta = carritoClonado;
        this.idEmpleado = idEmpleado;
        this.idSucursal = idSucursal;
        this.observaciones = observaciones;

        configurarAutocompletarCliente();
        cargarMetodosPago();

        // Grupos de botones
        bg_boletaOfactura = new ButtonGroup();
        bg_boletaOfactura.add(rb_boleta);
        bg_boletaOfactura.add(rb_factura);
        rb_boleta.setSelected(true); // Boleta por defecto

        btn_mostrarMaquina.setEnabled(false);
        cb_metodoPago.addActionListener(e -> controlarBotonMaquina());

        // Configurar Tabla
        jTable1.setModel(modeloBoleta);
        jTable1.setEnabled(false);

        // Mostrar totales iniciales
        lbl_subtotal.setText("Subtotal: " + subtotal);
        lbl_descueto.setText("Descuento: " + descuento);
        lbl_total.setText("Total: " + total);

        // Recalcular IGV para mostrarlo desglosado correctamente
        calcularTotales();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ✅ Constructor vacío (Solo para evitar errores de diseño en NetBeans)
    public jFrame_GenerarBoleta() {
        initComponents();
    }

    private void verificarMontoMovimientoCaja(double totalVenta) {
        try {
            System.out.println("🔍 VERIFICANDO MONTO MOVIMIENTO CAJA:");
            System.out.println("   - Total venta calculado: " + totalVenta);

            // Consultar el último movimiento de caja registrado
            String sql = "SELECT monto FROM movimiento_caja ORDER BY id_movimiento_caja DESC LIMIT 1";
            Connection conn = new Conexion().establecerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double montoRegistrado = rs.getDouble("monto");
                System.out.println("   - Monto en movimiento_caja: " + montoRegistrado);

                if (Math.abs(totalVenta - montoRegistrado) > 0.01) {
                    System.err.println("❌ DISCREPANCIA: Movimiento caja registró " + montoRegistrado + " pero venta es " + totalVenta);
                } else {
                    System.out.println("✅ Monto correcto en movimiento_caja");
                }
            }

            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Error verificando movimiento caja: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")

    private void calcularTotales() {
        double subtotal = 0.0;
        for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
            Object val = modeloBoleta.getValueAt(i, 3); // Columna Subtotal
            if (val != null) {
                subtotal += Double.parseDouble(String.valueOf(val));
            }
        }

        // Recuperar descuento si existe
        double descuento = 0.0;
        try {
            String descText = lbl_descueto.getText().replace("Descuento:", "").replace("S/", "").trim();
            descuento = Double.parseDouble(descText);
        } catch (Exception e) {
        }

        double baseImponible = (subtotal - descuento) / 1.18;
        double igv = baseImponible * 0.18;
        double total = subtotal - descuento;

        // Actualizar etiquetas
        lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", baseImponible));
        lbl_igv.setText("IGV (18%): S/ " + String.format("%.2f", igv));
        lbl_total.setText("Total: S/ " + String.format("%.2f", total));
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
            // 1. OBTENER DATOS
            String totalText = lbl_total.getText().replace("Total:", "").replace("S/", "").replace(",", ".").trim();
            double totalVenta = Double.parseDouble(totalText);

            // Recalcular bases para guardar exacto
            double subtotalBase = totalVenta / 1.18;
            double igv = subtotalBase * 0.18;

            String dni = tf_dni.getText().trim();
            String nombres = tf_nombres.getText().trim();
            String apellidos = tf_apellidos.getText().trim();
            String metodoPago = (String) cb_metodoPago.getSelectedItem();

            // 2. REGISTRAR CLIENTE (Si no existe)
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.registrarClienteSiNoExiste(dni, nombres.isEmpty() ? "CLIENTE" : nombres, apellidos.isEmpty() ? "GENERICO" : apellidos);
            clienteDAO.cerrarConexion();

            // 3. PREPARAR DETALLE VENTA
            List<DetalleVenta> detalles = new ArrayList<>();
            ProductoDAO productoDAO = new ProductoDAO();

            for (int i = 0; i < modeloBoleta.getRowCount(); i++) {
                String codigo = modeloBoleta.getValueAt(i, 4).toString();
                int cantidad = Integer.parseInt(modeloBoleta.getValueAt(i, 1).toString());
                double precio = Double.parseDouble(modeloBoleta.getValueAt(i, 2).toString());

                Producto p = productoDAO.buscarPorCodigo(codigo, this.idSucursal);
                if (p != null) {
                    detalles.add(new DetalleVenta(p, cantidad, precio));
                }
            }
            productoDAO.cerrarConexion();

            // 4. CREAR OBJETO VENTA
            Venta venta = new Venta();
            venta.setIdEmpleado(this.idEmpleado);
            venta.setIdSucursal(this.idSucursal);
            venta.setDniCliente(dni);
            venta.setMetodoPago(metodoPago);
            venta.setDetalleVenta(detalles);
            venta.setSubtotal(subtotalBase);
            venta.setIgv(igv);
            venta.setTotal(totalVenta);

            // 5. GUARDAR VENTA EN BD
            VentaDAO ventaDAO = new VentaDAO();
            int idVenta = ventaDAO.registrarVenta(venta);

            if (idVenta > 0) {
                // ===============================================================
                // 🔥 EL TRUCO SIMPLE: AGREGAR EL DESCUENTO VISUALMENTE PARA EL PDF
                // ===============================================================
                try {
                    // 1. Obtenemos el valor del descuento desde tu etiqueta
                    String descText = lbl_descueto.getText().replace("Descuento:", "").replace("S/", "").trim();
                    double descuentoVal = Double.parseDouble(descText);

                    // 2. Si hay descuento, lo metemos a la lista de la venta como un "Item Falso"
                    // Esto NO afecta a la base de datos porque ya se guardó arriba.
                    // Solo afecta al PDF que se va a generar ahora.
                    if (descuentoVal > 0) {
                        Producto pDescuento = new Producto();
                        pDescuento.setNombre(">> DESCUENTO CUPÓN <<"); // Nombre que saldrá en el PDF

                        // Agregamos a la lista en memoria (Item, Cantidad 1, Precio Negativo)
                        venta.getDetalleVenta().add(new DetalleVenta(pDescuento, 1, -descuentoVal));
                    }
                } catch (Exception ex) {
                    System.out.println("No se pudo aplicar descuento visual: " + ex.getMessage());
                }
                // ===============================================================

                // 6. GENERAR COMPROBANTE Y PDF (Tu código original)
                // Al llamar a esto, el PDF leerá la lista que acabamos de modificar
                generarComprobanteYVistaPrevia(venta, idVenta, dni, observaciones);

                JOptionPane.showMessageDialog(this, "✅ Venta registrada correctamente!\nID: " + idVenta);

                // 7. ACTUALIZAR STOCK Y LIMPIAR MENU
                if (this.menuPrincipal != null) {
                    this.menuPrincipal.finalizarVenta();
                }

                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al guardar venta en BD.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error crítico: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_pagadoActionPerformed

    private void generarComprobanteYVistaPrevia(Venta venta, int idVenta, String dni, String obs) {
        try {
            // Registrar comprobante en BD
            new ComprobanteDAO().registrarComprobante(idVenta, "BOLETA", venta);

            // Generar PDF
            String numComprobante = String.format("B001-%08d", idVenta);
            String rutaPDF = GeneradorPDF.generarBoleta(venta, numComprobante);

            // Abrir Vista Previa pasando el menú para que el botón "Listo" también pueda actualizar
            jFrame_VistaPrevia vista = new jFrame_VistaPrevia(
                    this.menuPrincipal, // ✅ Pasamos la referencia
                    venta,
                    dni,
                    obs,
                    rutaPDF
            );
            vista.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error generando comprobante: " + e.getMessage());
        }
    }

    private void btn_mostrarMaquinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_mostrarMaquinaActionPerformed
        if (maquina == null || !maquina.isDisplayable()) {
            maquina = new jFram_MaquinaDePAgo();
            maquina.setLocationRelativeTo(this);
            maquina.setVisible(true);
        } else {
            maquina.toFront();
            maquina.requestFocus();
        }
    }//GEN-LAST:event_btn_mostrarMaquinaActionPerformed

    private String obtenerIdEmpleadoDeSesion() {
        if (this.idEmpleado != null && !this.idEmpleado.isEmpty()) {
            System.out.println("✅ Usando id_empleado de sesión: " + this.idEmpleado);
            return this.idEmpleado;
        }

        System.err.println("⚠️ No se pudo obtener id_empleado de sesión");
        JOptionPane.showMessageDialog(this,
                "⚠️ No se pudo obtener empleado de sesión. Contacte al administrador.");
        return "12000001";
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
        System.out.println("✅ Observaciones establecidas: " + observaciones);
    }

    private boolean validarCampos() {
        if (modeloBoleta.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "❌ No hay productos en la boleta");
            return false;
        }

        String dni = tf_dni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese DNI del cliente");
            tf_dni.requestFocus();
            return false;
        }

        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "❌ DNI debe tener 8 dígitos");
            tf_dni.requestFocus();
            return false;
        }

        String nombres = tf_nombres.getText().trim();
        if (nombres.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese los nombres del cliente");
            tf_nombres.requestFocus();
            return false;
        }

        String apellidos = tf_apellidos.getText().trim();
        if (apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Ingrese los apellidos del cliente");
            tf_apellidos.requestFocus();
            return false;
        }

        if (cb_metodoPago.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "❌ Seleccione método de pago");
            cb_metodoPago.requestFocus();
            return false;
        }

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

// MÉTODO AUXILIAR PARA CARGAR MÉTODOS POR DEFECTO
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

    private void autocompletarDatosCliente() {
        String dni = tf_dni.getText().trim();

        System.out.println("🔍 Buscando cliente con DNI: " + dni);

        if (dni.isEmpty() || !dni.matches("\\d{8}")) {
            System.out.println("❌ DNI inválido para búsqueda");
            return;
        }

        try {
            ClienteDAO clienteDAO = new ClienteDAO();
            String[] datosCliente = clienteDAO.obtenerNombresApellidosPorDNI(dni);
            clienteDAO.cerrarConexion();

            if (datosCliente != null && !datosCliente[0].isEmpty()) {
                tf_nombres.setText(datosCliente[0]);
                tf_apellidos.setText(datosCliente[1]);
                System.out.println("✅ Cliente encontrado: " + datosCliente[0] + " " + datosCliente[1]);

                // Opcional: Mostrar mensaje de confirmación
                // JOptionPane.showMessageDialog(this, "Cliente encontrado: " + datosCliente[0] + " " + datosCliente[1]);
            } else {
                // Cliente no existe - limpiar campos y poner foco en nombres
                tf_nombres.setText("");
                tf_apellidos.setText("");
                tf_nombres.requestFocus();
                System.out.println("❌ Cliente no encontrado con DNI: " + dni);

                // Opcional: Preguntar si quiere crear nuevo cliente
                // int respuesta = JOptionPane.showConfirmDialog(this, 
                //     "Cliente no encontrado. ¿Desea registrar uno nuevo?", 
                //     "Cliente Nuevo", JOptionPane.YES_NO_OPTION);
            }
        } catch (Exception e) {
            System.err.println("❌ Error autocompletando cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarAutocompletarCliente() {
        // Evento cuando se presiona ENTER en el DNI
        tf_dni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autocompletarDatosCliente();
            }
        });

        // Evento cuando se pierde el foco (tab o click fuera)
        tf_dni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Pequeño delay para asegurar que el texto se haya actualizado
                Timer timer = new Timer(100, e -> autocompletarDatosCliente());
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Evento cuando se cambia el texto (opcional, para búsqueda en tiempo real)
        tf_dni.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (tf_dni.getText().length() == 8) {
                    autocompletarDatosCliente();
                }
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });
    }

    private int obtenerSucursalEmpleado(String idEmpleado) {
        String sql = "SELECT id_sucursal FROM empleado WHERE id_empleado = ?";

        try (Connection conn = new Conexion().establecerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_sucursal");
            }
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo sucursal: " + e.getMessage());
        }
        return 1; // Sucursal por defecto
    }

    private void generarComprobante(Venta venta, int idVentaGenerada) {
        try {
            ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
            String tipoComprobante = "BOLETA"; // Por ahora solo boletas

            boolean exito = comprobanteDAO.registrarComprobante(idVentaGenerada, tipoComprobante, venta);

            if (exito) {
                System.out.println("✅ Comprobante PDF generado y registrado");

                // Opcional: Mostrar mensaje al usuario
                JOptionPane.showMessageDialog(this,
                        "✅ Comprobante generado exitosamente");
            } else {
                System.err.println("⚠️ No se pudo generar comprobante");
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error generando comprobante: " + e.getMessage());
            // NO interrumpir el flujo por error en comprobante
        }
    }

    private void diagnosticarCalculos() {
        try {
            System.out.println("🔍 DIAGNÓSTICO DE CÁLCULOS:");

            // Verificar valores actuales en labels
            String totalLabel = lbl_total.getText();
            String subtotalLabel = lbl_subtotal.getText();
            String igvLabel = lbl_igv.getText();

            System.out.println("   - Total en label: " + totalLabel);
            System.out.println("   - Subtotal en label: " + subtotalLabel);
            System.out.println("   - IGV en label: " + igvLabel);

            // Calcular correctamente
            double totalNum = Double.parseDouble(totalLabel.replace("Total:", "").replace("S/", "").trim());
            double subtotalCorrecto = totalNum / 1.18;
            double igvCorrecto = subtotalCorrecto * 0.18;

            System.out.println("   - Total numérico: " + totalNum);
            System.out.println("   - Subtotal correcto: " + subtotalCorrecto);
            System.out.println("   - IGV correcto: " + igvCorrecto);
            System.out.println("   - Verificación: " + subtotalCorrecto + " + " + igvCorrecto + " = " + (subtotalCorrecto + igvCorrecto));

        } catch (Exception e) {
            System.err.println("❌ Error en diagnóstico de cálculos: " + e.getMessage());
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
