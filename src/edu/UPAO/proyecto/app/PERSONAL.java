package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.*;  // si usas eventos
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import edu.UPAO.proyecto.Modelo.ResumenDiario;
import edu.UPAO.proyecto.Modelo.Usuario;
import edu.UPAO.proyecto.Service.AsistenciaService;
import edu.UPAO.proyecto.Service.UsuarioService;
import javax.swing.table.DefaultTableModel;
import edu.UPAO.proyecto.Util.AsistenciaTxtRepo;

public class PERSONAL extends javax.swing.JFrame {

    private final edu.UPAO.proyecto.Service.UsuarioService usuarioService = new edu.UPAO.proyecto.Service.UsuarioService();
    private final edu.UPAO.proyecto.Service.AsistenciaService asistenciaService = new edu.UPAO.proyecto.Service.AsistenciaService();
    private java.time.LocalDate fechaActual = java.time.LocalDate.now();
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final AsistenciaTxtRepo repo = new AsistenciaTxtRepo("registros_asistencia.txt");
    private javax.swing.JTable tblEmpleados;

    private java.util.List<ResumenDiario> resumenFull = new ArrayList<>();
    private ResumenTableModel model; // (ver 4.5)
    private java.time.LocalDate fechaSeleccionada = java.time.LocalDate.now();

    public PERSONAL() {
        initComponents();
        // ==== tabla empleados dentro del scroll ====
        tblEmpleados = new javax.swing.JTable();
        tblEmpleados.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Nombre y Cargo", "Turno", "Entrada", "Salida",
                    "Total de Horas", "Tardanza (min)", "Estado"
                }
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        jScrollPane1.setViewportView(tblEmpleados);

// cargar data inicial
        cargarUsuariosGestion();    // <-- método del paso 3

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        centrarTituloBarra();
        centrarKpisHorizontal();
        cargarUsuariosConAsistencia();
        poblarTiendas();                  // NUEVO
        recargarResumen();
        configurarTabla();
        actualizarKPIs(resumenFull);
        wireListeners();
        btnVerTodos.addActionListener(e -> cargarUsuariosGestion());
    }
// Devuelve la JTable correcta (según tu Navigator es jTable1)

    private void centrarTituloBarra() {
        // panelTitle es la franja roja, lblTitulo es “GESTIÓN DE EMPLEADOS”
        panelTitle.setLayout(new java.awt.BorderLayout());
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);
    }
private void cargarDesdeTxt() {
    // 1) usuarios base (sin gerente) desde tu servicio/DAO actual
    java.util.List<edu.UPAO.proyecto.Modelo.Usuario> usuarios =
            usuarioService.listarParaGestion();

    // (opcional) filtros por tienda y buscador aquí, igual que ya haces...

    // 2) leer mapa del TXT para la fecha seleccionada
    var map = repo.leerPorFecha(fechaSeleccionada);

    // 3) pintar la tabla
    var m = (javax.swing.table.DefaultTableModel) tblEmpleados.getModel();
    m.setRowCount(0);

    int presentes = 0, tardanzas = 0;

    for (var u : usuarios) {
        var r = map.get(u.getUsuario() == null ? "" : u.getUsuario().toLowerCase());

        String turno = (u.getHoraEntradaProg()==null || u.getHoraSalidaProg()==null)
                ? "— / —" : (u.getHoraEntradaProg() + " / " + u.getHoraSalidaProg());

        String entrada = (r == null || r.entrada == null) ? "—" : r.entrada.toString();
        String salida  = (r == null || r.salida  == null) ? "—" : r.salida.toString();

        // total horas simple (si hay salida)
        String total = "0 h 0 m";
        if (r != null && r.entrada != null && r.salida != null) {
            long mins = java.time.Duration.between(r.entrada, r.salida).toMinutes();
            if (mins < 0) mins = 0;
            total = (mins/60) + " h " + (mins%60) + " m";
        }

        // tardanza (si hay hora programada y hay entrada)
        long tardMin = 0;
        if (u.getHoraEntradaProg()!=null && r!=null && r.entrada!=null) {
            tardMin = Math.max(0, java.time.Duration
                    .between(u.getHoraEntradaProg(), r.entrada).toMinutes());
            if (tardMin > 0) tardanzas++;
        }

        // presente = marcó ENTRADA y no ha marcado SALIDA (opción más común en tiempo real)
        boolean presente = (r != null && r.entrada != null && (r.salida == null || r.salida.isBefore(r.entrada)==false));
        if (presente) presentes++;

        m.addRow(new Object[]{
                u.getNombreComp() + "\n" + u.getCargo(),
                turno,
                entrada,
                salida,
                total,
                tardMin,
                presente ? "Presente" : "Ausente"
        });
    }

    // 4) KPIs
    int total = usuarios.size();
    int ausentes = total - presentes;
    lblValorTotal.setText(String.valueOf(total));
    lblValorPresentes.setText(String.valueOf(presentes));
    lblValorTardanzas.setText(String.valueOf(tardanzas));
    lblValorAusencias.setText(String.valueOf(ausentes));
}
    private void centrarKpisHorizontal() {
        javax.swing.JLabel[] titulos = {
            lblTituloTotal, lblTituloPresentes, lblTituloTardanzas, lblTituloAusencias
        };
        javax.swing.JLabel[] valores = {
            lblValorTotal, lblValorPresentes, lblValorTardanzas, lblValorAusencias
        };
        for (javax.swing.JLabel l : titulos) {
            l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
        for (javax.swing.JLabel l : valores) {
            l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            l.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        }
    }

    private void cargarUsuariosConAsistencia() {
        var usuarios = usuarioService.listarParaGestion();
        var m = (javax.swing.table.DefaultTableModel) tblEmpleados.getModel();
        m.setRowCount(0);

        var mapAsis = asistenciaService.mapaDelDia(fechaActual);

        int presentes = 0, tardanzas = 0;
        for (edu.UPAO.proyecto.Modelo.Usuario u : usuarios) {
            var a = mapAsis.getOrDefault(u.getUsuario().toLowerCase(), null);

            String turno = (u.getHoraEntradaProg() == null || u.getHoraSalidaProg() == null)
                    ? "— / —"
                    : (u.getHoraEntradaProg() + " / " + u.getHoraSalidaProg());

            String entrada = (a == null || a.getHoraEntrada() == null) ? "—" : a.getHoraEntrada().toString();
            String salida = (a == null || a.getHoraSalida() == null) ? "—" : a.getHoraSalida().toString();
            String total = asistenciaService.totalHoras(a);
            long tardMin = asistenciaService.tardanzaMin(u, a);

            boolean presente = (a != null && a.getHoraEntrada() != null);
            if (presente) {
                presentes++;
            }
            if (tardMin > 0) {
                tardanzas++;
            }

            m.addRow(new Object[]{
                u.getNombreComp() + "\n" + u.getCargo(), // Nombre y Cargo
                turno,
                entrada,
                salida,
                total,
                tardMin,
                presente ? "Presente" : "Ausente"
            });
        }

        int total = usuarios.size();
        int ausencias = total - presentes;

        // KPIs (tus labels reales)
        lblValorTotal.setText(String.valueOf(total));
        lblValorPresentes.setText(String.valueOf(presentes));
        lblValorTardanzas.setText(String.valueOf(tardanzas));
        lblValorAusencias.setText(String.valueOf(ausencias));
    }

    private void cargarUsuariosGestion() {
        usuarioService.seedIfMissing();

        java.util.List<edu.UPAO.proyecto.Modelo.Usuario> lista
                = usuarioService.listarParaGestion(); // excluye GERENTE

        javax.swing.table.DefaultTableModel m
                = (javax.swing.table.DefaultTableModel) tblEmpleados.getModel();
        m.setRowCount(0);

        for (edu.UPAO.proyecto.Modelo.Usuario u : lista) {
            String nombreCargo = u.getNombreComp() + "\n" + u.getCargo();
            String turno = (u.getHoraEntradaProg() == null || u.getHoraSalidaProg() == null)
                    ? "— / —"
                    : (u.getHoraEntradaProg() + " / " + u.getHoraSalidaProg());

            m.addRow(new Object[]{
                nombreCargo,
                turno,
                "—", // Entrada real (aún no hay lógica de asistencia)
                "—", // Salida real
                "0 h 0 m", // Total horas
                0, // Tardanza (min)
                u.isEstado() ? "Habilitado" : "Bloqueado"
            });
        }

        // Actualiza los KPIs en el panel lateral
        lblValorTotal.setText(String.valueOf(lista.size()));
        lblValorPresentes.setText("0");
        lblValorTardanzas.setText("0");
        lblValorAusencias.setText("0");
    }

    private JTable getTable() {
        // 1) Intenta obtener por reflexión 'jTable1'
        try {
            java.lang.reflect.Field f = getClass().getDeclaredField("jTable1");
            f.setAccessible(true);
            Object o = f.get(this);
            if (o instanceof JTable jt) {
                return jt;
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ignore) {
        }

        // 2) Si no hay campo, intenta sacar la vista del scroll
        if (jScrollPane1 != null) {
            Component v = jScrollPane1.getViewport().getView();
            if (v instanceof JTable jt) {
                return jt;
            }
            // 3) Como fallback, crea una JTable y colócala en el scroll
            JTable jt = new JTable();
            jScrollPane1.setViewportView(jt);
            return jt;
        }
        throw new IllegalStateException("No encuentro la JTable: agrega una JTable dentro de jScrollPane1 o crea el campo jTable1.");
    }

    private void configurarTabla() {
        JTable t = getTable();

        t.setRowHeight(44);
        if (t.getTableHeader() != null) {
            t.getTableHeader().setReorderingAllowed(false);
        }

        // Ajusta anchos a tus columnas: Nombre/Cargo, Turno, Entrada, Salida, Total, Tardanza, Estado
        int[] widths = {220, 120, 110, 110, 130, 100, 120};
        TableColumnModel cm = t.getColumnModel();
        int n = Math.min(widths.length, cm.getColumnCount());
        for (int i = 0; i < n; i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }

        // Renderer para la última columna (Estado) con puntito de color
        int estadoCol = cm.getColumnCount() - 1;
        if (estadoCol >= 0) {
            cm.getColumn(estadoCol).setCellRenderer(new EstadoRenderer());
        }
    }

    private void poblarTiendas() {
        try {
            java.util.Set<String> tiendas = new java.util.TreeSet<>();
            for (edu.UPAO.proyecto.Modelo.Usuario u : usuarioService.listarUsuarios()) {
                if (u.getTienda() != null && !u.getTienda().isBlank()) {
                    tiendas.add(u.getTienda());
                }
            }
            cmbTienda.removeAllItems();
            cmbTienda.addItem("Todas");
            for (String t : tiendas) {
                cmbTienda.addItem(t);
            }
            cmbTienda.setSelectedIndex(0);
        } catch (Exception e) {
            cmbTienda.removeAllItems();
            cmbTienda.addItem("Todas");
        }
    }

    private void wireListeners() {
        // TIENDA
        if (cmbTienda != null) {
            cmbTienda.addActionListener(e -> aplicarFiltros());
        }

        // FECHA como diálogo simple (si luego tienes txtFecha, lo sincronizas)
        if (btnFecha != null) {
            btnFecha.addActionListener(e -> {
                String s = JOptionPane.showInputDialog(this, "Fecha (yyyy-MM-dd):", fechaSeleccionada.format(DF));
                if (s != null && !s.isBlank()) {
                    try {
                        fechaSeleccionada = LocalDate.parse(s, DF);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Formato inválido. Usa yyyy-MM-dd");
                        return;
                    }
                    aplicarFiltros(); // o recargarResumen() si quieres recomputar desde servicio
                }
            });
        }

        // BUSCADOR
        if (txtBuscar != null) {
            txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void filtra() {
                    aplicarFiltros();
                }

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    filtra();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    filtra();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    filtra();
                }
            });
        }

        // VER TODOS (limpia búsqueda y aplica)
        if (btnVerTodos != null) {
            btnVerTodos.addActionListener(e -> {
                if (txtBuscar != null) {
                    txtBuscar.setText("");
                }
                aplicarFiltros();
            });
        }

        // RETRASADOS (filtra por estado=Tarde)
        if (btnRetrasados != null) {
            btnRetrasados.addActionListener(e -> {
                aplicarFiltros(); // base filtrada (tienda/fecha/busqueda)
                List<ResumenDiario> tard = new ArrayList<>();
                for (ResumenDiario r : model.filas) {
                    if ("Tarde".equals(r.getEstado())) {
                        tard.add(r);
                    }
                }
                model.setData(tard);
                actualizarKPIs(tard);
            });
        }

        // EXPORTAR CSV
        if (btnExportar != null) {
            btnExportar.addActionListener(e -> exportarCSV(model.filas));
        }
    }

    private void actualizarKPIs(List<ResumenDiario> resumen) {
        int total = resumen.size(), presentes = 0, ausentes = 0, tardanzas = 0;
        for (var r : resumen) {
            String est = r.getEstado();
            if ("Responsable".equalsIgnoreCase(est)) {
                presentes++;
            } else if ("Tarde".equalsIgnoreCase(est)) {
                presentes++;
                tardanzas++;
            } else if ("Ausente".equalsIgnoreCase(est) || "Incompleto".equalsIgnoreCase(est)) {
                ausentes++;
            }
        }
        lblValorTotal.setText(String.valueOf(total));
        lblValorPresentes.setText(String.valueOf(presentes));
        lblValorAusencias.setText(String.valueOf(ausentes));
        lblValorTardanzas.setText(String.valueOf(tardanzas));
    }

    private void exportarCSV(List<ResumenDiario> data) {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) {
                f = new java.io.File(f.getAbsolutePath() + ".csv");
            }
            try (java.io.PrintWriter pw = new java.io.PrintWriter(
                    f, java.nio.charset.StandardCharsets.UTF_8)) {
                pw.println("ID,Nombre,Cargo,Tienda,Fecha,Entrada,Salida,TotalHoras,TardanzaMin,Estado");
                for (ResumenDiario r : data) {
                    String in = r.getEntrada() == null ? "" : r.getEntrada().toString();
                    String out = r.getSalida() == null ? "" : r.getSalida().toString();
                    String tot = (r.getMinutosTrab() / 60) + "h " + (r.getMinutosTrab() % 60) + "m";
                    pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            r.getUsuario().getId(), r.getUsuario().getNombreComp(), r.getUsuario().getCargo(),
                            (r.getUsuario().getTienda() == null ? "" : r.getUsuario().getTienda()),
                            r.getFecha(), in, out, tot, r.getMinTarde(), r.getEstado());
                }
                JOptionPane.showMessageDialog(this, "Exportado a:\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo exportar");
            }
        }
    }

    private void recargarResumen() {
        aplicarFiltros(); // delega en el filtro que ya arma la tabla
    }

    private void aplicarFiltros() {
        // ------------- base: usuarios (sin GERENTE) -------------
        java.util.List<edu.UPAO.proyecto.Modelo.Usuario> base
                = usuarioService.listarParaGestion();

        // ------------- filtro por tienda -------------
        if (cmbTienda != null && cmbTienda.getSelectedItem() != null) {
            String tienda = String.valueOf(cmbTienda.getSelectedItem());
            if (!"Todas".equalsIgnoreCase(tienda)) {
                base = base.stream()
                        .filter(u -> u.getTienda() != null && tienda.equalsIgnoreCase(u.getTienda()))
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // ------------- filtro por buscador (id/dni/nombre/usuario/cargo) -------------
        if (txtBuscar != null && !txtBuscar.getText().isBlank()) {
            String q = txtBuscar.getText().toLowerCase().trim();
            base = base.stream()
                    .filter(u
                            -> String.valueOf(u.getId()).contains(q)
                    || String.valueOf(u.getDni()).contains(q)
                    || (u.getNombreComp() != null && u.getNombreComp().toLowerCase().contains(q))
                    || (u.getUsuario() != null && u.getUsuario().toLowerCase().contains(q))
                    || (u.getCargo() != null && u.getCargo().toLowerCase().contains(q))
                    )
                    .collect(java.util.stream.Collectors.toList());
        }

        // ------------- asistencia del día seleccionado -------------
        java.util.Map<String, edu.UPAO.proyecto.Modelo.Asistencia> mapAsis
                = asistenciaService.mapaDelDia(fechaSeleccionada);

        // ------------- pinta la tabla -------------
        javax.swing.table.DefaultTableModel m
                = (javax.swing.table.DefaultTableModel) tblEmpleados.getModel();
        m.setRowCount(0);

        int presentes = 0;
        int tardanzas = 0;

        for (edu.UPAO.proyecto.Modelo.Usuario u : base) {
            edu.UPAO.proyecto.Modelo.Asistencia a
                    = mapAsis.getOrDefault(u.getUsuario().toLowerCase(), null);

            String turno = (u.getHoraEntradaProg() == null || u.getHoraSalidaProg() == null)
                    ? "— / —"
                    : (u.getHoraEntradaProg() + " / " + u.getHoraSalidaProg());

            String entrada = (a == null || a.getHoraEntrada() == null) ? "—" : a.getHoraEntrada().toString();
            String salida = (a == null || a.getHoraSalida() == null) ? "—" : a.getHoraSalida().toString();
            String total = asistenciaService.totalHoras(a);
            long tardMin = asistenciaService.tardanzaMin(u, a);

            boolean presente = (a != null && a.getHoraEntrada() != null);
            if (presente) {
                presentes++;
            }
            if (tardMin > 0) {
                tardanzas++;
            }

            m.addRow(new Object[]{
                u.getNombreComp() + "\n" + u.getCargo(), // Nombre y Cargo
                turno,
                entrada,
                salida,
                total,
                tardMin,
                presente ? "Presente" : "Ausente"
            });
        }

        // ------------- KPIs -------------
        int total = base.size();
        int ausencias = total - presentes;

        lblValorTotal.setText(String.valueOf(total));
        lblValorPresentes.setText(String.valueOf(presentes));
        lblValorTardanzas.setText(String.valueOf(tardanzas));
        lblValorAusencias.setText(String.valueOf(ausencias));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTop = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        panelTitle = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelBody = new javax.swing.JPanel();
        panelKPIs = new javax.swing.JPanel();
        KpiTotal = new javax.swing.JPanel();
        lblTituloTotal = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        kpiPresentes = new javax.swing.JPanel();
        lblTituloPresentes = new javax.swing.JLabel();
        lblValorPresentes = new javax.swing.JLabel();
        kpiAusencias = new javax.swing.JPanel();
        lblTituloAusencias = new javax.swing.JLabel();
        lblValorAusencias = new javax.swing.JLabel();
        kpiTardanzas = new javax.swing.JPanel();
        lblTituloTardanzas = new javax.swing.JLabel();
        lblValorTardanzas = new javax.swing.JLabel();
        panelCentro = new javax.swing.JPanel();
        panelFiltros = new javax.swing.JPanel();
        cmbTienda = new javax.swing.JComboBox<>();
        btnFecha = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        btnVerTodos = new javax.swing.JButton();
        btnRetrasados = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEmpleados = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

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

        jLabel1.setFont(new java.awt.Font("Harlow Solid Italic", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(193, 28, 28));
        jLabel1.setText("Todo lo que necesitas al alcance");
        panelHeader.add(jLabel1, java.awt.BorderLayout.CENTER);

        panelTop.add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelTitle.setBackground(new java.awt.Color(153, 0, 0));
        panelTitle.setPreferredSize(new java.awt.Dimension(780, 40));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("GESTIÓN DE EMPLEADOS");
        panelTitle.add(lblTitulo, java.awt.BorderLayout.CENTER);

        panelTop.add(panelTitle, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        panelBody.setBackground(new java.awt.Color(219, 236, 232));
        panelBody.setLayout(new java.awt.BorderLayout());

        panelKPIs.setBackground(new java.awt.Color(204, 255, 255));
        panelKPIs.setPreferredSize(new java.awt.Dimension(164, 260));
        panelKPIs.setLayout(new java.awt.GridLayout(4, 1, 0, 10));

        KpiTotal.setBackground(new java.awt.Color(46, 111, 167));
        KpiTotal.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        KpiTotal.setLayout(new java.awt.BorderLayout());

        lblTituloTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloTotal.setText("TOTAL EMPLEADOS");
        KpiTotal.add(lblTituloTotal, java.awt.BorderLayout.PAGE_START);

        lblValorTotal.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblValorTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblValorTotal.setText("jLabel2");
        KpiTotal.add(lblValorTotal, java.awt.BorderLayout.CENTER);

        panelKPIs.add(KpiTotal);

        kpiPresentes.setBackground(new java.awt.Color(76, 175, 80));
        kpiPresentes.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        kpiPresentes.setLayout(new java.awt.BorderLayout());

        lblTituloPresentes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloPresentes.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloPresentes.setText("PRESENTES");
        kpiPresentes.add(lblTituloPresentes, java.awt.BorderLayout.PAGE_START);

        lblValorPresentes.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblValorPresentes.setForeground(new java.awt.Color(255, 255, 255));
        lblValorPresentes.setText("jLabel2");
        kpiPresentes.add(lblValorPresentes, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiPresentes);

        kpiAusencias.setBackground(new java.awt.Color(255, 112, 67));
        kpiAusencias.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        kpiAusencias.setLayout(new java.awt.BorderLayout());

        lblTituloAusencias.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloAusencias.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloAusencias.setText("TARDANZAS");
        kpiAusencias.add(lblTituloAusencias, java.awt.BorderLayout.PAGE_START);

        lblValorAusencias.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblValorAusencias.setForeground(new java.awt.Color(255, 255, 255));
        lblValorAusencias.setText("jLabel5");
        kpiAusencias.add(lblValorAusencias, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiAusencias);

        kpiTardanzas.setBackground(new java.awt.Color(211, 47, 47));
        kpiTardanzas.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        kpiTardanzas.setLayout(new java.awt.BorderLayout());

        lblTituloTardanzas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloTardanzas.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloTardanzas.setText("AUSENCIAS");
        kpiTardanzas.add(lblTituloTardanzas, java.awt.BorderLayout.PAGE_START);

        lblValorTardanzas.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblValorTardanzas.setForeground(new java.awt.Color(255, 255, 255));
        lblValorTardanzas.setText("jLabel6");
        kpiTardanzas.add(lblValorTardanzas, java.awt.BorderLayout.CENTER);

        panelKPIs.add(kpiTardanzas);

        panelBody.add(panelKPIs, java.awt.BorderLayout.LINE_START);

        panelCentro.setBackground(new java.awt.Color(255, 255, 255));
        panelCentro.setPreferredSize(new java.awt.Dimension(600, 400));
        panelCentro.setLayout(new java.awt.BorderLayout());

        panelFiltros.setBackground(new java.awt.Color(204, 255, 255));
        panelFiltros.setLayout(new java.awt.GridBagLayout());

        cmbTienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTienda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTiendaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelFiltros.add(cmbTienda, gridBagConstraints);

        btnFecha.setText("FECHA");
        btnFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panelFiltros.add(btnFecha, gridBagConstraints);

        btnExportar.setText("EXPORTAR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        panelFiltros.add(btnExportar, gridBagConstraints);

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelFiltros.add(txtBuscar, gridBagConstraints);

        btnVerTodos.setText("VER TODOS");
        btnVerTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTodosActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panelFiltros.add(btnVerTodos, gridBagConstraints);

        btnRetrasados.setText("RETRASADOS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        panelFiltros.add(btnRetrasados, gridBagConstraints);

        panelCentro.add(panelFiltros, java.awt.BorderLayout.PAGE_START);

        panelTabla.setBackground(new java.awt.Color(255, 255, 255));
        panelTabla.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout tablaEmpleadosLayout = new javax.swing.GroupLayout(tablaEmpleados);
        tablaEmpleados.setLayout(tablaEmpleadosLayout);
        tablaEmpleadosLayout.setHorizontalGroup(
            tablaEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 685, Short.MAX_VALUE)
        );
        tablaEmpleadosLayout.setVerticalGroup(
            tablaEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(tablaEmpleados);

        panelTabla.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelCentro.add(panelTabla, java.awt.BorderLayout.CENTER);

        panelBody.add(panelCentro, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelBody, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFechaActionPerformed

    private void cmbTiendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTiendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTiendaActionPerformed

    private void btnVerTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerTodosActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        Panel_Gerente g = new Panel_Gerente(); // cambia por el nombre de tu JFrame de Gerente
        g.setVisible(true);
        g.setLocationRelativeTo(null); // centrar la ventana en la pantalla

        // Cerrar esta ventana (PERSONAL)
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

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
            java.util.logging.Logger.getLogger(PERSONAL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PERSONAL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PERSONAL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PERSONAL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PERSONAL().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel KpiTotal;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFecha;
    private javax.swing.JButton btnRetrasados;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVerTodos;
    private javax.swing.JComboBox<String> cmbTienda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel kpiAusencias;
    private javax.swing.JPanel kpiPresentes;
    private javax.swing.JPanel kpiTardanzas;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloAusencias;
    private javax.swing.JLabel lblTituloPresentes;
    private javax.swing.JLabel lblTituloTardanzas;
    private javax.swing.JLabel lblTituloTotal;
    private javax.swing.JLabel lblValorAusencias;
    private javax.swing.JLabel lblValorPresentes;
    private javax.swing.JLabel lblValorTardanzas;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelCentro;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelKPIs;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel panelTop;
    private javax.swing.JPanel tablaEmpleados;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}

class ResumenTableModel extends javax.swing.table.AbstractTableModel {

    final String[] cols = {"Nombre y Cargo", "Turno", "Entrada", "Salida", "Total de Horas", "Tardanza (min)", "Estado"};
    java.util.List<edu.UPAO.proyecto.Modelo.ResumenDiario> filas;

    ResumenTableModel(java.util.List<edu.UPAO.proyecto.Modelo.ResumenDiario> f) {
        this.filas = f;
    }

    @Override
    public int getRowCount() {
        return filas == null ? 0 : filas.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        return false;
    }

    @Override
    public Object getValueAt(int r, int c) {
        var a = filas.get(r);
        var u = a.getUsuario();
        String nombre = (u == null ? "" : u.getNombreComp());
        String cargo = (u == null ? "" : u.getCargo());
        String turno = (u == null ? "" : "%s / %s".formatted(u.getHoraEntradaProg(), u.getHoraSalidaProg()));

        return switch (c) {
            case 0 ->
                "<html>" + nombre + "<br><span style='color:#777;font-size:10px;'>" + cargo + "</span></html>";
            case 1 ->
                turno;
            case 2 ->
                a.getEntrada() == null ? "—" : a.getEntrada().toString();
            case 3 ->
                a.getSalida() == null ? "—" : a.getSalida().toString();
            case 4 ->
                (a.getMinutosTrab() / 60) + " h " + (a.getMinutosTrab() % 60) + " m";
            case 5 ->
                String.valueOf(a.getMinTarde());
            case 6 ->
                a.getEstado();
            default ->
                "";
        };
    }

    void setData(java.util.List<edu.UPAO.proyecto.Modelo.ResumenDiario> f) {
        this.filas = f;
        fireTableDataChanged();
    }
}

class EstadoRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
        String e = String.valueOf(v).toLowerCase();
        Color col = switch (e) {
            case "responsable" ->
                new Color(76, 175, 80);
            case "tarde" ->
                new Color(255, 167, 38);
            case "incompleto" ->
                new Color(33, 150, 243);
            default ->
                new Color(229, 57, 53); // ausente
        };
        l.setText("  " + String.valueOf(v));
        return l;
    }
}
