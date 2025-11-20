package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboProveedor = new javax.swing.JComboBox<>();
        cboEstado = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

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
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
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

        jButton2.setBackground(new java.awt.Color(255, 153, 0));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("+     Recepcion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                .addComponent(jButton2)
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
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producto (Lote/SKU)", "Provvedor", "Fecha de Caducidad", "Días Restantes", "Cantidad"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", jPanel2);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JComboBox<String> cboProveedor;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblPedidos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
