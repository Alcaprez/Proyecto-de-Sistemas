package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.AsistenciaDAOADM;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;

public class panel_Administrador_Asistencia extends javax.swing.JPanel {

    private AsistenciaDAOADM asistenciaDAO = new AsistenciaDAOADM();
    private javax.swing.JButton btnActualizar2;
    
    // Memoria para los datos
    private List<Object[]> datosEnMemoria = new ArrayList<>();
    private String filtroEstadoActual = "TODOS"; 

    // COLORES
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(15, 23, 42);
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    
    // Colores Etiquetas
    private final Color BG_VERDE = new Color(220, 252, 231); private final Color TXT_VERDE = new Color(22, 163, 74);
    private final Color BG_ROJO = new Color(254, 226, 226); private final Color TXT_ROJO = new Color(220, 38, 38);
    private final Color BG_AMBAR = new Color(254, 243, 199); private final Color TXT_AMBAR = new Color(217, 119, 6);
    private final Color BG_GRIS = new Color(241, 245, 249); private final Color TXT_GRIS = new Color(100, 116, 139);

    private JLabel lblPresentes, lblTardanzas, lblAusencias;
    
    public panel_Administrador_Asistencia() {
        initComponents(); // Bloque de NetBeans
        
        // INICIALIZAR COMPONENTES MANUALMENTE SI NETBEANS NO LO HIZO
        if (btnActualizar2 == null) btnActualizar2 = new JButton("↻ ACTUALIZAR");
        if (jComboBox1 == null) jComboBox1 = new JComboBox<>();
        if (jButton5 == null) jButton5 = new JButton();
        if (jTextField1 == null) jTextField1 = new JTextField();
        if (jButton1 == null) jButton1 = new JButton();
        if (jButton2 == null) jButton2 = new JButton();
        if (jButton3 == null) jButton3 = new JButton();
        if (jButton4 == null) jButton4 = new JButton();
        if (jTable1 == null) jTable1 = new JTable();

        construirInterfazModerna();
        cargarDatosIniciales();
    }
    
    private void construirInterfazModerna() {
        this.removeAll(); 
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel filtrosPanel = new JPanel();
        filtrosPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Control de Asistencia Diaria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        jComboBox1.setPreferredSize(new Dimension(200, 35));
        jComboBox1.setBackground(COLOR_BLANCO);
        for(ActionListener al : jComboBox1.getActionListeners()) jComboBox1.removeActionListener(al);
        jComboBox1.addActionListener(e -> cargarDatosDesdeBD()); 

        jButton5.setText("EXPORTAR REPORTE");
        estilizarBotonAccion(jButton5, COLOR_AZUL, Color.WHITE);
        for(ActionListener al : jButton5.getActionListeners()) jButton5.removeActionListener(al);
        jButton5.addActionListener(e -> exportarAExcel());
        
        btnActualizar2.setText("↻ ACTUALIZAR");
        estilizarBotonAccion(btnActualizar2, COLOR_BLANCO, COLOR_TEXTO);
        btnActualizar2.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 
        for(ActionListener al : btnActualizar2.getActionListeners()) btnActualizar2.removeActionListener(al);
        btnActualizar2.addActionListener(e -> cargarDatosDesdeBD());
        
        filtrosPanel.add(new JLabel("Sucursal: "));
        filtrosPanel.add(jComboBox1);
        filtrosPanel.add(Box.createHorizontalStrut(15));
        filtrosPanel.add(jButton5);
        filtrosPanel.add(btnActualizar2);
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(filtrosPanel, BorderLayout.EAST);

        // --- BARRA SUPERIOR ---
        JPanel barraSuperior = new JPanel(new BorderLayout(15, 0));
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        jTextField1.setPreferredSize(new Dimension(250, 35));
        for(KeyListener kl : jTextField1.getKeyListeners()) jTextField1.removeKeyListener(kl);
        jTextField1.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrarTablaLocalmente(); }
        });
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 0));
        panelBotones.setOpaque(false);
        
        configurarBotonFiltro(jButton1, "Todos", "TODOS");
        configurarBotonFiltro(jButton2, "Presentes", "ASISTIO"); // Filtro interno
        configurarBotonFiltro(jButton3, "Tardanzas", "TARDANZA");
        configurarBotonFiltro(jButton4, "Ausentes", "FALTA");

        panelBotones.add(jButton1);
        panelBotones.add(jButton2);
        panelBotones.add(jButton3);
        panelBotones.add(jButton4);
        
        barraSuperior.add(jTextField1, BorderLayout.WEST);
        barraSuperior.add(panelBotones, BorderLayout.CENTER);

        // --- TABLA ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(COLOR_BLANCO);
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        
        DefaultTableModel modelo = new DefaultTableModel(
            new Object [][] {},
            new String [] { "Nombre y Cargo", "Rol", "Turno", "Entrada", "Salida", "Estado" }
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        jTable1.setModel(modelo);
        jTable1.setRowHeight(45);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(COLOR_BLANCO);
        
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());
        
        JScrollPane scroll = new JScrollPane(jTable1);
        scroll.getViewport().setBackground(COLOR_BLANCO);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        centerPanel.add(scroll, BorderLayout.CENTER);
        
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.add(barraSuperior, BorderLayout.NORTH);
        leftContainer.add(centerPanel, BorderLayout.CENTER);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JLabel lblResumen = new JLabel("Resumen Operativo");
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(lblResumen);
        sidebar.add(Box.createVerticalStrut(15));
        
        lblPresentes = new JLabel("0");
        lblTardanzas = new JLabel("0");
        lblAusencias = new JLabel("0");
        
        sidebar.add(crearTarjetaKPI("ASISTENCIAS", lblPresentes, BG_VERDE, TXT_VERDE));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(crearTarjetaKPI("TARDANZAS", lblTardanzas, BG_AMBAR, TXT_AMBAR));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(crearTarjetaKPI("AUSENCIAS", lblAusencias, BG_ROJO, TXT_ROJO));
        
        sidebar.add(Box.createVerticalGlue());

        this.add(header, BorderLayout.NORTH);
        this.add(leftContainer, BorderLayout.CENTER);
        this.add(sidebar, BorderLayout.EAST);
        
        this.revalidate();
        this.repaint();
    }

    // --- LÓGICA DE DATOS CORREGIDA ---

    private void cargarDatosIniciales() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Todas las Sucursales");
        
        List<String> sucursales = asistenciaDAO.listarNombresSucursales();
        for (String s : sucursales) jComboBox1.addItem(s);
        
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0); 
            cargarDatosDesdeBD();
        }
    }
    
private void cargarDatosDesdeBD() {
        Object itemSeleccionado = jComboBox1.getSelectedItem();
        if (itemSeleccionado == null) return;
        String sucursalSel = itemSeleccionado.toString();
        if (sucursalSel.contains("Item")) return; 
        
        // 1. Cargar datos
        datosEnMemoria = asistenciaDAO.listarAsistenciaDiaria(sucursalSel);
        
        // 2. Refrescar Tabla
        filtrarTablaLocalmente();
        
        // 3. CALCULAR KPIs (Lógica Exacta según tus datos)
        int presentes = 0;
        int tardanzas = 0;
        int ausencias = 0;
        
        for (Object[] fila : datosEnMemoria) {
            // Obtenemos estado y normalizamos (Mayúsculas)
            String estado = (fila[5] != null) ? fila[5].toString().toUpperCase() : "FALTA";
            
            // Lógica basada en TUS datos:
            // ASISTENCIAS: "RESPONSABLE", "ASISTIO", "1", "CIERRE AUTOMÁTICO" (Implica que vino)
            if (estado.contains("RESPONSABLE") || estado.contains("ASISTIO") || estado.contains("CIERRE") || estado.equals("1")) {
                presentes++;
            } 
            // TARDANZAS: "TARDE", "TARDANZA", "2"
            else if (estado.contains("TARDE") || estado.contains("TARDANZA") || estado.equals("2")) {
                tardanzas++;
            } 
            // AUSENCIAS: "AUSENTE", "FALTA", "3", o si es nulo
            else {
                ausencias++;
            }
        }
        
        lblPresentes.setText(String.valueOf(presentes));
        lblTardanzas.setText(String.valueOf(tardanzas));
        lblAusencias.setText(String.valueOf(ausencias));
        
        this.revalidate();
        this.repaint();
    }

    private void filtrarTablaLocalmente() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        String busqueda = jTextField1.getText().trim();
        if (busqueda.equals("Buscar empleado...")) busqueda = "";
        
        for (Object[] fila : datosEnMemoria) {
            String nombre = (String) fila[0]; 
            String estadoBD = (fila[5] != null) ? fila[5].toString().toUpperCase() : "FALTA";
            
            boolean cumpleBusqueda = busqueda.isEmpty() || nombre.toLowerCase().contains(busqueda.toLowerCase());
            boolean cumpleFiltro = false;

            if (filtroEstadoActual.equals("TODOS")) {
                cumpleFiltro = true;
            } 
            else if (filtroEstadoActual.equals("ASISTIO")) {
                if (estadoBD.contains("RESPONSABLE") || estadoBD.contains("ASISTIO") || estadoBD.contains("CIERRE")) cumpleFiltro = true;
            }
            else if (filtroEstadoActual.equals("TARDANZA")) {
                if (estadoBD.contains("TARDE") || estadoBD.contains("TARDANZA")) cumpleFiltro = true;
            }
            else if (filtroEstadoActual.equals("FALTA")) {
                if (estadoBD.contains("AUSENTE") || estadoBD.contains("FALTA")) cumpleFiltro = true;
            }

            if (cumpleBusqueda && cumpleFiltro) {
                model.addRow(fila);
            }
        }
    }
    
    // --- RENDERIZADOR DE COLORES (Actualizado con tus palabras clave) ---
    class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            
            String estado = (value != null) ? value.toString().toUpperCase().trim() : "";
            
            // ROJO (Faltas)
            if (estado.contains("FALTA") || estado.contains("AUSENTE") || estado.equals("3")) {
                label.setBackground(BG_ROJO); 
                label.setForeground(TXT_ROJO); 
                label.setText(" AUSENTE ");
                
            // AMBAR (Tardanzas)
            } else if (estado.contains("TARDANZA") || estado.contains("TARDE") || estado.equals("2")) {
                label.setBackground(BG_AMBAR); 
                label.setForeground(TXT_AMBAR); 
                label.setText(" TARDANZA ");
                
            // VERDE (Asistencias - Incluye RESPONSABLE y CIERRE)
            } else if (estado.contains("ASISTIO") || estado.contains("RESPONSABLE") || estado.contains("CIERRE") || estado.equals("1")) {
                label.setBackground(BG_VERDE); 
                label.setForeground(TXT_VERDE); 
                
                // Opcional: Mostrar el texto original o normalizar a "PRESENTE"
                // Si prefieres ver "RESPONSABLE" en la tabla, deja 'value.toString()'
                // Si quieres que todo diga "PRESENTE", usa setText(" PRESENTE ")
                label.setText(" PRESENTE "); 
                
            } else { 
                label.setBackground(BG_GRIS); 
                label.setForeground(TXT_GRIS); 
                label.setText(" PENDIENTE ");
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return label;
        }
    }
    
    // --- HELPERS VISUALES ---
    private void configurarBotonFiltro(JButton btn, String texto, String valorFiltro) {
        btn.setText(texto);
        btn.setBackground(COLOR_BLANCO);
        btn.setForeground(COLOR_TEXTO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        for(ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
        btn.addActionListener(e -> {
            filtroEstadoActual = valorFiltro;
            restaurarBotones();
            btn.setBackground(new Color(220, 230, 240)); 
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            filtrarTablaLocalmente();
        });
    }
    
    private void restaurarBotones() {
        JButton[] botones = {jButton1, jButton2, jButton3, jButton4};
        for(JButton b : botones) {
            b.setBackground(COLOR_BLANCO);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
    }
    
    private void estilizarBotonAccion(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel crearTarjetaKPI(String titulo, JLabel valorLabel, Color bg, Color fg) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(300, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(fg.darker());
        
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valorLabel.setForeground(fg);
        
        card.add(lblTit, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }
    
    private void exportarAExcel() {
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Asistencia");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Asistencia_" + java.time.LocalDate.now() + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jTable1.getColumnCount(); i++) {
                    sb.append(jTable1.getColumnName(i)).append(",");
                }
                writer.println(sb.toString());
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    sb.setLength(0);
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        Object val = jTable1.getValueAt(i, j);
                        sb.append(val != null ? val.toString().replace(",", " ") : "").append(",");
                    }
                    writer.println(sb.toString());
                }
                JOptionPane.showMessageDialog(this, "Reporte exportado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton5.setText("Exportar Reporte");

        jLabel5.setText("Tienda");

        jLabel6.setText("Fecha");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(352, 352, 352)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jButton1.setText("jButton1");

        jButton2.setText("jButton1");

        jButton3.setText("jButton1");

        jButton4.setText("jButton1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Resumen Operativo");

        jLabel2.setText("Programados");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("5");

        jLabel4.setText("Empleados en turno de hoy");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
