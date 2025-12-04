
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class panel_Administrador_Vista_Usuarios extends javax.swing.JPanel {

    private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();
    private List<UsuarioADM> listaUsuariosReales;
    // COLORES MODERNOS
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_TEXTO_GRIS = new Color(100, 116, 139);
    
    public panel_Administrador_Vista_Usuarios() {
        initComponents();
        aplicarEstiloModerno();
        configurarTabla();
        configurarBusqueda();
        // 2. CARGAR DATOS
        cargarTodosLosUsuarios();
    }
    
    private void configurarTabla() {
        // Modelo de tabla no editable
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Rol", "Sucursal", "Estatus", "Último Cambio"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        jTable1.setModel(modelo);
        
        // Ocultar ID
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        
        // Anchos preferidos
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120); // Rol
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(150); // Sucursal
    }
    
    private void configurarBusqueda() {
        BuscarporNombre.setForeground(java.awt.Color.GRAY);
        BuscarporNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                if (BuscarporNombre.getText().equals("Buscar por nombre...")) {
                    BuscarporNombre.setText(""); BuscarporNombre.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                if (BuscarporNombre.getText().isEmpty()) {
                    BuscarporNombre.setText("Buscar por nombre..."); BuscarporNombre.setForeground(Color.GRAY);
                }
            }
        });
        
        // Filtrado en tiempo real al escribir
        BuscarporNombre.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { aplicarFiltros(); }
        });
    }
    
    private void cargarTodosLosUsuarios() {
        // 1. Traer datos de la BD
        listaUsuariosReales = usuarioDAO.listar(); 
        // Eliminamos de la lista a cualquier usuario cuyo rol sea "Gerente" (ignorando mayúsculas/minúsculas)
        if (listaUsuariosReales != null) {
            listaUsuariosReales.removeIf(u -> 
                u.getNombreRol() != null && 
                u.getNombreRol().equalsIgnoreCase("Gerente")
            );
        }
        // 2. Llenar tabla
        mostrarUsuariosEnTabla(listaUsuariosReales);
        
        // 3. Llenar combos dinámicamente
        cargarCombosDinamicos();
    }
    
    private void cargarCombosDinamicos() {
        Set<String> rolesUnicos = new HashSet<>();
        Set<String> sucursalesUnicas = new HashSet<>();
        
        if (listaUsuariosReales != null) {
            for (UsuarioADM u : listaUsuariosReales) {
                if (u.getNombreRol() != null) rolesUnicos.add(u.getNombreRol());
                if (u.getNombreSucursal() != null) sucursalesUnicas.add(u.getNombreSucursal());
            }
        }
        
        // Llenar Combo Roles
        Todoslosroles.setModel(new DefaultComboBoxModel<>(new String[]{"Todos los roles"}));
        for (String rol : rolesUnicos) Todoslosroles.addItem(rol);
        
        // Llenar Combo Sucursales
        TodaslasSucursales.setModel(new DefaultComboBoxModel<>(new String[]{"Todas las sucursales"}));
        for (String suc : sucursalesUnicas) TodaslasSucursales.addItem(suc);
        
        // Llenar Combo Estatus
        TodoslosEstatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todos", "ACTIVO", "INACTIVO"}));
    }
    
    private void aplicarFiltros() {
        String texto = BuscarporNombre.getText().trim();
        if (texto.equals("Buscar por nombre...")) texto = "";
        
        String rolSel = (String) Todoslosroles.getSelectedItem();
        String sucSel = (String) TodaslasSucursales.getSelectedItem();
        String estSel = (String) TodoslosEstatus.getSelectedItem();
        
        List<UsuarioADM> filtrados = new ArrayList<>();
        
        if (listaUsuariosReales != null) {
            for (UsuarioADM u : listaUsuariosReales) {
                boolean cumpleNombre = texto.isEmpty() || u.getNombre().toLowerCase().contains(texto.toLowerCase());
                
                boolean cumpleRol = true;
                if (rolSel != null && !rolSel.equals("Todos los roles")) {
                    cumpleRol = u.getNombreRol().equalsIgnoreCase(rolSel);
                }
                
                boolean cumpleSuc = true;
                if (sucSel != null && !sucSel.equals("Todas las sucursales")) {
                    cumpleSuc = u.getNombreSucursal().equalsIgnoreCase(sucSel);
                }
                
                boolean cumpleEst = true;
                if (estSel != null && !estSel.equals("Todos")) {
                    cumpleEst = u.getEstatus().equalsIgnoreCase(estSel);
                }
                
                if (cumpleNombre && cumpleRol && cumpleSuc && cumpleEst) {
                    filtrados.add(u);
                }
            }
        }
        mostrarUsuariosEnTabla(filtrados);
    }
    
    private void mostrarUsuariosEnTabla(List<UsuarioADM> usuarios) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (UsuarioADM u : usuarios) {
            modelo.addRow(new Object[]{
                u.getId(),
                u.getNombre(), // Aquí se mostrará el nombre real o DNI según tu DAO
                u.getNombreRol(),
                u.getNombreSucursal(),
                u.getEstatus(),
                u.getUltimoCambio().format(formatter)
            });
        }
        jLabel1.setText("Vista Global de Usuarios (" + usuarios.size() + " encontrados)");
    }
    
    // --- DISEÑO MODERNO ---
    private void aplicarEstiloModerno() {
        this.setBackground(COLOR_FONDO);
        
        jPanel1.setBackground(COLOR_BLANCO);
        jPanel1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel1.setForeground(new Color(30, 41, 59));
        
        // Estilo Tabla
        jTable1.setRowHeight(40);
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
        
        // Estilo Combos
        estilizarCombo(Todoslosroles);
        estilizarCombo(TodaslasSucursales);
        estilizarCombo(TodoslosEstatus);
    }
    
    private void estilizarCombo(javax.swing.JComboBox combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
