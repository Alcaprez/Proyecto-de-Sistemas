package edu.UPAO.proyecto.app;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;

public class DialogoNuevoPedido extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoNuevoPedido.class.getName());

    public DialogoNuevoPedido(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Registrar Nuevo Pedido");

        // 1. Preparamos la fecha de hoy por defecto
        // (Si usas JCalendar)
        if (dcFecha != null) {
            dcFecha.setDate(new Date());
        }

        // 2. Cargamos los datos de la BD
        cargarCombos();

        // 3. Inicializamos la tabla vacía con sus columnas
        DefaultTableModel modelo = (DefaultTableModel) tblDetalle.getModel();
        modelo.setColumnIdentifiers(new Object[]{"Producto", "Stock Actual", "Stock Min", "Cantidad"});
        modelo.setRowCount(0);
    }

    private void cargarCombos() {
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; // <--- ¡TU CLAVE!

        try {
            Connection con = DriverManager.getConnection(url, usuario, password);

            // A. LLENAR PROVEEDORES
            PreparedStatement psProv = con.prepareStatement("SELECT Razon_Social FROM proveedor");
            ResultSet rsProv = psProv.executeQuery();
            cboProveedor.removeAllItems();
            cboProveedor.addItem("Seleccione un proveedor...");
            while (rsProv.next()) {
                cboProveedor.addItem(rsProv.getString("Razon_Social"));
            }

            // B. LLENAR PRODUCTOS (Para elegir qué pedir)
            PreparedStatement psProd = con.prepareStatement("SELECT nombre FROM producto");
            ResultSet rsProd = psProd.executeQuery();
            cboProducto.removeAllItems();
            cboProducto.addItem("Seleccione un producto...");
            while (rsProd.next()) {
                cboProducto.addItem(rsProd.getString("nombre"));
            }

            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cboProveedor = new javax.swing.JComboBox<>();
        dcFecha = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        spnCantidad = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnAgregar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cboProducto = new javax.swing.JComboBox<>();
        btnRegistrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("A. Datos del pedido");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Registrar Pedido");

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Proveedor");

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Fecha Comprometida");

        cboProveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProveedorActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Observaciones");

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("B. Productos a solicitar");

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Cantidad");

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Producto", "Stock Actual", "Stock minimo", "Cantidad"
            }
        ));
        jScrollPane1.setViewportView(tblDetalle);

        btnAgregar.setText("+    Agregar producto");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Producto");

        cboProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProductoActionPerformed(evt);
            }
        });

        btnRegistrar.setText("+    Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(cboProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(164, 164, 164)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dcFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addGap(238, 238, 238)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(btnAgregar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(btnRegistrar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregar)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRegistrar)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(462, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // 1. Validaciones simples
        if (cboProducto.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto primero.");
            return;
        }

        int cantidad = (int) spnCantidad.getValue();
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        // 2. Obtenemos los datos
        String nombreProducto = cboProducto.getSelectedItem().toString();

        // 3. (Opcional) Consultamos Stock Actual y Mínimo de la BD para mostrarlo en la tabla
        // Si te da pereza o error, puedes poner variables fijas int stock = 0, min = 0;
        int stockActual = 0;
        int stockMin = 0;

        try {
            String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
            String usuario = "root";
            String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

            Connection con = (Connection) DriverManager.getConnection(url, usuario, password);
            // Buscamos por nombre (asumiendo que son únicos)
            String sql = "SELECT p.stock_minimo, COALESCE(i.stock_actual, 0) as stock_real " +
                         "FROM producto p " +
                         "LEFT JOIN inventario_sucursal i ON p.id_producto = i.id_producto AND i.id_sucursal = 1 " +
                         "WHERE p.nombre = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stockMin = rs.getInt("stock_minimo");
                stockActual = rs.getInt("stock_real"); // ¡Valor real de la BD!
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error consultando stock: " + e);
        }

        // 4. Agregamos la fila a la tabla visual
        DefaultTableModel modelo = (DefaultTableModel) tblDetalle.getModel();

        // Verificamos si ya está en la tabla para no repetir (Opcional)
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getValueAt(i, 0).toString().equals(nombreProducto)) {
                JOptionPane.showMessageDialog(this, "El producto ya está en la lista. Elimínelo si desea cambiar la cantidad.");
                return;
            }
        }

        // Añadimos la fila: {Producto, StockActual, StockMin, Cantidad Solicitada}
        modelo.addRow(new Object[]{
            nombreProducto,
            stockActual,
            stockMin,
            cantidad
        });

        // 5. Limpiamos para el siguiente
        spnCantidad.setValue(1);
        cboProducto.setSelectedIndex(0);
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void cboProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboProveedorActionPerformed

    private void cboProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboProductoActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
       // 1. Validaciones básicas
        if (tblDetalle.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en el pedido.");
            return;
        }
        if (cboProveedor.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor.");
            return;
        }
        if (dcFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha comprometida.");
            return;
        }

        String nombreProveedor = cboProveedor.getSelectedItem().toString();
        java.util.Date fechaUtil = dcFecha.getDate();
        java.sql.Date fechaSql = new java.sql.Date(fechaUtil.getTime());
        
        // Variables de conexión
        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; // <--- ¡PON TU CONTRASEÑA AQUÍ!

        Connection con = null;
        
        try {
            con = DriverManager.getConnection(url, usuario, password);
            con.setAutoCommit(false); // Inicio de transacción

            // --- PASO A: Generar ID Secuencial (P-001, P-002...) ---
            String codigoPedido = "P-001"; // Valor inicial por defecto
            
            String sqlUltimo = "SELECT codigo FROM pedido ORDER BY id_pedido DESC LIMIT 1";
            PreparedStatement psUlt = con.prepareStatement(sqlUltimo);
            ResultSet rsUlt = psUlt.executeQuery();

            if (rsUlt.next()) {
                String ultimoCodigo = rsUlt.getString("codigo");
                String soloNumeros = ultimoCodigo.replaceAll("[^0-9]", "");
                if (!soloNumeros.isEmpty()) {
                    int numero = Integer.parseInt(soloNumeros) + 1;
                    codigoPedido = String.format("P-%03d", numero);
                }
            }

            // --- PASO B: Obtener ID del Proveedor ---
            String sqlProv = "SELECT id_proveedor FROM proveedor WHERE Razon_Social = ?";
            PreparedStatement psProv = con.prepareStatement(sqlProv);
            psProv.setString(1, nombreProveedor);
            ResultSet rsProv = psProv.executeQuery();
            
            String idProveedor = "";
            if (rsProv.next()) {
                idProveedor = rsProv.getString("id_proveedor");
            } else {
                throw new Exception("Proveedor no encontrado en BD.");
            }

            // --- PASO C: Insertar Cabecera (Pedido) ---
            // Asumimos id_sucursal = 1. Si tienes login, aquí iría la sucursal del usuario.
            String sqlCabecera = "INSERT INTO pedido (codigo, id_proveedor, id_sucursal, fecha_comprometida, estado) VALUES (?, ?, 1, ?, 'Pendiente')";
            
            PreparedStatement psCab = con.prepareStatement(sqlCabecera, Statement.RETURN_GENERATED_KEYS);
            psCab.setString(1, codigoPedido);
            psCab.setString(2, idProveedor);
            psCab.setDate(3, fechaSql);
            psCab.executeUpdate();

            // Recuperar el ID numérico (id_pedido) generado
            ResultSet rsKeys = psCab.getGeneratedKeys();
            int idPedidoGenerado = 0;
            if (rsKeys.next()) {
                idPedidoGenerado = rsKeys.getInt(1);
            }

            // --- PASO D: Insertar Detalles (Recorriendo la Tabla Visual) ---
            String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (?, ?, ?)";
            PreparedStatement psDet = con.prepareStatement(sqlDetalle);
            PreparedStatement psBuscaProd = con.prepareStatement("SELECT id_producto FROM producto WHERE nombre = ?");

            DefaultTableModel modelo = (DefaultTableModel) tblDetalle.getModel();
            
            for (int i = 0; i < modelo.getRowCount(); i++) {
                String nombreProd = modelo.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modelo.getValueAt(i, 3).toString());

                // Buscar ID del producto
                psBuscaProd.setString(1, nombreProd);
                ResultSet rsProd = psBuscaProd.executeQuery();
                
                int idProducto = 0;
                if (rsProd.next()) {
                    idProducto = rsProd.getInt("id_producto");
                }

                // Insertar fila
                psDet.setInt(1, idPedidoGenerado);
                psDet.setInt(2, idProducto);
                psDet.setInt(3, cantidad);
                psDet.executeUpdate();
            }

            // --- PASO E: Confirmar todo (Commit) ---
            con.commit();
            JOptionPane.showMessageDialog(this, "¡Pedido " + codigoPedido + " registrado con éxito!");
            this.dispose(); // Cerrar ventana

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { }
            JOptionPane.showMessageDialog(this, "Error al registrar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { }
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cboProducto;
    private javax.swing.JComboBox<String> cboProveedor;
    private com.toedter.calendar.JDateChooser dcFecha;
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
    private javax.swing.JTextField jTextField1;
    private javax.swing.JSpinner spnCantidad;
    private javax.swing.JTable tblDetalle;
    // End of variables declaration//GEN-END:variables
}
