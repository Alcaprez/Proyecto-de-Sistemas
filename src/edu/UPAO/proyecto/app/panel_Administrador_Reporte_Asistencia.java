
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.ReporteDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class panel_Administrador_Reporte_Asistencia extends javax.swing.JPanel {

    private ReporteDAO reporteDAO = new ReporteDAO();
    private List<Object[]> datosOriginales; 
    
    // COLORES MODERNOS
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_TEXTO_GRIS = new Color(100, 116, 139);
    private final Color COLOR_BORDE_SUAVE = new Color(230, 230, 230);
    
    public panel_Administrador_Reporte_Asistencia() {
        initComponents();
        reconstruirPanelGrafico();
        aplicarEstiloModerno();
        cargarDatosIniciales();
        
        // Listeners
        jComboBox1.addActionListener(e -> aplicarFiltros());
        jComboBox2.addActionListener(e -> aplicarFiltros());
        
        // --- NUEVO: ACCIÓN DEL BOTÓN EXPORTAR ---
        Exportar.addActionListener(e -> exportarAExcel());
    }
    
    private void exportarAExcel() {
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        fileChooser.setSelectedFile(new File("Reporte_" + LocalDate.now() + ".csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < jTable1.getColumnCount(); i++) {
                    writer.write(jTable1.getColumnName(i));
                    if (i < jTable1.getColumnCount() - 1) writer.write(",");
                }
                writer.newLine();
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        Object val = jTable1.getValueAt(i, j);
                        writer.write((val != null ? val.toString().replace(",", " ") : ""));
                        if (j < jTable1.getColumnCount() - 1) writer.write(",");
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Exportado exitosamente.");
            } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }
    
    private void cargarDatosIniciales() {
        datosOriginales = reporteDAO.listarDetalleAsistencias();
        configurarCombos();
        aplicarFiltros(); 
    }
    
    private void configurarCombos() {
        jComboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "Todas las Sucursales", "Tienda Central", "Sucursal Norte", "Sucursal Sur" }));
        jComboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "Todo el Historial", "Hoy", "Últimos 7 Días" }));
    }
    
    private void aplicarFiltros() {
        String filtroSucursal = (String) jComboBox1.getSelectedItem();
        String filtroTiempo = (String) jComboBox2.getSelectedItem();
        List<Object[]> datosFiltrados = new ArrayList<>();
        int[] conteoDias = new int[7]; int maxAsistencia = 0; LocalDate hoy = LocalDate.now();
        
        if (datosOriginales != null) {
            for (Object[] fila : datosOriginales) {
                boolean cumple = true;
                String sucursalFila = (String) fila[1];
                if (!filtroSucursal.equals("Todas las Sucursales") && !sucursalFila.equalsIgnoreCase(filtroSucursal)) cumple = false;
                
                if (cumple) {
                    try {
                        String fechaStr = fila[2].toString(); LocalDate fechaFila = LocalDate.parse(fechaStr);
                        if (filtroTiempo.equals("Hoy") && !fechaFila.isEqual(hoy)) cumple = false;
                        else if (filtroTiempo.equals("Últimos 7 Días") && fechaFila.isBefore(hoy.minusDays(7))) cumple = false;
                        
                        if (cumple) {
                            DayOfWeek dia = fechaFila.getDayOfWeek();
                            int idx = dia.getValue() - 1; conteoDias[idx]++;
                            if (conteoDias[idx] > maxAsistencia) maxAsistencia = conteoDias[idx];
                        }
                    } catch (Exception e) { }
                }
                if (cumple) datosFiltrados.add(fila);
            }
        }
        llenarTabla(datosFiltrados);
        actualizarBarrasGrafico(conteoDias, maxAsistencia);
    }
    
    private void actualizarBarrasGrafico(int[] conteos, int max) {
        JProgressBar[] barras = {jProgressBar1, jProgressBar2, jProgressBar3, jProgressBar4, jProgressBar5, jProgressBar6, jProgressBar7};
        if (max == 0) max = 1; 
        for (int i = 0; i < 7; i++) {
            barras[i].setMaximum(max); barras[i].setValue(conteos[i]); barras[i].setToolTipText("Asistencias: " + conteos[i]);
        }
    }

    private void llenarTabla(List<Object[]> datos) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) modelo.addRow(fila);
    }
    
    private void reconstruirPanelGrafico() {
        jPanel1.removeAll(); jPanel1.setLayout(new BorderLayout(0, 20)); jPanel1.setBackground(COLOR_CARD);
        JLabel titulo = new JLabel("Tendencia Semanal");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16)); titulo.setForeground(new Color(50, 50, 50));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); jPanel1.add(titulo, BorderLayout.NORTH);
        JPanel panelBarras = new JPanel(new GridLayout(1, 7, 15, 0)); panelBarras.setOpaque(false);
        JProgressBar[] barras = {jProgressBar1, jProgressBar2, jProgressBar3, jProgressBar4, jProgressBar5, jProgressBar6, jProgressBar7};
        JLabel[] dias = {jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8};
        String[] nombresDias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < 7; i++) {
            JPanel diaPanel = new JPanel(new BorderLayout(0, 10)); diaPanel.setOpaque(false);
            JProgressBar bar = barras[i]; bar.setOrientation(JProgressBar.VERTICAL); bar.setPreferredSize(new Dimension(40, 150));
            bar.setBorderPainted(false); bar.setBackground(new Color(240, 242, 245)); bar.setForeground(COLOR_AZUL);
            JLabel lbl = dias[i]; lbl.setText(nombresDias[i]); lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lbl.setForeground(Color.GRAY);
            diaPanel.add(bar, BorderLayout.CENTER); diaPanel.add(lbl, BorderLayout.SOUTH);
            panelBarras.add(diaPanel);
        }
        jPanel1.add(panelBarras, BorderLayout.CENTER); jPanel1.revalidate(); jPanel1.repaint();
    }

    // --- AQUÍ ESTÁ LA MAGIA VISUAL (DISEÑO MODERNO) ---
    private void aplicarEstiloModerno() {
        this.setBackground(COLOR_FONDO);
        
        // Estilos Paneles Contenedores
        estilizarPanelBlanco(jPanel1);
        estilizarPanelBlanco(jPanel2);
        
        // Estilo Títulos
        estilizarTitulo(jLabel1);
        estilizarTitulo(jLabel9);
        
        // Estilo Combos
        estilizarCombo(jComboBox1);
        estilizarCombo(jComboBox2);
        
        // Estilo Botón
        Exportar.setText("EXPORTAR");
        Exportar.setBackground(COLOR_CARD); // Fondo blanco
        Exportar.setForeground(new Color(50, 50, 50));
        Exportar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        Exportar.setFocusPainted(false);
        Exportar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE_SUAVE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        Exportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // --- TRANSFORMACIÓN DE LA TABLA (LOOK WEB) ---
        
        // 1. Configuración General
        jTable1.setRowHeight(40); // Filas más altas (espaciado moderno)
        jTable1.setShowVerticalLines(false); // Solo líneas horizontales
        jTable1.setShowHorizontalLines(true);
        jTable1.setGridColor(COLOR_BORDE_SUAVE);
        jTable1.setSelectionBackground(new Color(240, 245, 255)); // Azul muy pálido al seleccionar
        jTable1.setSelectionForeground(Color.BLACK);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jTable1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder()); // Quitar borde del scroll
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        
        // 2. Encabezado (Header) Personalizado
        jTable1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.WHITE); // Fondo blanco
                lbl.setForeground(COLOR_TEXTO_GRIS); // Texto gris suave
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Negrita pequeña
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE_SUAVE)); // Solo línea abajo
                lbl.setHorizontalAlignment(SwingConstants.LEFT); // Alineado a la izquierda
                lbl.setPreferredSize(new Dimension(lbl.getWidth(), 40)); // Altura header
                return lbl;
            }
        });
        
        // 3. Celdas Personalizadas (Padding y alineación)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Padding izquierdo
                    
                    // ESTADO: Badge simulado (Color de texto)
                    if (column == 6) { // Columna Estado
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        String estado = (value != null) ? value.toString().toUpperCase() : "";
                        if (estado.contains("ASISTIO") || estado.contains("PUNTUAL")) lbl.setForeground(new Color(22, 163, 74)); // Verde
                        else if (estado.contains("FALTA")) lbl.setForeground(new Color(220, 38, 38)); // Rojo
                        else if (estado.contains("TARDE")) lbl.setForeground(new Color(202, 138, 4)); // Amarillo oscuro
                        else lbl.setForeground(Color.BLACK);
                    } else {
                        lbl.setForeground(new Color(50, 50, 50));
                        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                return c;
            }
        };
        
        // Aplicar render a todas las columnas
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
    
    private void estilizarPanelBlanco(JPanel panel) {
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE_SUAVE, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
    }
    
    private void estilizarTitulo(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(30, 41, 59));
    }
    
    private void estilizarCombo(JComboBox combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // En Swing puro los combos son difíciles de estilizar 100% planos sin librerías, 
        // pero esto ayuda.
        ((JComponent) combo.getRenderer()).setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar2 = new javax.swing.JProgressBar();
        jProgressBar3 = new javax.swing.JProgressBar();
        jProgressBar4 = new javax.swing.JProgressBar();
        jProgressBar5 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jProgressBar6 = new javax.swing.JProgressBar();
        jProgressBar7 = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Exportar = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Tendencia de Asistencia");

        jProgressBar1.setPreferredSize(new java.awt.Dimension(101, 111));

        jProgressBar2.setPreferredSize(new java.awt.Dimension(101, 111));

        jProgressBar3.setPreferredSize(new java.awt.Dimension(101, 111));

        jProgressBar4.setPreferredSize(new java.awt.Dimension(101, 111));

        jProgressBar5.setPreferredSize(new java.awt.Dimension(101, 111));

        jLabel2.setText("Lunes");

        jLabel3.setText("Martes");

        jLabel4.setText("Miercoles");

        jLabel5.setText("Jueves");

        jLabel6.setText("Viernes");

        jProgressBar6.setPreferredSize(new java.awt.Dimension(101, 111));

        jProgressBar7.setPreferredSize(new java.awt.Dimension(101, 111));

        jLabel7.setText("Sabado");

        jLabel8.setText("Domingo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jProgressBar7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jProgressBar6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(jProgressBar5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(19, 19, 19))
        );

        Exportar.setText("Exportar Reporte");
        Exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportarActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Empleado", "Sucursal", "Fecha", "Entrada", "Salida", "Total Horas", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Registro Detallado");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox2)
                    .addComponent(Exportar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void ExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ExportarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Exportar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JProgressBar jProgressBar4;
    private javax.swing.JProgressBar jProgressBar5;
    private javax.swing.JProgressBar jProgressBar6;
    private javax.swing.JProgressBar jProgressBar7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
