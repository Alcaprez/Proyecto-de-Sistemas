
package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GestionUsuarios extends javax.swing.JPanel {
    
    private DefaultTableModel modeloUsuarios;
    private DefaultTableModel modeloLogs;
    
    public GestionUsuarios() {
        initComponents();
        configurarTablaUsuarios();
        configurarTablaLogs();
        cargarDatosEjemplo();
    }
    
    private void configurarTablas() {
        configurarTablaUsuarios();
        configurarTablaLogs();
    }
    
    private void configurarTablaUsuarios() {
        modeloUsuarios = (DefaultTableModel) jTable1.getModel();
        
        
        jTable1.setRowHeight(45);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.setGridColor(new Color(230, 230, 230));
        jTable1.setSelectionBackground(new Color(230, 240, 255));
        jTable1.setShowGrid(true);
        jTable1.setIntercellSpacing(new Dimension(1, 1));
        
        
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new Color(25, 118, 210));
        jTable1.getTableHeader().setForeground(Color.WHITE);
        jTable1.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        
        if (jTable1.getColumnCount() == 6) {
            int[] anchos = {120, 150, 200, 100, 80, 130};
            for (int i = 0; i < anchos.length; i++) {
                jTable1.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
            
            
            jTable1.getColumnModel().getColumn(3).setCellRenderer(new RolRenderer());
            jTable1.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        }
    }
    
    private void configurarTablaLogs() {
        modeloLogs = (DefaultTableModel) tablaLogs.getModel();
        
        
        tablaLogs.setRowHeight(40);
        tablaLogs.setFont(new Font("Monospaced", Font.PLAIN, 11));
        tablaLogs.setGridColor(new Color(240, 240, 240));
        tablaLogs.setSelectionBackground(new Color(255, 245, 230));
        
        
        tablaLogs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaLogs.getTableHeader().setBackground(new Color(250, 250, 250));
        tablaLogs.getTableHeader().setForeground(new Color(60, 60, 60));
        
        
        if (tablaLogs.getColumnCount() == 5) {
            int[] anchos = {150, 180, 180, 150, 100};
            for (int i = 0; i < anchos.length; i++) {
                tablaLogs.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
            
            
            tablaLogs.getColumnModel().getColumn(4).setCellRenderer(new EstadoLogRenderer());
        }
    }
    
    
    class RolRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(CENTER);
            label.setOpaque(true);
            
            if (value != null) {
                String rol = value.toString();
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                
                switch (rol.toLowerCase()) {
                    case "admin":
                        label.setBackground(new Color(139, 0, 139));
                        label.setForeground(Color.WHITE);
                        break;
                    case "supervisor":
                        label.setBackground(new Color(70, 130, 180));
                        label.setForeground(Color.WHITE);
                        break;
                    case "empleado":
                        label.setBackground(new Color(100, 149, 237));
                        label.setForeground(Color.WHITE);
                        break;
                }
                
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
            return label;
        }
    }
    
    
    class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            if (value != null && "activo".equals(value.toString().toLowerCase())) {
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(34, 139, 34));
                label.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
            } else {
                label.setBackground(new Color(220, 20, 60));
                label.setForeground(Color.WHITE);
            }
            
            return label;
        }
    }
    
    
    class EstadoLogRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            if (value != null && "Exitoso".equals(value.toString())) {
                label.setForeground(new Color(34, 139, 34));
            } else {
                label.setForeground(new Color(220, 20, 60));
            }
            
            return label;
        }
    }
    
    
    private void agregarUsuario() {
        JTextField txtUsuario = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"admin", "supervisor", "empleado"});
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"activo", "inactivo"});
        
        Object[] campos = {
            "Usuario:", txtUsuario,
            "Nombre:", txtNombre,
            "Email:", txtEmail,
            "Rol:", comboRol,
            "Estado:", comboEstado
        };
        
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Agregar Nuevo Usuario", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String usuario = txtUsuario.getText().trim();
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String rol = comboRol.getSelectedItem().toString();
            String estado = comboEstado.getSelectedItem().toString();
            
            if (!usuario.isEmpty() && !nombre.isEmpty() && !email.isEmpty()) {
                String fechaActual = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                Object[] fila = {usuario, nombre, email, rol, estado, fechaActual};
                modeloUsuarios.addRow(fila);
                
                JOptionPane.showMessageDialog(this, 
                    "Usuario creado exitosamente\n" +
                    "Se han asignado los permisos según el rol: " + rol, 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                
                registrarLog(usuario, "Creación de usuario con rol " + rol, "Sistema");
            } else {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", 
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    
    private void editarPermisos() {
        int fila = jTable1.getSelectedRow();
        
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String usuario = modeloUsuarios.getValueAt(fila, 0).toString();
        String nombre = modeloUsuarios.getValueAt(fila, 1).toString();
        String rolActual = modeloUsuarios.getValueAt(fila, 3).toString();
        String estadoActual = modeloUsuarios.getValueAt(fila, 4).toString();
        
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"administrador", "gerente", "empleado"});
        comboRol.setSelectedItem(rolActual);
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"activo", "inactivo"});
        comboEstado.setSelectedItem(estadoActual);
        
        Object[] campos = {
            "Usuario: " + usuario + " (" + nombre + ")",
            "",
            "Nuevo Rol:", comboRol,
            "Estado:", comboEstado
        };
        
        int opcion = JOptionPane.showConfirmDialog(this, campos, 
            "Editar Permisos y Estado", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String nuevoRol = comboRol.getSelectedItem().toString();
            String nuevoEstado = comboEstado.getSelectedItem().toString();
            
            modeloUsuarios.setValueAt(nuevoRol, fila, 3);
            modeloUsuarios.setValueAt(nuevoEstado, fila, 4);
            
            
            String fechaActual = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            modeloUsuarios.setValueAt(fechaActual, fila, 5);
            
            String mensaje = "Permisos actualizados:\n";
            if (!rolActual.equals(nuevoRol)) {
                mensaje += "- Rol cambiado: " + rolActual + " → " + nuevoRol + "\n";
            }
            if (!estadoActual.equals(nuevoEstado)) {
                mensaje += "- Estado cambiado: " + estadoActual + " → " + nuevoEstado;
                if (nuevoEstado.equals("inactivo")) {
                    mensaje += " (Usuario bloqueado)";
                }
            }
            
            JOptionPane.showMessageDialog(this, mensaje, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            
            String accion = "Modificación de permisos: rol=" + nuevoRol + ", estado=" + nuevoEstado;
            registrarLog(usuario, accion, "Sistema");
        }
    }
    
    
    private void eliminarUsuario() {
        int fila = jTable1.getSelectedRow();
        
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String usuario = modeloUsuarios.getValueAt(fila, 0).toString();
        String nombre = modeloUsuarios.getValueAt(fila, 1).toString();
        String estadoActual = modeloUsuarios.getValueAt(fila, 4).toString();
        
        String[] opciones = {"Desactivar (Bloquear)", "Eliminar Definitivamente", "Cancelar"};
        
        int opcion = JOptionPane.showOptionDialog(this,
            "Usuario: " + usuario + " (" + nombre + ")\n" +
            "Estado actual: " + estadoActual + "\n\n" +
            "¿Qué desea hacer?",
            "Dar de Baja Usuario",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        if (opcion == 0) { 
            modeloUsuarios.setValueAt("inactivo", fila, 4);
            JOptionPane.showMessageDialog(this, 
                "Usuario bloqueado exitosamente\n" +
                "El usuario no podrá acceder al sistema", 
                "Usuario Desactivado", JOptionPane.INFORMATION_MESSAGE);
            
            registrarLog(usuario, "Usuario bloqueado/desactivado", "Sistema");
            
        } else if (opcion == 1) { 
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar permanentemente a:\n" +
                usuario + " (" + nombre + ")?\n\n" +
                "Esta acción NO se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                modeloUsuarios.removeRow(fila);
                JOptionPane.showMessageDialog(this, 
                    "Usuario eliminado de la base de datos", 
                    "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                
                registrarLog(usuario, "Usuario eliminado permanentemente", "Sistema");
            }
        }
    }
    
    private void registrarLog(String usuario, String accion, String ip) {
        String fecha = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Object[] log = {usuario, accion, fecha, ip, "Exitoso"};
        modeloLogs.insertRow(0, log);
    }
    
    private void cargarDatosEjemplo() {
        Object[][] usuarios = {
            {"administrador01", "Juan Pérez", "juan@empresa.com", "administrador", "activo", "2024-01-20 10:30"},
            {"gerente01", "María García", "maria@empresa.com", "gerente", "activo", "2024-01-20 09:15"},
            {"empleado01", "Carlos López", "carlos@empresa.com", "empleado", "activo", "2024-01-19 16:45"},
            {"empleado02", "Ana Martínez", "ana@empresa.com", "empleado", "inactivo", "2024-01-15 14:20"}
        };
        
        for (Object[] usuario : usuarios) {
            modeloUsuarios.addRow(usuario);
        }
        
        Object[][] logs = {
            {"administrador01", "Inicio de sesión", "2024-01-20 10:30:15", "192.168.1.100", "Exitoso"},
            {"gerente01", "Inicio de sesión", "2024-01-20 09:15:22", "192.168.1.101", "Exitoso"},
            {"empleado01", "Intento fallido", "2024-01-20 08:45:10", "192.168.1.102", "Fallido"},
            {"empleado02", "Inicio de sesión", "2024-01-19 16:45:30", "192.168.1.102", "Exitoso"},
        };
        
        for (Object[] log : logs) {
            modeloLogs.addRow(log);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnAgregarUsuario = new javax.swing.JButton();
        editarpermisos = new javax.swing.JButton();
        eliminarusuario = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaLogs = new javax.swing.JTable();

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        jLabel1.setText("Gestión de Usuarios y Permisos");

        btnAgregarUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAgregarUsuario.setText("Agregar Usuario");
        btnAgregarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarUsuarioActionPerformed(evt);
            }
        });

        editarpermisos.setText("Editar");
        editarpermisos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarpermisosActionPerformed(evt);
            }
        });

        eliminarusuario.setText("Eliminar");
        eliminarusuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarusuarioActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Usuario", "Nombre", "Email", "Rol", "Estado", "Ultimo acceso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jScrollPane2.setViewportView(jScrollPane1);

        tablaLogs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Usuario", "Accion", "Fecha y Hora", "IP", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tablaLogs);

        jScrollPane3.setViewportView(jScrollPane4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eliminarusuario)
                            .addComponent(editarpermisos))
                        .addGap(88, 88, 88))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregarUsuario)
                        .addGap(54, 54, 54))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(btnAgregarUsuario))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(editarpermisos)
                        .addGap(18, 18, 18)
                        .addComponent(eliminarusuario)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarUsuarioActionPerformed
        agregarUsuario();
    }//GEN-LAST:event_btnAgregarUsuarioActionPerformed

    private void editarpermisosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarpermisosActionPerformed
        editarPermisos();
    }//GEN-LAST:event_editarpermisosActionPerformed

    private void eliminarusuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarusuarioActionPerformed
        eliminarUsuario();
    }//GEN-LAST:event_eliminarusuarioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarUsuario;
    private javax.swing.JButton editarpermisos;
    private javax.swing.JButton eliminarusuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tablaLogs;
    // End of variables declaration//GEN-END:variables
}
