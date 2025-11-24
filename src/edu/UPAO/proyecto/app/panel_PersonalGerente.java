package edu.UPAO.proyecto.app;

import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import BaseDatos.Conexion;
import edu.UPAO.proyecto.DAO.EmpleadoDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import edu.UPAO.proyecto.Modelo.Empleado;
import java.util.ArrayList;
import java.util.List;
import edu.UPAO.proyecto.modelo.Sucursal;
import java.awt.Dimension;

public class panel_PersonalGerente extends javax.swing.JPanel {

    public panel_PersonalGerente() {
        initComponents();
        configurarComboTurno();
        cargarEmpleadosEnTabla();
        cargarCombosSucursales();
        activarBusqueda();
        activarFiltrosAvanzados();
        configurarTabAsistencias();
        //Evento para llenar formulario al hacer clic en la tabla (Para editar)
        tablaEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                llenarFormularioDesdeTabla();
            }
        });
        
jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.removeAll(); // Limpiar lo que haya
        jPanel1.add(new PanelNominaGerente(), java.awt.BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
        
        
        
        // Evento DNI (Enter)
        txtDni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                // Si presiona ENTER (Código 10)
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String dni = txtDni.getText().trim();
                    if (!dni.isEmpty()) {
                        EmpleadoDAO dao = new EmpleadoDAO();
                        // Buscamos si la persona existe
                        Empleado persona = dao.buscarPersonaPorDni(dni);

                        if (persona != null) {
                            // ¡EXISTE! Llenamos los campos automáticamente
                            txtNombres.setText(persona.getNombres());
                            txtApellidos.setText(persona.getApellidos());
                            txtTelefono.setText(persona.getTelefono());
                            txtCorreo.setText(persona.getCorreo());

                            javax.swing.JOptionPane.showMessageDialog(null,
                                    "Persona encontrada. Complete el Cargo y Sueldo para registrarlo como Empleado.");

                            // Opcional: Bloquear nombre y apellido para no editarlos por error
                            // txtNombres.setEditable(false);
                            // txtApellidos.setEditable(false);
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(null, "Persona nueva. Ingrese los datos manualmente.");
                        }
                    }
                }
            }
        });

    }

    private void configurarTabAsistencias() {
        // 1. Configurar columnas de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Empleado");
        modelo.addColumn("Nombres");
        modelo.addColumn("Cargo");
        modelo.addColumn("Tienda");
        modelo.addColumn("Entrada");
        modelo.addColumn("Salida");
        modelo.addColumn("Estado");

        tablaAsistencias.setModel(modelo);

        // 2. Cargar Combo de Tiendas (Filtro)
        cargarComboFiltroTienda();

        // 3. Cargar datos iniciales (Todas)
        cargarTablaAsistencias(null);

        // 4. Evento del Filtro
        cbTienda.addActionListener(e -> {
            String seleccion = (String) cbTienda.getSelectedItem();
            cargarTablaAsistencias(seleccion);
        });

        // 5. Botón Actualizar/Fecha (Opcional, para refrescar)
        if (btnFecha != null) {
            btnFecha.addActionListener(e -> cargarTablaAsistencias((String) cbTienda.getSelectedItem()));
        }
    }

    private void cargarComboFiltroTienda() {
        if (cbTienda == null) {
            return;
        }

        cbTienda.removeAllItems();
        cbTienda.addItem("Todas las Sucursales");

        edu.UPAO.proyecto.DAO.SucursalDAO sucDao = new edu.UPAO.proyecto.DAO.SucursalDAO();
        List<String> tiendas = sucDao.obtenerSucursalesActivas(); // Asegúrate que este método devuelva lista de nombres

        for (String t : tiendas) {
            cbTienda.addItem(t);
        }
    }



    private void cargarTablaAsistencias(String filtroTienda) {
        DefaultTableModel modelo = (DefaultTableModel) tablaAsistencias.getModel();
        modelo.setRowCount(0); // Limpiar

        edu.UPAO.proyecto.DAO.AsistenciaDAO asisDao = new edu.UPAO.proyecto.DAO.AsistenciaDAO();

        // Si es nulo o vacío, el DAO interpretará "Todas"
        List<Object[]> datos = asisDao.listarAsistenciasDetalladas(filtroTienda);

        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }

    private void configurarComboTurno() {
        if (cb_turno != null) {
            cb_turno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"MAÑANA", "TARDE", "NOCHE"}));
        }
    }

    private void cargarEmpleadosEnTabla() {
        // 1. Configurar el modelo de la tabla
        DefaultTableModel modelo = (DefaultTableModel) tablaEmpleados.getModel();
        modelo.setRowCount(0); // Limpiar tabla antes de cargar

        // 2. Llamar al DAO
        EmpleadoDAO dao = new EmpleadoDAO();
        List<Empleado> lista = dao.listarEmpleadosDetallado();

        // 3. Llenar la tabla con el orden CORRECTO que tienes en tu diseño
        for (Empleado e : lista) {
            Object[] fila = new Object[10];

            fila[0] = e.getIdEmpleado();
            fila[1] = e.getNombres();
            fila[2] = e.getApellidos();
            fila[3] = e.getDni();

            fila[4] = e.getTelefono();
            fila[5] = e.getCorreo();
            fila[6] = e.getNombreSucursal();
            fila[7] = e.getCargo();
            fila[8] = e.getSueldo();
            fila[9] = e.getEstado();
            modelo.addRow(fila);
        }

        tablaEmpleados.setModel(modelo);
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

    private void cargarCombosSucursales() {
        SucursalDAO sucursalDAO = new SucursalDAO();
        List<Sucursal> listaSucursales = sucursalDAO.listar(); // Asegúrate que tu DAO tenga este método

        // Limpiamos y cargamos el combo del FORMULARIO
        cbTiendaP.removeAllItems();

        // Limpiamos y cargamos el combo del FILTRO (jComboBox2)
        jComboBox2.removeAllItems();
        jComboBox2.addItem("Todas las Sucursales"); // Opción extra para el filtro

        for (Sucursal s : listaSucursales) {
            String nombre = s.getNombre(); // O s.getNombreSucursal() según tu modelo
            cbTiendaP.addItem(nombre);
            jComboBox2.addItem(nombre);
        }
    }

    private void activarFiltrosAvanzados() {
        DefaultTableModel modelo = (DefaultTableModel) tablaEmpleados.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tablaEmpleados.setRowSorter(sorter);

        // Lógica que se ejecuta cada vez que tocas algo
        Runnable filtrar = () -> {
            List<RowFilter<Object, Object>> filtros = new ArrayList<>();

            // 1. Filtro por TEXTO (jTextField1) - Busca en Nombre (col 1) o DNI (col 3)
            String texto = jTextField1.getText().trim();
            if (!texto.isEmpty()) {
                filtros.add(RowFilter.regexFilter("(?i)" + texto));
            }

            // 2. Filtro por ESTADO (jComboBox1) - Columna 9
            String estadoSeleccionado = (String) jComboBox1.getSelectedItem();
            if (estadoSeleccionado != null && !estadoSeleccionado.equals("Ver Todos") && !estadoSeleccionado.trim().isEmpty()) {
                // "Activos" -> "ACTIVO" (Ajusta según cómo lo guardes en BD)
                String estadoBusqueda = estadoSeleccionado.toUpperCase().startsWith("ACT") ? "ACTIVO" : "INACTIVO";
                filtros.add(RowFilter.regexFilter(estadoBusqueda, 9));
            }

            // 3. Filtro por SUCURSAL (jComboBox2) - Columna 6
            String tiendaSeleccionada = (String) jComboBox2.getSelectedItem();
            if (tiendaSeleccionada != null && !tiendaSeleccionada.equals("Todas las Sucursales")) {
                filtros.add(RowFilter.regexFilter(tiendaSeleccionada, 6));
            }

            // Aplicar todos los filtros juntos
            if (filtros.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filtros));
            }
        };

        // Agregar los "Escuchadores" (Listeners) a los componentes
        jTextField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrar.run();
            }
        });

        jComboBox1.addActionListener(e -> filtrar.run());
        jComboBox2.addActionListener(e -> filtrar.run());
    }

    private int obtenerIdSucursalPorNombre(String nombreSucursal) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre_sucursal = ?";

        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_sucursal");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando ID sucursal: " + e.getMessage());
        }
        // Si falla, devuelve 1 (Por eso se te ponía siempre en Tienda Central)
        return 1;
    }

    private void activarBusqueda() {
        // Creamos el clasificador de filas basado en el modelo de la tabla
        DefaultTableModel modelo = (DefaultTableModel) tablaEmpleados.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tablaEmpleados.setRowSorter(sorter);

        // Añadimos el evento al campo de texto txtBuscar
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = txtBuscar.getText();
                if (texto.trim().length() == 0) {
                    sorter.setRowFilter(null); // Si está vacío, muestra todo
                } else {
                    // Filtra sin importar mayúsculas/minúsculas ("(?i)")
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });
    }

    private void llenarFormularioDesdeTabla() {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila >= 0) {
            txtIdEmpleado.setText(tablaEmpleados.getValueAt(fila, 0).toString());
            txtNombres.setText(tablaEmpleados.getValueAt(fila, 1).toString());
            txtApellidos.setText(tablaEmpleados.getValueAt(fila, 2).toString());
            txtDni.setText(tablaEmpleados.getValueAt(fila, 3).toString());
            txtTelefono.setText(tablaEmpleados.getValueAt(fila, 4).toString());
            txtCorreo.setText(tablaEmpleados.getValueAt(fila, 5).toString());

            cbTiendaP.setSelectedItem(tablaEmpleados.getValueAt(fila, 6).toString());
            cbCargo.setSelectedItem(tablaEmpleados.getValueAt(fila, 7).toString());
            txtSueldo.setText(tablaEmpleados.getValueAt(fila, 8).toString());
            cbEstado.setSelectedItem(tablaEmpleados.getValueAt(fila, 9).toString());

            // ✅ NUEVO: Seleccionar el turno correcto
            // Asumiendo que agregaste el horario en la columna 10 al cargar la tabla
            if (tablaEmpleados.getColumnCount() > 10) {
                cb_turno.setSelectedItem(tablaEmpleados.getValueAt(fila, 10).toString());
            }
        }
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
        jPanel1 = new javax.swing.JPanel();
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
        lblTienda1 = new javax.swing.JLabel();
        cb_turno = new javax.swing.JComboBox<>();
        panelLista = new javax.swing.JPanel();
        panelListaTop = new javax.swing.JPanel();
        scrollEmpleados = new javax.swing.JScrollPane();
        tablaEmpleados = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        lblListaEmpleadosP = new javax.swing.JLabel();
        btn_horarios = new javax.swing.JButton();

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

        txtBuscar.setMinimumSize(new java.awt.Dimension(15, 100));
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
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

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout NOMINA_EMPLEADOSLayout = new javax.swing.GroupLayout(NOMINA_EMPLEADOS);
        NOMINA_EMPLEADOS.setLayout(NOMINA_EMPLEADOSLayout);
        NOMINA_EMPLEADOSLayout.setHorizontalGroup(
            NOMINA_EMPLEADOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        NOMINA_EMPLEADOSLayout.setVerticalGroup(
            NOMINA_EMPLEADOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabControlPersonal.addTab("NOMINA DE EMPLEADOS", NOMINA_EMPLEADOS);

        REGISTRAR_PERSONAL.setLayout(new java.awt.BorderLayout());

        panelForm.setBackground(new java.awt.Color(255, 255, 255));
        panelForm.setPreferredSize(new java.awt.Dimension(874, 190));

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
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnAgregar.setText("AGREGAR");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        lblTienda1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienda1.setForeground(new java.awt.Color(0, 0, 0));
        lblTienda1.setText("TURNO:");

        cb_turno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout panelFormLayout = new javax.swing.GroupLayout(panelForm);
        panelForm.setLayout(panelFormLayout);
        panelFormLayout.setHorizontalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormLayout.createSequentialGroup()
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblNombres)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFormLayout.createSequentialGroup()
                                        .addGap(286, 286, 286)
                                        .addComponent(lblDni))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblTelefono, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblCorreo, javax.swing.GroupLayout.Alignment.TRAILING))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cbCargo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbEstado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSueldo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelFormLayout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(lblTienda1)
                                        .addGap(18, 18, 18)
                                        .addComponent(cb_turno, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFormLayout.createSequentialGroup()
                                        .addComponent(lblTienda)
                                        .addGap(16, 16, 16)
                                        .addComponent(cbTiendaP, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(27, 27, 27)
                                .addComponent(lblIdEmpleado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(48, 48, 48))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblApellidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(411, 411, 411)))
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 256, Short.MAX_VALUE))
        );
        panelFormLayout.setVerticalGroup(
            panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                        .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNombres))
                            .addGroup(panelFormLayout.createSequentialGroup()
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblIdEmpleado)
                                        .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTienda1)
                                        .addComponent(cb_turno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblApellidos))
                                    .addGroup(panelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTienda)
                                        .addComponent(cbTiendaP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(20, 20, 20)
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
                                .addComponent(txtSueldo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormLayout.createSequentialGroup()
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregar)
                        .addGap(22, 22, 22))))
        );

        REGISTRAR_PERSONAL.add(panelForm, java.awt.BorderLayout.PAGE_START);

        panelLista.setPreferredSize(new java.awt.Dimension(1212, 450));

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

        btn_horarios.setText("Horarios");
        btn_horarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_horariosActionPerformed(evt);
            }
        });

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
                                .addGap(0, 0, 0)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jButton1)
                                .addGap(18, 18, 18)
                                .addComponent(btn_horarios, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(scrollEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(49, 49, 49))
        );
        panelListaLayout.setVerticalGroup(
            panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaLayout.createSequentialGroup()
                .addComponent(panelListaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblListaEmpleadosP)
                .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelListaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1)
                                .addComponent(btn_horarios)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(tabControlPersonal, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // 1. RECOLECCIÓN DE DATOS
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        String rol = cbCargo.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().toString();
        String sucursalNombre = cbTiendaP.getSelectedItem().toString();
        String turnoSeleccionado = cb_turno.getSelectedItem().toString(); // Ej: "MAÑANA"

        // Validar campos vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Faltan datos personales.");
            return;
        }

        int idSucursal = obtenerIdSucursalPorNombre(sucursalNombre);

        if (rol.equalsIgnoreCase("ADMINISTRADOR") && estado.equals("ACTIVO")) {
            EmpleadoDAO daoVal = new EmpleadoDAO();
            if (daoVal.existeAdministradorEnSucursal(idSucursal)) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "⛔ ERROR DE ESTRUCTURA:\n"
                        + "La tienda '" + sucursalNombre + "' YA TIENE un Administrador activo.\n"
                        + "Solo se permite uno por sucursal.\n\n"
                        + "Solución: Desactive al anterior antes de registrar uno nuevo.",
                        "Restricción de Cargo",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // ⛔ DETIENE EL REGISTRO
            }
        }

        // =================================================================
        // 🛑 REGLA DE NEGOCIO: LÍMITE DE CAJEROS (La que ya tenías)
        // =================================================================
        if (rol.equalsIgnoreCase("CAJERO")) {
            // ... (Tu código de validación de 3 cajeros) ...
        }

        // 2. DEFINIR HORAS EXACTAS
        java.sql.Time horaEntrada = null;
        java.sql.Time horaSalida = null;

        if (turnoSeleccionado.contains("MAÑANA")) {
            horaEntrada = java.sql.Time.valueOf("07:00:00");
            horaSalida = java.sql.Time.valueOf("12:00:00");
        } else if (turnoSeleccionado.contains("TARDE")) {
            horaEntrada = java.sql.Time.valueOf("12:00:00");
            horaSalida = java.sql.Time.valueOf("17:00:00");
        } else if (turnoSeleccionado.contains("NOCHE")) {
            horaEntrada = java.sql.Time.valueOf("17:00:00");
            horaSalida = java.sql.Time.valueOf("22:00:00");
        }

        // 3. ✅ VALIDACIÓN DE TURNO SEMANAL (Una sola persona por turno)
        edu.UPAO.proyecto.DAO.HorarioDAO horarioDAO = new edu.UPAO.proyecto.DAO.HorarioDAO();

        if (horaEntrada != null && horaSalida != null) {
            // Usamos el nuevo método que ignora el día
            boolean ocupado = horarioDAO.esTurnoOcupado(idSucursal, horaEntrada.toLocalTime(), horaSalida.toLocalTime());

            if (ocupado) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "⛔ EL TURNO ESTÁ OCUPADO:\n"
                        + "Ya existe un empleado asignado al turno " + turnoSeleccionado + " en esta tienda.\n"
                        + "No se pueden asignar dos personas al mismo horario semanal.",
                        "Conflicto de Turno",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 4. PROCESO DE GUARDADO
        String idEmpleadoNuevo = generarNuevoIdEmpleado(rol);

        try (java.sql.Connection cn = new BaseDatos.Conexion().establecerConexion()) {
            cn.setAutoCommit(false);

            // A) Persona
            String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, telefono, correo, estado) VALUES (?, ?, ?, ?, ?, 'ACTIVO') ON DUPLICATE KEY UPDATE nombres=VALUES(nombres)";
            try (java.sql.PreparedStatement ps = cn.prepareStatement(sqlPersona)) {
                ps.setString(1, dni);
                ps.setString(2, nombres);
                ps.setString(3, apellidos);
                ps.setString(4, telefono);
                ps.setString(5, correo);
                ps.executeUpdate();
            }

            // B) Empleado
            String sqlEmpleado = "INSERT INTO empleado (id_empleado, dni, id_sucursal, rol, estado, sueldo, horario) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (java.sql.PreparedStatement ps = cn.prepareStatement(sqlEmpleado)) {
                ps.setString(1, idEmpleadoNuevo);
                ps.setString(2, dni);
                ps.setInt(3, idSucursal);
                ps.setString(4, rol);
                ps.setString(5, estado);
                double sueldo = Double.parseDouble(txtSueldo.getText().isEmpty() ? "0" : txtSueldo.getText());
                ps.setDouble(6, sueldo);
                ps.setString(7, turnoSeleccionado);
                ps.executeUpdate();
            }

            // C) ✅ INSERTAR HORARIO PARA TODA LA SEMANA (Lunes a Domingo)
            String sqlHorario = "INSERT INTO horario_empleado (id_empleado, dia_semana, hora_entrada, hora_salida) VALUES (?, ?, ?, ?)";
            String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

            try (java.sql.PreparedStatement psH = cn.prepareStatement(sqlHorario)) {
                for (String dia : diasSemana) {
                    psH.setString(1, idEmpleadoNuevo);
                    psH.setString(2, dia); // Inserta Lunes, luego Martes, etc.
                    psH.setTime(3, horaEntrada);
                    psH.setTime(4, horaSalida);
                    psH.addBatch(); // Preparamos lote
                }
                psH.executeBatch(); // Ejecutamos las 7 inserciones de una
            }
            String sqlUsuario = "INSERT INTO usuario (id_empleado, `contraseña`, estado) VALUES (?, ?, 'ACTIVO')";

            try (java.sql.PreparedStatement psU = cn.prepareStatement(sqlUsuario)) {
                psU.setString(1, idEmpleadoNuevo); // Vincula con el empleado

                // Usamos el MISMO ID como contraseña inicial
                psU.setString(2, idEmpleadoNuevo);

                psU.executeUpdate();
            }

            cn.commit();

            txtIdEmpleado.setText(idEmpleadoNuevo);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "¡Registro Exitoso!\n"
                    + "- Empleado creado: " + nombres + "\n"
                    + "- ID Generado: " + idEmpleadoNuevo + "\n"
                    + "- Credenciales de Acceso:\n"
                    + "  Usuario: " + idEmpleadoNuevo + "\n"
                    + "  Clave: " + idEmpleadoNuevo);

            cargarEmpleadosEnTabla();
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error crítico: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void txtIdEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEmpleadoActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtIdEmpleado.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtSueldo.setText("");
        cbTiendaP.setSelectedIndex(0);
        cbCargo.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        txtDni.setEditable(true);
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        String idEmp = txtIdEmpleado.getText();

        if (idEmp.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecciona un empleado de la tabla primero.");
            return;
        }

        // 1. Recolectar datos del formulario
        String correo = txtCorreo.getText();
        String telefono = txtTelefono.getText();
        String sucursalNombre = cbTiendaP.getSelectedItem().toString();
        String cargo = cbCargo.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().toString();

        // Asegúrate que este sea el nombre correcto de tu ComboBox de turno
        String turnoSeleccionado = cb_turno.getSelectedItem().toString();

        double sueldo = Double.parseDouble(txtSueldo.getText().isEmpty() ? "0" : txtSueldo.getText());
        int idSucursal = obtenerIdSucursalPorNombre(sucursalNombre);

        // ============================================================
        // ✅ VALIDACIÓN: Límite de 3 Cajeros (Excluyendo al actual)
        // ============================================================
        if (cargo.equalsIgnoreCase("CAJERO") && estado.equals("ACTIVO")) {
            EmpleadoDAO daoValidacion = new EmpleadoDAO();
            // Verificamos si hay espacio en el turno destino
            int cantidad = daoValidacion.contarCajerosPorTurnoExcluyendo(idSucursal, turnoSeleccionado, idEmp);

            if (cantidad >= 3) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "⚠️ Cupo lleno:\nYa existen 3 cajeros activos en el turno " + turnoSeleccionado + ".\n"
                        + "No se puede mover a este empleado a ese horario.",
                        "Límite Alcanzado", javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // 🛑 Detiene la actualización
            }
        }
        // ============================================================

        // Consultas SQL
        String sqlEmpleado = "UPDATE empleado SET id_sucursal=?, rol=?, estado=?, sueldo=?, horario=? WHERE id_empleado=?";
        String sqlPersona = "UPDATE persona SET correo=?, telefono=? WHERE dni=?";

        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false); // Iniciar Transacción

            // 2. Actualizar datos de EMPLEADO (Incluyendo Turno)
            try (PreparedStatement ps = cn.prepareStatement(sqlEmpleado)) {
                ps.setInt(1, idSucursal);
                ps.setString(2, cargo);
                ps.setString(3, estado);
                ps.setDouble(4, sueldo);
                ps.setString(5, turnoSeleccionado); // ✅ Guardamos el turno seleccionado
                ps.setString(6, idEmp);
                ps.executeUpdate();
            }

            // 3. Actualizar datos de CONTACTO
            try (PreparedStatement ps2 = cn.prepareStatement(sqlPersona)) {
                ps2.setString(1, correo);
                ps2.setString(2, telefono);
                ps2.setString(3, txtDni.getText());
                ps2.executeUpdate();
            }

            cn.commit(); // Confirmar cambios
            javax.swing.JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente.");

            cargarEmpleadosEnTabla(); // Refrescar tabla visual
            btnLimpiarActionPerformed(null); // Limpiar formulario

        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btn_horariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_horariosActionPerformed
        java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
        java.awt.Frame parentFrame = (parentWindow instanceof java.awt.Frame) ? (java.awt.Frame) parentWindow : null;

        // Abrir el visualizador
        DialogoVerHorarios dialog = new DialogoVerHorarios(parentFrame, true);
        dialog.setVisible(true);

    }//GEN-LAST:event_btn_horariosActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ASISTENCIAS;
    private javax.swing.JPanel NOMINA_EMPLEADOS;
    private javax.swing.JPanel REGISTRAR_PERSONAL;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btn_horarios;
    private javax.swing.JComboBox<String> cbCargo;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<String> cbFiltroEstado;
    private javax.swing.JComboBox<String> cbTienda;
    private javax.swing.JComboBox<String> cbTiendaP;
    private javax.swing.JComboBox<String> cb_turno;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JLabel lblTienda1;
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
