package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.ProductoDAO;

import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import edu.UPAO.proyecto.ProductoController;
import edu.UPAO.proyecto.PromocionController;
import edu.UPAO.proyecto.Modelo.Producto;
import java.util.ArrayList;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Menu2 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu2.class.getName());
    private String idEmpleado;

    public Menu2(String idEmpleado) {
        initComponents();
        btn_validar.addActionListener(e -> onValidarCupon());
        this.idEmpleado = idEmpleado;
        System.out.println("✅ Menu2 - Empleado en sesión: " + this.idEmpleado);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Sistema Kuyay - Menú Principal");
        inicializarTablaProductos(); // ✅ Esto asegura el orden correcto
        txtObservaciones.setEnabled(false);
        txtCupon.setEnabled(false);

        spCantidad.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        sp_item.setModel(spinnerModel);

        btn_validar.addActionListener(e -> onValidarCupon());

        spCantidad.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        sp_item.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        ///////////////////////ACA SOLO PARA LA RUEDITA/////////////////////////
        sp_item.addMouseWheelListener(e -> {
            int rot = e.getWheelRotation();
            int val = (int) sp_item.getValue();
            if (rot < 0) {
                sp_item.setValue(Math.max(1, val + 1));
            } else if (rot > 0) {
                sp_item.setValue(Math.max(1, val - 1));
            }
        });

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                spCantidad.setValue(1); // 👈 Reinicia cantidad
            }
        });

        // Al inicializar tu formulario
        spCantidad.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation(); // movimiento de la rueda
            int valorActual = (int) spCantidad.getValue();

            int fila = tablaProductos.getSelectedRow();
            if (fila == -1) {
                return; // no hay producto seleccionado
            }
            DefaultTableModel modeloProductos = new DefaultTableModel(
                    new Object[]{"Código", "Nombre", "Precio", "Stock"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Hacer la tabla no editable
                }
            };
            tablaProductos.setModel(modeloProductos);

            // Asegurar que las columnas tengan el ancho adecuado
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);  // Código
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(60);  // Precio
            tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(50);  // Stock
            String codigo = modeloProductos.getValueAt(fila, 0).toString();

            ProductoDAO productoDAO = new ProductoDAO();
            Producto producto = productoDAO.buscarPorCodigo(codigo);
            if (producto == null) {
                return;
            }

            int stock = producto.getStock();
            int nuevoValor = valorActual;

            if (notches < 0) {
                // Rueda hacia arriba → aumentar
                if (valorActual < stock) {
                    nuevoValor++;
                }
            } else {
                // Rueda hacia abajo → disminuir
                if (valorActual > 1) {
                    nuevoValor--;
                }
            }

            spCantidad.setValue(nuevoValor);
        });

        // 🔄 Resetear spinner al seleccionar otro ítem en miniTabla
        miniTabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && miniTabla.getSelectedRow() != -1) {
                sp_item.setValue(1); // tu spinner de actualizar carrito
            }
        });
        ///////////////////////ACA SOLO PARA LA RUEDITA/////////////////////////

        //////////OCULATMOS LA COLUMNA DE "CODIGO DE LA MINITABLA"//////////////
        miniTabla.getColumnModel().getColumn(4).setMinWidth(0);
        miniTabla.getColumnModel().getColumn(4).setMaxWidth(0);
        miniTabla.getColumnModel().getColumn(4).setWidth(0);
        //////////OCULATMOS LA COLUMNA DE "CODIGO DE LA MINITABLA"//////////////

        txtBuscarCodigo.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String texto = txtBuscarCodigo.getText();
                List<Producto> filtrados = buscarProductosPorNombre(texto);

                DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
                modelo.setRowCount(0);

                for (Producto p : filtrados) {
                    modelo.addRow(new Object[]{
                        p.getCodigo(),
                        p.getNombre(),
                        p.getPrecioVenta(),
                        p.getStock()
                    });
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
        btn_inicio.doClick();
    }

    public String getIdEmpleado() {
        return this.idEmpleado;
    }

    public void cargarProductosEnTabla() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.listar();

            System.out.println("🔍 DEBUG: Se encontraron " + productos.size() + " productos");

            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            if (productos.isEmpty()) {
                System.out.println("⚠️ ADVERTENCIA: No se encontraron productos en la BD");
                JOptionPane.showMessageDialog(this,
                        "No se encontraron productos en la base de datos.\nVerifica que haya productos cargados.",
                        "Sin productos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Producto p : productos) {
                System.out.println("📦 Producto: " + p.getCodigo() + " - " + p.getNombre() + " - S/" + p.getPrecioVenta());
                modelo.addRow(new Object[]{
                    p.getCodigo(), // Columna 0: Código
                    p.getNombre(), // Columna 1: Nombre  
                    p.getPrecioVenta(), // Columna 2: Precio
                    p.getStock() // Columna 3: Stock
                });
            }

            System.out.println("✅ Tabla actualizada con " + modelo.getRowCount() + " productos");

        } catch (Exception e) {
            System.err.println("❌ ERROR en cargarProductosEnTabla: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }finally {
        if (dao != null) {
            dao.cerrarDAO(); // ✅ AQUÍ
        }
    }
    }

    private void inicializarTablaProductos() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Precio", "Stock"}, 0
        );
        tablaProductos.setModel(modelo);
    }

    private void onValidarCupon() {
        try {
            String cupon = txtCupon.getText().trim(); // o txtBuscarCodigo si ese es tu campo
            if (cupon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un cupón primero.");
                return;
            }

            // items del carrito desde tu JTable (miniTabla)
            java.util.List<edu.UPAO.proyecto.Modelo.VentaItem> itemsDelCarrito = obtenerItemsDelCarrito();

            double subtotal = calcularSubtotalCarrito();

            // aplicar cupon (método estático, NO necesitas new PromocionController())
            double descuento = edu.UPAO.proyecto.PromocionController.aplicarCupon(
                    cupon, itemsDelCarrito, subtotal
            );
            double totalConCupon = subtotal - descuento;

            // feedback + labels
            if (descuento > 0) {
                double porcentaje = (subtotal > 0) ? (descuento / subtotal) * 100 : 0;
                JOptionPane.showMessageDialog(this, "✓ Cupón válido: " + (int) porcentaje + "% aplicado.");
            } else {
                JOptionPane.showMessageDialog(this, "✗ Cupón inválido, caducado o inactivo.");
            }

            lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
            lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
            resultadoTotal.setText(String.format("S/ %.2f", totalConCupon));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al validar cupón: " + ex.getMessage());
        }
    }

    // helper robusto para números (soporta "15", "15.0", "15,0")
    private double parseNum(Object v) {
        if (v == null) {
            return 0.0;
        }
        String s = String.valueOf(v).trim().replace(",", ".");
        if (s.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private List<edu.UPAO.proyecto.Modelo.VentaItem> obtenerItemsDelCarrito() {
        List<edu.UPAO.proyecto.Modelo.VentaItem> items = new ArrayList<>();
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        for (int i = 0; i < carrito.getRowCount(); i++) {
            String nombre = String.valueOf(carrito.getValueAt(i, 0)); // Nombre
            double cantD = parseNum(carrito.getValueAt(i, 1));       // Cantidad (col 1)
            int cantidad = (int) Math.round(cantD <= 0 ? 0 : cantD);
            double precioUnit = parseNum(carrito.getValueAt(i, 2));       // P/U (col 2)
            double subtotal = parseNum(carrito.getValueAt(i, 3));       // Subtotal (col 3)

            if (precioUnit <= 0 && cantidad > 0) {
                precioUnit = subtotal / cantidad;
            }

            items.add(new edu.UPAO.proyecto.Modelo.VentaItem(nombre, cantidad, precioUnit));
        }
        return items;
    }

    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btn_salir = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblFrase = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tb_reportes = new javax.swing.JToggleButton();
        tb_entrada = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btn_ventas = new javax.swing.JButton();
        btn_inicio = new javax.swing.JButton();
        panel_contenido = new javax.swing.JInternalFrame();
        panelFormulario = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btn_agregar = new javax.swing.JButton();
        spCantidad = new javax.swing.JSpinner();
        jButton11 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        btn_SKU = new javax.swing.JButton();
        btn_cancelar = new javax.swing.JButton();
        btn_Siguiente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        txtCupon = new javax.swing.JTextField();
        rb_observacion = new javax.swing.JRadioButton();
        rb_cupon = new javax.swing.JRadioButton();
        lbl_total = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        sp_item = new javax.swing.JSpinner();
        btn_actualizarItem = new javax.swing.JButton();
        btn_eliminarItem = new javax.swing.JButton();
        btn_validar = new javax.swing.JButton();
        lbl_descuento = new javax.swing.JLabel();
        lbl_subtotal = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        miniTabla = new javax.swing.JTable();
        resultadoTotal = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarCodigo = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        btn_buscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 153, 0));

        btn_salir.setText("SALIR");
        btn_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salirActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/miniLogo.png"))); // NOI18N

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 36)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(57, 57, 57)
                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                .addComponent(btn_salir, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_salir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1380, 100));

        jPanel2.setBackground(new java.awt.Color(179, 9, 1));

        tb_reportes.setBackground(new java.awt.Color(118, 158, 139));
        tb_reportes.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tb_reportes.setText("REPORTES");
        tb_reportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tb_reportesActionPerformed(evt);
            }
        });

        tb_entrada.setBackground(new java.awt.Color(118, 158, 139));
        tb_entrada.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tb_entrada.setText("R. ENT / SAL");
        tb_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tb_entradaActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(113, 153, 143));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton1.setText("R- PRODUCTOS");
        jButton1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jButton1ComponentMoved(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btn_ventas.setText("VENTAS");
        btn_ventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventasActionPerformed(evt);
            }
        });

        btn_inicio.setText("iNICIO");
        btn_inicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_inicioActionPerformed(evt);
            }
        });

        panel_contenido.setVisible(true);

        javax.swing.GroupLayout panel_contenidoLayout = new javax.swing.GroupLayout(panel_contenido.getContentPane());
        panel_contenido.getContentPane().setLayout(panel_contenidoLayout);
        panel_contenidoLayout.setHorizontalGroup(
            panel_contenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel_contenidoLayout.setVerticalGroup(
            panel_contenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btn_inicio, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tb_reportes, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tb_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btn_ventas, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(647, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_contenido)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_reportes, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(tb_entrada, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btn_inicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ventas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_contenido, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(107, 107, 107)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(685, 685, 685))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 104, 1380, 50));

        panelFormulario.setPreferredSize(new java.awt.Dimension(1400, 646));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_agregar.setBackground(new java.awt.Color(0, 153, 0));
        btn_agregar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_agregar.setForeground(new java.awt.Color(255, 255, 255));
        btn_agregar.setText("AGREGAR");
        btn_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregarActionPerformed(evt);
            }
        });
        jPanel8.add(btn_agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 530, 132, 48));
        jPanel8.add(spCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 120, 48));

        jButton11.setBackground(new java.awt.Color(110, 149, 106));
        jButton11.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("REPORTAR PRODUCTO");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 530, 200, 50));

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(500);
        }

        jPanel8.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 980, 490));

        panel.setBackground(new java.awt.Color(255, 255, 255));

        btn_SKU.setBackground(new java.awt.Color(110, 149, 106));
        btn_SKU.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btn_SKU.setForeground(new java.awt.Color(255, 255, 255));
        btn_SKU.setText("ESCANEAR SKU");
        btn_SKU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SKUActionPerformed(evt);
            }
        });

        btn_cancelar.setBackground(new java.awt.Color(255, 0, 0));
        btn_cancelar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_cancelar.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelar.setText("CANCELAR");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });

        btn_Siguiente.setBackground(new java.awt.Color(0, 153, 0));
        btn_Siguiente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_Siguiente.setForeground(new java.awt.Color(255, 255, 255));
        btn_Siguiente.setText("SIGUIENTE");
        btn_Siguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SiguienteActionPerformed(evt);
            }
        });

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(5);
        jScrollPane1.setViewportView(txtObservaciones);

        txtCupon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCuponActionPerformed(evt);
            }
        });

        rb_observacion.setText("Observación");
        rb_observacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_observacionActionPerformed(evt);
            }
        });

        rb_cupon.setText("Cupón:");
        rb_cupon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_cuponActionPerformed(evt);
            }
        });

        lbl_total.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_total.setText("TOTAL:");

        btn_actualizarItem.setText("Actualizar");
        btn_actualizarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarItemActionPerformed(evt);
            }
        });

        btn_eliminarItem.setText("Eliminar");
        btn_eliminarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(sp_item, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_actualizarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_eliminarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sp_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_actualizarItem)
                    .addComponent(btn_eliminarItem))
                .addContainerGap())
        );

        btn_validar.setText("VALIDAR CUP");
        btn_validar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_validarActionPerformed(evt);
            }
        });

        lbl_descuento.setText("Descuento: S./ 0.00");

        lbl_subtotal.setText("Subtotal: S./ 0.00");

        miniTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Cantidad", "P/U", "Subtotal", "CODIGO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(miniTabla);
        if (miniTabla.getColumnModel().getColumnCount() > 0) {
            miniTabla.getColumnModel().getColumn(0).setResizable(false);
            miniTabla.getColumnModel().getColumn(0).setPreferredWidth(220);
            miniTabla.getColumnModel().getColumn(1).setResizable(false);
            miniTabla.getColumnModel().getColumn(2).setResizable(false);
            miniTabla.getColumnModel().getColumn(3).setResizable(false);
            miniTabla.getColumnModel().getColumn(4).setResizable(false);
        }

        resultadoTotal.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        resultadoTotal.setText("S/.  0.00");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addComponent(rb_observacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(rb_cupon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCupon, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_validar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(btn_SKU, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelLayout.createSequentialGroup()
                                        .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lbl_total, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(resultadoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_Siguiente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbl_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_SKU, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(rb_observacion))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_subtotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_cupon)
                    .addComponent(txtCupon, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_validar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_total, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultadoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Siguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("PRODUCTO:");

        txtBuscarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarCodigoActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(110, 149, 106));
        jButton12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("FRECUENTES");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        btn_buscar.setBackground(new java.awt.Color(110, 149, 106));
        btn_buscar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btn_buscar.setForeground(new java.awt.Color(255, 255, 255));
        btn_buscar.setText("BUSCAR");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarCodigo)
                        .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        getContentPane().add(panelFormulario, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 152, 1380, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private List<Producto> buscarProductosPorNombre(String texto) {
        ProductoDAO dao = new ProductoDAO();
        // El DAO debería hacer la consulta SQL con LIKE
        return dao.buscarPorNombre(texto); // SELECT * FROM producto WHERE nombre LIKE ?

    }

    public void vaciarCarrito() {
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        modeloCarrito.setRowCount(0);
        spCantidad.setValue(1);

        lbl_subtotal.setText("Subtotal: S/ 0.00");
        lbl_descuento.setText("Descuento: S/ 0.00");
        resultadoTotal.setText("S/ 0.00");
        txtObservaciones.setText("");
        txtCupon.setText("");
    }

    // 🚀 Recalcula subtotal, descuento y total
    public void actualizarTotal() {
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        double subtotal = 0.0;

        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            Object valorSubtotal = modeloCarrito.getValueAt(i, 3); // Columna 3 = Subtotal
            if (valorSubtotal != null) {
                try {
                    subtotal += Double.parseDouble(valorSubtotal.toString());
                } catch (NumberFormatException e) {
                    System.err.println("⚠ Error en fila " + i + ": " + valorSubtotal);
                }
            }
        }

        // 🔥 Aplicar reglas de negocio
        PromocionController promoCtrl = new PromocionController();
        double descuento = promoCtrl.calcularDescuentoReglas(modeloCarrito);

        double total = subtotal - descuento;

        // Actualiza los labels
        lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
        lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
        resultadoTotal.setText("S/ " + String.format("%.2f", total));
    }

    private double calcularSubtotalCarrito() {
        double subtotalGeneral = 0.0;
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        for (int i = 0; i < carrito.getRowCount(); i++) {
            subtotalGeneral += parseNum(carrito.getValueAt(i, 3)); // Subtotal en col 3
        }
        return subtotalGeneral;
    }

    private boolean contieneSalteado(String texto, String patron) {
        int j = 0;
        for (int i = 0; i < texto.length() && j < patron.length(); i++) {
            if (texto.charAt(i) == patron.charAt(j)) {
                j++;
            }
        }
        return j == patron.length();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jFrame_añadirProducto jFrame_añadirProducto1 = new jFrame_añadirProducto();
        jFrame_añadirProducto1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tb_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tb_entradaActionPerformed
        jFrame_Asistncias jFrame_Asistncias1;
        jFrame_Asistncias1 = new jFrame_Asistncias();
        jFrame_Asistncias1.setVisible(true);
    }//GEN-LAST:event_tb_entradaActionPerformed

    private void tb_reportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tb_reportesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tb_reportesActionPerformed

    private void btn_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salirActionPerformed
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            // ✅ Volver al login
            LoginjFrame login = new LoginjFrame();
            login.setVisible(true);

            // ✅ Cerrar el panel de gerente
            this.dispose();
        }
    }//GEN-LAST:event_btn_salirActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String texto = txtBuscarCodigo.getText().trim().toLowerCase();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un texto para buscar.");
            return;
        }

        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.listar();
        List<Producto> filtrados = new ArrayList<>();

        for (Producto p : productos) {
            // 🔎 Coincidencias no necesariamente en orden exacto
            String nombre = p.getNombre().toLowerCase();
            if (contieneSalteado(nombre, texto)) {
                filtrados.add(p);
            }
        }

        // Mostrar resultados en tabla
        DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
        modelo.setRowCount(0);
        for (Producto p : filtrados) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecioVenta(), p.getStock()});
        }
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        ProductoController pc = new ProductoController();

        try {
            // Usar cargarProductos en lugar de productosMasVendidos temporalmente
            List<Producto> productos = pc.cargarProductos();

            // Mostrar en tablaProductos (sin la columna de vendidos por ahora)
            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            for (Producto p : productos) {
                modelo.addRow(new Object[]{
                    p.getNombre(),
                    p.getPrecioVenta(),
                    p.getStock()
                // p.getVendidos() // ❌ Comentado temporalmente
                });
            }

            JOptionPane.showMessageDialog(this, "Productos cargados (función de más vendidos temporalmente deshabilitada)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void txtBuscarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarCodigoActionPerformed

    }//GEN-LAST:event_txtBuscarCodigoActionPerformed

    private void btn_validarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_validarActionPerformed
        String cupon = txtCupon.getText().trim().replaceAll("\\s+", ""); // limpia espacios

        if (cupon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un cupón primero.");
            return;
        }

        double subtotal = calcularSubtotalCarrito();
        List<edu.UPAO.proyecto.Modelo.VentaItem> itemsDelCarrito = obtenerItemsDelCarrito();

        double descuento = edu.UPAO.proyecto.PromocionController.aplicarCupon(cupon, itemsDelCarrito, subtotal);
        double totalConCupon = subtotal - descuento;

        if (descuento > 0) {
            double porcentaje = (subtotal > 0) ? (descuento / subtotal) * 100 : 0;
            JOptionPane.showMessageDialog(this, "✓ Cupón válido: " + (int) porcentaje + "% aplicado.");
        } else {
            JOptionPane.showMessageDialog(this, "✗ Cupón inválido, caducado o inactivo.");
        }

        lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
        lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
        resultadoTotal.setText(String.format("S/ %.2f", totalConCupon));
    }//GEN-LAST:event_btn_validarActionPerformed

    private void btn_eliminarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarItemActionPerformed
        int fila = miniTabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel modelo = (DefaultTableModel) miniTabla.getModel();
            modelo.removeRow(fila);
            actualizarTotal(); // recalcula el total después de eliminar
        }
    }//GEN-LAST:event_btn_eliminarItemActionPerformed

    private void btn_actualizarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarItemActionPerformed
        int fila = miniTabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar.");
            return;
        }

        int nuevaCantidad = (int) sp_item.getValue();
        if (nuevaCantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) miniTabla.getModel();

        // ✅ Recuperar el código oculto (columna 0)
        String codigo = modelo.getValueAt(fila, 4).toString();

        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.buscarPorCodigo(codigo);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el producto en la base de datos.");
            return;
        }

        // ✅ Validar stock
        if (nuevaCantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
            return;
        }

        // ✅ Actualizar cantidad y subtotal en la tabla
        modelo.setValueAt(nuevaCantidad, fila, 1); // cantidad
        modelo.setValueAt(producto.getPrecioVenta(), fila, 2); // P/U
        modelo.setValueAt(producto.getPrecioVenta() * nuevaCantidad, fila, 3); // subtotal

        actualizarTotal();
    }//GEN-LAST:event_btn_actualizarItemActionPerformed

    private void rb_cuponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_cuponActionPerformed
        txtCupon.setEnabled(rb_cupon.isSelected());
    }//GEN-LAST:event_rb_cuponActionPerformed

    private void rb_observacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_observacionActionPerformed
        txtObservaciones.setEnabled(rb_observacion.isSelected());
    }//GEN-LAST:event_rb_observacionActionPerformed

    private void txtCuponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCuponActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCuponActionPerformed

    private void btn_SiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SiguienteActionPerformed
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        // Crear un clon del carrito
        DefaultTableModel carritoClonado = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "P/U", "Subtotal", "Código"}, 0);

        for (int i = 0; i < carrito.getRowCount(); i++) {
            Object[] fila = new Object[carrito.getColumnCount()];
            for (int j = 0; j < carrito.getColumnCount(); j++) {
                fila[j] = carrito.getValueAt(i, j);
            }
            carritoClonado.addRow(fila);
        }

        // Obtener los valores actuales de los labels
        String subtotal = lbl_subtotal.getText().replace("Subtotal:", "").trim();
        String descuento = lbl_descuento.getText().replace("Descuento:", "").trim();
        String total = resultadoTotal.getText().trim();

        // ✅ OBTENER OBSERVACIONES DEL TEXTAREA
        String observaciones = txtObservaciones.getText().trim();

        // ✅ PASAR id_empleado al crear jFrame_GenerarBoleta
        jFrame_GenerarBoleta boletaFrame = new jFrame_GenerarBoleta(this, carritoClonado, subtotal, descuento, total, this.idEmpleado);

        // ✅ PASAR OBSERVACIONES
        boletaFrame.setObservaciones(observaciones);

        boletaFrame.setLocationRelativeTo(this);
        boletaFrame.setVisible(true);
    }//GEN-LAST:event_btn_SiguienteActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        modeloCarrito.setRowCount(0); // vacía el carrito
        spCantidad.setValue(1);
        sp_item.setValue(1);

        lbl_subtotal.setText("Subtotal: S/ 0.00");
        lbl_descuento.setText("Descuento: S/ 0.00");
        resultadoTotal.setText("S/ 0.00");

        txtObservaciones.setText("");
        txtCupon.setText("");
    }//GEN-LAST:event_btn_cancelarActionPerformed

    private void btn_SKUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SKUActionPerformed
        // Cambiar texto y bloquear botón mientras simula escaneo
        btn_SKU.setText("Escaneando...");
        btn_SKU.setEnabled(false);

        // Crear Timer (ejecuta la acción tras 2000 ms)
        Timer t = new Timer(2000, e -> {
            ProductoController pc = new ProductoController();
            List<Producto> productos = pc.cargarProductos();

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos cargados.");
            } else {
                Random rand = new Random();
                Producto producto = productos.get(rand.nextInt(productos.size()));

                // 🔹 Cantidad fija = 1 (como un escaneo real)
                int cantidad = 1;

                if (producto.getStock() <= 0) {
                    JOptionPane.showMessageDialog(this, "❌ El producto " + producto.getNombre() + " no tiene stock.");
                } else {
                    DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();

                    // 🔹 Validar stock
                    if (cantidad > producto.getStock()) {
                        JOptionPane.showMessageDialog(this,
                                "⚠ Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
                        return;
                    }

                    // 🔹 Calcular subtotal
                    double precioUnitario = producto.getPrecioVenta();
                    double subtotal = precioUnitario * cantidad;

                    // 🚀 Agregar al carrito en el mismo orden que btn_agregar
                    modeloCarrito.addRow(new Object[]{
                        producto.getNombre(), // Columna 0: Nombre
                        cantidad, // Columna 1: Cantidad
                        precioUnitario, // Columna 2: P/U
                        subtotal, // Columna 3: Subtotal
                        producto.getCodigo() // Columna 4: Código (oculto)
                    });

                    // 🔹 Recalcular total
                    actualizarTotal();

                    JOptionPane.showMessageDialog(this, "✅ Producto escaneado: " + producto.getNombre());
                }
            }

            // Restaurar el botón
            btn_SKU.setText("ESCANEAR SKU");
            btn_SKU.setEnabled(true);
        });

        t.setRepeats(false);
        t.start();
    }//GEN-LAST:event_btn_SKUActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

    }//GEN-LAST:event_jButton11ActionPerformed

    private void btn_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregarActionPerformed
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto primero.");
            return;
        }

        DefaultTableModel modeloProductos = (DefaultTableModel) tablaProductos.getModel();
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();

        String codigo = tablaProductos.getValueAt(filaSeleccionada, 0).toString().trim();
        System.out.println("Código seleccionado: " + codigo);

        // 🔹 Usar DAO para obtener el objeto producto
        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.buscarPorCodigo(codigo);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, "❌ No se encontró el producto en el DAO");
            return;
        }

        // 🔹 Obtener la cantidad del spinner
        int cantidad = (int) spCantidad.getValue();

        // 🔹 Validar stock
        if (cantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this, "⚠ Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
            return;
        }

        // 🔹 Calcular subtotal
        double precioUnitario = producto.getPrecioVenta();
        double subtotal = precioUnitario * cantidad;

        modeloCarrito.addRow(new Object[]{
            producto.getNombre(), // Columna 0: Nombre
            cantidad, // Columna 1: Cantidad
            precioUnitario, // Columna 2: P/U
            subtotal, // Columna 3: Subtotal
            producto.getCodigo() // Columna 4: Código (oculto)
        });

        // 🔹 Recalcular totales
        actualizarTotal();
    }//GEN-LAST:event_btn_agregarActionPerformed

    private void jButton1ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jButton1ComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ComponentMoved

    private void btn_inicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_inicioActionPerformed

        jPanel2.setSize(1380, 43);        jPanel2.setSize(1380, 43);    }//GEN-LAST:event_btn_inicioActionPerformed

    private void btn_ventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventasActionPerformed
        panel_contenido.setSize(1380, 661);
        jPanel2.setSize(1380, 700);
        panel_contenido.setClosable(false);
        panel_contenido.setMaximizable(false);
        panel_contenido.setIconifiable(false);

    }//GEN-LAST:event_btn_ventasActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* ✅ CAMBIO: Abrir LoginjFrame en lugar de Menu2 directamente */
        java.awt.EventQueue.invokeLater(() -> {
            LoginjFrame login = new LoginjFrame();
            login.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_SKU;
    private javax.swing.JButton btn_Siguiente;
    private javax.swing.JButton btn_actualizarItem;
    private javax.swing.JButton btn_agregar;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_eliminarItem;
    private javax.swing.JButton btn_inicio;
    private javax.swing.JButton btn_salir;
    private javax.swing.JButton btn_validar;
    private javax.swing.JButton btn_ventas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lbl_descuento;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JTable miniTabla;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JInternalFrame panel_contenido;
    private javax.swing.JRadioButton rb_cupon;
    private javax.swing.JRadioButton rb_observacion;
    private javax.swing.JLabel resultadoTotal;
    private javax.swing.JSpinner spCantidad;
    private javax.swing.JSpinner sp_item;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JToggleButton tb_entrada;
    private javax.swing.JToggleButton tb_reportes;
    private javax.swing.JTextField txtBuscarCodigo;
    private javax.swing.JTextField txtCupon;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
