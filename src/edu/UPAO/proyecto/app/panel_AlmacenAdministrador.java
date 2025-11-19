
package edu.UPAO.proyecto.app;


public class panel_AlmacenAdministrador extends javax.swing.JPanel {

    
    public panel_AlmacenAdministrador() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tf_nombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tf_precio = new javax.swing.JTextField();
        tf_stock = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        rb_generarcodigo = new javax.swing.JRadioButton();
        rb_sku = new javax.swing.JRadioButton();
        tf_codigoProducto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_escanearrSKU = new javax.swing.JButton();
        btn_añadir = new javax.swing.JButton();
        cb_categoria = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btn_actualizar = new javax.swing.JButton();
        btn_eliminar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_productos = new javax.swing.JTable();
        btn_cancelar = new javax.swing.JButton();
        PROMOCIONES = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabla_productos1 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        tf_nombre5 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tf_precio5 = new javax.swing.JTextField();
        tf_stock5 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        tf_codigoProducto5 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        btn_añadir5 = new javax.swing.JButton();
        cb_categoria5 = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        btn_actualizar1 = new javax.swing.JButton();
        btn_DeseleccionarLimpiar = new javax.swing.JButton();
        btn_eliminar1 = new javax.swing.JButton();
        tf_busqueda = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("Registro de productos");

        jPanel4.setBackground(new java.awt.Color(194, 211, 205));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Precio:");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setText("Stock:");

        rb_generarcodigo.setText("Ingresar codigo");
        rb_generarcodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_generarcodigoActionPerformed(evt);
            }
        });

        rb_sku.setText("SKU");
        rb_sku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_skuActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("Codigo:");

        btn_escanearrSKU.setText("Escanear SKU");
        btn_escanearrSKU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_escanearrSKUActionPerformed(evt);
            }
        });

        btn_añadir.setText("Añadir");
        btn_añadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_añadirActionPerformed(evt);
            }
        });

        cb_categoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_categoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_categoriaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Categoría:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tf_codigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(75, 75, 75))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(rb_generarcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rb_sku)
                            .addComponent(btn_escanearrSKU)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cb_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addGap(91, 91, 91)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tf_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tf_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addContainerGap(780, Short.MAX_VALUE)
                    .addComponent(btn_añadir, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_categoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_generarcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rb_sku, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_codigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btn_escanearrSKU))
                .addGap(19, 19, 19))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addContainerGap(171, Short.MAX_VALUE)
                    .addComponent(btn_añadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)))
        );

        btn_actualizar.setText("Actualizar");
        btn_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarActionPerformed(evt);
            }
        });

        btn_eliminar.setText("Eliminar");
        btn_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarActionPerformed(evt);
            }
        });

        tabla_productos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabla_productos);

        btn_cancelar.setText("Cancelar");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });

        tabla_productos1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tabla_productos1);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel7.setText("Registro de productos");

        jPanel10.setBackground(new java.awt.Color(194, 211, 205));
        jPanel10.setForeground(new java.awt.Color(255, 255, 255));

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel28.setText("Nombre:");

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel29.setText("Precio:");

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel30.setText("Stock:");

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel31.setText("Codigo:");

        btn_añadir5.setText("Añadir");
        btn_añadir5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_añadir5ActionPerformed(evt);
            }
        });

        cb_categoria5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_categoria5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_categoria5ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel32.setText("Categoría:");

        btn_actualizar1.setText("Actualizar");
        btn_actualizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizar1ActionPerformed(evt);
            }
        });

        btn_DeseleccionarLimpiar.setText("RESET");
        btn_DeseleccionarLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DeseleccionarLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_nombre5, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                            .addGap(91, 91, 91)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel29)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tf_precio5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel30)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tf_stock5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel32)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cb_categoria5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel31)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tf_codigoProducto5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(0, 377, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_DeseleccionarLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_actualizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_añadir5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_nombre5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_precio5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_stock5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_categoria5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_codigoProducto5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(58, 58, 58))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_añadir5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_actualizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_DeseleccionarLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btn_eliminar1.setText("Eliminar");
        btn_eliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminar1ActionPerformed(evt);
            }
        });

        btn_buscar.setText("BUSCAR");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_eliminar1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(tf_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(btn_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))))
                .addGap(153, 153, 153))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_eliminar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        PROMOCIONES.addTab("GESTION PRODUCTOS", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1165, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 619, Short.MAX_VALUE)
        );

        PROMOCIONES.addTab("GESTION DE CADUCIDAD", jPanel2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1165, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 619, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PROMOCIONES.addTab("INVENTARIO TIENDA", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PROMOCIONES)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PROMOCIONES)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rb_generarcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_generarcodigoActionPerformed
        // "Ingresar código" seleccionado
        tf_codigoProducto.setEditable(true);
        btn_escanearrSKU.setEnabled(false);
    }//GEN-LAST:event_rb_generarcodigoActionPerformed

    private void rb_skuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_skuActionPerformed
        // "SKU" seleccionado
        tf_codigoProducto.setEditable(false);
        btn_escanearrSKU.setEnabled(true);

        // Generar código automático
        String categoria = (String) cb_categoria.getSelectedItem();
        if (categoria != null) {
            String codigoGenerado = generarCodigoPorCategoria(categoria);
            tf_codigoProducto.setText(codigoGenerado);
        }
    }//GEN-LAST:event_rb_skuActionPerformed

    private void btn_escanearrSKUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_escanearrSKUActionPerformed
        // Simulación de escaneo
        String[] codigosSimulados = {"7501234567890", "7509876543210", "7501112223334"};
        String codigoEscaneado = codigosSimulados[(int) (Math.random() * codigosSimulados.length)];
        tf_codigoProducto.setText(codigoEscaneado);

        JOptionPane.showMessageDialog(this,
            "Código escaneado: " + codigoEscaneado,
            "SKU Escaneado",
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btn_escanearrSKUActionPerformed

    private void btn_añadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_añadirActionPerformed
        // ✅ PRIMERO VALIDAR
        if (!validarCampos()) {
            return;
        }

        try {
            String nombre = tf_nombre.getText().trim();
            double precio = Double.parseDouble(tf_precio.getText().trim());
            int stock = Integer.parseInt(tf_stock.getText().trim());
            String codigo = tf_codigoProducto.getText().trim().toUpperCase();
            String categoria = (String) cb_categoria.getSelectedItem();

            // Verificar si el código ya existe
            ProductoController pc = new ProductoController();
            List<Producto> productos = pc.cargarProductos();

            boolean codigoExiste = productos.stream()
            .anyMatch(p -> p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigo));

            if (codigoExiste) {
                JOptionPane.showMessageDialog(this,
                    "El código " + codigo + " ya existe. Use un código único.",
                    "Código Duplicado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ CREAR PRODUCTO CON CONSTRUCTOR EXISTENTE
            Producto nuevo = new Producto(nombre, precio, stock, codigo);
            // ✅ SETTEAR CATEGORÍA POR SEPARADO
            nuevo.setCategoria(categoria);

            // Guardar producto
            pc.agregarProducto(nuevo);

            // Refrescar tablas
            cargarTablaProductos();
            if (ventanaPrincipal != null) {
                ventanaPrincipal.cargarProductosEnTabla();
            }

            // Limpiar campos
            limpiarCampos();

            JOptionPane.showMessageDialog(this,
                "Producto agregado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error en los valores numéricos",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al agregar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_añadirActionPerformed

    private void cb_categoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_categoriaActionPerformed
        if (rb_sku.isSelected()) {
            String categoria = (String) cb_categoria.getSelectedItem();
            if (categoria != null) {
                String codigoGenerado = generarCodigoPorCategoria(categoria);
                tf_codigoProducto.setText(codigoGenerado);
            }
        }
    }//GEN-LAST:event_cb_categoriaActionPerformed

    private void btn_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarActionPerformed
        int fila = tabla_productos.getSelectedRow();
        if (fila != -1) {
            try {
                if (!validarCampos()) {
                    return;
                }

                String nombre = tf_nombre.getText().trim();
                double precio = Double.parseDouble(tf_precio.getText().trim());
                int stock = Integer.parseInt(tf_stock.getText().trim());
                String codigo = tf_codigoProducto.getText().trim().toUpperCase();
                String categoria = (String) cb_categoria.getSelectedItem();
                String codigoOriginal = tabla_productos.getValueAt(fila, 3).toString();

                // Verificar si el código cambió y si ya existe
                if (!codigoOriginal.equals(codigo)) {
                    ProductoController pc = new ProductoController();
                    List<Producto> productos = pc.cargarProductos();
                    boolean codigoExiste = productos.stream()
                    .anyMatch(p -> p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigo));

                    if (codigoExiste) {
                        JOptionPane.showMessageDialog(this,
                            "El código " + codigo + " ya existe.",
                            "Código Duplicado",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // ✅ ACTUALIZAR USANDO MÉTODO EXISTENTE
                Producto actualizado = new Producto(nombre, precio, stock, codigo);
                // ✅ SETTEAR CATEGORÍA POR SEPARADO
                actualizado.setCategoria(categoria);

                // Actualizar archivo
                ProductoController pc = new ProductoController();
                List<Producto> productos = pc.cargarProductos();

                // Buscar y reemplazar el producto
                for (int i = 0; i < productos.size(); i++) {
                    if (productos.get(i).getCodigo().equals(codigoOriginal)) {
                        productos.set(i, actualizado);
                        break;
                    }
                }

                pc.guardarProductos(productos);

                // Refrescar tabla
                cargarTablaProductos();
                if (ventanaPrincipal != null) {
                    ventanaPrincipal.cargarProductosEnTabla();
                }

                // Limpiar campos
                limpiarCampos();

                JOptionPane.showMessageDialog(this,
                    "Producto actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese valores válidos.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar producto: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar.");
        }
    }//GEN-LAST:event_btn_actualizarActionPerformed

    private void btn_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarActionPerformed
        int fila = tabla_productos.getSelectedRow();
        if (fila != -1) {
            String nombreProducto = tabla_productos.getValueAt(fila, 0).toString();
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto: " + nombreProducto + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    ProductoController pc = new ProductoController();
                    List<Producto> productos = pc.cargarProductos();

                    String codigoEliminar = tabla_productos.getValueAt(fila, 3).toString();

                    productos.removeIf(p -> p.getCodigo() != null
                        && p.getCodigo().equals(codigoEliminar));

                    pc.guardarProductos(productos);

                    cargarTablaProductos();
                    if (ventanaPrincipal != null) {
                        ventanaPrincipal.cargarProductosEnTabla();
                    }

                    limpiarCampos();

                    JOptionPane.showMessageDialog(this,
                        "Producto eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar producto: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para eliminar.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_eliminarActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cancelarActionPerformed

    private void btn_DeseleccionarLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DeseleccionarLimpiarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_DeseleccionarLimpiarActionPerformed

    private void btn_añadir5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_añadir5ActionPerformed
        // ✅ PRIMERO VALIDAR
        if (!validarCampos()) {
            return;
        }

        try {
            String nombre = tf_nombre.getText().trim();
            double precio = Double.parseDouble(tf_precio.getText().trim());
            int stock = Integer.parseInt(tf_stock.getText().trim());
            String codigo = tf_codigoProducto.getText().trim().toUpperCase();
            String categoria = (String) cb_categoria.getSelectedItem();

            // Verificar si el código ya existe
            ProductoController pc = new ProductoController();
            List<Producto> productos = pc.cargarProductos();

            boolean codigoExiste = productos.stream()
            .anyMatch(p -> p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigo));

            if (codigoExiste) {
                JOptionPane.showMessageDialog(this,
                    "El código " + codigo + " ya existe. Use un código único.",
                    "Código Duplicado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ CREAR PRODUCTO CON CONSTRUCTOR EXISTENTE
            Producto nuevo = new Producto(nombre, precio, stock, codigo);
            // ✅ SETTEAR CATEGORÍA POR SEPARADO
            nuevo.setCategoria(categoria);

            // Guardar producto
            pc.agregarProducto(nuevo);

            // Refrescar tablas
            cargarTablaProductos();
            if (ventanaPrincipal != null) {
                ventanaPrincipal.cargarProductosEnTabla();
            }

            // Limpiar campos
            limpiarCampos();

            JOptionPane.showMessageDialog(this,
                "Producto agregado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error en los valores numéricos",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al agregar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_añadir5ActionPerformed

    private void cb_categoria5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_categoria5ActionPerformed
        if (rb_sku.isSelected()) {
            String categoria = (String) cb_categoria.getSelectedItem();
            if (categoria != null) {
                String codigoGenerado = generarCodigoPorCategoria(categoria);
                tf_codigoProducto.setText(codigoGenerado);
            }
        }
    }//GEN-LAST:event_cb_categoria5ActionPerformed

    private void btn_eliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminar1ActionPerformed
        int fila = tabla_productos.getSelectedRow();
        if (fila != -1) {
            String nombreProducto = tabla_productos.getValueAt(fila, 0).toString();
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto: " + nombreProducto + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    ProductoController pc = new ProductoController();
                    List<Producto> productos = pc.cargarProductos();

                    String codigoEliminar = tabla_productos.getValueAt(fila, 3).toString();

                    productos.removeIf(p -> p.getCodigo() != null
                        && p.getCodigo().equals(codigoEliminar));

                    pc.guardarProductos(productos);

                    cargarTablaProductos();
                    if (ventanaPrincipal != null) {
                        ventanaPrincipal.cargarProductosEnTabla();
                    }

                    limpiarCampos();

                    JOptionPane.showMessageDialog(this,
                        "Producto eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar producto: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para eliminar.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_eliminar1ActionPerformed

    private void btn_actualizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizar1ActionPerformed
        int fila = tabla_productos.getSelectedRow();
        if (fila != -1) {
            try {
                if (!validarCampos()) {
                    return;
                }

                String nombre = tf_nombre.getText().trim();
                double precio = Double.parseDouble(tf_precio.getText().trim());
                int stock = Integer.parseInt(tf_stock.getText().trim());
                String codigo = tf_codigoProducto.getText().trim().toUpperCase();
                String categoria = (String) cb_categoria.getSelectedItem();
                String codigoOriginal = tabla_productos.getValueAt(fila, 3).toString();

                // Verificar si el código cambió y si ya existe
                if (!codigoOriginal.equals(codigo)) {
                    ProductoController pc = new ProductoController();
                    List<Producto> productos = pc.cargarProductos();
                    boolean codigoExiste = productos.stream()
                    .anyMatch(p -> p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigo));

                    if (codigoExiste) {
                        JOptionPane.showMessageDialog(this,
                            "El código " + codigo + " ya existe.",
                            "Código Duplicado",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // ✅ ACTUALIZAR USANDO MÉTODO EXISTENTE
                Producto actualizado = new Producto(nombre, precio, stock, codigo);
                // ✅ SETTEAR CATEGORÍA POR SEPARADO
                actualizado.setCategoria(categoria);

                // Actualizar archivo
                ProductoController pc = new ProductoController();
                List<Producto> productos = pc.cargarProductos();

                // Buscar y reemplazar el producto
                for (int i = 0; i < productos.size(); i++) {
                    if (productos.get(i).getCodigo().equals(codigoOriginal)) {
                        productos.set(i, actualizado);
                        break;
                    }
                }

                pc.guardarProductos(productos);

                // Refrescar tabla
                cargarTablaProductos();
                if (ventanaPrincipal != null) {
                    ventanaPrincipal.cargarProductosEnTabla();
                }

                // Limpiar campos
                limpiarCampos();

                JOptionPane.showMessageDialog(this,
                    "Producto actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese valores válidos.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar producto: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar.");
        }
    }//GEN-LAST:event_btn_actualizar1ActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane PROMOCIONES;
    private javax.swing.JButton btn_DeseleccionarLimpiar;
    private javax.swing.JButton btn_actualizar;
    private javax.swing.JButton btn_actualizar1;
    private javax.swing.JButton btn_añadir;
    private javax.swing.JButton btn_añadir1;
    private javax.swing.JButton btn_añadir2;
    private javax.swing.JButton btn_añadir3;
    private javax.swing.JButton btn_añadir4;
    private javax.swing.JButton btn_añadir5;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_eliminar;
    private javax.swing.JButton btn_eliminar1;
    private javax.swing.JButton btn_escanearrSKU;
    private javax.swing.JButton btn_escanearrSKU1;
    private javax.swing.JButton btn_escanearrSKU2;
    private javax.swing.JButton btn_escanearrSKU3;
    private javax.swing.JButton btn_escanearrSKU4;
    private javax.swing.JComboBox<String> cb_categoria;
    private javax.swing.JComboBox<String> cb_categoria1;
    private javax.swing.JComboBox<String> cb_categoria2;
    private javax.swing.JComboBox<String> cb_categoria3;
    private javax.swing.JComboBox<String> cb_categoria4;
    private javax.swing.JComboBox<String> cb_categoria5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JRadioButton rb_generarcodigo;
    private javax.swing.JRadioButton rb_generarcodigo1;
    private javax.swing.JRadioButton rb_generarcodigo2;
    private javax.swing.JRadioButton rb_generarcodigo3;
    private javax.swing.JRadioButton rb_generarcodigo4;
    private javax.swing.JRadioButton rb_sku;
    private javax.swing.JRadioButton rb_sku1;
    private javax.swing.JRadioButton rb_sku2;
    private javax.swing.JRadioButton rb_sku3;
    private javax.swing.JRadioButton rb_sku4;
    private javax.swing.JTable tabla_productos;
    private static javax.swing.JTable tabla_productos1;
    private javax.swing.JTextField tf_busqueda;
    private javax.swing.JTextField tf_codigoProducto;
    private javax.swing.JTextField tf_codigoProducto1;
    private javax.swing.JTextField tf_codigoProducto2;
    private javax.swing.JTextField tf_codigoProducto3;
    private javax.swing.JTextField tf_codigoProducto4;
    private javax.swing.JTextField tf_codigoProducto5;
    private javax.swing.JTextField tf_nombre;
    private javax.swing.JTextField tf_nombre1;
    private javax.swing.JTextField tf_nombre2;
    private javax.swing.JTextField tf_nombre3;
    private javax.swing.JTextField tf_nombre4;
    private javax.swing.JTextField tf_nombre5;
    private javax.swing.JTextField tf_precio;
    private javax.swing.JTextField tf_precio1;
    private javax.swing.JTextField tf_precio2;
    private javax.swing.JTextField tf_precio3;
    private javax.swing.JTextField tf_precio4;
    private javax.swing.JTextField tf_precio5;
    private javax.swing.JTextField tf_stock;
    private javax.swing.JTextField tf_stock1;
    private javax.swing.JTextField tf_stock2;
    private javax.swing.JTextField tf_stock3;
    private javax.swing.JTextField tf_stock4;
    private javax.swing.JTextField tf_stock5;
    // End of variables declaration//GEN-END:variables
}
