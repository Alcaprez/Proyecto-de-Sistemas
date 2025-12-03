package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.DAO.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class panel_Administrador_Asignacion_Sucursal extends javax.swing.JPanel {

private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();
    public panel_Administrador_Asignacion_Sucursal() {
        initComponents();

        // TRUCO CLAVE: Renderizador para mostrar el nombre SIN tocar la clase Sucursal
        jComboBox1.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Sucursal) {
                    // Aquí extraemos el nombre manualmente para mostrarlo
                    setText(((Sucursal) value).getNombre());
                }
                return this;
            }
        });

        configurarTabla();
        cargarSucursalesDesdeBD();
    }

    public panel_Administrador_Asignacion_Sucursal(int idAdministrador) {
        this();
    }

    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Rol", "Fecha Asignación", "Acción"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        jTable1.setModel(modelo);

        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(250);

        jTable1.getColumn("Acción").setCellRenderer(new ButtonRenderer());

        TableButtonEditor editor = new TableButtonEditor(new JCheckBox());
        editor.setActionListener(e -> {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                if (jTable1.getCellEditor() != null) {
                    jTable1.getCellEditor().stopCellEditing();
                }
                quitarEmpleadoDeSucursal(row);
            }
        });
        jTable1.getColumn("Acción").setCellEditor(editor);
        jTable1.setRowHeight(35);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Quitar");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE); 
            setBackground(new Color(220, 53, 69)); 
            return this;
        }
    }
    
    private void cargarSucursalesDesdeBD() {
        jComboBox1.removeAllItems();
        // Agregamos un objeto "falso" o null para el texto inicial, o simplemente el texto
        // Para evitar problemas de tipos, usaremos un ítem nulo si es necesario o validamos en el evento
        jComboBox1.addItem("Seleccione una sucursal..."); 
        
        List<Sucursal> listaSucursales = usuarioDAO.listarSucursales();
        for (Sucursal sucursal : listaSucursales) {
            jComboBox1.addItem(sucursal);
        }
    }
    
    private void cargarEmpleadosPorSucursal(int idSucursal) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        List<UsuarioADM> todosLosUsuarios = usuarioDAO.listar();
        for (UsuarioADM usuario : todosLosUsuarios) {
            if (usuario.getIdSucursal() == idSucursal) {
                String fechaStr = (usuario.getUltimoCambio() != null) ? usuario.getUltimoCambio().format(formatter) : "";
                modelo.addRow(new Object[]{usuario.getId(), usuario.getNombre(), usuario.getNombreRol(), fechaStr, "Quitar"});
            }
        }
    }
    
    private void quitarEmpleadoDeSucursal(int row) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        if (row >= modelo.getRowCount()) return;
        
        int idUsuario = (int) modelo.getValueAt(row, 0);
        String nombreUsuario = (String) modelo.getValueAt(row, 1);
        Object item = jComboBox1.getSelectedItem();
        
        if (!(item instanceof Sucursal)) return;
        Sucursal sucursal = (Sucursal) item;
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Quitar a " + nombreUsuario + " de " + sucursal.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioDAO.actualizarSucursal(idUsuario, 0)) {
                JOptionPane.showMessageDialog(this, "Empleado removido.");
                cargarEmpleadosPorSucursal(sucursal.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.");
            }
        }
    }
    
    private void mostrarDialogoAsignarEmpleado() {
        Object item = jComboBox1.getSelectedItem();
        if (!(item instanceof Sucursal)) {
            JOptionPane.showMessageDialog(this, "Seleccione una sucursal primero.");
            return;
        }
        Sucursal sucursal = (Sucursal) item;
        
        List<UsuarioADM> disponibles = usuarioDAO.listarSinSucursal();
        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay empleados libres.");
            return;
        }
        
        UsuarioADM seleccionado = (UsuarioADM) JOptionPane.showInputDialog(
            this, "Seleccione empleado:", "Asignar", JOptionPane.QUESTION_MESSAGE, null, disponibles.toArray(), disponibles.get(0)
        );
        
        if (seleccionado != null) {
            if (usuarioDAO.actualizarSucursal(seleccionado.getId(), sucursal.getId())) {
                JOptionPane.showMessageDialog(this, "Empleado asignado.");
                cargarEmpleadosPorSucursal(sucursal.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Error al asignar.");
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
