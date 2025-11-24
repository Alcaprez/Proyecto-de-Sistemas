
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class panel_Administrador_Asignacion_Sucursal extends javax.swing.JPanel {
    
    private edu.UPAO.proyecto.dao.UsuarioDAOADM usuarioDAO = new edu.UPAO.proyecto.dao.UsuarioDAOADM();
    
    public panel_Administrador_Asignacion_Sucursal() {
        initComponents();
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
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        jTable1.getColumn("Acción").setCellRenderer(new ButtonRenderer());
        
        TableButtonEditor editor = new TableButtonEditor(new JCheckBox());
        editor.setActionListener(e -> {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                quitarEmpleadoDeSucursal(row);
            }
        });
        jTable1.getColumn("Acción").setCellEditor(editor);
        jTable1.setRowHeight(35);
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Quitar");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.BLACK); 
            setBackground(Color.WHITE);
            setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200,200,200), 1));
            if (isSelected) {
                setBackground(new Color(220, 53, 69));
                setForeground(Color.WHITE);
            }
            return this;
        }
    }
    
    private void cargarSucursalesDesdeBD() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Seleccione una sucursal...");
        
        // Llamada al DAO
        List<Sucursal> listaSucursales = usuarioDAO.listarSucursales();
        
        for (Sucursal sucursal : listaSucursales) {
            jComboBox1.addItem(sucursal);
        }
    }
    
    private void cargarEmpleadosPorSucursal(int idSucursal) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // 1. Traer TODOS los usuarios reales
        List<UsuarioADM> todosLosUsuarios = usuarioDAO.listar();
        
        boolean hayEmpleados = false;
        for (UsuarioADM usuario : todosLosUsuarios) {
            // 2. Filtrar solo los de la sucursal seleccionada
            if (usuario.getIdSucursal() == idSucursal) {
                hayEmpleados = true;
                modelo.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getNombreRol(),
                    usuario.getUltimoCambio().format(formatter),
                    "Quitar"
                });
            }
        }
        
        if (!hayEmpleados) {
            // Opcional: Mostrar mensaje o dejar tabla vacía
        }
    }
    
    private void quitarEmpleadoDeSucursal(int row) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        // Validar que la fila exista
        if (row >= modelo.getRowCount()) return;
        
        int idUsuario = (int) modelo.getValueAt(row, 0);
        String nombreUsuario = (String) modelo.getValueAt(row, 1);
        
        Object sucursalSeleccionada = jComboBox1.getSelectedItem();
        if (sucursalSeleccionada == null || sucursalSeleccionada instanceof String) return;
        
        Sucursal sucursal = (Sucursal) sucursalSeleccionada;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Quitar a " + nombreUsuario + " de " + sucursal.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // ACTUALIZAR BD: Enviar 'null' (0) como idSucursal
            boolean exito = usuarioDAO.actualizarSucursal(idUsuario, null);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Empleado removido exitosamente.");
                cargarEmpleadosPorSucursal(sucursal.getId()); // Recargar tabla
            } else {
                mostrarError("Error al actualizar la base de datos.");
            }
        }
    }
    
    private void mostrarDialogoAsignarEmpleado() {
        Object sucursalSeleccionada = jComboBox1.getSelectedItem();
        
        if (sucursalSeleccionada == null || sucursalSeleccionada instanceof String) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sucursal primero.", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Sucursal sucursal = (Sucursal) sucursalSeleccionada;
        
        // 1. TRAER USUARIOS SIN SUCURSAL DESDE BD
        List<UsuarioADM> disponibles = usuarioDAO.listarSinSucursal();
        
        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay empleados libres para asignar.", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        UsuarioADM seleccionado = (UsuarioADM) JOptionPane.showInputDialog(
            this, "Seleccione empleado:", "Asignar Empleado",
            JOptionPane.QUESTION_MESSAGE, null,
            disponibles.toArray(), disponibles.get(0)
        );
        
        if (seleccionado != null) {
            // ACTUALIZAR BD
            boolean exito = usuarioDAO.actualizarSucursal(seleccionado.getId(), sucursal.getId());
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Empleado asignado con éxito.");
                cargarEmpleadosPorSucursal(sucursal.getId());
            } else {
                mostrarError("Error al asignar empleado.");
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
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Empleados Asignados");

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
                .addGap(38, 38, 38)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 867, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(525, 525, 525)
                        .addComponent(AsignarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(AsignarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
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
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
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
