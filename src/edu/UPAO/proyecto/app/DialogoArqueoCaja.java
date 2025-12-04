package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.CajaDAO;
import java.awt.*;
import javax.swing.*;

public class DialogoArqueoCaja extends javax.swing.JDialog {

    private JLabel lblSaldoSistema, lblDiferencia;
    private JTextField txtMontoReal;
    private JTextArea txtObservaciones;
    private JButton btnCerrarCaja;

    private int idCaja;
    private double saldoTeorico;
    public boolean cajaCerradaExito = false; // Para avisar al Menu2

    public DialogoArqueoCaja(java.awt.Frame parent, boolean modal, int idCaja) {
        super(parent, modal);
        this.idCaja = idCaja;
        initComponents();
        calcularDatosSistema();

        setTitle("Arqueo de Caja - Cierre de Turno");
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Obliga a usar los botones
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlCentro = new JPanel(new GridLayout(6, 1, 5, 5));
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblSaldoSistema = new JLabel("Sistema calcula: S/ 0.00");
        lblSaldoSistema.setFont(new Font("Arial", Font.BOLD, 14));

        txtMontoReal = new JTextField();
        txtMontoReal.setBorder(BorderFactory.createTitledBorder("Ingrese Dinero Físico (Conteo):"));
        txtMontoReal.setFont(new Font("Arial", Font.PLAIN, 16));

        lblDiferencia = new JLabel("Diferencia: S/ 0.00");
        lblDiferencia.setForeground(Color.BLUE);

        txtObservaciones = new JTextArea();
        txtObservaciones.setBorder(BorderFactory.createTitledBorder("Observaciones / Justificación:"));

        pnlCentro.add(lblSaldoSistema);
        pnlCentro.add(txtMontoReal);
        pnlCentro.add(lblDiferencia);
        pnlCentro.add(new JScrollPane(txtObservaciones));

        btnCerrarCaja = new JButton("CONFIRMAR CIERRE DE CAJA");
        btnCerrarCaja.setBackground(new Color(200, 50, 50));
        btnCerrarCaja.setForeground(Color.WHITE);
        btnCerrarCaja.setFont(new Font("Arial", Font.BOLD, 12));

        JButton btnCancelar = new JButton("Cancelar (Seguir Trabajando)");

        JPanel pnlSur = new JPanel(new GridLayout(1, 2, 5, 5));
        pnlSur.add(btnCancelar);
        pnlSur.add(btnCerrarCaja);

        add(new JLabel("  CIERRE DE TURNO - CONTEO DE CAJA"), BorderLayout.NORTH);
        add(pnlCentro, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);

        // Eventos
        txtMontoReal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                actualizarDiferencia();
            }
        });

        btnCerrarCaja.addActionListener(e -> ejecutarCierre());
        btnCancelar.addActionListener(e -> dispose()); // Cierra ventana, NO cierra caja
    }

    private void calcularDatosSistema() {
        CajaDAO dao = new CajaDAO();
        this.saldoTeorico = dao.calcularSaldoTeorico(this.idCaja);
        lblSaldoSistema.setText("Sistema espera: S/ " + String.format("%.2f", saldoTeorico));
    }

    private void actualizarDiferencia() {
        try {
            double real = Double.parseDouble(txtMontoReal.getText());
            double diff = real - saldoTeorico;
            lblDiferencia.setText("Diferencia: S/ " + String.format("%.2f", diff));

            if (Math.abs(diff) > 0.5) { // Si hay diferencia mayor a 50 centimos
                lblDiferencia.setForeground(Color.RED);
            } else {
                lblDiferencia.setForeground(new Color(0, 150, 0)); // Verde
            }
        } catch (NumberFormatException e) {
            lblDiferencia.setText("...");
        }
    }

    private void ejecutarCierre() {
        try {
            if (txtMontoReal.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el monto contado.");
                return;
            }

            double real = Double.parseDouble(txtMontoReal.getText());
            double diff = real - saldoTeorico;
            String obs = txtObservaciones.getText();

            if (Math.abs(diff) > 10 && obs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Existe una diferencia grande. Debe ingresar una observación obligatoria.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que desea cerrar la caja con S/ " + real + "?\nEsta acción es irreversible y cierra su turno.",
                    "Confirmar Cierre", JOptionPane.YES_NO_OPTION);

            // En el método ejecutarCierre() de DialogoArqueoCaja
// ... (Validaciones previas y obtención del monto 'real') ...
            if (confirm == JOptionPane.YES_OPTION) {
                CajaDAO dao = new CajaDAO();

                // 1. Cerrar la caja en la tabla 'caja'
                if (dao.cerrarCajaConArqueo(idCaja, real, diff, obs)) {

                    // --- NUEVA LÓGICA: DEVOLVER DINERO A LA TIENDA ---
                    // Obtener el ID de la sucursal (puedes pasarlo al constructor del Dialogo o buscarlo por la caja)
                    // Asumiremos que puedes obtenerlo o que lo agregas al constructor de DialogoArqueoCaja
                    int idSucursal = dao.obtenerIdSucursalPorCaja(idCaja); // Necesitarás este método simple en el DAO

                    if (idSucursal > 0) {
                        edu.UPAO.proyecto.DAO.SucursalDAO sucursalDAO = new edu.UPAO.proyecto.DAO.SucursalDAO();

                        // 'real' es el dinero físico que contaron. Ese dinero vuelve a la bóveda/presupuesto.
                        boolean devolucionExitosa = sucursalDAO.actualizarPresupuesto(idSucursal, real, true); // true = Sumar

                        if (devolucionExitosa) {
                            JOptionPane.showMessageDialog(this, "✅ Caja Cerrada y fondos devueltos al presupuesto general.");
                        } else {
                            JOptionPane.showMessageDialog(this, "⚠️ Caja cerrada, pero error al actualizar presupuesto de tienda.");
                        }
                    }
                    // -------------------------------------------------

                    this.cajaCerradaExito = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar cierre en BD.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
        }
    }
}
