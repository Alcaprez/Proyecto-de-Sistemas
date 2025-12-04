package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.dao.RepositorioLog;
import edu.UPAO.proyecto.dao.UsuarioDAOADM;
import edu.UPAO.proyecto.modelo.HistorialCambio;
import edu.UPAO.proyecto.modelo.Permiso;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class panel_Administrador_Roles_y_Permisos extends javax.swing.JPanel {

    private int idAdministrador = 1; 
    private UsuarioADM usuarioSeleccionado;
    private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();
    private List<UsuarioADM> usuariosReales;
    private List<Permiso> permisosLogicos; 
    private Map<String, UsuarioADM> mapaUsuariosCombo;
    private JPanel panelPermisosContenido;
    private boolean isUpdating = false; 
    // COLORES
    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_NARANJA = new Color(249, 115, 22);
    private final Color COLOR_TEXTO_GRIS = new Color(100, 116, 139);
    private final Color COLOR_TEXTO_OSCURO = new Color(50, 50, 50);
    
    public panel_Administrador_Roles_y_Permisos() {
        initComponents();
        // 1. DISEÑO ESTÁTICO (NO SE MUEVE)
        reorganizarLayoutCompleto(); 
        
        inicializarPermisosLogicos(); 
        configurarBuscador(); 
        cargarUsuariosDesdeBD();
        
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        cargarHistorialVisualGlobal();
        
        if (usuarioSeleccionado == null) {
             mostrarMensajeInicial();
             limpiarPanelPermisos();
        }
    }

    private void reorganizarLayoutCompleto() {
        this.removeAll(); 
        this.setLayout(new GridBagLayout()); 
        this.setBackground(COLOR_FONDO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Más margen lateral
        gbc.fill = GridBagConstraints.BOTH;
        
        // --- COLUMNA IZQUIERDA (35% Ancho - Más espacio) ---
        JPanel panelIzqContainer = new JPanel();
        panelIzqContainer.setLayout(new BoxLayout(panelIzqContainer, BoxLayout.Y_AXIS));
        panelIzqContainer.setOpaque(false);
        
        estilizarPanelBlanco(jPanel1); // Selector
        jPanel1.setMaximumSize(new Dimension(5000, 80)); 
        
        estilizarPanelBlanco(jPanel2); // Info Usuario
        jPanel2.setMaximumSize(new Dimension(5000, 250)); 
        
        estilizarBoton(CambiarRol);
        
        panelIzqContainer.add(jPanel1);
        panelIzqContainer.add(Box.createVerticalStrut(15));
        panelIzqContainer.add(jPanel2);
        panelIzqContainer.add(Box.createVerticalGlue()); 
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2; 
        gbc.weightx = 0.35; // AUMENTADO DE 0.3 A 0.35 (Para que quepa el Combo)
        gbc.weighty = 1.0;
        panelIzqContainer.setPreferredSize(new Dimension(0, 0)); 
        this.add(panelIzqContainer, gbc);
        
        // --- COLUMNA DERECHA (65% Ancho) ---
        
        // 1. Panel Permisos (Arriba Derecha) - 40% Altura
        estilizarPanelBlanco(jPanel3);
        
        panelPermisosContenido = new JPanel();
        panelPermisosContenido.setLayout(new BoxLayout(panelPermisosContenido, BoxLayout.Y_AXIS));
        panelPermisosContenido.setBackground(COLOR_BLANCO);
        
        // QUIMAMOS EL JSCROLLPANE AQUÍ (Directo al panel)
        jPanel3.removeAll();
        jPanel3.setLayout(new BorderLayout());
        JLabel lblTitPermisos = new JLabel("Permisos Actuales");
        lblTitPermisos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitPermisos.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Usamos un contenedor intermedio para centrar verticalmente si hay pocos items
        JPanel contenedorCentrado = new JPanel(new BorderLayout());
        contenedorCentrado.setBackground(COLOR_BLANCO);
        contenedorCentrado.add(panelPermisosContenido, BorderLayout.NORTH);
        
        jPanel3.add(lblTitPermisos, BorderLayout.NORTH);
        jPanel3.add(contenedorCentrado, BorderLayout.CENTER); // Sin Scroll
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.65; // Resto del ancho
        gbc.weighty = 0.4; // 40% Altura (Ajustado)
        jPanel3.setPreferredSize(new Dimension(0, 0)); 
        this.add(jPanel3, gbc);
        
        // 2. Panel Historial (Abajo Derecha) - 60% Altura
        JPanel panelHistorialContainer = new JPanel(new BorderLayout());
        panelHistorialContainer.setBackground(COLOR_BLANCO);
        panelHistorialContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        jPanel4.setBackground(new Color(245, 247, 250));
        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));
        
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(new Color(245, 247, 250));
        jScrollPane1.setViewportView(jPanel4); 
        
        jLabel14.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        panelHistorialContainer.add(jLabel14, BorderLayout.NORTH);
        panelHistorialContainer.add(jScrollPane1, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.65;
        gbc.weighty = 0.6; // 60% Altura (Ajustado)
        panelHistorialContainer.setPreferredSize(new Dimension(0, 0));
        this.add(panelHistorialContainer, gbc);
    }
    
    private void estilizarPanelBlanco(JPanel p) {
        p.setBackground(COLOR_BLANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
    }
    
    private void estilizarTitulo(JLabel label) {
        if(label != null) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(COLOR_TEXTO_OSCURO);
        }
    }
    
    private void estilizarBoton(JButton btn) {
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- LÓGICA ---
    
    private void configurarBuscador() {
        mapaUsuariosCombo = new HashMap<>();
    }

    private void cargarUsuariosDesdeBD() {
        isUpdating = true; 
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Seleccione un usuario...");
        mapaUsuariosCombo = new HashMap<>();
        
        usuariosReales = usuarioDAO.listar();
        
        for (UsuarioADM u : usuariosReales) {
            if (u.getNombreRol() != null && u.getNombreRol().equalsIgnoreCase("GERENTE")) {
                continue;
            }
            String item = u.getNombre() + " [" + u.getNombreRol() + "]";
            if (!mapaUsuariosCombo.containsKey(item)) {
                jComboBox1.addItem(item);
                mapaUsuariosCombo.put(item, u);
            }
        }
        isUpdating = false; 
    }
    
    private void mostrarInformacionUsuario(UsuarioADM usuario) {
        this.usuarioSeleccionado = usuario;
        jLabel6.setText(usuario.getNombre());
        jLabel7.setText(usuario.getNombreSucursal());
        jLabel8.setText(usuario.getNombreRol()); 
        
        cargarPermisosVisuales(usuario.getNombreRol());
        cargarHistorialDeUsuario(usuario.getId(), usuario.getNombre());
    }
    
    private void limpiarInformacionUsuario() {
        if (isUpdating) return; 
        this.usuarioSeleccionado = null;
        jLabel6.setText("-");
        jLabel7.setText("-");
        jLabel8.setText("-");
        limpiarPanelPermisos();
        mostrarMensajeInicial();
    }

    private void cargarPermisosVisuales(String rolTexto) {
        int idRolLogico = 0;
        if (rolTexto.equalsIgnoreCase("ADMINISTRADOR")) idRolLogico = 1;
        else if (rolTexto.equalsIgnoreCase("CAJERO")) idRolLogico = 2;
        else if (rolTexto.equalsIgnoreCase("GERENTE")) idRolLogico = 3;
        
        panelPermisosContenido.removeAll();
        panelPermisosContenido.add(Box.createVerticalStrut(10));
        
        boolean tienePermisos = false;
        for (Permiso p : permisosLogicos) {
            if (p.getIdRol() == idRolLogico) {
                tienePermisos = true;
                
                JPanel pPanel = new JPanel();
                pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
                pPanel.setBackground(COLOR_BLANCO); 
                pPanel.setAlignmentX(LEFT_ALIGNMENT);
                pPanel.setMaximumSize(new Dimension(1000, 30)); 
                
                JLabel check = new JLabel("✔");
                check.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
                check.setForeground(new Color(34, 177, 76)); 
                
                JLabel texto = new JLabel(p.getNombre());
                texto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                texto.setForeground(COLOR_TEXTO_OSCURO); 
                
                pPanel.add(check);
                pPanel.add(Box.createHorizontalStrut(10)); 
                pPanel.add(texto);
                
                panelPermisosContenido.add(pPanel);
                panelPermisosContenido.add(Box.createVerticalStrut(5)); 
            }
        }
        
        if (!tienePermisos) {
            JLabel lbl = new JLabel("Sin permisos configurados.");
            lbl.setForeground(Color.GRAY);
            panelPermisosContenido.add(lbl);
        }
        
        panelPermisosContenido.add(Box.createVerticalGlue()); 
        panelPermisosContenido.revalidate();
        panelPermisosContenido.repaint();
    }
    
    private void limpiarPanelPermisos() {
        if(panelPermisosContenido != null) {
            panelPermisosContenido.removeAll();
            panelPermisosContenido.revalidate();
            panelPermisosContenido.repaint();
        }
    }

    private void mostrarMensajeInicial() {
        jPanel4.removeAll();
        JLabel lbl = new JLabel("Seleccione un usuario para ver historial");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        lbl.setForeground(COLOR_TEXTO_GRIS);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        
        jPanel4.add(Box.createVerticalGlue());
        jPanel4.add(lbl);
        jPanel4.add(Box.createVerticalGlue());
        
        jPanel4.revalidate(); jPanel4.repaint();
        
        jLabel6.setText("-"); jLabel7.setText("-"); jLabel8.setText("-");
        limpiarPanelPermisos();
    }

    private void cargarHistorialVisualGlobal() {
        cargarHistorialFiltrado(null, null);
    }

    private void cargarHistorialDeUsuario(int idUsuario, String nombreUsuario) {
        cargarHistorialFiltrado(idUsuario, nombreUsuario);
    }

    private void cargarHistorialFiltrado(Integer idUsuario, String nombreUsuario) {
        jPanel4.removeAll();
        List<HistorialCambio> todosLosLogs = RepositorioLog.obtenerLogs();
        List<HistorialCambio> logsAMostrar = new ArrayList<>();
        
        if (idUsuario == null) {
            logsAMostrar = todosLosLogs; 
        } else {
            for (HistorialCambio log : todosLosLogs) {
                if (log.getIdUsuario() == idUsuario) logsAMostrar.add(log);
            }
        }
        
        String textoTitulo = (nombreUsuario == null) ? "Log Global (Sesión Actual)" : "Historial de: " + nombreUsuario;
        jLabel14.setText(textoTitulo);
        
        if (logsAMostrar.isEmpty()) {
            JLabel lbl = new JLabel("Sin cambios registrados.");
            lbl.setForeground(Color.GRAY);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            jPanel4.add(lbl);
        } else {
            for (HistorialCambio h : logsAMostrar) {
                jPanel4.add(crearTarjetaLog(h));
                jPanel4.add(Box.createVerticalStrut(10));
            }
        }
        jPanel4.revalidate(); jPanel4.repaint();
    }
    
    private JPanel crearTarjetaLog(HistorialCambio h) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_BLANCO);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 80)); 
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_NARANJA), 
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        ));
        
        JLabel lblDesc = new JLabel("<html>" + h.getDescripcionCambio() + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(COLOR_TEXTO_OSCURO);
        
        String hora = h.getFechaCambio().format(DateTimeFormatter.ofPattern("hh:mm a"));
        JLabel lblSub = new JLabel(hora + " - Motivo: " + h.getMotivo());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);
        
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSub);
        return card;
    }
    
    private void mostrarDialogoCambiarRol() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        String[] rolesDisponibles = {"ADMINISTRADOR", "CAJERO"};
        // 1. PEDIR EL NUEVO ROL
        String nuevoRol = (String) JOptionPane.showInputDialog(
            this, "Cambiar rol de " + usuarioSeleccionado.getNombre() + ":",
            "Gestión de Roles", JOptionPane.QUESTION_MESSAGE, null,
            rolesDisponibles, usuarioSeleccionado.getNombreRol()
        );
        
        // Si seleccionó algo y es diferente al actual
        if (nuevoRol != null && !nuevoRol.equals(usuarioSeleccionado.getNombreRol())) {
            
            // 2. PEDIR EL MOTIVO (NUEVO PASO)
            String motivo = JOptionPane.showInputDialog(this, 
                "Ingrese el motivo del cambio (Ej: Ascenso, Error de registro):", 
                "Registro de Auditoría", 
                JOptionPane.PLAIN_MESSAGE);
                
            // Si cancela o lo deja vacío, ponemos un texto por defecto
            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "Sin motivo especificado";
            }
            // 3. ACTUALIZAR EN BASE DE DATOS
            boolean exito = usuarioDAO.actualizarRol(usuarioSeleccionado.getId(), nuevoRol);
            if (exito) {
                String rolAnterior = usuarioSeleccionado.getNombreRol();
                // Actualizar objeto en memoria
                usuarioSeleccionado.setNombreRol(nuevoRol);
                // Crear descripción para el log
                String descripcion = "Usuario: " + usuarioSeleccionado.getNombre() + 
                                     " | Rol: " + rolAnterior + " -> " + nuevoRol;
                // 4. GUARDAR EL LOG CON EL MOTIVO REAL
                HistorialCambio nuevoLog = new HistorialCambio(
                    1, // ID ficticio, la BD lo autogeneraría si tuvieras tabla logs
                    usuarioSeleccionado.getId(), 
                    idAdministrador, 
                    descripcion, 
                    LocalDateTime.now(), 
                    "Admin", 
                    motivo // <--- AQUÍ VA TU MOTIVO ESCRITO
                );
                // Agregar al repositorio compartido
                RepositorioLog.agregarLog(nuevoLog);
                // Refrescar interfaz
                mostrarInformacionUsuario(usuarioSeleccionado);
                cargarHistorialDeUsuario(usuarioSeleccionado.getId(), usuarioSeleccionado.getNombre());
                JOptionPane.showMessageDialog(this, "Rol actualizado correctamente en la Base de Datos.");
            } else {
                JOptionPane.showMessageDialog(this, "Error crítico: No se pudo actualizar la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void inicializarPermisosLogicos() {
        permisosLogicos = new ArrayList<>();
        permisosLogicos.add(new Permiso(1, 1, "", "Acceso Total al Sistema"));
        permisosLogicos.add(new Permiso(2, 1, "", "Gestionar Usuarios"));
        permisosLogicos.add(new Permiso(3, 1, "", "Configuración Global"));
        permisosLogicos.add(new Permiso(5, 2, "", "Procesar Pagos"));
        permisosLogicos.add(new Permiso(6, 2, "", "Apertura/Cierre Caja"));
        permisosLogicos.add(new Permiso(7, 2, "", "Emitir Facturas"));
        permisosLogicos.add(new Permiso(9, 3, "", "Gestionar Empleados"));
        permisosLogicos.add(new Permiso(10, 3, "", "Ver Reportes Financieros"));
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        CambiarRol = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Seleccionar Usuario");

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
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Información del Usuario");

        jLabel3.setText("Nombre");

        jLabel4.setText("Sucursal");

        jLabel5.setText("Rol Actual");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel6.setText("Hornet koji");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel7.setText("Sucursal Centro");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel8.setText("Cajero");

        CambiarRol.setText("Cambiar Rol");
        CambiarRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CambiarRolActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(157, Short.MAX_VALUE)
                .addComponent(CambiarRol, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CambiarRol, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Permisos Actuales");

        jLabel10.setText("Acceso Total");

        jLabel11.setText("Gestionar Usuarios");

        jLabel12.setText("Configurar Sistema");

        jLabel13.setText("Ver Auditoria");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel9)
                .addGap(37, 37, 37)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Historial de Cambios");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel14)
                .addContainerGap(817, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel14)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (isUpdating) return;
        String item = (String) jComboBox1.getSelectedItem();
        if (item != null && mapaUsuariosCombo != null && mapaUsuariosCombo.containsKey(item)) {
            mostrarInformacionUsuario(mapaUsuariosCombo.get(item));
        } else {
            limpiarInformacionUsuario();
        }        
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void CambiarRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CambiarRolActionPerformed
        mostrarDialogoCambiarRol();
    }//GEN-LAST:event_CambiarRolActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CambiarRol;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
