
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.AsistenciaDAOADM;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class panel_Administrador_Asistencia extends javax.swing.JPanel {

    private AsistenciaDAOADM asistenciaDAO = new AsistenciaDAOADM();
    private javax.swing.JButton btnActualizar2;
    
    // Memoria para el buscador rápido
    private List<Object[]> datosEnMemoria = new ArrayList<>();
    
    // Variables de Filtro
    private String filtroEstadoActual = "TODOS"; 

    // COLORES MODERNOS
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(15, 23, 42);
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    
    // Colores Badges
    private final Color BG_VERDE = new Color(220, 252, 231); private final Color TXT_VERDE = new Color(22, 163, 74);
    private final Color BG_ROJO = new Color(254, 226, 226); private final Color TXT_ROJO = new Color(220, 38, 38);
    private final Color BG_AMBAR = new Color(254, 243, 199); private final Color TXT_AMBAR = new Color(217, 119, 6);
    private final Color BG_GRIS = new Color(241, 245, 249); private final Color TXT_GRIS = new Color(100, 116, 139);

    private JLabel lblTotal, lblPresentes, lblTardanzas, lblAusencias;
    
    public panel_Administrador_Asistencia() {
        initComponents();
        construirInterfazModerna();
        cargarDatosIniciales();
        btnActualizar2.setText("ACTUALIZAR"); 
        
        // 2. Quitar el borde fino gris y el fondo azul/gris
        btnActualizar2.setBackground(Color.WHITE);
        btnActualizar2.setBorder(null); // Esto borra la línea gris
        btnActualizar2.setFocusPainted(false); // Esto quita el recuadro al hacer clic
        
        // (Opcional) Si quieres que se note que es un botón, ponle un cursor de mano
        btnActualizar2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
    }
    
    
    private void construirInterfazModerna() {
        this.removeAll(); 
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel filtrosPanel = new JPanel();
        filtrosPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Control de Asistencia Diaria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        jComboBox1.setPreferredSize(new Dimension(200, 35));
        jComboBox1.setBackground(COLOR_BLANCO);
        jComboBox1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // AL CAMBIAR SUCURSAL -> Recargar desde BD
        jComboBox1.addActionListener(e -> cargarDatosDesdeBD()); 

        jButton5.setText("EXPORTAR REPORTE");
        estilizarBotonAccion(jButton5, COLOR_AZUL, Color.WHITE);
        
        // --- CONEXIÓN DEL EVENTO ---
        // Eliminamos listeners viejos por seguridad
        for(java.awt.event.ActionListener al : jButton5.getActionListeners()) jButton5.removeActionListener(al);
        
        // Agregamos la acción nueva
        jButton5.addActionListener(e -> exportarAExcel());
        
        // --- AQUÍ ESTÁ LA CORRECCIÓN: INICIALIZAR EL BOTÓN ---
        if (btnActualizar2 == null) { // Usamos btnActualizar2 porque así lo llamaste en tu diseño
             btnActualizar2 = new JButton("↻ ACTUALIZAR");
        } else {
             btnActualizar2.setText("↻ ACTUALIZAR");
        }
        
        estilizarBotonAccion(btnActualizar2, COLOR_BLANCO, COLOR_TEXTO);
        btnActualizar2.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 
        
        // Limpiamos listeners anteriores
        for(java.awt.event.ActionListener al : btnActualizar2.getActionListeners()) btnActualizar2.removeActionListener(al);
        
        btnActualizar2.addActionListener(e -> cargarDatosDesdeBD());
        // -----------------------------------------------------
        
        filtrosPanel.add(new JLabel("Sucursal: "));
        filtrosPanel.add(jComboBox1);
        filtrosPanel.add(Box.createHorizontalStrut(15));
        filtrosPanel.add(jButton5);
        
        // ¡¡¡FALTA ESTA LÍNEA!!! 👇
        filtrosPanel.add(btnActualizar2); // <--- AGREGAR ESTO
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(filtrosPanel, BorderLayout.EAST);

        // --- 2. BARRA SUPERIOR (Buscador + Botones) ---
        JPanel barraSuperior = new JPanel(new BorderLayout(15, 0));
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        jTextField1.setPreferredSize(new Dimension(250, 35));
        jTextField1.setText("Buscar empleado...");
        jTextField1.setForeground(Color.GRAY);
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().equals("Buscar empleado...")) {
                    jTextField1.setText(""); jTextField1.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Buscar empleado..."); jTextField1.setForeground(Color.GRAY);
                }
            }
        });
        // AL ESCRIBIR -> Filtramos en Memoria (Rápido)
        jTextField1.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrarTablaLocalmente(); }
        });
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 0));
        panelBotones.setOpaque(false);
        
        // Configuramos botones de filtro rápido
        configurarBotonFiltro(jButton1, "Todos", "TODOS");
        configurarBotonFiltro(jButton2, "Presentes", "ASISTIO");
        configurarBotonFiltro(jButton3, "Tardanzas", "TARDANZA");
        configurarBotonFiltro(jButton4, "Ausentes", "FALTA");

        panelBotones.add(jButton1);
        panelBotones.add(jButton2);
        panelBotones.add(jButton3);
        panelBotones.add(jButton4);
        
        barraSuperior.add(jTextField1, BorderLayout.WEST);
        barraSuperior.add(panelBotones, BorderLayout.CENTER);

        // --- 3. TABLA ---
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
        jTable1.setGridColor(new Color(241, 245, 249));
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(COLOR_BLANCO);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());
        
        JScrollPane scroll = new JScrollPane(jTable1);
        scroll.getViewport().setBackground(COLOR_BLANCO);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        centerPanel.add(scroll, BorderLayout.CENTER);
        
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.add(barraSuperior, BorderLayout.NORTH);
        leftContainer.add(centerPanel, BorderLayout.CENTER);

        // --- 4. SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JLabel lblResumen = new JLabel("Resumen Operativo");
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResumen.setAlignmentX(LEFT_ALIGNMENT);
        
        sidebar.add(lblResumen);
        sidebar.add(Box.createVerticalStrut(15));
        
        lblTotal = new JLabel("0");
        lblPresentes = new JLabel("0");
        lblTardanzas = new JLabel("0");
        lblAusencias = new JLabel("0");
        
        sidebar.add(crearTarjetaKPI("Total Programados", lblTotal));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(crearTarjetaKPI("Presentes", lblPresentes));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(crearTarjetaKPI("Tardanzas", lblTardanzas));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(crearTarjetaKPI("Ausencias", lblAusencias));
        sidebar.add(Box.createVerticalGlue());

        this.add(header, BorderLayout.NORTH);
        this.add(leftContainer, BorderLayout.CENTER);
        this.add(sidebar, BorderLayout.EAST);
    }

    // --- LÓGICA DE DATOS (CORREGIDA) ---

    private void cargarDatosIniciales() {
        // 1. LIMPIAR BASURA
        jComboBox1.removeAllItems();
        
        // 2. AGREGAR OPCIÓN "TODAS" (Esta es la clave)
        jComboBox1.addItem("Todas las Sucursales");
        
        // 3. AGREGAR LAS DEMÁS DESDE BD
        List<String> sucursales = asistenciaDAO.listarNombresSucursales();
        for (String s : sucursales) {
            jComboBox1.addItem(s);
        }
        
        // 4. FORZAR CARGA (Selecciona "Todas..." por defecto)
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0); 
            cargarDatosDesdeBD();
        }
    }
    
    // Va a la BD (Lento)
    private void cargarDatosDesdeBD() {
        Object itemSeleccionado = jComboBox1.getSelectedItem();
        if (itemSeleccionado == null) return;
        
        String sucursalSel = itemSeleccionado.toString();
        
        // FILTRO DE SEGURIDAD: Si sigue siendo "Item 1", abortamos
        if (sucursalSel.contains("Item")) return;
        
        // 1. Cargar en memoria
        datosEnMemoria = asistenciaDAO.listarAsistenciaDiaria(sucursalSel);
        
        System.out.println("--- Cargados " + datosEnMemoria.size() + " registros para: " + sucursalSel + " ---");
        
        // 2. Filtrar visualmente
        filtrarTablaLocalmente();
        
        // 3. KPIs
        Map<String, Integer> kpis = asistenciaDAO.obtenerKPIsDiarios(sucursalSel);
        lblTotal.setText(String.valueOf(kpis.get("Programados")));
        lblPresentes.setText(String.valueOf(kpis.get("Presentes")));
        lblTardanzas.setText(String.valueOf(kpis.get("Tardanzas")));
        lblAusencias.setText(String.valueOf(kpis.get("Ausencias")));
    }
    
    // Filtra en RAM (LÓGICA CORREGIDA PARA LOS BOTONES)
    private void filtrarTablaLocalmente() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        String busqueda = jTextField1.getText().trim();
        if (busqueda.equals("Buscar empleado...")) busqueda = "";
        
        for (Object[] fila : datosEnMemoria) {
            String nombre = (String) fila[0]; 
            String estadoBD = (String) fila[5]; // El estado que viene de la BD (ej: "3", "FALTA", "PENDIENTE")
            if (estadoBD == null) estadoBD = "PENDIENTE";
            
            boolean cumpleBusqueda = busqueda.isEmpty() || nombre.toLowerCase().contains(busqueda.toLowerCase());
            boolean cumpleFiltroBoton = false; // Empezamos asumiendo que no cumple

            // Lógica flexible para los botones
            if (filtroEstadoActual.equals("TODOS")) {
                cumpleFiltroBoton = true; // Si es TODOS, pasa siempre
            } 
            else if (filtroEstadoActual.equals("ASISTIO")) {
                if (estadoBD.contains("ASISTIO") || estadoBD.equals("1") || estadoBD.contains("PRESENTE")) cumpleFiltroBoton = true;
            }
            else if (filtroEstadoActual.equals("TARDANZA")) {
                if (estadoBD.contains("TARDANZA") || estadoBD.equals("2") || estadoBD.contains("TARDE")) cumpleFiltroBoton = true;
            }
            else if (filtroEstadoActual.equals("FALTA")) {
                if (estadoBD.contains("FALTA") || estadoBD.equals("3") || estadoBD.contains("AUSENTE")) cumpleFiltroBoton = true;
            }
            // Podríamos agregar un caso para "PENDIENTES" si quisieras filtrar solo esos.

            if (cumpleBusqueda && cumpleFiltroBoton) {
                model.addRow(fila);
            }
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
        
        btn.addActionListener(e -> {
            filtroEstadoActual = valorFiltro;
            restaurarBotones();
            // Resaltar botón activo
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

    // --- REEMPLAZA TODO ESTO ---
    class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            
            // Limpiamos el valor (mayúsculas y sin espacios)
            String estado = (value != null) ? value.toString().toUpperCase().trim() : "";
            
            // Lógica SIMPLE y DIRECTA
            if (estado.contains("FALTA") || estado.contains("AUSENTE") || estado.equals("3")) {
                // PRIORIDAD 1: SI ES FALTA, ROJO SÍ O SÍ
                label.setBackground(BG_ROJO); 
                label.setForeground(TXT_ROJO); 
                label.setText(" AUSENTE ");
                
            } else if (estado.contains("TARDANZA") || estado.contains("TARDE") || estado.equals("2")) {
                // PRIORIDAD 2: TARDANZA, AMBAR
                label.setBackground(BG_AMBAR); 
                label.setForeground(TXT_AMBAR); 
                label.setText(" TARDANZA ");
                
            } else if (estado.contains("ASISTIO") || estado.contains("PRESENTE") || estado.equals("1")) {
                // PRIORIDAD 3: ASISTENCIA, VERDE
                label.setBackground(BG_VERDE); 
                label.setForeground(TXT_VERDE); 
                label.setText(" PRESENTE ");
                
            } else { 
                // RESTO: PENDIENTE
                label.setBackground(BG_GRIS); 
                label.setForeground(TXT_GRIS); 
                label.setText(" PENDIENTE ");
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            return label;
        }
    }
    
    private JPanel crearTarjetaKPI(String titulo, JLabel valorLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(COLOR_BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(300, 80));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(Color.GRAY);
        
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valorLabel.setForeground(COLOR_TEXTO);
        
        card.add(lblTit, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }
    
    // --- MÉTODO PARA EXPORTAR A CSV (EXCEL COMPATIBLE) ---
    private void exportarAExcel() {
        // 1. Verificar si hay datos
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Abrir selector de archivos (JFileChooser)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Asistencia");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV (Excel)", "csv"));
        
        // Nombre por defecto: Reporte_FECHA.csv
        String nombreArchivo = "Reporte_Asistencia_" + java.time.LocalDate.now() + ".csv";
        fileChooser.setSelectedFile(new java.io.File(nombreArchivo));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Asegurar extensión .csv
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileToSave))) {
                
                // A. Escribir Cabeceras (Títulos de columnas)
                for (int i = 0; i < jTable1.getColumnCount(); i++) {
                    writer.write(jTable1.getColumnName(i));
                    if (i < jTable1.getColumnCount() - 1) writer.write(",");
                }
                writer.newLine(); // Salto de línea

                // B. Escribir Datos (Fila por fila)
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        Object valor = jTable1.getValueAt(i, j);
                        String texto = (valor != null) ? valor.toString() : "";
                        
                        // Limpiar comas dentro del texto para no romper el CSV
                        texto = texto.replace(",", " "); 
                        
                        writer.write(texto);
                        if (j < jTable1.getColumnCount() - 1) writer.write(",");
                    }
                    writer.newLine();
                }
                
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente:\n" + fileToSave.getAbsolutePath());
                
                // Opcional: Abrir el archivo automáticamente
                try {
                    java.awt.Desktop.getDesktop().open(fileToSave);
                } catch (Exception ex) { /* No hacer nada si falla abrir */ }
                
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jTextField1.setText("jTextField1");

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
