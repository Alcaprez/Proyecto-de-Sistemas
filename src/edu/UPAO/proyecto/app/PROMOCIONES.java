package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.Service.PromocionService;
import edu.UPAO.proyecto.Modelo.Promocion;
import javax.swing.DefaultComboBoxModel;

public class PROMOCIONES extends javax.swing.JFrame {

    private final PromocionService promoService = new PromocionService();
    private final edu.UPAO.proyecto.DAO.ProductoDAO productoDAO = new edu.UPAO.proyecto.DAO.ProductoDAO();

    private final String[] CATEGORIAS_DEF = {
        "Lácteos", "Bebidas", "Snacks", "Panadería", "Limpieza"
    };

    public PROMOCIONES() {
        initComponents();
        cargarTabla();
        cargarAmbito();
        cargarTipos();

        cmbAmbito.addActionListener(e -> cargarSelectorPorAmbito());
        cargarSelectorPorAmbito();
    }

    private void cargarTabla() {
        var lista = promoService.listar();
        var model = (javax.swing.table.DefaultTableModel) tablaPromos.getModel();
        model.setRowCount(0);
        for (var p : lista) {
            model.addRow(new Object[]{
                p.getCodigo(),
                p.getTipo(),
                p.getDescuento(),
                p.getCantidadMinima()
            });
        }
    }

    private void cargarAmbito() {

        cmbAmbito.setModel(new DefaultComboBoxModel<>(new String[]{
            "Producto", "Categoría", "Ticket"
        }));
    }


    private void cargarTipos() {
        cmbTipo.setModel(new DefaultComboBoxModel<>(new String[]{
            "Porcentaje (%)", 
            "Monto fijo (S/)", 
            "2x1", 
            "3x2" 
        }));
    }

    private void cargarSelectorPorAmbito() {
        String ambito = (cmbAmbito.getSelectedItem() != null)
                ? cmbAmbito.getSelectedItem().toString()
                : "Producto";

        switch (ambito) {
            case "Producto":
                cargarProductosEnSelector();
                cmbSelector.setEnabled(true);
                break;

            case "Categoría":
                cargarCategoriasEnSelector();
                cmbSelector.setEnabled(true);
                break;

            case "Ticket":
                // Para Ticket no necesitas seleccionar nada específico
                cmbSelector.setModel(new DefaultComboBoxModel<>(new String[]{"—"}));
                cmbSelector.setEnabled(false);
                break;
        }
    }


    private void cargarProductosEnSelector() {
        try {
            java.util.List<edu.UPAO.proyecto.Modelo.Producto> productos = productoDAO.listar();

            String[] nombres = productos.stream()
                    .map(edu.UPAO.proyecto.Modelo.Producto::getNombre)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .sorted(String::compareToIgnoreCase)
                    .toArray(String[]::new);

            cmbSelector.setModel(new DefaultComboBoxModel<>(nombres.length > 0 ? nombres : new String[]{"(Sin productos)"}));
        } catch (Exception ex) {
            cmbSelector.setModel(new DefaultComboBoxModel<>(new String[]{"(No disponible)"}));
            System.err.println("Error cargando productos: " + ex.getMessage());
        }
    }

    private void cargarCategoriasEnSelector() {
        try {
            java.util.List<edu.UPAO.proyecto.Modelo.Producto> productos = productoDAO.listar();

          
            String[] cats = CATEGORIAS_DEF;

            cmbSelector.setModel(new DefaultComboBoxModel<>(cats.length > 0 ? cats : new String[]{"(Sin categorías)"}));
        } catch (Exception ex) {
            cmbSelector.setModel(new DefaultComboBoxModel<>(CATEGORIAS_DEF));
            System.err.println("Error cargando categorías: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTop = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        lblFrase = new javax.swing.JLabel();
        panelTitle = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelBody = new javax.swing.JPanel();
        panelFiltros = new javax.swing.JPanel();
        lblFiltros = new javax.swing.JLabel();
        cmbTienda = new javax.swing.JComboBox<>();
        cmbEstado = new javax.swing.JComboBox<>();
        btnFecha = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        panelSplit = new javax.swing.JSplitPane();
        panelForm = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblValor = new javax.swing.JLabel();
        spnValor = new javax.swing.JSpinner();
        lblAmbito = new javax.swing.JLabel();
        cmbAmbito = new javax.swing.JComboBox<>();
        lblSelector = new javax.swing.JLabel();
        cmbSelector = new javax.swing.JComboBox<>();
        lblFechaInciio = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        lblFechaFin = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cmbEstado2 = new javax.swing.JComboBox<>();
        tblPromos = new javax.swing.JScrollPane();
        tablaPromos = new javax.swing.JTable();
        panelAcciones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelTop.setLayout(new java.awt.BorderLayout());

        panelHeader.setBackground(new java.awt.Color(255, 153, 0));
        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 16, 0, 16));
        panelHeader.setPreferredSize(new java.awt.Dimension(780, 70));
        panelHeader.setLayout(new java.awt.BorderLayout());
        panelHeader.add(logoLabel, java.awt.BorderLayout.LINE_START);

        btnSalir.setFont(new java.awt.Font("Leelawadee UI", 1, 12)); // NOI18N
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelHeader.add(btnSalir, java.awt.BorderLayout.LINE_END);

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 12)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");
        panelHeader.add(lblFrase, java.awt.BorderLayout.CENTER);

        panelTop.add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelTitle.setBackground(new java.awt.Color(153, 0, 0));
        panelTitle.setPreferredSize(new java.awt.Dimension(780, 40));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("PROMOCIONES");
        panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);

        panelTop.add(panelTitle, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        panelBody.setBackground(new java.awt.Color(191, 237, 237));
        panelBody.setLayout(new java.awt.BorderLayout());

        panelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 8));

        lblFiltros.setText("FILTRAR POR:");
        panelFiltros.add(lblFiltros);

        cmbTienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todas", "Tienda A", "Tienda B", " " }));
        panelFiltros.add(cmbTienda);

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos ", "Activas ", "Inactivas", " " }));
        panelFiltros.add(cmbEstado);

        btnFecha.setText("FECHA");
        panelFiltros.add(btnFecha);

        btnExportar.setText("EXPORTAR");
        panelFiltros.add(btnExportar);

        panelBody.add(panelFiltros, java.awt.BorderLayout.NORTH);

        panelForm.setLayout(new java.awt.GridBagLayout());

        lblCodigo.setText("Codigo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        panelForm.add(lblCodigo, gridBagConstraints);

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(txtCodigo, gridBagConstraints);

        lblNombre.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        panelForm.add(lblNombre, gridBagConstraints);

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(txtNombre, gridBagConstraints);

        lblTipo.setText("Tipo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblTipo, gridBagConstraints);

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Descuento %", "Descuento fijo (S/)", "2 x 1" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(cmbTipo, gridBagConstraints);

        lblValor.setText("Valor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblValor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(spnValor, gridBagConstraints);

        lblAmbito.setText("Ambito:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblAmbito, gridBagConstraints);

        cmbAmbito.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(cmbAmbito, gridBagConstraints);

        lblSelector.setText("Seleccion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblSelector, gridBagConstraints);

        cmbSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Producto", "Categoria", " " }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(cmbSelector, gridBagConstraints);

        lblFechaInciio.setText("Fecha Incio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblFechaInciio, gridBagConstraints);

        txtFechaInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaInicioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(txtFechaInicio, gridBagConstraints);

        lblFechaFin.setText("Fecha Fin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(lblFechaFin, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(txtFechaFin, gridBagConstraints);

        lblEstado.setText("Estado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        panelForm.add(lblEstado, gridBagConstraints);

        cmbEstado2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activar", "Desactivar", " " }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        panelForm.add(cmbEstado2, gridBagConstraints);

        panelSplit.setLeftComponent(panelForm);

        tablaPromos.setAutoCreateRowSorter(true);
        tablaPromos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Tipo", "Valor", "Ambito", "Seleccion", "Inicio", "Fin", "Estado"
            }
        ));
        tablaPromos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPromos.setViewportView(tablaPromos);

        panelSplit.setRightComponent(tblPromos);

        panelBody.add(panelSplit, java.awt.BorderLayout.CENTER);

        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        panelAcciones.add(btnGuardar);

        btnActivar.setText("ACTIVAR");
        panelAcciones.add(btnActivar);

        btnDesactivar.setText("DESACTIVAR");
        panelAcciones.add(btnDesactivar);

        btnLimpiar.setText("LIMPIAR");
        panelAcciones.add(btnLimpiar);

        panelBody.add(panelAcciones, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panelBody, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        Panel_Gerente g = new Panel_Gerente(); // cambia por el nombre de tu JFrame de Gerente
        g.setVisible(true);
        g.setLocationRelativeTo(null); // centrar la ventana en la pantalla

        // Cerrar esta ventana (PERSONAL)
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtFechaInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaInicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaInicioActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String codigo = txtCodigo.getText();
        String tipo = cmbTipo.getSelectedItem().toString();

        double desc = ((Number) spnValor.getValue()).doubleValue(); // ✅ lee del spinner
        int cant = tipo.equals("producto") ? 1 : 1; // puedes agregar campo si necesitas

        Promocion p = new Promocion(codigo, tipo, desc, cant);
        promoService.guardar(p);
        cargarTabla();
    }//GEN-LAST:event_btnGuardarActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PROMOCIONES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PROMOCIONES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PROMOCIONES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PROMOCIONES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PROMOCIONES().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cmbAmbito;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JComboBox<String> cmbEstado2;
    private javax.swing.JComboBox<String> cmbSelector;
    private javax.swing.JComboBox<String> cmbTienda;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel lblAmbito;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaFin;
    private javax.swing.JLabel lblFechaInciio;
    private javax.swing.JLabel lblFiltros;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblSelector;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblValor;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelForm;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JSplitPane panelSplit;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel panelTop;
    private javax.swing.JSpinner spnValor;
    private javax.swing.JTable tablaPromos;
    private javax.swing.JScrollPane tblPromos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
