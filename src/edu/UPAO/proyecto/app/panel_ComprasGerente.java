/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.ProveedorDAO;
import edu.UPAO.proyecto.Modelo.Proveedor;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ALBERTH
 */
public class panel_ComprasGerente extends javax.swing.JPanel {

// Variables de control
    private ProveedorDAO proveedorDAO;
    private String idProveedorSeleccionado = null; // Para saber si editamos, ahora es String (PRxxxxx)
    private int idSucursalActual; // Necesitamos saber en qué sucursal estamos

    /**
     * Creates new form panel_ComprasGerente IMPORTANTE: Ahora recibe el ID de
     * la sucursal actual del gerente logueado.
     */
    public panel_ComprasGerente(int idSucursal) {
        this.idSucursalActual = idSucursal;
        initComponents();

        // Inicialización propia
        proveedorDAO = new ProveedorDAO();
        configurarTablaProveedores();
        cargarProveedoresEnTabla(""); // Carga inicial sin filtro
        limpiarFormulario();
    }

    private void configurarTablaProveedores() {
        DefaultTableModel modelo = new DefaultTableModel();
        // Definimos columnas que coincidan con el modelo
        modelo.addColumn("ID");
        modelo.addColumn("RUC");
        modelo.addColumn("Razón Social");
        modelo.addColumn("DNI Contacto");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Dirección");
        modelo.addColumn("Estado");
        jTable2.setModel(modelo);

        // Ocultar la columna ID visualmente (pero sigue estando en el modelo)
        jTable2.getColumnModel().getColumn(0).setMinWidth(0);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable2.getColumnModel().getColumn(0).setWidth(0);

        // Listener para cuando clickean una fila
        jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable2.getSelectedRow() != -1) {
                llenarFormularioDesdeTabla();
            }
        });
    }

    private void cargarProveedoresEnTabla(String filtro) {
        DefaultTableModel modelo = (DefaultTableModel) jTable2.getModel();
        modelo.setRowCount(0); // Limpiar tabla

        List<Proveedor> lista = proveedorDAO.listar(filtro);

        for (Proveedor p : lista) {
            modelo.addRow(new Object[]{
                p.getIdProveedor(),
                p.getRuc(),
                p.getRazonSocial(),
                p.getDniAsociado(),
                p.getTelefonoContacto(),
                p.getDireccion(),
                p.getEstado()
            });
        }
    }

    private void limpiarFormulario() {
        tf_razonSocial.setText("");
        tf_RUC.setText("");
        tf_dniAsosiado.setText("");
        tf_Direccion.setText("");
        tf_Telefono.setText("");
        jCheckBox1.setSelected(true); // Activo por defecto

        idProveedorSeleccionado = null;
        btn_GuardarActualizar.setText("Guardar"); // Botón en modo Guardar
        jTable2.clearSelection();
        tf_dniAsosiado.setEditable(true); // El DNI se puede editar al crear
        tf_RUC.setEditable(true); // El RUC se puede editar al crear
    }

    private void llenarFormularioDesdeTabla() {
        int fila = jTable2.getSelectedRow();
        if (fila >= 0) {
            // Obtenemos datos del modelo de la tabla
            idProveedorSeleccionado = jTable2.getValueAt(fila, 0).toString();
            tf_RUC.setText(jTable2.getValueAt(fila, 1).toString());
            tf_razonSocial.setText(jTable2.getValueAt(fila, 2).toString());
            tf_dniAsosiado.setText(jTable2.getValueAt(fila, 3).toString());
            tf_Telefono.setText(jTable2.getValueAt(fila, 4).toString());
            tf_Direccion.setText(jTable2.getValueAt(fila, 5).toString());

            String estado = jTable2.getValueAt(fila, 6).toString();
            jCheckBox1.setSelected(estado.equals("ACTIVO"));

            // Cambios visuales para modo Edición
            btn_GuardarActualizar.setText("Actualizar");
            // Por regla de negocio, usualmente no se permite cambiar DNI o RUC al editar,
            // solo datos de contacto o razón social.
            tf_dniAsosiado.setEditable(false);
            tf_RUC.setEditable(false);
        }
    }

    private void guardarOActualizar() {
        // 1. Recolectar y Validar datos
        String razonSocial = tf_razonSocial.getText().trim();
        String ruc = tf_RUC.getText().trim();
        String dni = tf_dniAsosiado.getText().trim();
        String direccion = tf_Direccion.getText().trim();
        String telefono = tf_Telefono.getText().trim();
        String estado = jCheckBox1.isSelected() ? "ACTIVO" : "INACTIVO";

        if (razonSocial.isEmpty() || ruc.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Razón Social, RUC y DNI son obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ruc.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "El RUC debe tener 11 dígitos numéricos.", "Formato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI asociado debe tener 8 dígitos numéricos.", "Formato Incorrecto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Crear objeto Modelo
        Proveedor p = new Proveedor();
        p.setRazonSocial(razonSocial);
        p.setRuc(ruc);
        p.setDniAsociado(dni);
        p.setDireccion(direccion);
        p.setTelefonoContacto(telefono);
        p.setEstado(estado);
        p.setIdSucursal(this.idSucursalActual); // Asignamos la sucursal actual

        // 3. Llamar al DAO
        if (idProveedorSeleccionado == null) {
            // === MODO GUARDAR NUEVO ===
            if (proveedorDAO.existeRuc(ruc)) {
                JOptionPane.showMessageDialog(this, "El RUC ingresado ya está registrado.", "Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (proveedorDAO.guardar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor registrado correctamente.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar proveedor. Verifique los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // === MODO ACTUALIZAR ===
            p.setIdProveedor(idProveedorSeleccionado);

            if (proveedorDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
                limpiarFormulario();
                cargarProveedoresEnTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        ComprasGlobales = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        Devoluciones = new javax.swing.JPanel();
        DevolucionesPorTienda = new javax.swing.JLabel();
        Proveedor = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        DetalleDevolucionesRecientes = new javax.swing.JLabel();
        GraficoBarras = new javax.swing.JPanel();
        GraficoCircular = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        tf_razonSocial = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tf_RUC = new javax.swing.JTextField();
        tf_dniAsosiado = new javax.swing.JTextField();
        tf_Direccion = new javax.swing.JTextField();
        tf_Telefono = new javax.swing.JTextField();
        btn_GuardarActualizar = new javax.swing.JToggleButton();
        btn_Cancerlarlimpiar = new javax.swing.JToggleButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        btn_AbrirHistorial = new javax.swing.JToggleButton();
        btn_Exportar = new javax.swing.JToggleButton();
        tf_buscar = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIENDA" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 664, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout ComprasGlobalesLayout = new javax.swing.GroupLayout(ComprasGlobales);
        ComprasGlobales.setLayout(ComprasGlobalesLayout);
        ComprasGlobalesLayout.setHorizontalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(549, Short.MAX_VALUE))
        );
        ComprasGlobalesLayout.setVerticalGroup(
            ComprasGlobalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComprasGlobalesLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(276, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("COMPRAS GLOBALES", ComprasGlobales);

        DevolucionesPorTienda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        DevolucionesPorTienda.setText("DEVOLUCIONES POR TIENDA:");

        Proveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PROVEEDOR" }));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID PEDIDO", "FECHA", "TIENDA", "PROVEEDOR", "PRODUCTOS", "UNIDADES", "VALOR", "ESTADO"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        DetalleDevolucionesRecientes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        DetalleDevolucionesRecientes.setText("DETALLE DE DEVOLUCIONES RECIENTES:");

        GraficoBarras.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout GraficoBarrasLayout = new javax.swing.GroupLayout(GraficoBarras);
        GraficoBarras.setLayout(GraficoBarrasLayout);
        GraficoBarrasLayout.setHorizontalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 539, Short.MAX_VALUE)
        );
        GraficoBarrasLayout.setVerticalGroup(
            GraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        GraficoCircular.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout GraficoCircularLayout = new javax.swing.GroupLayout(GraficoCircular);
        GraficoCircular.setLayout(GraficoCircularLayout);
        GraficoCircularLayout.setHorizontalGroup(
            GraficoCircularLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 637, Short.MAX_VALUE)
        );
        GraficoCircularLayout.setVerticalGroup(
            GraficoCircularLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout DevolucionesLayout = new javax.swing.GroupLayout(Devoluciones);
        Devoluciones.setLayout(DevolucionesLayout);
        DevolucionesLayout.setHorizontalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DevolucionesLayout.createSequentialGroup()
                        .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DevolucionesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(DetalleDevolucionesRecientes))
                            .addGroup(DevolucionesLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(DevolucionesPorTienda)
                                    .addComponent(Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(DevolucionesLayout.createSequentialGroup()
                                        .addComponent(GraficoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(GraficoCircular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        DevolucionesLayout.setVerticalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DevolucionesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DevolucionesPorTienda)
                .addGap(18, 18, 18)
                .addGroup(DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GraficoCircular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GraficoBarras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(DetalleDevolucionesRecientes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DEVOLUCIONES", Devoluciones);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1228, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("RECEPCION", jPanel4);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));

        jLabel2.setText("Razon social:");

        jLabel3.setText("DNI del asosiado");

        jLabel4.setText("RUC:");

        jLabel5.setText("Telefono:");

        jLabel6.setText("Direccion");

        btn_GuardarActualizar.setText("Guardar/Actualizar");
        btn_GuardarActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActualizarActionPerformed(evt);
            }
        });

        btn_Cancerlarlimpiar.setText("Cancelar");
        btn_Cancerlarlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CancerlarlimpiarActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Activo");

        btn_AbrirHistorial.setText("HISTORIAL");
        btn_AbrirHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirHistorialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_razonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_RUC, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_dniAsosiado, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_GuardarActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_razonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_dniAsosiado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tf_RUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tf_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(tf_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jCheckBox1)
                .addGap(151, 151, 151)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Cancerlarlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_GuardarActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_AbrirHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        btn_Exportar.setText("Exportar");

        btn_buscar.setText("Buscar");
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
                .addGap(22, 22, 22)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tf_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_Exportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("PROVEEDORES", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btn_AbrirHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirHistorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_AbrirHistorialActionPerformed

    private void btn_GuardarActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActualizarActionPerformed
        guardarOActualizar();        // TODO add your handling code here:
    }//GEN-LAST:event_btn_GuardarActualizarActionPerformed

    private void btn_CancerlarlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CancerlarlimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btn_CancerlarlimpiarActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String textoBusqueda = tf_buscar.getText().trim();
        cargarProveedoresEnTabla(textoBusqueda);    }//GEN-LAST:event_btn_buscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ComprasGlobales;
    private javax.swing.JLabel DetalleDevolucionesRecientes;
    private javax.swing.JPanel Devoluciones;
    private javax.swing.JLabel DevolucionesPorTienda;
    private javax.swing.JPanel GraficoBarras;
    private javax.swing.JPanel GraficoCircular;
    private javax.swing.JComboBox<String> Proveedor;
    private javax.swing.JToggleButton btn_AbrirHistorial;
    private javax.swing.JToggleButton btn_Cancerlarlimpiar;
    private javax.swing.JToggleButton btn_Exportar;
    private javax.swing.JToggleButton btn_GuardarActualizar;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField tf_Direccion;
    private javax.swing.JTextField tf_RUC;
    private javax.swing.JTextField tf_Telefono;
    private javax.swing.JTextField tf_buscar;
    private javax.swing.JTextField tf_dniAsosiado;
    private javax.swing.JTextField tf_razonSocial;
    // End of variables declaration//GEN-END:variables
}
