package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.CajaDAO;
import edu.UPAO.proyecto.Modelo.Caja;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GestionCajaFrame extends JFrame {

    private int idSucursal;
    private CajaDAO cajaDAO;
    private Caja cajaActual;

    // Componentes visuales
    private JPanel mainPanel;
    private JLabel lblEstado, lblInstruccion, lblSaldoAcumuladoTitulo;
    private JTextField txtSaldoAcumulado; // Antes txtSaldoInicial
    private JButton btnAccion, btnActualizar;
    private JPanel panelDetalles;
    private JLabel lblVentasTitulo, lblVentas, lblTotalEsperadoTitulo, lblTotalEsperado;
    private JLabel lblConteoTitulo, lblDiferenciaTitulo, lblDiferencia;
    private JTextField txtSaldoReal;
    private JSeparator separador;

    // Variable lógica crítica
    private double granTotalHistorico = 0.0;

    public GestionCajaFrame(int idSucursal) {
        this.idSucursal = idSucursal;
        this.cajaDAO = new CajaDAO();

        initUI(); // Construir interfaz (Layout Nulo corregido)

        setLocationRelativeTo(null);
        setTitle("Gestión de Caja Acumulada - Sucursal " + idSucursal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cargarDatos();
        agregarListeners();
    }

    private void initUI() {
        setSize(400, 560);
        setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(245, 245, 245));
        setContentPane(mainPanel);

        lblEstado = new JLabel("CARGANDO...");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstado.setBounds(20, 20, 360, 30);
        mainPanel.add(lblEstado);

        btnActualizar = new JButton("🔄");
        btnActualizar.setToolTipText("Recalcular Ventas");
        btnActualizar.setBounds(330, 60, 45, 25);
        btnActualizar.addActionListener(e -> refrescarDatos());
        mainPanel.add(btnActualizar);

        lblInstruccion = new JLabel("Instrucción...");
        lblInstruccion.setBounds(30, 60, 290, 20);
        mainPanel.add(lblInstruccion);

        lblSaldoAcumuladoTitulo = new JLabel("Saldo Acumulado (Histórico):");
        lblSaldoAcumuladoTitulo.setBounds(30, 90, 200, 20);
        mainPanel.add(lblSaldoAcumuladoTitulo);

        txtSaldoAcumulado = new JTextField();
        txtSaldoAcumulado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSaldoAcumulado.setBounds(30, 110, 330, 40);
        txtSaldoAcumulado.setEditable(false); // Bloqueado porque viene de la BD
        mainPanel.add(txtSaldoAcumulado);

        // --- PANEL ARQUEO ---
        panelDetalles = new JPanel();
        panelDetalles.setBorder(new TitledBorder("Arqueo de Caja"));
        panelDetalles.setLayout(null);
        panelDetalles.setBounds(30, 170, 330, 240);
        panelDetalles.setBackground(Color.WHITE);
        mainPanel.add(panelDetalles);

        lblVentasTitulo = new JLabel("+ Ventas (Sesión actual):");
        lblVentasTitulo.setBounds(20, 30, 150, 20);
        panelDetalles.add(lblVentasTitulo);

        lblVentas = new JLabel("S/ 0.00");
        lblVentas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVentas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVentas.setBounds(170, 30, 140, 20);
        panelDetalles.add(lblVentas);

        separador = new JSeparator();
        separador.setBounds(20, 60, 290, 10);
        panelDetalles.add(separador);

        lblTotalEsperadoTitulo = new JLabel("= TOTAL EN CAJA:");
        lblTotalEsperadoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalEsperadoTitulo.setBounds(20, 70, 150, 20);
        panelDetalles.add(lblTotalEsperadoTitulo);

        lblTotalEsperado = new JLabel("S/ 0.00");
        lblTotalEsperado.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalEsperado.setForeground(new Color(0, 102, 204));
        lblTotalEsperado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalEsperado.setBounds(170, 70, 140, 25);
        panelDetalles.add(lblTotalEsperado);

        lblConteoTitulo = new JLabel("DINERO FÍSICO (Conteo):");
        lblConteoTitulo.setBounds(20, 110, 250, 20);
        panelDetalles.add(lblConteoTitulo);

        txtSaldoReal = new JTextField();
        txtSaldoReal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSaldoReal.setBackground(new Color(255, 255, 204));
        txtSaldoReal.setBounds(20, 130, 290, 40);
        panelDetalles.add(txtSaldoReal);

        lblDiferenciaTitulo = new JLabel("Diferencia:");
        lblDiferenciaTitulo.setBounds(20, 190, 80, 20);
        panelDetalles.add(lblDiferenciaTitulo);

        lblDiferencia = new JLabel("...");
        lblDiferencia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDiferencia.setBounds(100, 190, 200, 20);
        panelDetalles.add(lblDiferencia);

        btnAccion = new JButton("ACCIÓN");
        btnAccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAccion.setForeground(Color.WHITE);
        btnAccion.setBounds(30, 430, 330, 50);
        btnAccion.addActionListener(e -> procesarAccion());
        mainPanel.add(btnAccion);
    }

    private void cargarDatos() {
        // 1. Verificar si hay caja abierta
        cajaActual = cajaDAO.obtenerCajaAbierta(idSucursal);

        // 2. CALCULAR EL GRAN TOTAL HISTÓRICO (ESTA ES LA CLAVE)
        // Suma todas las ventas de la historia de la sucursal, ignorando cierres.
        granTotalHistorico = cajaDAO.obtenerSaldoAcumuladoHistorico(idSucursal);
        // Mostrar siempre el total histórico como referencia
        txtSaldoAcumulado.setText(String.format("%.2f", granTotalHistorico).replace(",", "."));

        if (cajaActual == null) {
            // --- APERTURA ---
            lblEstado.setText("CAJA CERRADA");
            lblEstado.setForeground(Color.RED);
            panelDetalles.setVisible(false);

            btnAccion.setText("INICIAR TURNO");
            btnAccion.setBackground(new Color(0, 153, 51));
            lblInstruccion.setText("Saldo acumulado actual:");

        } else {
            // --- CIERRE / GESTIÓN ---
            lblEstado.setText("TURNO ABIERTO");
            lblEstado.setForeground(new Color(0, 153, 51));
            panelDetalles.setVisible(true);

            // Mostrar ventas de ESTA sesión solo como informativo
            double ventasSesion = cajaDAO.obtenerTotalVentasSesion(cajaActual.getIdCaja());
            lblVentas.setText("S/ " + String.format("%.2f", ventasSesion));

            // EL TOTAL ESPERADO ES EL GRAN TOTAL HISTÓRICO
            lblTotalEsperado.setText("S/ " + String.format("%.2f", granTotalHistorico));

            // Pre-llenar con el total histórico para facilitar el cierre
            txtSaldoReal.setText(String.format("%.2f", granTotalHistorico).replace(",", "."));
            calcularDiferencia();

            btnAccion.setText("CERRAR TURNO");
            btnAccion.setBackground(new Color(204, 0, 0));
            lblInstruccion.setText("Dinero total que debe haber:");
        }
    }

    private void refrescarDatos() {
        cargarDatos();
        JOptionPane.showMessageDialog(this, "Saldos actualizados desde la base de datos.");
    }

    private void procesarAccion() {
        if (cajaActual == null) {
            // ABRIR CON EL ACUMULADO HISTÓRICO
            if (cajaDAO.abrirCaja(idSucursal, granTotalHistorico)) {
                JOptionPane.showMessageDialog(this, "Turno iniciado con saldo acumulado: S/ " + granTotalHistorico);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al abrir caja.");
            }
        } else {
            // CERRAR
            try {
                double saldoReal = Double.parseDouble(txtSaldoReal.getText().replace(",", "."));

                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Cerrar turno con S/ " + saldoReal + "?",
                        "Confirmar Cierre", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (cajaDAO.cerrarCaja(cajaActual.getIdCaja(), saldoReal)) {
                        JOptionPane.showMessageDialog(this, "Turno cerrado.");
                        this.dispose();
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
            }
        }
    }

    private void calcularDiferencia() {
        try {
            String textoReal = txtSaldoReal.getText().replace(",", ".");
            if (textoReal.isEmpty()) {
                lblDiferencia.setText("...");
                return;
            }
            double real = Double.parseDouble(textoReal);
            // Diferencia = Lo que cuento físicamente - El total histórico que debería haber
            double diferencia = real - granTotalHistorico;

            lblDiferencia.setText("S/ " + String.format("%.2f", diferencia));

            if (Math.abs(diferencia) < 0.1) {
                lblDiferencia.setForeground(new Color(0, 153, 51)); // Verde
                lblDiferencia.setText("CUADRADO");
            } else if (diferencia < 0) {
                lblDiferencia.setForeground(Color.RED); // Faltante
            } else {
                lblDiferencia.setForeground(Color.BLUE); // Sobrante
            }
        } catch (NumberFormatException e) {
            lblDiferencia.setText("Error");
        }
    }

    private void agregarListeners() {
        txtSaldoReal.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                calcularDiferencia();
            }

            public void removeUpdate(DocumentEvent e) {
                calcularDiferencia();
            }

            public void changedUpdate(DocumentEvent e) {
                calcularDiferencia();
            }
        });
    }
}
