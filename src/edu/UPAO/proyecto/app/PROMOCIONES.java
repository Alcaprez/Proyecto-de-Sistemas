package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.Modelo.Cupon;
import edu.UPAO.proyecto.Modelo.Cupon.TipoDescuento;
import edu.UPAO.proyecto.PromocionController;
import edu.UPAO.proyecto.Promociones;
import edu.UPAO.proyecto.app.Panel_Gerente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PROMOCIONES extends JFrame {

    // --- UI components ---
    private JTable tblCupones;
    private DefaultTableModel model;

    private JTextField txtCodigo;
    private JSpinner   spnValor;
    private JRadioButton rbPorcentaje, rbMontoFijo;
    private ButtonGroup grpTipo;

    private JTextField txtSku;
    private JSpinner   spnMinimo;
    private JTextField txtInicio, txtFin;
    private JCheckBox  chkActivo;
    private JSpinner   spnMaxUsos;

    private JButton btnGuardar, btnLimpiar, btnActivar, btnDesactivar, btnEliminar,
                    btnExportar, btnFecha, btnSalir;

    public PROMOCIONES() {
        setTitle("Gestión de Promociones / Cupones (Gerente)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);

        initComponents();
        configurarTabla();
        cargarCupones();
        hookSeleccionTabla();
    }

    // ----------------- INIT UI -----------------
    private void initComponents() {
        // Root
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Header
        JLabel title = new JLabel("Promociones (Cupones)");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        root.add(title, BorderLayout.NORTH);

        // Center split: left(form) + right(table)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.38); // 38% form, 62% tabla
        root.add(split, BorderLayout.CENTER);

        // ---- LEFT: FORM ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        split.setLeftComponent(new JScrollPane(formPanel));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int row = 0;

        // Código
        addL(formPanel, gc, row, "Código:");
        txtCodigo = new JTextField();
        addF(formPanel, gc, row++, txtCodigo);

        // Tipo (radio)
        addL(formPanel, gc, row, "Tipo:");
        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rbPorcentaje = new JRadioButton("Porcentaje (%)", true);
        rbMontoFijo = new JRadioButton("Monto fijo (S/)");
        grpTipo = new ButtonGroup();
        grpTipo.add(rbPorcentaje);
        grpTipo.add(rbMontoFijo);
        tipoPanel.add(rbPorcentaje);
        tipoPanel.add(rbMontoFijo);
        addF(formPanel, gc, row++, tipoPanel);

        // Valor
        addL(formPanel, gc, row, "Valor:");
        spnValor = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000.0, 0.1));
        addF(formPanel, gc, row++, spnValor);

        // SKU (opcional)
        addL(formPanel, gc, row, "SKU aplicado (opcional):");
        txtSku = new JTextField();
        addF(formPanel, gc, row++, txtSku);

        // Mínimo compra
        addL(formPanel, gc, row, "Mínimo compra (S/):");
        spnMinimo = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000.0, 0.5));
        addF(formPanel, gc, row++, spnMinimo);

        // Inicio
        addL(formPanel, gc, row, "Inicio (yyyy-MM-dd):");
        txtInicio = new JTextField();
        addF(formPanel, gc, row++, txtInicio);

        // Fin
        addL(formPanel, gc, row, "Fin (yyyy-MM-dd):");
        txtFin = new JTextField();
        addF(formPanel, gc, row++, txtFin);

        // Activo
        addL(formPanel, gc, row, "Activo:");
        chkActivo = new JCheckBox("Habilitado", true);
        addF(formPanel, gc, row++, chkActivo);

        // Máx usos
        addL(formPanel, gc, row, "Máx usos (0 = ilimitado):");
        spnMaxUsos = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        addF(formPanel, gc, row++, spnMaxUsos);

        // Fila extra: botones de formulario
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2; gc.weightx = 1;
        JPanel formBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGuardar = new JButton("Guardar/Actualizar");
        btnLimpiar = new JButton("Limpiar");
        btnFecha   = new JButton("Fecha (hoy / +30)");
        formBtns.add(btnGuardar);
        formBtns.add(btnLimpiar);
        formBtns.add(btnFecha);
        formPanel.add(formBtns, gc);
        row++;

        // Wire actions form
        btnGuardar.addActionListener(e -> onGuardar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnFecha.addActionListener(e -> onFecha());

        // ---- RIGHT: TABLE + FOOTER BUTTONS ----
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        split.setRightComponent(rightPanel);

        tblCupones = new JTable();
        rightPanel.add(new JScrollPane(tblCupones), BorderLayout.CENTER);

        JPanel listBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnActivar    = new JButton("Activar");
        btnDesactivar = new JButton("Desactivar");
        btnEliminar   = new JButton("Eliminar");
        btnExportar   = new JButton("Abrir CSV");
        btnSalir      = new JButton("Salir");
        listBtns.add(btnActivar);
        listBtns.add(btnDesactivar);
        listBtns.add(btnEliminar);
        listBtns.add(btnExportar);
        listBtns.add(btnSalir);
        rightPanel.add(listBtns, BorderLayout.SOUTH);

        // Wire actions list
        btnActivar.addActionListener(e -> onActivar());
        btnDesactivar.addActionListener(e -> onDesactivar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnExportar.addActionListener(e -> onExportar());
        btnSalir.addActionListener(e -> onSalir());
    }

    // Helpers to place labels/fields nicely
    private void addL(JPanel p, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0;
        JLabel l = new JLabel(text);
        p.add(l, gc);
    }

    private void addF(JPanel p, GridBagConstraints gc, int row, JComponent comp) {
        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 1;
        p.add(comp, gc);
    }

    // ----------------- TABLE -----------------
    private void configurarTabla() {
        model = new DefaultTableModel(
            new Object[]{
                "Código","Tipo","Valor","SKU","Mínimo",
                "Inicio","Fin","Activo","Máx Usos","Usos"
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCupones.setModel(model);
        tblCupones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // opcional: ajustar ancho
        tblCupones.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblCupones.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblCupones.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblCupones.getColumnModel().getColumn(3).setPreferredWidth(120);
    }

    private void hookSeleccionTabla() {
        tblCupones.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblCupones.getSelectedRow();
            if (row >= 0) llenarFormularioDesdeFila(row);
        });
    }

    private void cargarCupones() {
        List<Cupon> lista = PromocionController.listarCupones();
        model.setRowCount(0);
        for (Cupon c : lista) {
            model.addRow(new Object[]{
                c.getCodigo(),
                c.getTipo().name(),
                c.getValor(),
                c.getSkuAplicado(),
                c.getMinimoCompra(),
                c.getInicio(),
                c.getFin(),
                c.isActivo(),
                c.getMaxUsos(),
                c.getUsos()
            });
        }
    }

    // ----------------- FORM BINDING -----------------
    private Cupon leerFormulario() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) throw new IllegalArgumentException("Código es obligatorio.");

        TipoDescuento tipo = rbPorcentaje.isSelected() ? TipoDescuento.PERCENT : TipoDescuento.FLAT;
        double valor   = ((Number) spnValor.getValue()).doubleValue();
        String sku     = txtSku.getText().trim();
        if (sku.isEmpty()) sku = null;
        double minimo  = ((Number) spnMinimo.getValue()).doubleValue();

        LocalDate inicio = null, fin = null;
        String sInicio = txtInicio.getText().trim();
        String sFin    = txtFin.getText().trim();
        try {
            if (!sInicio.isEmpty()) inicio = LocalDate.parse(sInicio);
            if (!sFin.isEmpty())    fin    = LocalDate.parse(sFin);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido: usa yyyy-MM-dd.");
        }

        boolean activo = chkActivo.isSelected();
        int maxUsos = ((Number) spnMaxUsos.getValue()).intValue();

        return new Cupon(codigo, tipo, valor, sku, minimo, inicio, fin, activo, maxUsos, 0);
    }

    private void llenarFormularioDesdeFila(int row) {
        txtCodigo.setText(String.valueOf(model.getValueAt(row,0)));

        String tipo = String.valueOf(model.getValueAt(row,1));
        rbPorcentaje.setSelected("PERCENT".equalsIgnoreCase(tipo));
        rbMontoFijo.setSelected("FLAT".equalsIgnoreCase(tipo));

        spnValor.setValue(Double.valueOf(String.valueOf(model.getValueAt(row,2))));

        Object sku = model.getValueAt(row,3);
        txtSku.setText(sku == null ? "" : String.valueOf(sku));

        spnMinimo.setValue(Double.valueOf(String.valueOf(model.getValueAt(row,4))));
        txtInicio.setText(String.valueOf(model.getValueAt(row,5)));
        txtFin.setText(String.valueOf(model.getValueAt(row,6)));

        chkActivo.setSelected(Boolean.parseBoolean(String.valueOf(model.getValueAt(row,7))));
        spnMaxUsos.setValue(Integer.valueOf(String.valueOf(model.getValueAt(row,8))));
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        rbPorcentaje.setSelected(true);
        spnValor.setValue(0.0);
        txtSku.setText("");
        spnMinimo.setValue(0.0);
        txtInicio.setText("");
        txtFin.setText("");
        chkActivo.setSelected(true);
        spnMaxUsos.setValue(0);
        tblCupones.clearSelection();
    }

    // ----------------- ACTIONS -----------------
    private void onGuardar() {
        try {
            Cupon c = leerFormulario();
            PromocionController.crearActualizarCupon(c);
            cargarCupones();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cupón guardado/actualizado ✅");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar cupón.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onActivar() {
        int row = tblCupones.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un cupón."); return; }
        String codigo = String.valueOf(model.getValueAt(row,0));
        PromocionController.activar(codigo);
        cargarCupones();
    }

    private void onDesactivar() {
        int row = tblCupones.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un cupón."); return; }
        String codigo = String.valueOf(model.getValueAt(row,0));
        PromocionController.desactivar(codigo);
        cargarCupones();
    }

    private void onEliminar() {
        int row = tblCupones.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un cupón."); return; }
        String codigo = String.valueOf(model.getValueAt(row,0));
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar " + codigo + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            PromocionController.eliminar(codigo);
            cargarCupones();
            limpiarFormulario();
        }
    }

    private void onExportar() {
        try {
            Desktop.getDesktop().open(new java.io.File("data/cupones.csv"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir data/cupones.csv. Verifica que exista.");
        }
    }

    private void onFecha() {
        LocalDate hoy = LocalDate.now();
        txtInicio.setText(hoy.toString());
        txtFin.setText(hoy.plusDays(30).toString());
    }
private void onSalir() {
    // Abre el panel del gerente y cierra esta ventana
    SwingUtilities.invokeLater(() -> {
        try {
            new Panel_Gerente().setVisible(true);
        } catch (Exception ex) {
            // Por si Panel_Gerente tiene constructor con argumentos
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir Panel_Gerente.\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.dispose();
        }
    });
}

    // ----------------- MAIN (opcional para test independiente) -----------------
    public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        @Override public void run() {
            new PROMOCIONES().setVisible(true);
        }
    });
}
}
