
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.RepositorioLog;
import edu.UPAO.proyecto.dao.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.HistorialCambio;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class panel_Administrador_Historial_Permisos extends javax.swing.JPanel {
    
    private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();
    private List<UsuarioADM> listaUsuariosReales;
    private List<UsuarioADM> listaUsuariosEnTabla; 
    
    // COLORES MODERNOS
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_TEXTO_GRIS = new Color(100, 116, 139);
    
    public panel_Administrador_Historial_Permisos() {
        initComponents();
        // Aplicar diseño ANTES de cargar datos
        aplicarEstiloModerno();
        configurarDiseño();
        
        configurarBuscador();
        refrescarDatosGlobales();
        
        configurarTablaInteraccion(); 
        mostrarMensajeInicial();
    }
    
    // --- MÉTODO DE ESTILO VISUAL CORREGIDO (BOTÓN LIMPIO) ---
    private void aplicarEstiloModerno() {
        this.setBackground(COLOR_FONDO);
        
        // Paneles contenedores
        jPanel1.setBackground(COLOR_BLANCO); 
        jPanel1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        jPanel2.setBackground(COLOR_BLANCO); 
        jPanel2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Títulos
        estilizarTitulo(jLabel1);
        estilizarTitulo(jLabel2);
        
        // --- BOTÓN ACTUALIZAR (CORREGIDO SIN BORDES) ---
        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.setIcon(null);
        btnActualizar.setBackground(COLOR_BLANCO);
        btnActualizar.setForeground(new Color(50, 50, 50)); // Gris Oscuro
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // QUITAMOS TODAS LAS LÍNEAS
        btnActualizar.setFocusPainted(false);   // Quita el recuadro de clic
        btnActualizar.setBorderPainted(false);  // Quita la línea del borde
        btnActualizar.setContentAreaFilled(true); 
        
        // Solo dejamos espacio interno (Padding)
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tabla Moderna
        jTable1.setRowHeight(35);
        jTable1.setShowVerticalLines(false);
        jTable1.setGridColor(new Color(230, 230, 230));
        jTable1.setSelectionBackground(new Color(240, 245, 255));
        jTable1.setSelectionForeground(Color.BLACK);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Encabezado Tabla
        jTable1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COLOR_BLANCO);
                lbl.setForeground(COLOR_TEXTO_GRIS);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                lbl.setPreferredSize(new Dimension(lbl.getWidth(), 40));
                return lbl;
            }
        });
        
        jScrollPane1.getViewport().setBackground(COLOR_BLANCO);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
    }
    
    private void estilizarTitulo(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(30, 41, 59));
    }
    
    private void refrescarDatosGlobales() {
        listaUsuariosReales = usuarioDAO.listar();
        listaUsuariosEnTabla = new ArrayList<>(listaUsuariosReales);
        filtrar();
        
        if (jTable1.getSelectedRow() != -1) {
            int fila = jTable1.getSelectedRow();
            if (fila < listaUsuariosEnTabla.size()) {
                UsuarioADM seleccionado = listaUsuariosEnTabla.get(fila);
                cargarHistorialDeUsuario(seleccionado.getId(), seleccionado.getNombre());
            }
        } else {
            mostrarMensajeInicial();
        }
    }
    
    private void configurarDiseño() {
        jPanel6.setLayout(new BoxLayout(jPanel6, BoxLayout.Y_AXIS));
        jPanel6.setBackground(new Color(245, 247, 250)); 
        jPanel6.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        jTextField1.setForeground(java.awt.Color.GRAY);
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().equals("Buscar Usuario...")) {
                    jTextField1.setText("");
                    jTextField1.setForeground(java.awt.Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Buscar Usuario...");
                    jTextField1.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }
    
    private void configurarBuscador() {
        jComboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "Todos", "ADMINISTRADOR", "CAJERO", "GERENTE" }));

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        
        jComboBox1.addActionListener(e -> filtrar());
    }
    
    private void configurarTablaInteraccion() {
        jTable1.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                int fila = jTable1.getSelectedRow();
                if (fila < listaUsuariosEnTabla.size()) {
                    UsuarioADM seleccionado = listaUsuariosEnTabla.get(fila);
                    cargarHistorialDeUsuario(seleccionado.getId(), seleccionado.getNombre());
                }
            }
        });
    }

    private void filtrar() {
        String texto = jTextField1.getText().trim();
        if(texto.equals("Buscar Usuario...")) texto = "";
        
        String rolFiltro = (String) jComboBox1.getSelectedItem();
        
        List<UsuarioADM> filtrados = new ArrayList<>();
        
        if(listaUsuariosReales != null) {
            for(UsuarioADM u : listaUsuariosReales) {
                String nombre = (u.getNombre() != null) ? u.getNombre() : "";
                String rol = (u.getNombreRol() != null) ? u.getNombreRol() : "";
                
                boolean cumpleNombre = texto.isEmpty() || nombre.toLowerCase().contains(texto.toLowerCase());
                boolean cumpleRol = true;
                
                if(rolFiltro != null && !rolFiltro.equals("Todos")) {
                    cumpleRol = rol.equalsIgnoreCase(rolFiltro);
                }
                
                if(cumpleNombre && cumpleRol) {
                    filtrados.add(u);
                }
            }
        }
        
        listaUsuariosEnTabla = filtrados;
        cargarTablaEstadoActual(filtrados);
    }

    private void cargarTablaEstadoActual(List<UsuarioADM> usuarios) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        
        for (UsuarioADM u : usuarios) {
            modelo.addRow(new Object[]{
                u.getNombre(),       
                u.getNombreRol(),    
                u.getNombreSucursal(), 
                u.getEstatus()       
            });
        }
    }

    private void mostrarMensajeInicial() {
        jPanel6.removeAll();
        
        JLabel lbl = new JLabel("Seleccione un usuario para ver detalles");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        lbl.setForeground(COLOR_TEXTO_GRIS);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        
        jPanel6.add(Box.createVerticalGlue());
        jPanel6.add(lbl);
        jPanel6.add(Box.createVerticalGlue());
        
        jPanel6.revalidate();
        jPanel6.repaint();
    }

    private void cargarHistorialDeUsuario(int idUsuario, String nombreUsuario) {
        jPanel6.removeAll();
        List<HistorialCambio> todosLosLogs = RepositorioLog.obtenerLogs();
        List<HistorialCambio> logsUsuario = new ArrayList<>();
        
        for (HistorialCambio log : todosLosLogs) {
            if (log.getIdUsuario() == idUsuario) {
                logsUsuario.add(log);
            }
        }
        
        JLabel titulo = new JLabel("Historial de: " + nombreUsuario);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COLOR_AZUL);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        jPanel6.add(titulo);
        jPanel6.add(Box.createVerticalStrut(15));
        
        if (logsUsuario.isEmpty()) {
            JPanel panelVacio = new JPanel();
            panelVacio.setOpaque(false);
            panelVacio.setLayout(new BoxLayout(panelVacio, BoxLayout.Y_AXIS));
            panelVacio.setAlignmentX(LEFT_ALIGNMENT);
            
            JLabel lbl = new JLabel("Sin cambios registrados.");
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lbl.setForeground(java.awt.Color.GRAY);
            
            panelVacio.add(lbl);
            jPanel6.add(panelVacio);
            
        } else {
            for (HistorialCambio h : logsUsuario) {
                jPanel6.add(crearTarjetaLog(h));
                jPanel6.add(Box.createVerticalStrut(10));
            }
        }
        
        jPanel6.revalidate();
        jPanel6.repaint();
    }
    
    private JPanel crearTarjetaLog(HistorialCambio h) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_BLANCO);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 90)); 
        
        // DISEÑO TARJETA MODERNA
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_AZUL), // Franja azul
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230), 1), // Borde gris suave
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding interno
            )
        ));
        
        JLabel lblDesc = new JLabel("<html>" + h.getDescripcionCambio() + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(50, 50, 50));
        
        String hora = h.getFechaCambio().format(DateTimeFormatter.ofPattern("hh:mm a"));
        JLabel lblSub = new JLabel("Hora: " + hora);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Color.GRAY);
        
        String motivoTexto = (h.getMotivo() != null && !h.getMotivo().isEmpty()) ? "Motivo: " + h.getMotivo() : "";
        JLabel lblMotivo = new JLabel(motivoTexto);
        lblMotivo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblMotivo.setForeground(new Color(100, 100, 100));
        
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(5));
        if(!motivoTexto.isEmpty()) {
            card.add(lblMotivo);
            card.add(Box.createVerticalStrut(3));
        }
        card.add(lblSub);
        
        return card;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        btnActualizar = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Estado de Roles y Permisos");

        jTextField1.setText("Buscar Usuario...");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Usuario", "Rol Actual", "Sucursal", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(39, 39, 39))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Historial de Cambio");

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane2.setViewportView(jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
       refrescarDatosGlobales();
    }//GEN-LAST:event_btnActualizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
