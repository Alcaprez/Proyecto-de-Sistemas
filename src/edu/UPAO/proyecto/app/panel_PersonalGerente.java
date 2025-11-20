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
        scrollEmpleados = new javax.swing.JScrollPane();
        tablaEmpleados = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        lblListaEmpleadosP = new javax.swing.JLabel();

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
            .addGap(0, 1212, Short.MAX_VALUE)
        );
        NOMINA_EMPLEADOSLayout.setVerticalGroup(
            NOMINA_EMPLEADOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 658, Short.MAX_VALUE)
        );

        tabControlPersonal.addTab("NOMINA DE EMPLEADOS", NOMINA_EMPLEADOS);

        REGISTRAR_PERSONAL.setLayout(new java.awt.BorderLayout());

        panelForm.setBackground(new java.awt.Color(255, 255, 255));
        panelForm.setPreferredSize(new java.awt.Dimension(874, 150));

        lblNombres.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNombres.setForeground(new java.awt.Color(0, 0, 0));
        lblNombres.setText("NOMBRES:");

        lblApellidos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblApellidos.setForeground(new java.awt.Color(0, 0, 0));
        lblApellidos.setText("APELLIDOS:");

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDni.setForeground(new java.awt.Color(0, 0, 0));
        lblDni.setText("DNI:");

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(0, 0, 0));
        lblTelefono.setText("TELEFONO:");

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(0, 0, 0));
        lblCorreo.setText("CORREO:");

        lblIdEmpleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblIdEmpleado.setForeground(new java.awt.Color(0, 0, 0));
        lblIdEmpleado.setText("ID:");

        txtIdEmpleado.setEditable(false);
        txtIdEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEmpleadoActionPerformed(evt);
            }
        });

        lblTienda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienda.setForeground(new java.awt.Color(0, 0, 0));
        lblTienda.setText("TIENDA:");

        cbTiendaP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblCargo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCargo.setForeground(new java.awt.Color(0, 0, 0));
        lblCargo.setText("CARGO:");

        cbCargo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CAJERO", "ADMINISTRADOR" }));

        lblEstado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEstado.setForeground(new java.awt.Color(0, 0, 0));
        lblEstado.setText("ESTADO:");

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "INACTIVO", " " }));

        lblSueldo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSueldo.setForeground(new java.awt.Color(0, 0, 0));
        lblSueldo.setText("SUELDO:");

        btnLimpiar.setText("LIMPIAR");

        btnActualizar.setText("ACTUALIZAR");

        btnAgregar.setText("AGREGAR");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFormLayout = new javax.swing.GroupLayout(panelForm);
        panelForm.setLayout(panelFormLayout);
        panelFormLayout.setHorizontalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addGap(249, 249, 249)
                        .addComponent(lblCorreo)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblApellidos, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblNombres, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDni, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTelefono, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lblTienda)
                                .addGap(18, 18, 18)
                                .addComponent(cbTiendaP, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFormLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(lblEstado))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblCargo, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblSueldo, javax.swing.GroupLayout.Alignment.TRAILING))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSueldo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(lblIdEmpleado)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(335, 335, 335)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panelFormLayout.setVerticalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnLimpiar)
                        .addGap(6, 6, 6)
                        .addComponent(btnActualizar)
                        .addGap(6, 6, 6)
                        .addComponent(btnAgregar))
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblApellidos)))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblIdEmpleado)
                                    .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblNombres))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTienda)
                                    .addComponent(cbTiendaP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCorreo)
                                .addComponent(lblSueldo))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelFormLayout.createSequentialGroup()
                                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(cbCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblCargo))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFormLayout.createSequentialGroup()
                                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblDni))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblEstado)
                                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblTelefono))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSueldo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        REGISTRAR_PERSONAL.add(panelForm, java.awt.BorderLayout.PAGE_START);

        panelListaTop.setLayout(new java.awt.GridBagLayout());

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

        jTextField1.setText("Buscar Por Nombre o ID...");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ver Todos", "Activos", "Inactivos", " " }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("EXPORTAR");

        lblListaEmpleadosP.setText("Lista de Empleados");

        javax.swing.GroupLayout panelListaLayout = new javax.swing.GroupLayout(panelLista);
        panelLista.setLayout(panelListaLayout);
        panelListaLayout.setHorizontalGroup(
            panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaLayout.createSequentialGroup()
                .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(1163, 1163, 1163)
                        .addComponent(panelListaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelListaLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(lblListaEmpleadosP))
                            .addGroup(panelListaLayout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton1))
                            .addComponent(scrollEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 1157, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(35, 35, 35))
        );
        panelListaLayout.setVerticalGroup(
            panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaLayout.createSequentialGroup()
                .addComponent(panelListaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblListaEmpleadosP)
                .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

    private void txtIdEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEmpleadoActionPerformed


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
