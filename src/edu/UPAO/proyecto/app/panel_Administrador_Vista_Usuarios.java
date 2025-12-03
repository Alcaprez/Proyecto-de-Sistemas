
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class panel_Administrador_Vista_Usuarios extends javax.swing.JPanel {

  private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();
    private List<UsuarioADM> listaUsuariosReales;
    
    public panel_Administrador_Vista_Usuarios() {
        initComponents();
        // Ya no usamos datos simulados manuales
        configurarTabla();
        configurarBusqueda();
        
        // Esto carga los datos Y llena los combos automáticamente
        cargarTodosLosUsuarios();
    }
    
    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Rol", "Sucursal", "Estatus", "Último Cambio"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modelo);
        
        // Ocultar columna ID
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        
        // Ajustar anchos
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
    }
    
    private void configurarBusqueda() {
        BuscarporNombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
        
        BuscarporNombre.setForeground(java.awt.Color.GRAY);
        BuscarporNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (BuscarporNombre.getText().equals("Buscar por nombre...")) {
                    BuscarporNombre.setText("");
                    BuscarporNombre.setForeground(java.awt.Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (BuscarporNombre.getText().isEmpty()) {
                    BuscarporNombre.setText("Buscar por nombre...");
                    BuscarporNombre.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }
    
    private void cargarTodosLosUsuarios() {
        // 1. Traemos los datos de la BD
        listaUsuariosReales = usuarioDAO.listar(); 
        
        // 2. Llenamos la tabla
        mostrarUsuariosEnTabla(listaUsuariosReales);
        
        // 3. ¡MAGIA! Llenamos los combos basados en lo que acabamos de traer
        cargarCombosDinamicos();
    }
    
    private void cargarCombosDinamicos() {
        // Usamos 'Set' para que no se repitan los nombres
        Set<String> rolesUnicos = new HashSet<>();
        Set<String> sucursalesUnicas = new HashSet<>();
        
        // Recorremos la lista real para ver qué roles y sucursales existen
        if (listaUsuariosReales != null) {
            for (UsuarioADM u : listaUsuariosReales) {
                if (u.getNombreRol() != null) rolesUnicos.add(u.getNombreRol());
                if (u.getNombreSucursal() != null) sucursalesUnicas.add(u.getNombreSucursal());
            }
        }
        
        // Llenar Combo Roles
        Todoslosroles.removeAllItems();
        Todoslosroles.addItem("Todos los roles");
        for (String rol : rolesUnicos) {
            Todoslosroles.addItem(rol);
        }
        
        // Llenar Combo Sucursales
        TodaslasSucursales.removeAllItems();
        TodaslasSucursales.addItem("Todas las sucursales");
        for (String suc : sucursalesUnicas) {
            TodaslasSucursales.addItem(suc);
        }
        
        // Llenar Combo Estatus (Este sí puede ser fijo o dinámico)
        TodoslosEstatus.removeAllItems();
        TodoslosEstatus.addItem("Todos");
        TodoslosEstatus.addItem("ACTIVO"); // Mayúsculas como en tu BD
        TodoslosEstatus.addItem("INACTIVO");
    }
    
    private void aplicarFiltros() {
        String nombreBusqueda = BuscarporNombre.getText();
        if (nombreBusqueda.equals("Buscar por nombre...")) nombreBusqueda = "";

        String nombreRolSel = (String) Todoslosroles.getSelectedItem();
        String nombreSucSel = (String) TodaslasSucursales.getSelectedItem();
        String estatusSel = (String) TodoslosEstatus.getSelectedItem();

        List<UsuarioADM> usuariosFiltrados = new ArrayList<>();

        if (listaUsuariosReales != null) {
            for (UsuarioADM usuario : listaUsuariosReales) {
                boolean cumple = true;

                // Filtro Nombre
                if (!nombreBusqueda.isEmpty() && 
                    !usuario.getNombre().toLowerCase().contains(nombreBusqueda.toLowerCase())) {
                    cumple = false;
                }

                // Filtro Rol
                if (nombreRolSel != null && !nombreRolSel.equals("Todos los roles")) {
                    if (usuario.getNombreRol() == null || 
                        !usuario.getNombreRol().equalsIgnoreCase(nombreRolSel)) {
                        cumple = false;
                    }
                }

                // Filtro Sucursal
                if (nombreSucSel != null && !nombreSucSel.equals("Todas las sucursales")) {
                    if (usuario.getNombreSucursal() == null || 
                        !usuario.getNombreSucursal().equalsIgnoreCase(nombreSucSel)) {
                        cumple = false;
                    }
                }

                // Filtro Estatus
                if (estatusSel != null && !estatusSel.equals("Todos")) {
                     if (usuario.getEstatus() == null || 
                        !usuario.getEstatus().equalsIgnoreCase(estatusSel)) {
                        cumple = false;
                    }
                }

                if (cumple) usuariosFiltrados.add(usuario);
            }
        }
        mostrarUsuariosEnTabla(usuariosFiltrados);
    }
    
    private void mostrarUsuariosEnTabla(List<UsuarioADM> usuarios) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (UsuarioADM usuario : usuarios) {
            modelo.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getNombreRol(),
                usuario.getNombreSucursal(),
                usuario.getEstatus(),
                usuario.getUltimoCambio().format(formatter)
            });
        }
        
        jLabel1.setText(String.format("Vista Global de Usuarios (%d encontrados)", usuarios.size()));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        BuscarporNombre = new javax.swing.JTextField();
        Todoslosroles = new javax.swing.JComboBox<>();
        TodaslasSucursales = new javax.swing.JComboBox<>();
        TodoslosEstatus = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Vista Global de Usuarios");

        BuscarporNombre.setText("Buscar por nombre...");

        Todoslosroles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        Todoslosroles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TodoslosrolesActionPerformed(evt);
            }
        });

        TodaslasSucursales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TodaslasSucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TodaslasSucursalesActionPerformed(evt);
            }
        });

        TodoslosEstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TodoslosEstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TodoslosEstatusActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Rol", "Sucursal", "Estatus", "Ultimo cambio"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Todoslosroles, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(TodaslasSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(TodoslosEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1019, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                        .addComponent(BuscarporNombre, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(BuscarporNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Todoslosroles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TodaslasSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TodoslosEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TodoslosrolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TodoslosrolesActionPerformed
        aplicarFiltros();
    }//GEN-LAST:event_TodoslosrolesActionPerformed

    private void TodaslasSucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TodaslasSucursalesActionPerformed
        aplicarFiltros();
    }//GEN-LAST:event_TodaslasSucursalesActionPerformed

    private void TodoslosEstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TodoslosEstatusActionPerformed
        aplicarFiltros();
    }//GEN-LAST:event_TodoslosEstatusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BuscarporNombre;
    private javax.swing.JComboBox<String> TodaslasSucursales;
    private javax.swing.JComboBox<String> TodoslosEstatus;
    private javax.swing.JComboBox<String> Todoslosroles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
