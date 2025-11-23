package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.GastoDAO;
import java.awt.*;
import javax.swing.*;

public class DialogoRegistrarGasto extends javax.swing.JDialog {

    private JSpinner spMonto;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private int idCaja;
    private int idSucursal;

    public DialogoRegistrarGasto(java.awt.Frame parent, boolean modal, int idCaja, int idSucursal) {
        super(parent, modal);
        this.idCaja = idCaja;
        this.idSucursal = idSucursal;
        
        initComponentsPropio();
        setTitle("Registrar Salida de Dinero (Gasto)");
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initComponentsPropio() {
        setLayout(new BorderLayout(15, 15));
        
        // Panel Central
        JPanel pnlForm = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblMonto = new JLabel("Monto a Retirar (S/):");
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        spMonto = new JSpinner(new SpinnerNumberModel(1.00, 0.10, 10000.00, 0.50));
        spMonto.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel lblDesc = new JLabel("Motivo / Descripción:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtDescripcion = new JTextArea();
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        pnlForm.add(lblMonto);
        pnlForm.add(spMonto);
        pnlForm.add(lblDesc);
        pnlForm.add(new JScrollPane(txtDescripcion));
        
        // Botón Guardar
        btnGuardar = new JButton("REGISTRAR SALIDA");
        btnGuardar.setBackground(new Color(220, 50, 50)); // Rojo (Alerta de salida)
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(100, 50));
        
        btnGuardar.addActionListener(e -> guardarGasto());

        add(new JLabel("  REGISTRO DE GASTOS OPERATIVOS"), BorderLayout.NORTH);
        add(pnlForm, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private void guardarGasto() {
        double monto = (double) spMonto.getValue();
        String descripcion = txtDescripcion.getText().trim();

        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.");
            return;
        }
        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el motivo del gasto.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Confirmar salida de S/ " + String.format("%.2f", monto) + "?\nSe descontará de la caja.",
                "Confirmar Gasto", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            GastoDAO dao = new GastoDAO();
            boolean exito = dao.registrarGasto(monto, descripcion, idCaja, idSucursal);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "✅ Gasto registrado correctamente.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al registrar en base de datos.");
            }
        }
    }
}