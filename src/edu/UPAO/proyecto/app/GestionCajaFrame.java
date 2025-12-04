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
        // 1. Verificar si hay caja abierta para esta sucursal (Lógica Diaria)
        // Usamos la búsqueda genérica de caja abierta hoy
        cajaActual = cajaDAO.obtenerCajaAbierta(idSucursal);

        if (cajaActual == null) {
            // --- ESTADO: CAJA CERRADA ---
            lblEstado.setText("CAJA CERRADA");
            lblEstado.setForeground(Color.RED);
            panelDetalles.setVisible(false);

            // Si está cerrada, sugerimos abrirla con el 5% del presupuesto (Solo informativo aquí)
            edu.UPAO.proyecto.DAO.SucursalDAO sucDao = new edu.UPAO.proyecto.DAO.SucursalDAO();
            double presupuesto = sucDao.obtenerPresupuesto(idSucursal);
            double sugerido = presupuesto * 0.05; 
            
            txtSaldoAcumulado.setText(String.format("%.2f", sugerido).replace(",", "."));
            btnAccion.setText("INICIAR TURNO (Automático en Login)");
            btnAccion.setEnabled(false); // Ahora se abre en el Login, no aquí.
            lblInstruccion.setText("La caja se abre automáticamente al entrar el primer usuario.");

        } else {
            // --- ESTADO: CAJA ABIERTA (TURNO EN CURSO) ---
            lblEstado.setText("TURNO ABIERTO (ID: " + cajaActual.getIdCaja() + ")");
            lblEstado.setForeground(new Color(0, 153, 51));
            panelDetalles.setVisible(true);

            // A. Mostrar con cuánto dinero EMPEZÓ el día (El 5% que se trajo del presupuesto)
            double saldoInicialDia = cajaActual.getSaldoInicial();
            txtSaldoAcumulado.setText(String.format("%.2f", saldoInicialDia).replace(",", "."));

            // B. Mostrar ventas de HOY (Sesión actual de la caja)
            // Nota: calcularSaldoTeorico ya suma (SaldoInicial + Ventas - Egresos)
            double totalTeorico = cajaDAO.calcularSaldoTeorico(cajaActual.getIdCaja());
            
            // Calculamos solo las ventas puras para mostrar en el label pequeño
            double soloVentas = totalTeorico - saldoInicialDia; 
            
            lblVentas.setText("S/ " + String.format("%.2f", soloVentas));

            // C. EL TOTAL ESPERADO (Lo que debe haber en el cajón Físicamente)
            // Aquí es donde corregimos el error. Ya no es el histórico, es el teórico de HOY.
            lblTotalEsperado.setText("S/ " + String.format("%.2f", totalTeorico));
            
            // Variable global para usar en el cálculo de diferencia
            this.granTotalHistorico = totalTeorico; 

            // Pre-llenar el campo de conteo para facilitar (opcional)
            txtSaldoReal.setText(""); 
            lblDiferencia.setText("Ingrese monto...");

            btnAccion.setText("REALIZAR ARQUEO / CERRAR");
            btnAccion.setBackground(new Color(204, 0, 0));
            btnAccion.setEnabled(true);
            lblInstruccion.setText("Dinero total que debe haber HOY:");
        }
    }

    private void refrescarDatos() {
        cargarDatos();
        JOptionPane.showMessageDialog(this, "Saldos actualizados desde la base de datos.");
    }

private void procesarAccion() {
        if (cajaActual == null) {
            // LÓGICA NUEVA: Bloquear apertura manual
            // Como la apertura es automática en el Login, aquí solo avisamos.
            JOptionPane.showMessageDialog(this, 
                "⚠️ La caja se encuentra CERRADA.\n" +
                "El sistema abre la caja automáticamente con el primer inicio de sesión del día.\n" +
                "Si necesita abrirla, por favor ingrese nuevamente al sistema.", 
                "Acción no permitida", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // LÓGICA DE CIERRE / ARQUEO
            try {
                // Verificar que el campo no esté vacío
                if (txtSaldoReal.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese el monto contado en 'Dinero Físico'.");
                    return;
                }

                double saldoReal = Double.parseDouble(txtSaldoReal.getText().replace(",", "."));
                
                // Calculamos diferencias para mostrar en el mensaje
                // Usamos el total esperado calculado en cargarDatos()
                double diferencia = saldoReal - this.granTotalHistorico; 
                
                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Confirmar arqueo con S/ " + String.format("%.2f", saldoReal) + "?\n" +
                        "Diferencia calculada: S/ " + String.format("%.2f", diferencia),
                        "Confirmar Arqueo", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Usamos la nueva lógica de ENCUADRE (No cierre definitivo) para que el Admin revise luego
                    // O si prefieres cierre directo, usa cerrarCajaConArqueo.
                    // Aquí usaremos cerrarCajaConArqueo del DAO para mantener coherencia con tu código anterior
                    // pero asegurándonos de pasar los 4 parámetros que pide el DAO actualizado.
                    
                    String observacion = "Cierre desde Gestión de Caja";
                    if (Math.abs(diferencia) > 5) {
                        observacion = JOptionPane.showInputDialog("Existe una diferencia mayor a S/ 5.00.\nIngrese una justificación:");
                        if (observacion == null || observacion.trim().isEmpty()) observacion = "Sin justificación";
                    }

                    if (cajaDAO.cerrarCajaConArqueo(cajaActual.getIdCaja(), saldoReal, diferencia, observacion)) {
                        JOptionPane.showMessageDialog(this, "✅ Turno/Caja cerrada correctamente.");
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al guardar el cierre en la base de datos.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Monto inválido. Ingrese solo números (ej. 150.50).");
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
