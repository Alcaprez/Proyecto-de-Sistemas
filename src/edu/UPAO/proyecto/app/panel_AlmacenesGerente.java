package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.Modelo.Producto;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class panel_AlmacenesGerente extends javax.swing.JPanel {

    private ProductoDAO productoDAO;
    private DefaultTableModel modeloTabla;
    private int idSucursalActual = 1; // [!] Debes obtener esto del usuario logueado
    private int idProductoSeleccionado = -1; // Para controlar qué producto se edita

    public panel_AlmacenesGerente() {
        initComponents();
        productoDAO = new ProductoDAO();
        configurarTabla();
        configurarFormulario(); // Bloqueos iniciales
        cargarProductosGlobales(); // Carga GLOBAL
        cargarCategorias();
    }

    private void configurarFormulario() {
        // Bloqueo PERMANENTE del stock, ya que es calculado
        tf_stock5.setEditable(false);
        tf_stock5.setText("0"); // Valor por defecto visual
    }

    // ✅ Carga productos usando el nuevo método del DAO
    private void cargarProductosGlobales() {
        limpiarTablaVisual();
        List<Producto> lista = productoDAO.listarGlobal(); // Método nuevo

        for (Producto p : lista) {
            Object[] fila = {
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecioVenta(),
                p.getStock(), // Muestra el stock total calculado en la query
                p.getEstado()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarTablaVisual() {
        while (modeloTabla.getRowCount() > 0) {
            modeloTabla.removeRow(0);
        }
    }

    private void configurarTabla() {
        // "Stock Global" indica la suma de todas las tiendas
        String[] titulos = {"ID", "Código", "Nombre", "Categoría", "Precio Venta", "Stock Global", "Estado"};
        modeloTabla = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla_productos1.setModel(modeloTabla);

        tabla_productos1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla_productos1.getSelectedRow();
                if (fila >= 0) {
                    rellenarFormularioDesdeTabla(fila);
                }
            }
        });
    }

    // Carga los productos desde la BD a la tabla
    private void cargarProductos() {
        limpiarTabla();
        List<Producto> lista = productoDAO.listarPorSucursal(idSucursalActual);

        for (Producto p : lista) {
            Object[] fila = {
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecioVenta(),
                p.getStock(), // Usa el stockTemporal cargado por el DAO
                p.getEstado()
            };
            modeloTabla.addRow(fila);
        }
    }

    // Método auxiliar para limpiar la tabla antes de recargar
    private void limpiarTabla() {
        while (modeloTabla.getRowCount() > 0) {
            modeloTabla.removeRow(0);
        }
    }

    private void cargarCategorias() {
        cb_categoria5.removeAllItems();
        cb_categoria5.addItem("General");
        cb_categoria5.addItem("Bebidas");
        cb_categoria5.addItem("Alimentos");
        cb_categoria5.addItem("Limpieza");
    }

    private void rellenarFormularioDesdeTabla(int fila) {
        idProductoSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        tf_codigoProducto5.setText(modeloTabla.getValueAt(fila, 1).toString());
        tf_nombre5.setText(modeloTabla.getValueAt(fila, 2).toString());

        String cat = modeloTabla.getValueAt(fila, 3) != null ? modeloTabla.getValueAt(fila, 3).toString() : "General";
        cb_categoria5.setSelectedItem(cat);

        tf_precio5.setText(modeloTabla.getValueAt(fila, 4).toString());

        // Mostramos el stock global solo como referencia visual
        tf_stock5.setText(modeloTabla.getValueAt(fila, 5).toString());

        btn_añadir5.setEnabled(false);
        btn_actualizar1.setEnabled(true);
        btn_eliminar1.setEnabled(true);
    }

    private void limpiarFormulario() {
        tf_codigoProducto5.setText("");
        tf_nombre5.setText("");
        tf_precio5.setText("");
        tf_stock5.setText("");
        cb_categoria5.setSelectedIndex(0);
        idProductoSeleccionado = -1;
        tabla_productos1.clearSelection();

        tf_stock5.setEditable(true); // Reactivar por si acaso (aunque al añadir suele ser 0)
        btn_añadir5.setEnabled(true);
        btn_actualizar1.setEnabled(false);
        btn_eliminar1.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Recibido = new javax.swing.JLabel();
        Enmano = new javax.swing.JLabel();
        Shipped = new javax.swing.JLabel();
        TotalInventario = new javax.swing.JLabel();
        PanelEnMano = new javax.swing.JPanel();
        lbl_totalIngresos4 = new javax.swing.JLabel();
        EstadodeInventario = new javax.swing.JPanel();
        EstadodeSucursal = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        PanelTotalInventario = new javax.swing.JPanel();
        lbl_totalIngresos = new javax.swing.JLabel();
        Filtrar = new javax.swing.JLabel();
        Ingresos_totales2 = new javax.swing.JPanel();
        lbl_totalIngresos2 = new javax.swing.JLabel();
        PanelRecibido = new javax.swing.JPanel();
        lbl_totalIngresos1 = new javax.swing.JLabel();
        Inventario = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_buscar = new javax.swing.JButton();
        tf_busqueda = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabla_productos1 = new javax.swing.JTable();
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
        jLabel8 = new javax.swing.JLabel();
        btn_eliminar1 = new javax.swing.JButton();

        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorRemoved(evt);
            }
        });

        Recibido.setText("RECIBIDO");

        Enmano.setText("EN MANO");

        Shipped.setText("SHIPPED");

        TotalInventario.setText("TOTAL DE INVENTARIO");

        PanelEnMano.setBackground(new java.awt.Color(23, 87, 55));
        PanelEnMano.setPreferredSize(new java.awt.Dimension(230, 121));

        lbl_totalIngresos4.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_totalIngresos4.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos4.setText("0.00");
        lbl_totalIngresos4.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout PanelEnManoLayout = new javax.swing.GroupLayout(PanelEnMano);
        PanelEnMano.setLayout(PanelEnManoLayout);
        PanelEnManoLayout.setHorizontalGroup(
            PanelEnManoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelEnManoLayout.createSequentialGroup()
                .addGap(0, 53, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos4, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelEnManoLayout.setVerticalGroup(
            PanelEnManoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelEnManoLayout.createSequentialGroup()
                .addGap(0, 20, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        EstadodeInventario.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout EstadodeInventarioLayout = new javax.swing.GroupLayout(EstadodeInventario);
        EstadodeInventario.setLayout(EstadodeInventarioLayout);
        EstadodeInventarioLayout.setHorizontalGroup(
            EstadodeInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        EstadodeInventarioLayout.setVerticalGroup(
            EstadodeInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 331, Short.MAX_VALUE)
        );

        EstadodeSucursal.setBackground(new java.awt.Color(204, 204, 204));
        EstadodeSucursal.setPreferredSize(new java.awt.Dimension(560, 331));

        javax.swing.GroupLayout EstadodeSucursalLayout = new javax.swing.GroupLayout(EstadodeSucursal);
        EstadodeSucursal.setLayout(EstadodeSucursalLayout);
        EstadodeSucursalLayout.setHorizontalGroup(
            EstadodeSucursalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        EstadodeSucursalLayout.setVerticalGroup(
            EstadodeSucursalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 331, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(EstadodeInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelEnMano, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EstadodeSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(TotalInventario)
                .addGap(203, 203, 203)
                .addComponent(Recibido)
                .addGap(237, 237, 237)
                .addComponent(Enmano)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Shipped)
                .addGap(128, 128, 128))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(PanelEnMano, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TotalInventario)
                    .addComponent(Recibido)
                    .addComponent(Enmano)
                    .addComponent(Shipped))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EstadodeInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EstadodeSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIENDA" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CATEGORIA" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        PanelTotalInventario.setBackground(new java.awt.Color(23, 87, 55));
        PanelTotalInventario.setPreferredSize(new java.awt.Dimension(230, 121));

        lbl_totalIngresos.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_totalIngresos.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos.setText("0.00");
        lbl_totalIngresos.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout PanelTotalInventarioLayout = new javax.swing.GroupLayout(PanelTotalInventario);
        PanelTotalInventario.setLayout(PanelTotalInventarioLayout);
        PanelTotalInventarioLayout.setHorizontalGroup(
            PanelTotalInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTotalInventarioLayout.createSequentialGroup()
                .addGap(0, 53, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelTotalInventarioLayout.setVerticalGroup(
            PanelTotalInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTotalInventarioLayout.createSequentialGroup()
                .addGap(0, 20, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Filtrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Filtrar.setText("jLabel1");

        Ingresos_totales2.setBackground(new java.awt.Color(23, 87, 55));
        Ingresos_totales2.setPreferredSize(new java.awt.Dimension(230, 121));

        lbl_totalIngresos2.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_totalIngresos2.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos2.setText("0.00");
        lbl_totalIngresos2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout Ingresos_totales2Layout = new javax.swing.GroupLayout(Ingresos_totales2);
        Ingresos_totales2.setLayout(Ingresos_totales2Layout);
        Ingresos_totales2Layout.setHorizontalGroup(
            Ingresos_totales2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ingresos_totales2Layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Ingresos_totales2Layout.setVerticalGroup(
            Ingresos_totales2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ingresos_totales2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PanelRecibido.setBackground(new java.awt.Color(23, 87, 55));
        PanelRecibido.setPreferredSize(new java.awt.Dimension(230, 121));

        lbl_totalIngresos1.setFont(new java.awt.Font("Dialog", 0, 36)); // NOI18N
        lbl_totalIngresos1.setForeground(new java.awt.Color(255, 255, 255));
        lbl_totalIngresos1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_totalIngresos1.setText("0.00");
        lbl_totalIngresos1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout PanelRecibidoLayout = new javax.swing.GroupLayout(PanelRecibido);
        PanelRecibido.setLayout(PanelRecibidoLayout);
        PanelRecibidoLayout.setHorizontalGroup(
            PanelRecibidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRecibidoLayout.createSequentialGroup()
                .addGap(0, 53, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelRecibidoLayout.setVerticalGroup(
            PanelRecibidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRecibidoLayout.createSequentialGroup()
                .addGap(0, 20, Short.MAX_VALUE)
                .addComponent(lbl_totalIngresos1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Inventario.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        Inventario.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(Filtrar)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(PanelTotalInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PanelRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 311, Short.MAX_VALUE)
                        .addComponent(Ingresos_totales2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(77, 77, 77))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(516, 516, 516)
                .addComponent(Inventario)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 6, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 5, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(Inventario)
                .addGap(36, 36, 36)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Ingresos_totales2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Filtrar)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PanelTotalInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PanelRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(537, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("INVENTARIO", jPanel5);

        btn_buscar.setText("BUSCAR");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
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

        btn_añadir5.setText("Crear");
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

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel8.setText("Registro de productos");

        btn_eliminar1.setText("Eliminar");
        btn_eliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(129, 129, 129)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(243, 243, 243))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_stock5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_categoria5, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_codigoProducto5, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tf_nombre5, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tf_precio5, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_actualizar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_añadir5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_DeseleccionarLimpiar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_eliminar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tf_nombre5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel29)
                            .addComponent(tf_precio5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_stock5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(btn_eliminar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(btn_DeseleccionarLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btn_actualizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_añadir5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(cb_categoria5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31)
                            .addComponent(tf_codigoProducto5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tf_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addGap(103, 122, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("GESTION DE INVENTARIOS", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jPanel1AncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1AncestorRemoved

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String termino = tf_busqueda.getText().trim();
        if (termino.isEmpty()) {
            cargarProductosGlobales();
            return;
        }
        // Nota: Para búsqueda global real, deberías añadir un método buscarGlobal(termino) en el DAO
        // Por ahora reutilizamos la carga completa y filtramos o usamos el método existente si aplica.
        // Dado que tu DAO busca por sucursal, lo ideal es crear buscarGlobal() similar a listarGlobal().
        // Aquí asumiremos que recargas todo para simplificar si no modificas más el DAO.
        cargarProductosGlobales();
        // (Opcional: Implementar filtro en memoria o nuevo método SQL)
     }//GEN-LAST:event_btn_buscarActionPerformed

    private void btn_añadir5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_añadir5ActionPerformed
        if (tf_nombre5.getText().isEmpty() || tf_precio5.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Precio son obligatorios.");
            return;
        }

        try {
            Producto p = new Producto();
            p.setCodigo(tf_codigoProducto5.getText());
            p.setNombre(tf_nombre5.getText());
            p.setPrecioVenta(Double.parseDouble(tf_precio5.getText()));
            p.setPrecioCompra(p.getPrecioVenta() * 0.7); // Lógica negocio ejemplo
            p.setStockMinimo(10);
            p.setEstado("ACTIVO");
            // El stock inicial en la tabla PRODUCTO no existe, se crea en 0 en inventario luego
            // Así que ignoramos tf_stock5 aquí.

            if (productoDAO.insertar(p)) {
                JOptionPane.showMessageDialog(this, "Producto registrado en el catálogo global.");
                cargarProductosGlobales();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar producto.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido.");
        }
    }//GEN-LAST:event_btn_añadir5ActionPerformed

    private void cb_categoria5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_categoria5ActionPerformed

    }//GEN-LAST:event_cb_categoria5ActionPerformed

    private void btn_actualizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizar1ActionPerformed
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }

        try {
            Producto p = new Producto();
            p.setId(idProductoSeleccionado);
            p.setCodigo(tf_codigoProducto5.getText());
            p.setNombre(tf_nombre5.getText());
            p.setPrecioVenta(Double.parseDouble(tf_precio5.getText()));
            p.setPrecioCompra(Double.parseDouble(tf_precio5.getText()) * 0.7);
            p.setStockMinimo(10);
            p.setEstado("ACTIVO");
            // NO tocamos el stock. Solo actualizamos la definición del producto.

            if (productoDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Datos del producto actualizados.");
                cargarProductosGlobales();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido.");
        }
    }//GEN-LAST:event_btn_actualizar1ActionPerformed

    private void btn_DeseleccionarLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DeseleccionarLimpiarActionPerformed
        limpiarFormulario();
        cargarProductos();    }//GEN-LAST:event_btn_DeseleccionarLimpiarActionPerformed

    private void btn_eliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminar1ActionPerformed
        if (idProductoSeleccionado == -1) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar este producto del catálogo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Usamos el método de actualización para cambiar estado a INACTIVO (Soft Delete)
            try {
                Producto p = productoDAO.buscarPorCodigo(String.valueOf(idProductoSeleccionado), 1); // O buscar por ID si tienes el método
                // Si no tienes buscarPorId, crea un objeto dummy con lo necesario
                if (p == null) {
                    p = new Producto();
                }

                p.setId(idProductoSeleccionado);
                p.setCodigo(tf_codigoProducto5.getText());
                p.setNombre(tf_nombre5.getText());
                p.setPrecioVenta(Double.parseDouble(tf_precio5.getText()));
                p.setEstado("INACTIVO"); // <--- CLAVE

                if (productoDAO.actualizar(p)) {
                    JOptionPane.showMessageDialog(this, "Producto desactivado.");
                    cargarProductosGlobales();
                    limpiarFormulario();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_eliminar1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Enmano;
    private javax.swing.JPanel EstadodeInventario;
    private javax.swing.JPanel EstadodeSucursal;
    private javax.swing.JLabel Filtrar;
    private javax.swing.JPanel Ingresos_totales2;
    private javax.swing.JLabel Inventario;
    private javax.swing.JPanel PanelEnMano;
    private javax.swing.JPanel PanelRecibido;
    private javax.swing.JPanel PanelTotalInventario;
    private javax.swing.JLabel Recibido;
    private javax.swing.JLabel Shipped;
    private javax.swing.JLabel TotalInventario;
    private javax.swing.JButton btn_DeseleccionarLimpiar;
    private javax.swing.JButton btn_actualizar1;
    private javax.swing.JButton btn_añadir5;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_eliminar1;
    private javax.swing.JComboBox<String> cb_categoria5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lbl_totalIngresos;
    private javax.swing.JLabel lbl_totalIngresos1;
    private javax.swing.JLabel lbl_totalIngresos2;
    private javax.swing.JLabel lbl_totalIngresos4;
    private static javax.swing.JTable tabla_productos1;
    private javax.swing.JTextField tf_busqueda;
    private javax.swing.JTextField tf_codigoProducto5;
    private javax.swing.JTextField tf_nombre5;
    private javax.swing.JTextField tf_precio5;
    private javax.swing.JTextField tf_stock5;
    // End of variables declaration//GEN-END:variables
}
