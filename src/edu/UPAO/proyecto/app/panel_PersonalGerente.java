/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package edu.UPAO.proyecto.app;

import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import BaseDatos.Conexion;

public class panel_PersonalGerente extends javax.swing.JPanel {

    /**
     * Creates new form panel_ComprasGerente
     */
    public panel_PersonalGerente() {
        initComponents();
    }
 private void cargarEmpleadosEnTabla() {
    DefaultTableModel modelo = (DefaultTableModel) tablaEmpleados.getModel();
    modelo.setRowCount(0);

    String sql = "SELECT e.id_empleado, p.nombres, p.apellidos, p.dni, " +
                 "       p.telefono, p.correo, s.nombre AS tienda, " +
                 "       e.rol, e.estado, e.sueldo " +
                 "FROM empleado e " +
                 "JOIN persona p  ON e.dni = p.dni " +
                 "JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                 "WHERE e.rol <> 'GERENTE' " +
                 "ORDER BY p.apellidos, p.nombres";

    try (Connection cn = new Conexion().establecerConexion();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getString("id_empleado"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("tienda"),
                rs.getString("rol"),
                rs.getDouble("sueldo"),   // si quieres mostrarlo
                rs.getString("estado")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    tablaEmpleados.setRowHeight(24);
}


    private String generarNuevoIdEmpleado(String rol) {
        String prefijo;
        switch (rol.toUpperCase()) {
            case "ADMINISTRADOR":
                prefijo = "11";
                break;
            case "CAJERO":
                prefijo = "12";
                break;
            default:
                prefijo = "19"; // por si luego agregas otros roles
        }

        String sql = "SELECT MAX(id_empleado) AS max_id "
                + "FROM empleado WHERE rol = ?";

        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("max_id") != null) {
                    int actual = Integer.parseInt(rs.getString("max_id"));
                    int nuevo = actual + 1;
                    return String.format("%08d", nuevo);
                } else {
                    // primer empleado de ese rol
                    int base = Integer.parseInt(prefijo + "000001");
                    return String.format("%08d", base);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // fallback por si algo falla
            int base = Integer.parseInt(prefijo + "000001");
            return String.format("%08d", base);
        }
    }

    private int obtenerIdSucursalPorNombre(String nombreSucursal) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre = ?";
        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_sucursal");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // fallback: sucursal 1
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabControlPersonal = new javax.swing.JTabbedPane();
        ASISTENCIAS = new javax.swing.JPanel();
        panelTop = new javax.swing.JPanel();
        cbTienda = new javax.swing.JComboBox<>();
        btnFecha = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        lblListaEmpleados = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        cbFiltroEstado = new javax.swing.JComboBox<>();
        panelCenter = new javax.swing.JPanel();
        panelKpis = new javax.swing.JPanel();
        panelKpiTotal = new javax.swing.JPanel();
        lblTotalEmpleadosTitulo = new javax.swing.JLabel();
        lblTtotalEmpleadosValor = new javax.swing.JLabel();
        panelKpiPresentes = new javax.swing.JPanel();
        lblPresentesTitulo = new javax.swing.JLabel();
        lblPresentesValor = new javax.swing.JLabel();
        panelKpiTardanzas = new javax.swing.JPanel();
        lblTardanzasTitulo = new javax.swing.JLabel();
        lblTardanzasValor = new javax.swing.JLabel();
        panelKpiAusencias = new javax.swing.JPanel();
        lblAusenciasTitulo = new javax.swing.JLabel();
        lblAusenciasValor = new javax.swing.JLabel();
        jScrollPaneAsis = new javax.swing.JScrollPane();
        tablaAsistencias = new javax.swing.JTable();
        NOMINA_EMPLEADOS = new javax.swing.JPanel();
        REGISTRAR_PERSONAL = new javax.swing.JPanel();
        panelForm = new javax.swing.JPanel();
        lblNombres = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        lblApellidos = new javax.swing.JLabel();
        txtApellidos = new javax.swing.JTextField();
        lblDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblCorreo = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        lblIdEmpleado = new javax.swing.JLabel();
        txtIdEmpleado = new javax.swing.JTextField();
        lblTienda = new javax.swing.JLabel();
        cbTiendaP = new javax.swing.JComboBox<>();
        lblCargo = new javax.swing.JLabel();
        cbCargo = new javax.swing.JComboBox<>();
        lblEstado = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        lblSueldo = new javax.swing.JLabel();
        txtSueldo = new javax.swing.JTextField();
        btnLimpiar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        panelLista = new javax.swing.JPanel();
        panelListaTop = new javax.swing.JPanel();
        lblListaEmpleadosP = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        scrollEmpleados = new javax.swing.JScrollPane();
        tablaEmpleados = new javax.swing.JTable();

        ASISTENCIAS.setLayout(new java.awt.BorderLayout());

        panelTop.setLayout(new java.awt.GridBagLayout());

        cbTienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbTienda.setPreferredSize(new java.awt.Dimension(150, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        panelTop.add(cbTienda, gridBagConstraints);

        btnFecha.setText("FECHA");
        btnFecha.setPreferredSize(new java.awt.Dimension(150, 30));
        panelTop.add(btnFecha, new java.awt.GridBagConstraints());

        btnExportar.setText("EXPORTAR");
        btnExportar.setPreferredSize(new java.awt.Dimension(150, 30));
        panelTop.add(btnExportar, new java.awt.GridBagConstraints());

        lblListaEmpleados.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblListaEmpleados.setText("Lista de Empleados");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        panelTop.add(lblListaEmpleados, gridBagConstraints);

        txtBuscar.setText("Buscar por Nombre o ID...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        panelTop.add(txtBuscar, gridBagConstraints);

        cbFiltroEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ver Todos", "Presentes", "Tardanzas", "Ausentes" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        panelTop.add(cbFiltroEstado, gridBagConstraints);

        ASISTENCIAS.add(panelTop, java.awt.BorderLayout.NORTH);

        panelCenter.setBackground(new java.awt.Color(255, 255, 255));
        panelCenter.setLayout(new java.awt.BorderLayout());

        panelKpis.setPreferredSize(new java.awt.Dimension(200, 592));
        panelKpis.setLayout(new javax.swing.BoxLayout(panelKpis, javax.swing.BoxLayout.Y_AXIS));

        panelKpiTotal.setBackground(new java.awt.Color(0, 102, 204));
        panelKpiTotal.setPreferredSize(new java.awt.Dimension(180, 80));
        panelKpiTotal.setLayout(new java.awt.BorderLayout());

        lblTotalEmpleadosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalEmpleadosTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEmpleadosTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalEmpleadosTitulo.setText("TOTAL DE EMPLEADOS");
        panelKpiTotal.add(lblTotalEmpleadosTitulo, java.awt.BorderLayout.NORTH);

        lblTtotalEmpleadosValor.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTtotalEmpleadosValor.setForeground(new java.awt.Color(255, 255, 255));
        lblTtotalEmpleadosValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTtotalEmpleadosValor.setText("jLabel2");
        panelKpiTotal.add(lblTtotalEmpleadosValor, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelKpiTotal);

        panelKpiPresentes.setBackground(new java.awt.Color(95, 173, 75));
        panelKpiPresentes.setPreferredSize(new java.awt.Dimension(180, 80));
        panelKpiPresentes.setLayout(new java.awt.BorderLayout());

        lblPresentesTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPresentesTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblPresentesTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPresentesTitulo.setText("PRESENTES");
        panelKpiPresentes.add(lblPresentesTitulo, java.awt.BorderLayout.NORTH);

        lblPresentesValor.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPresentesValor.setForeground(new java.awt.Color(255, 255, 255));
        lblPresentesValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPresentesValor.setText("jLabel3");
        panelKpiPresentes.add(lblPresentesValor, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelKpiPresentes);

        panelKpiTardanzas.setBackground(new java.awt.Color(247, 120, 48));
        panelKpiTardanzas.setPreferredSize(new java.awt.Dimension(180, 80));
        panelKpiTardanzas.setLayout(new java.awt.BorderLayout());

        lblTardanzasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTardanzasTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTardanzasTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTardanzasTitulo.setText("TARDANZAS");
        panelKpiTardanzas.add(lblTardanzasTitulo, java.awt.BorderLayout.PAGE_START);

        lblTardanzasValor.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTardanzasValor.setForeground(new java.awt.Color(255, 255, 255));
        lblTardanzasValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTardanzasValor.setText("jLabel2");
        panelKpiTardanzas.add(lblTardanzasValor, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelKpiTardanzas);

        panelKpiAusencias.setBackground(new java.awt.Color(195, 32, 22));
        panelKpiAusencias.setPreferredSize(new java.awt.Dimension(180, 80));
        panelKpiAusencias.setLayout(new java.awt.BorderLayout());

        lblAusenciasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAusenciasTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblAusenciasTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAusenciasTitulo.setText("AUSENCIAS");
        panelKpiAusencias.add(lblAusenciasTitulo, java.awt.BorderLayout.PAGE_START);

        lblAusenciasValor.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblAusenciasValor.setForeground(new java.awt.Color(255, 255, 255));
        lblAusenciasValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAusenciasValor.setText("jLabel2");
        panelKpiAusencias.add(lblAusenciasValor, java.awt.BorderLayout.CENTER);

        panelKpis.add(panelKpiAusencias);

        panelCenter.add(panelKpis, java.awt.BorderLayout.EAST);

        tablaAsistencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre y Cargo", "Turno", "Entrada", "Salida", "Estado"
            }
        ));
        jScrollPaneAsis.setViewportView(tablaAsistencias);

        panelCenter.add(jScrollPaneAsis, java.awt.BorderLayout.CENTER);

        ASISTENCIAS.add(panelCenter, java.awt.BorderLayout.CENTER);

        tabControlPersonal.addTab("ASISTENCIAS", ASISTENCIAS);

        javax.swing.GroupLayout NOMINA_EMPLEADOSLayout = new javax.swing.GroupLayout(NOMINA_EMPLEADOS);
        NOMINA_EMPLEADOS.setLayout(NOMINA_EMPLEADOSLayout);
        NOMINA_EMPLEADOSLayout.setHorizontalGroup(
            NOMINA_EMPLEADOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1228, Short.MAX_VALUE)
        );
        NOMINA_EMPLEADOSLayout.setVerticalGroup(
            NOMINA_EMPLEADOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 649, Short.MAX_VALUE)
        );

        tabControlPersonal.addTab("NOMINA DE EMPLEADOS", NOMINA_EMPLEADOS);

        REGISTRAR_PERSONAL.setLayout(new java.awt.BorderLayout());

        panelForm.setBackground(new java.awt.Color(255, 255, 255));
        panelForm.setLayout(new java.awt.GridBagLayout());

        lblNombres.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNombres.setForeground(new java.awt.Color(0, 0, 0));
        lblNombres.setText("NOMBRES:");
        panelForm.add(lblNombres, new java.awt.GridBagConstraints());
        panelForm.add(txtNombres, new java.awt.GridBagConstraints());

        lblApellidos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblApellidos.setForeground(new java.awt.Color(0, 0, 0));
        lblApellidos.setText("APELLIDOS:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panelForm.add(lblApellidos, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        panelForm.add(txtApellidos, gridBagConstraints);

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDni.setForeground(new java.awt.Color(0, 0, 0));
        lblDni.setText("DNI:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        panelForm.add(lblDni, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        panelForm.add(txtDni, gridBagConstraints);

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(0, 0, 0));
        lblTelefono.setText("TELEFONO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        panelForm.add(lblTelefono, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        panelForm.add(txtTelefono, gridBagConstraints);

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(0, 0, 0));
        lblCorreo.setText("CORREO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        panelForm.add(lblCorreo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        panelForm.add(txtCorreo, gridBagConstraints);

        lblIdEmpleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblIdEmpleado.setForeground(new java.awt.Color(0, 0, 0));
        lblIdEmpleado.setText("ID:");
        panelForm.add(lblIdEmpleado, new java.awt.GridBagConstraints());

        txtIdEmpleado.setEditable(false);
        panelForm.add(txtIdEmpleado, new java.awt.GridBagConstraints());

        lblTienda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienda.setForeground(new java.awt.Color(0, 0, 0));
        lblTienda.setText("TIENDA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        panelForm.add(lblTienda, gridBagConstraints);

        cbTiendaP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        panelForm.add(cbTiendaP, gridBagConstraints);

        lblCargo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCargo.setForeground(new java.awt.Color(0, 0, 0));
        lblCargo.setText("CARGO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        panelForm.add(lblCargo, gridBagConstraints);

        cbCargo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CAJERO", "ADMINISTRADOR" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        panelForm.add(cbCargo, gridBagConstraints);

        lblEstado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEstado.setForeground(new java.awt.Color(0, 0, 0));
        lblEstado.setText("ESTADO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        panelForm.add(lblEstado, gridBagConstraints);

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "INACTIVO", " " }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        panelForm.add(cbEstado, gridBagConstraints);

        lblSueldo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSueldo.setForeground(new java.awt.Color(0, 0, 0));
        lblSueldo.setText("SUELDO:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        panelForm.add(lblSueldo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        panelForm.add(txtSueldo, gridBagConstraints);

        btnLimpiar.setText("LIMPIAR");
        panelForm.add(btnLimpiar, new java.awt.GridBagConstraints());

        btnActualizar.setText("ACTUALIZAR");
        panelForm.add(btnActualizar, new java.awt.GridBagConstraints());

        btnAgregar.setText("AGREGAR");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        panelForm.add(btnAgregar, new java.awt.GridBagConstraints());

        REGISTRAR_PERSONAL.add(panelForm, java.awt.BorderLayout.PAGE_START);

        panelLista.setLayout(new java.awt.BorderLayout());

        panelListaTop.setLayout(new java.awt.GridBagLayout());

        lblListaEmpleadosP.setText("Lista de Empleados");
        panelListaTop.add(lblListaEmpleadosP, new java.awt.GridBagConstraints());

        jTextField1.setText("Buscar Por Nombre o ID...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panelListaTop.add(jTextField1, gridBagConstraints);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ver Todos", "Activos", "Inactivos", " " }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panelListaTop.add(jComboBox1, gridBagConstraints);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        panelListaTop.add(jComboBox2, gridBagConstraints);

        jButton1.setText("EXPORTAR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        panelListaTop.add(jButton1, gridBagConstraints);

        panelLista.add(panelListaTop, java.awt.BorderLayout.PAGE_START);

        tablaEmpleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombres", "Apellidos", "DNI", "Telefono", "Correo", "Tienda", "Cargo", "Sueldo", "Estado"
            }
        ));
        scrollEmpleados.setViewportView(tablaEmpleados);

        panelLista.add(scrollEmpleados, java.awt.BorderLayout.CENTER);

        REGISTRAR_PERSONAL.add(panelLista, java.awt.BorderLayout.CENTER);

        tabControlPersonal.addTab("REGISTRAR_PERSONAL", REGISTRAR_PERSONAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabControlPersonal)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabControlPersonal, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
          String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        String rol = cbCargo.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().toString();
        String sucursalNombre = cbTienda.getSelectedItem().toString();
        int idSucursal = obtenerIdSucursalPorNombre(sucursalNombre);

        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Completa al menos Nombres, Apellidos y DNI",
                    "Datos incompletos",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idEmpleadoNuevo = generarNuevoIdEmpleado(rol);

        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false);

            // 1) PERSONA (si no existe, la creamos)
            String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, telefono, correo, estado) "
                    + "VALUES (?, ?, ?, ?, ?, 'ACTIVO') "
                    + "ON DUPLICATE KEY UPDATE "
                    + "nombres = VALUES(nombres), "
                    + "apellidos = VALUES(apellidos), "
                    + "telefono = VALUES(telefono), "
                    + "correo = VALUES(correo)";
            try (PreparedStatement ps = cn.prepareStatement(sqlPersona)) {
                ps.setString(1, dni);
                ps.setString(2, nombres);
                ps.setString(3, apellidos);
                ps.setString(4, telefono);
                ps.setString(5, correo);
                ps.executeUpdate();
            }

            // =================================================
// LEER SUELDO DEL TXT
// =================================================
            String sueldoTexto = txtSueldo.getText().trim();
            double sueldo = (sueldoTexto.isEmpty())
                    ? 0.0
                    : Double.parseDouble(sueldoTexto);

// =================================================
// INSERT EMPLEADO
// =================================================
            String sqlEmpleado = "INSERT INTO empleado "
                    + "(id_empleado, dni, id_sucursal, rol, estado, sueldo, horario) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = cn.prepareStatement(sqlEmpleado)) {
                ps.setString(1, idEmpleadoNuevo);
                ps.setString(2, dni);
                ps.setInt(3, idSucursal);
                ps.setString(4, rol);
                ps.setString(5, estado);
                ps.setDouble(6, sueldo);       // ← YA NO MARCA ERROR
                ps.setString(7, "MAÑANA");
                ps.executeUpdate();
            }

            cn.commit();

            // Mostrar ID generado en el formulario
            txtIdEmpleado.setText(idEmpleadoNuevo);

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Empleado registrado correctamente",
                    "Éxito",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // recargar tabla
            cargarEmpleadosEnTabla();

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al registrar empleado. Revisa la consola.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        String sueldoTxt = txtSueldo.getText().trim();
        double sueldo = sueldoTxt.isEmpty() ? 0.0 : Double.parseDouble(sueldoTxt);

    }//GEN-LAST:event_btnAgregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ASISTENCIAS;
    private javax.swing.JPanel NOMINA_EMPLEADOS;
    private javax.swing.JPanel REGISTRAR_PERSONAL;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cbCargo;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<String> cbFiltroEstado;
    private javax.swing.JComboBox<String> cbTienda;
    private javax.swing.JComboBox<String> cbTiendaP;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JScrollPane jScrollPaneAsis;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblApellidos;
    private javax.swing.JLabel lblAusenciasTitulo;
    private javax.swing.JLabel lblAusenciasValor;
    private javax.swing.JLabel lblCargo;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblIdEmpleado;
    private javax.swing.JLabel lblListaEmpleados;
    private javax.swing.JLabel lblListaEmpleadosP;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblPresentesTitulo;
    private javax.swing.JLabel lblPresentesValor;
    private javax.swing.JLabel lblSueldo;
    private javax.swing.JLabel lblTardanzasTitulo;
    private javax.swing.JLabel lblTardanzasValor;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTienda;
    private javax.swing.JLabel lblTotalEmpleadosTitulo;
    private javax.swing.JLabel lblTtotalEmpleadosValor;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelForm;
    private javax.swing.JPanel panelKpiAusencias;
    private javax.swing.JPanel panelKpiPresentes;
    private javax.swing.JPanel panelKpiTardanzas;
    private javax.swing.JPanel panelKpiTotal;
    private javax.swing.JPanel panelKpis;
    private javax.swing.JPanel panelLista;
    private javax.swing.JPanel panelListaTop;
    private javax.swing.JPanel panelTop;
    private javax.swing.JScrollPane scrollEmpleados;
    private javax.swing.JTabbedPane tabControlPersonal;
    private javax.swing.JTable tablaAsistencias;
    private javax.swing.JTable tablaEmpleados;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtIdEmpleado;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtSueldo;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
