package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.awt.Component;
import java.sql.*;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class COMPRAS_Admin extends javax.swing.JPanel {

    public COMPRAS_Admin() {
    initComponents();
        try { jTabbedPane1.setSelectedIndex(0); } catch (Exception e) {}
        
        // 1. Llenamos las listas desplegables
        llenarFiltros(); 
        
        // 2. Mostramos la tabla inicial
        mostrarDatos();

        // 3. --- EVENTOS: SI TOCAN ALGO, REFRESCA LA TABLA ---
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if (jTabbedPane1.getSelectedIndex() == 1) { // Pestaña Devoluciones
                    mostrarHistorialCompras();
                    // Limpiamos el detalle para que no se vea lo anterior
                    ((DefaultTableModel) tblDetalleCompra.getModel()).setRowCount(0);
                }
            }
        });
        // Si cambian el proveedor...
        cboProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarDatos();
            }
        });

        // Si cambian el estado...
        cboEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarDatos();
            }
        });

        // Si escriben en la caja de texto (evento al soltar tecla)...
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mostrarDatos();
            }
        });
    }
private void mostrarHistorialCompras() {
        DefaultTableModel modelo = (DefaultTableModel) tblCompras.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"ID", "Fecha", "Proveedor", "Total", "Estado"});
        
        // Ajuste de anchos para que se vea profesional
        tblCompras.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID pequeño
        tblCompras.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        tblCompras.getColumnModel().getColumn(2).setPreferredWidth(250); // Proveedor grande
        
        // Ocultamos la columna ID visualmente si quieres, pero la necesitamos
        
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        // Traemos las compras que NO estén ya anuladas
        String sql = "SELECT c.id_compra, c.fecha_hora, p.Razon_Social, c.total, c.estado " +
                     "FROM compra c " +
                     "INNER JOIN proveedor p ON c.id_proveedor = p.id_proveedor " +
                     "ORDER BY c.fecha_hora DESC";

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_compra"),
                    rs.getTimestamp("fecha_hora"),
                    rs.getString("Razon_Social"),
                    "S/ " + rs.getDouble("total"),
                    rs.getString("estado")
                });
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error cargando compras: " + e);
        }
    }
    private void mostrarDatos() {
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 
        DefaultTableModel modelo = (DefaultTableModel) tblPedidos.getModel();
        modelo.setRowCount(0);

        // 1. Construimos la consulta BASE
        String sql = "SELECT p.codigo, pr.Razon_Social, p.fecha_pedido, p.fecha_comprometida, p.estado "
                   + "FROM pedido p "
                   + "INNER JOIN proveedor pr ON p.id_proveedor = pr.id_proveedor "
                   + "WHERE 1=1 "; // Truco para agregar condiciones con AND fácilmente

        // 2. Filtro por PROVEEDOR
        String provSeleccionado = (cboProveedor.getSelectedItem() != null) ? cboProveedor.getSelectedItem().toString() : "Todos";
        if (!provSeleccionado.equals("Todos")) {
            sql += " AND pr.Razon_Social = '" + provSeleccionado + "'";
        }

        // 3. Filtro por ESTADO
        String estSeleccionado = (cboEstado.getSelectedItem() != null) ? cboEstado.getSelectedItem().toString() : "Todos";
        if (!estSeleccionado.equals("Todos")) {
            sql += " AND p.estado = '" + estSeleccionado + "'";
        }

        // 4. Filtro por TEXTO (Fecha o Código)
        String texto = txtBuscar.getText().trim();
        if (!texto.isEmpty()) {
            // Busca si el texto coincide con la fecha O con el código
            sql += " AND (p.fecha_pedido LIKE '%" + texto + "%' OR p.codigo LIKE '%" + texto + "%')";
        }

        sql += " ORDER BY p.fecha_pedido DESC";

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[6];
                fila[0] = rs.getString("codigo");
                fila[1] = rs.getString("Razon_Social");
                fila[2] = rs.getDate("fecha_pedido");
                fila[3] = rs.getDate("fecha_comprometida");
                fila[4] = rs.getString("estado");
                fila[5] = "🔍 Ver Detalle";
                modelo.addRow(fila);
            }
            con.close();
            pintarColoresTabla();

        } catch (SQLException e) {
            System.out.println("Error filtrando: " + e);
        }
    }

    private void pintarColoresTabla() {
        // Renderizador para la columna ESTADO (Columna 4)
        tblPedidos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Centramos el texto y lo ponemos blanco/negro según convenga
                label.setHorizontalAlignment(CENTER);
                label.setOpaque(true); // Importante para que se vea el color de fondo

                String estado = (String) value;

                // Lógica de colores tipo "Semáforo"
                if (estado != null) {
                    switch (estado) {
                        case "Pendiente":
                            label.setBackground(new Color(255, 193, 7)); // Amarillo mostaza
                            label.setForeground(Color.BLACK);
                            break;
                        case "Enviado":
                            label.setBackground(new Color(23, 162, 184)); // Azul cian
                            label.setForeground(Color.WHITE);
                            break;
                        case "Recibido":
                            label.setBackground(new Color(40, 167, 69));  // Verde
                            label.setForeground(Color.WHITE);
                            break;
                        case "Atrasado":
                            label.setBackground(new Color(220, 53, 69));  // Rojo
                            label.setForeground(Color.WHITE);
                            break;
                        default:
                            label.setBackground(table.getBackground());
                            label.setForeground(table.getForeground());
                    }
                }
                return label;
            }
        });
    }
    private void llenarFiltros() {
        // 1. Configurar Combo de ESTADOS (Manual)
        cboEstado.removeAllItems();
        cboEstado.addItem("Todos");
        cboEstado.addItem("Pendiente");
        cboEstado.addItem("Enviado");
        cboEstado.addItem("Recibido");
        cboEstado.addItem("Atrasado");

        // 2. Configurar Combo de PROVEEDORES (Desde Base de Datos)
        cboProveedor.removeAllItems();
        cboProveedor.addItem("Todos");

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);
            // Seleccionamos solo los nombres únicos
            PreparedStatement ps = con.prepareStatement("SELECT Razon_Social FROM proveedor");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cboProveedor.addItem(rs.getString("Razon_Social"));
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error llenando combos: " + e);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        btnRecepcion = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboProveedor = new javax.swing.JComboBox<>();
        cboEstado = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetalleCompra = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCompras = new javax.swing.JTable();
        btnAnular = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Proveedor", "Fecha Pedido", "Fecha Comprometida", "Estado", "Detalle"
            }
        ));
        tblPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPedidosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPedidos);

        jButton1.setBackground(new java.awt.Color(255, 153, 0));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("+     Nuevo Pedido");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnRecepcion.setBackground(new java.awt.Color(255, 153, 0));
        btnRecepcion.setForeground(new java.awt.Color(255, 255, 255));
        btnRecepcion.setText("+     Recepcion");
        btnRecepcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecepcionActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel1.setText("Proveedor:");

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setText("Estado:");

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText("Fecha:");

        cboProveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProveedorActionPerformed(evt);
            }
        });

        cboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEstadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(88, 88, 88)
                .addComponent(btnRecepcion)
                .addGap(20, 20, 20))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(btnRecepcion)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(cboProveedor)
                    .addComponent(cboEstado)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("COMPRAR", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tblDetalleCompra.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblDetalleCompra);

        tblCompras.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCompras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblComprasMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblCompras);

        btnAnular.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnAnular.setText("Anular Compra");
        btnAnular.setActionCommand("Devolver Pedido");
        btnAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnularActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Seleccione un pedido y devuelvalo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAnular, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(431, 431, 431))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(btnAnular, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", jPanel2);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     // 1. Creamos la ventana del formulario (null porque no tenemos Frame padre directo, true para que sea Modal)
        DialogoNuevoPedido ventana = new DialogoNuevoPedido(null, true);
        
        // 2. La hacemos visible (El programa se "pausa" aquí hasta que cierres la ventana)
        ventana.setVisible(true);
        
        // 3. ¡TRUCO! Al cerrarse la ventana, recargamos la tabla para ver el nuevo pedido
        mostrarDatos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnRecepcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecepcionActionPerformed
     // 1. VERIFICAR SELECCIÓN
        int fila = tblPedidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla.");
            return;
        }

        String codigoPedido = tblPedidos.getValueAt(fila, 0).toString();
        String estadoActual = tblPedidos.getValueAt(fila, 4).toString();

        if (estadoActual.equals("Recibido")) {
            JOptionPane.showMessageDialog(this, "Este pedido ya fue procesado.");
            return;
        }

        // 2. FECHA DE VENCIMIENTO
        String fechaVencimiento = JOptionPane.showInputDialog(this, "Fecha de vencimiento del lote (YYYY-MM-DD):", "2026-12-31");
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Procesar recepción y descontar del presupuesto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // --- INICIO DE LA TRANSACCIÓN ---
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; // <--- ¡TU CONTRASEÑA AQUÍ!

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, usuario, password);
            con.setAutoCommit(false); // Transacción iniciada

            // DATOS FIJOS
            int idSucursal = 1; 
            String idEmpleado = "11000001"; // ID REAL DEL ADMIN

            // A. OBTENER ID PEDIDO
            String sqlPed = "SELECT id_pedido, id_proveedor FROM pedido WHERE codigo = ?";
            PreparedStatement psPed = con.prepareStatement(sqlPed);
            psPed.setString(1, codigoPedido);
            ResultSet rsPed = psPed.executeQuery();
            
            int idPedido = 0;
            String idProveedor = "";
            if (rsPed.next()) {
                idPedido = rsPed.getInt("id_pedido");
                idProveedor = rsPed.getString("id_proveedor");
            }

            // =================================================================
            // B. --- VALIDACIÓN DE PRESUPUESTO (ESTO ES LO QUE FALTABA) ---
            // =================================================================
            
            // 1. Calculamos cuánto costará el pedido ANTES de procesarlo
            String sqlCalcTotal = "SELECT SUM(d.cantidad * p.precio_compra) as total_pedido " +
                                  "FROM detalle_pedido d " +
                                  "INNER JOIN producto p ON d.id_producto = p.id_producto " +
                                  "WHERE d.id_pedido = ?";
            PreparedStatement psCalc = con.prepareStatement(sqlCalcTotal);
            psCalc.setInt(1, idPedido);
            ResultSet rsCalc = psCalc.executeQuery();
            
            double montoTotalPedido = 0;
            if (rsCalc.next()) {
                montoTotalPedido = rsCalc.getDouble("total_pedido");
            }

            // 2. Revisamos cuánto dinero tiene la sucursal
            String sqlPresupuesto = "SELECT presupuesto FROM sucursal WHERE id_sucursal = ?";
            PreparedStatement psCheckMoney = con.prepareStatement(sqlPresupuesto);
            psCheckMoney.setInt(1, idSucursal);
            ResultSet rsMoney = psCheckMoney.executeQuery();
            
            double saldoActual = 0;
            if (rsMoney.next()) {
                saldoActual = rsMoney.getDouble("presupuesto");
            }

            // 3. EL BLOQUEO: Si no hay dinero, lanzamos error y paramos todo
            if (saldoActual < montoTotalPedido) {
                throw new Exception("FONDOS INSUFICIENTES.\nSaldo actual: S/ " + saldoActual + "\nCosto del pedido: S/ " + montoTotalPedido);
            }

            // 4. COBRAR: Restamos el dinero
            String sqlRestarDinero = "UPDATE sucursal SET presupuesto = presupuesto - ? WHERE id_sucursal = ?";
            PreparedStatement psPay = con.prepareStatement(sqlRestarDinero);
            psPay.setDouble(1, montoTotalPedido);
            psPay.setInt(2, idSucursal);
            psPay.executeUpdate();

            // =================================================================
            // FIN DE LA VALIDACIÓN - AHORA SIGUE EL PROCESO NORMAL
            // =================================================================

            // C. CREAR COMPRA (Ya pagada)
            String sqlInsertCompra = "INSERT INTO compra (fecha_hora, total, id_proveedor, id_empleado, id_sucursal, estado) VALUES (NOW(), ?, ?, ?, ?, 'Pagada')";
            PreparedStatement psCompra = con.prepareStatement(sqlInsertCompra, Statement.RETURN_GENERATED_KEYS);
            psCompra.setDouble(1, montoTotalPedido);
            psCompra.setString(2, idProveedor);
            psCompra.setString(3, idEmpleado);
            psCompra.setInt(4, idSucursal);
            psCompra.executeUpdate();
            
            ResultSet rsKeyCompra = psCompra.getGeneratedKeys();
            int idCompraGenerada = 0;
            if (rsKeyCompra.next()) idCompraGenerada = rsKeyCompra.getInt(1);

            // D. PROCESAR ITEMS (Stock, Kardex, Detalle)
            String sqlItems = "SELECT d.id_producto, d.cantidad, p.precio_compra " +
                              "FROM detalle_pedido d " +
                              "INNER JOIN producto p ON d.id_producto = p.id_producto " +
                              "WHERE d.id_pedido = ?";
            PreparedStatement psItems = con.prepareStatement(sqlItems);
            psItems.setInt(1, idPedido);
            ResultSet rsItems = psItems.executeQuery();

            PreparedStatement psStockCheck = con.prepareStatement("SELECT stock_actual FROM inventario_sucursal WHERE id_producto=? AND id_sucursal=?");
            PreparedStatement psStockUpdate = con.prepareStatement("UPDATE inventario_sucursal SET stock_actual = stock_actual + ?, fecha_caducidad = ? WHERE id_producto=? AND id_sucursal=?");
            PreparedStatement psStockInsert = con.prepareStatement("INSERT INTO inventario_sucursal (id_producto, id_sucursal, stock_actual, fecha_caducidad) VALUES (?, ?, ?, ?)");
            PreparedStatement psKardex = con.prepareStatement("INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto, id_sucursal) VALUES (NOW(), 'ENTRADA COMPRA', ?, ?, ?, 'COMPLETADO', ?, ?)");
            PreparedStatement psDetCompra = con.prepareStatement("INSERT INTO detalle_compra (id_compra, id_producto, cantidad, precio_compra, subtotal) VALUES (?, ?, ?, ?, ?)");

            while (rsItems.next()) {
                int idProd = rsItems.getInt("id_producto");
                int cant = rsItems.getInt("cantidad");
                double precio = rsItems.getDouble("precio_compra");
                double subtotal = cant * precio;

                // Inventario
                int stockAnterior = 0;
                psStockCheck.setInt(1, idProd);
                psStockCheck.setInt(2, idSucursal);
                ResultSet rsStock = psStockCheck.executeQuery();
                
                if (rsStock.next()) {
                    stockAnterior = rsStock.getInt("stock_actual");
                    psStockUpdate.setInt(1, cant);
                    psStockUpdate.setDate(2, java.sql.Date.valueOf(fechaVencimiento));
                    psStockUpdate.setInt(3, idProd);
                    psStockUpdate.setInt(4, idSucursal);
                    psStockUpdate.executeUpdate();
                } else {
                    psStockInsert.setInt(1, idProd);
                    psStockInsert.setInt(2, idSucursal);
                    psStockInsert.setInt(3, cant);
                    psStockInsert.setDate(4, java.sql.Date.valueOf(fechaVencimiento));
                    psStockInsert.executeUpdate();
                }
                int stockNuevo = stockAnterior + cant;

                // Kardex
                psKardex.setInt(1, cant);
                psKardex.setInt(2, stockAnterior);
                psKardex.setInt(3, stockNuevo);
                psKardex.setInt(4, idProd);
                psKardex.setInt(5, idSucursal);
                psKardex.executeUpdate();

                // Detalle Compra
                psDetCompra.setInt(1, idCompraGenerada);
                psDetCompra.setInt(2, idProd);
                psDetCompra.setInt(3, cant);
                psDetCompra.setDouble(4, precio);
                psDetCompra.setDouble(5, subtotal);
                psDetCompra.executeUpdate();
            }

            // E. FINALIZAR PEDIDO
            String sqlUpdatePedido = "UPDATE pedido SET estado = 'Recibido' WHERE id_pedido = ?";
            PreparedStatement psUpPed = con.prepareStatement(sqlUpdatePedido);
            psUpPed.setInt(1, idPedido);
            psUpPed.executeUpdate();

            con.commit(); // Confirmar todo
            
            double nuevoSaldo = saldoActual - montoTotalPedido;
            JOptionPane.showMessageDialog(this, "¡Recepción y Pago Exitoso!\n\n"
                    + "Monto pagado: S/ " + String.format("%.2f", montoTotalPedido) + "\n"
                    + "Nuevo Saldo Sucursal: S/ " + String.format("%.2f", nuevoSaldo));
            
            mostrarDatos();

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error de Transacción", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }
    }//GEN-LAST:event_btnRecepcionActionPerformed

    private void cboProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboProveedorActionPerformed

    private void cboEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEstadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboEstadoActionPerformed

    private void tblPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPedidosMouseClicked
        int columna = tblPedidos.getColumnModel().getColumnIndexAtX(evt.getX());
        int fila = evt.getY() / tblPedidos.getRowHeight();

// Si el clic es en la columna 5 (La de la Lupa/Detalle)
        if (fila < tblPedidos.getRowCount() && fila >= 0 && columna == 5) {

            // Obtenemos el código (P-001) de la columna 0
            String codigo = tblPedidos.getValueAt(fila, 0).toString();

            // CREAMOS Y ABRIMOS LA VENTANA DE DETALLE
            // (null porque no tenemos un Frame padre fijo, true para que sea modal)
            DialogoDetalle ventana = new DialogoDetalle(null, true, codigo);
            ventana.setVisible(true);
            // TODO: Aquí llamaremos a tu ventana JDialog con los productos
    }//GEN-LAST:event_tblPedidosMouseClicked
}
    private void tblComprasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblComprasMouseClicked
      int fila = tblCompras.getSelectedRow();
        if (fila == -1) return;
        
        int idCompra = Integer.parseInt(tblCompras.getValueAt(fila, 0).toString());
        mostrarDetalleDeCompra(idCompra);  // TODO add your handling code here:
    }//GEN-LAST:event_tblComprasMouseClicked

    private void btnAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnularActionPerformed
  int fila = tblCompras.getSelectedRow();
    
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una compra para devolver.");
        return;
    }
    
    // Validación: Verificar que la celda no sea nula
    if (tblCompras.getValueAt(fila, 0) == null) return;

    String idCompra = tblCompras.getValueAt(fila, 0).toString();
    String estado = tblCompras.getValueAt(fila, 4).toString();
    
    // CAMBIO 1: Validamos si ya dice "DEVUELTA" (o ANULADA por compatibilidad)
    if ("DEVUELTA".equals(estado) || "ANULADA".equals(estado)) {
        JOptionPane.showMessageDialog(this, "Esta compra ya ha sido devuelta anteriormente.");
        return;
    }
    
    // CAMBIO 2: Textos actualizados a "Devolución"
    int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Seguro que desea DEVOLVER la compra #" + idCompra + " al proveedor?\n" +
            "Se recuperará el dinero en caja y se retirará el stock del almacén.", 
            "Confirmar Devolución", JOptionPane.YES_NO_OPTION);
            
    if (confirm == JOptionPane.YES_OPTION) {
        // Llamamos al método de lógica (sigue siendo el mismo, solo cambiaremos el SQL adentro)
        anularCompra(idCompra); 
        
        mostrarHistorialCompras();
        ((DefaultTableModel) tblDetalleCompra.getModel()).setRowCount(0);
    }     // TODO add your handling code here:
    }//GEN-LAST:event_btnAnularActionPerformed
   private void mostrarDetalleDeCompra(int idCompra) {
        DefaultTableModel modelo = (DefaultTableModel) tblDetalleCompra.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"Producto", "Cantidad", "P. Unitario", "Subtotal"});
        
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        String sql = "SELECT p.nombre, d.cantidad, d.precio_compra, d.subtotal " +
                     "FROM detalle_compra d " +
                     "INNER JOIN producto p ON d.id_producto = p.id_producto " +
                     "WHERE d.id_compra = ?";

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCompra);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_compra"),
                    rs.getDouble("subtotal")
                });
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error detalle: " + e);
        }
    }
   private void anularCompra(String codigoCompra) { // O id_compra
        String motivo = JOptionPane.showInputDialog(this, "Ingrese el motivo de la anulación:");
        if (motivo == null || motivo.trim().isEmpty()) return;

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, usuario, password);
            con.setAutoCommit(false);

            // 1. OBTENER ID COMPRA, SUCURSAL Y TOTAL
            // Asumimos que buscaremos por el ID interno, si usas código en compra, ajusta el WHERE
            String sqlInfo = "SELECT id_compra, id_sucursal, total, estado FROM compra WHERE id_compra = ?";
            PreparedStatement psInfo = con.prepareStatement(sqlInfo);
            psInfo.setInt(1, Integer.parseInt(codigoCompra)); 
            ResultSet rsInfo = psInfo.executeQuery();

            if (!rsInfo.next()) {
                JOptionPane.showMessageDialog(this, "Compra no encontrada.");
                return;
            }

            int idCompra = rsInfo.getInt("id_compra");
            int idSucursal = rsInfo.getInt("id_sucursal");
            double totalDevolucion = rsInfo.getDouble("total");
            String estado = rsInfo.getString("estado");

            if ("ANULADA".equals(estado)) {
                JOptionPane.showMessageDialog(this, "Esta compra ya fue anulada.");
                return;
            }

            // 2. OBTENER ITEMS Y VALIDAR STOCK (EL PASO CRÍTICO)
            String sqlDetalle = "SELECT id_producto, cantidad FROM detalle_compra WHERE id_compra = ?";
            PreparedStatement psDet = con.prepareStatement(sqlDetalle);
            psDet.setInt(1, idCompra);
            ResultSet rsDet = psDet.executeQuery();

            // Guardamos los items en memoria para procesarlos luego
            // Usamos una lista temporal o re-ejecutamos el resultset (para simplificar, re-ejecutamos abajo)
            
            // VERIFICACIÓN DE STOCK
            while (rsDet.next()) {
                int idProd = rsDet.getInt("id_producto");
                int cantComprada = rsDet.getInt("cantidad");

                // Consultar stock actual
                String sqlStock = "SELECT stock_actual FROM inventario_sucursal WHERE id_producto = ? AND id_sucursal = ?";
                PreparedStatement psStockCheck = con.prepareStatement(sqlStock);
                psStockCheck.setInt(1, idProd);
                psStockCheck.setInt(2, idSucursal);
                ResultSet rsStock = psStockCheck.executeQuery();

                int stockActual = 0;
                if (rsStock.next()) stockActual = rsStock.getInt("stock_actual");

                if (stockActual < cantComprada) {
                    throw new Exception("No se puede anular: El producto ID " + idProd + " ya fue vendido.\n"
                            + "Stock actual: " + stockActual + " | Se intenta devolver: " + cantComprada);
                }
            }

            // 3. SI PASÓ LA VALIDACIÓN: PROCEDEMOS A REVERTIR TODO
            
            // A. DEVOLVER DINERO A LA CAJA DE LA SUCURSAL (PRESUPUESTO)
            String sqlReembolso = "UPDATE sucursal SET presupuesto = presupuesto + ? WHERE id_sucursal = ?";
            PreparedStatement psMoney = con.prepareStatement(sqlReembolso);
            psMoney.setDouble(1, totalDevolucion);
            psMoney.setInt(2, idSucursal);
            psMoney.executeUpdate();

            // B. RESTAR EL STOCK (SALIDA HACIA PROVEEDOR)
            // Volvemos a recorrer los items
            rsDet = psDet.executeQuery(); // Ejecutamos query de nuevo para reiniciar cursor
            
            String sqlUpdateStock = "UPDATE inventario_sucursal SET stock_actual = stock_actual - ? WHERE id_producto = ? AND id_sucursal = ?";
            PreparedStatement psUpStock = con.prepareStatement(sqlUpdateStock);
            
            String sqlKardex = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto, id_sucursal) VALUES (NOW(), 'SALIDA POR ANULACION', ?, ?, ?, ?, ?, ?)";
            PreparedStatement psKardex = con.prepareStatement(sqlKardex);

            while (rsDet.next()) {
                int idProd = rsDet.getInt("id_producto");
                int cant = rsDet.getInt("cantidad");

                // Obtener stock previo para el kardex
                // (Podríamos optimizar esto, pero por claridad lo consultamos)
                PreparedStatement psGetS = con.prepareStatement("SELECT stock_actual FROM inventario_sucursal WHERE id_producto=? AND id_sucursal=?");
                psGetS.setInt(1, idProd);
                psGetS.setInt(2, idSucursal);
                ResultSet rsS = psGetS.executeQuery();
                rsS.next();
                int stockAnt = rsS.getInt("stock_actual");
                int stockNuevo = stockAnt - cant;

                // Restar Inventario
                psUpStock.setInt(1, cant);
                psUpStock.setInt(2, idProd);
                psUpStock.setInt(3, idSucursal);
                psUpStock.executeUpdate();

                // Registrar en Kardex
                psKardex.setInt(1, cant);
                psKardex.setInt(2, stockAnt);
                psKardex.setInt(3, stockNuevo);
                psKardex.setString(4, "MOTIVO: " + motivo);
                psKardex.setInt(5, idProd);
                psKardex.setInt(6, idSucursal);
                psKardex.executeUpdate();
            }

            // C. CAMBIAR ESTADO DE LA COMPRA
            // Ojo: En tu tabla 'compra' no vi columna 'motivo', 
            // así que solo cambiamos estado o podrías guardar el motivo en auditoría.
            String sqlEstado = "UPDATE compra SET estado = 'DEVUELTA' WHERE id_compra = ?";
            PreparedStatement psEst = con.prepareStatement(sqlEstado);
            psEst.setInt(1, idCompra);
            psEst.executeUpdate();

            // D. REGISTRAR EN MOVIMIENTO DE CAJA (Opcional pero recomendado si usas esa tabla)
            // INSERT INTO movimiento_caja ... (Tipo: INGRESO/REEMBOLSO)

            con.commit();
            JOptionPane.showMessageDialog(this, "Compra anulada correctamente.\nDinero devuelto y stock retirado.");

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(this, "Error al anular: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnular;
    private javax.swing.JButton btnRecepcion;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<String> cboProveedor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblCompras;
    private javax.swing.JTable tblDetalleCompra;
    private javax.swing.JTable tblPedidos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
