
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class panel_Administrador_Asignacion_Sucursal extends javax.swing.JPanel {

    private edu.UPAO.proyecto.dao.UsuarioDAOADM usuarioDAO = new edu.UPAO.proyecto.dao.UsuarioDAOADM();
    
    // COLORES MODERNOS
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_ROJO = new Color(239, 68, 68);
    private final Color COLOR_TEXTO = new Color(30, 41, 59);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    
    public panel_Administrador_Asignacion_Sucursal() {
        initComponents();
        reorganizarYEstilizar();
        configurarTabla();
        cargarSucursalesDesdeBD();
        cargarTodosLosEmpleados();
    }
    
     public panel_Administrador_Asignacion_Sucursal(int idAdministrador) {
        this();
    }
     
    private void reorganizarYEstilizar() {
        this.removeAll();
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        estilizarPanelBlanco(jPanel1);
        jPanel1.setLayout(new BorderLayout());
        JLabel lblTituloSelector = new JLabel("Filtrar por Sucursal");
        estilizarTitulo(lblTituloSelector);
        lblTituloSelector.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        jComboBox1.setBackground(COLOR_BLANCO);
        jComboBox1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jComboBox1.setPreferredSize(new Dimension(300, 40));
        JPanel containerCombo = new JPanel(new BorderLayout());
        containerCombo.setOpaque(false);
        containerCombo.add(jComboBox1, BorderLayout.WEST);
        jPanel1.removeAll(); 
        jPanel1.add(lblTituloSelector, BorderLayout.NORTH);
        jPanel1.add(containerCombo, BorderLayout.CENTER);
        headerPanel.add(jPanel1, BorderLayout.CENTER);
        
        // CUERPO
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setOpaque(false);
        estilizarPanelBlanco(jPanel2);
        jPanel2.setLayout(new BorderLayout(0, 10));
        jPanel2.removeAll(); 
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel lblTituloTabla = new JLabel("Empleados Asignados");
        estilizarTitulo(lblTituloTabla);
        estilizarBotonAccion(AsignarEmpleado, COLOR_AZUL);
        tableHeader.add(lblTituloTabla, BorderLayout.WEST);
        tableHeader.add(AsignarEmpleado, BorderLayout.EAST);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(COLOR_BLANCO);
        jPanel2.add(tableHeader, BorderLayout.NORTH);
        jPanel2.add(jScrollPane1, BorderLayout.CENTER);
        bodyPanel.add(jPanel2, BorderLayout.CENTER);
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(bodyPanel, BorderLayout.CENTER);
    }
    
    private void estilizarPanelBlanco(JPanel p) {
        p.setBackground(COLOR_BLANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
    }
    
    private void estilizarTitulo(JLabel l) {
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(COLOR_TEXTO);
    }
    
    private void estilizarBotonAccion(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarTabla() {
        jTable1.setRowHeight(40);
        jTable1.setShowVerticalLines(false);
        jTable1.setGridColor(new Color(240, 240, 240));
        jTable1.setSelectionBackground(new Color(240, 248, 255));
        jTable1.setSelectionForeground(Color.BLACK);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        jTable1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COLOR_BLANCO);
                lbl.setForeground(new Color(100, 116, 139));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
                lbl.setPreferredSize(new Dimension(lbl.getWidth(), 40));
                return lbl;
            }
        });

        DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Rol", "Sucursal", "Acción"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Solo la columna de botón es editable (clicable)
            }
        };
        jTable1.setModel(modelo);
        
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        // ASIGNAR RENDERER (Dibujo) Y EDITOR (Acción)
        jTable1.getColumn("Acción").setCellRenderer(new ButtonRenderer());
        jTable1.getColumn("Acción").setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    // --- RENDERIZADOR (Solo dibuja) ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { 
            setOpaque(true); 
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(Color.WHITE);
            setForeground(Color.BLACK); // Negro elegante
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Quitar");
            return this;
        }
    }

    // --- EDITOR (Detecta el clic) ---
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Quitar" : value.toString();
            button.setText(label);
            currentRow = row; // Guardamos la fila donde se hizo clic
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // AL HACER CLIC, LLAMAMOS A LA FUNCIÓN DE BORRAR
                quitarEmpleadoDeSucursal(currentRow);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    // --- LÓGICA DE DATOS ---
    
    private void cargarSucursalesDesdeBD() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Todas las Sucursales");
        List<Sucursal> listaSucursales = usuarioDAO.listarSucursales();
        for (Sucursal sucursal : listaSucursales) {
            jComboBox1.addItem(sucursal);
        }
    }
    
    private void cargarTodosLosEmpleados() {
        List<UsuarioADM> lista = usuarioDAO.listar();
        
        if (lista != null) {
            lista.removeIf(u -> u.getNombreRol() != null && u.getNombreRol().equalsIgnoreCase("Gerente"));
        }
        // ---------------------------
        
        cargarEmpleadosGenerico(lista);
    }
    
    private void cargarEmpleadosPorSucursal(int idSucursal) {
        List<UsuarioADM> todos = usuarioDAO.listar();
        List<UsuarioADM> filtrados = new java.util.ArrayList<>();
        for (UsuarioADM u : todos) {
            // Verificamos que sea de la sucursal Y que NO sea Gerente
            if (u.getIdSucursal() == idSucursal) {
                if (u.getNombreRol() != null && !u.getNombreRol().equalsIgnoreCase("Gerente")) {
                    filtrados.add(u);
                }
            }
        }
        cargarEmpleadosGenerico(filtrados);
    }
    
    private void cargarEmpleadosGenerico(List<UsuarioADM> lista) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        for (UsuarioADM usuario : lista) {
            if (usuario.getNombreSucursal() != null && !usuario.getNombreSucursal().equals("Sin Asignar")) {
                modelo.addRow(new Object[]{
                    usuario.getId(), usuario.getNombre(), usuario.getNombreRol(), usuario.getNombreSucursal(), "Quitar"
                });
            }
        }
    }
    
    private void quitarEmpleadoDeSucursal(int row) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        // Validación extra por si la tabla cambió
        if (row >= modelo.getRowCount()) return;
        
        int idUsuario = (int) modelo.getValueAt(row, 0);
        String nombreUsuario = (String) modelo.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Quitar a " + nombreUsuario + " de su sucursal?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // SQL: UPDATE empleado SET id_sucursal = NULL ...
            boolean exito = usuarioDAO.actualizarSucursal(idUsuario, null);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Asignación eliminada.");
                recargarSegunFiltro(); // Refrescar tabla
            } else {
                mostrarError("Error al actualizar BD.");
            }
        }
    }
    
    private void recargarSegunFiltro() {
        Object item = jComboBox1.getSelectedItem();
        if (item instanceof String && item.equals("Todas las Sucursales")) {
            cargarTodosLosEmpleados();
        } else if (item instanceof Sucursal) {
            cargarEmpleadosPorSucursal(((Sucursal) item).getId());
        }
    }
    
    private void mostrarDialogoAsignarEmpleado() {
        Object sucursalSeleccionada = jComboBox1.getSelectedItem();
        if (sucursalSeleccionada == null || sucursalSeleccionada instanceof String) {
            JOptionPane.showMessageDialog(this, "Seleccione una sucursal específica primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Sucursal sucursal = (Sucursal) sucursalSeleccionada;
        // AQUÍ ESTÁ LA MAGIA DE AGREGAR: LISTAMOS SOLO LOS QUE NO TIENEN SUCURSAL
        List<UsuarioADM> disponibles = usuarioDAO.listarSinSucursal();
        if (disponibles != null) {
            disponibles.removeIf(u -> u.getNombreRol() != null && u.getNombreRol().equalsIgnoreCase("Gerente"));
        }
        // ---------------------------------------
        
        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay empleados libres para asignar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        UsuarioADM seleccionado = (UsuarioADM) JOptionPane.showInputDialog(this, "Seleccione empleado:", "Asignar", JOptionPane.QUESTION_MESSAGE, null, disponibles.toArray(), disponibles.get(0));
        
        if (seleccionado != null) {
            boolean exito = usuarioDAO.actualizarSucursal(seleccionado.getId(), sucursal.getId());
            if (exito) {
                JOptionPane.showMessageDialog(this, "Empleado asignado.");
                recargarSegunFiltro();
            } else {
                mostrarError("Error al asignar.");
            }
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        AsignarEmpleado = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Seleccionar Sucursal");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Empleados Asignados:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Rol", "Fecha de Asignación", "Acción"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        AsignarEmpleado.setText("Asignar Empleado");
        AsignarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AsignarEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1035, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AsignarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(AsignarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void AsignarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AsignarEmpleadoActionPerformed
        mostrarDialogoAsignarEmpleado();
    }//GEN-LAST:event_AsignarEmpleadoActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        Object seleccion = jComboBox1.getSelectedItem();
        // Verificamos que sea un objeto Sucursal real y no el texto "Seleccione..."
        if (seleccion != null && seleccion instanceof Sucursal) {
            Sucursal sucursal = (Sucursal) seleccion;
            cargarEmpleadosPorSucursal(sucursal.getId());
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AsignarEmpleado;
    private javax.swing.JComboBox<Object> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
