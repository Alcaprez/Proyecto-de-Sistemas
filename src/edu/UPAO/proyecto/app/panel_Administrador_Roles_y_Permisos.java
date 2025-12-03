package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.UsuarioDAOADM;
import edu.UPAO.proyecto.dao.RepositorioLog; // <--- IMPORTACIÓN IMPORTANTE
import edu.UPAO.proyecto.modelo.HistorialCambio;
import edu.UPAO.proyecto.modelo.Permiso;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class panel_Administrador_Roles_y_Permisos extends javax.swing.JPanel {

    private int idAdministrador = 1;
    private UsuarioADM usuarioSeleccionado;

    private UsuarioDAOADM usuarioDAO = new UsuarioDAOADM();

    private List<UsuarioADM> usuariosReales;
    private List<Permiso> permisosLogicos;
    private List<HistorialCambio> historialSesion;

    private Map<String, UsuarioADM> mapaUsuariosCombo;
    private JPanel panelPermisosContenido;

    private boolean isUpdating = false;

    public panel_Administrador_Roles_y_Permisos() {
        initComponents();
        configurarPanelPermisos();
        inicializarPermisosLogicos();
        historialSimulado();
        cargarUsuariosDesdeBD();

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        cargarHistorialVisualGlobal();
    }

private void configurarPanelPermisos() {
        // 1. Ocultar los labels del diseño original (placeholders)
        jLabel9.setVisible(false); jLabel10.setVisible(false);
        jLabel11.setVisible(false); jLabel12.setVisible(false); jLabel13.setVisible(false);
        
        // 2. Limpiar jPanel3 y usar BorderLayout (Clave para que se estire)
        jPanel3.removeAll();
        jPanel3.setLayout(new java.awt.BorderLayout()); 
        
        // 3. Configurar el panel contenedor interno
        panelPermisosContenido = new JPanel();
        panelPermisosContenido.setLayout(new BoxLayout(panelPermisosContenido, BoxLayout.Y_AXIS));
        panelPermisosContenido.setBackground(Color.WHITE);
        panelPermisosContenido.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 4. AGREGAR SCROLL: Esto evita que se corte si hay muchos permisos
        JScrollPane scrollPermisos = new JScrollPane(panelPermisosContenido);
        scrollPermisos.setBorder(null); // Quitar borde para que se vea limpio
        scrollPermisos.getViewport().setBackground(Color.WHITE);
        
        // 5. Agregar el scroll al centro de jPanel3
        jPanel3.add(scrollPermisos, java.awt.BorderLayout.CENTER);
        
        // 6. Refrescar la interfaz
        jPanel3.revalidate();
        jPanel3.repaint();
        
        // Color de fondo opcional para el panel de historial (jPanel4)
        jPanel4.setBackground(new Color(245, 247, 250)); 
    }

    private void historialSimulado() {
        historialSesion = new ArrayList<>();
    }

    // 1. PERMISOS
    private void inicializarPermisosLogicos() {
        permisosLogicos = new ArrayList<>();
        // ADMINISTRADOR
        permisosLogicos.add(new Permiso(1, 1, "", "Acceso Total al Sistema"));
        permisosLogicos.add(new Permiso(2, 1, "", "Gestionar Usuarios y Accesos"));
        permisosLogicos.add(new Permiso(3, 1, "", "Configuración Global"));
        permisosLogicos.add(new Permiso(4, 1, "", "Ver Auditoría y Logs"));
        // CAJERO
        permisosLogicos.add(new Permiso(5, 2, "", "Procesar Pagos y Cobros"));
        permisosLogicos.add(new Permiso(6, 2, "", "Emitir Recibos y Facturas"));
        permisosLogicos.add(new Permiso(7, 2, "", "Apertura y Cierre de Caja"));
        // GERENTE
        permisosLogicos.add(new Permiso(9, 3, "", "Gestionar Empleados"));
        permisosLogicos.add(new Permiso(10, 3, "", "Aprobar Descuentos Especiales"));
        permisosLogicos.add(new Permiso(11, 3, "", "Ver Reportes Financieros Avanzados"));
    }

    private void cargarUsuariosDesdeBD() {
        isUpdating = true;
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Seleccione un usuario...");
        mapaUsuariosCombo = new HashMap<>();

        usuariosReales = usuarioDAO.listar();

        for (UsuarioADM u : usuariosReales) {
            String item = u.getNombre() + "  [" + u.getNombreRol() + "]";
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
        cargarHistorialVisualGlobal();
    }

    private void limpiarInformacionUsuario() {
        if (isUpdating) {
            return;
        }
        this.usuarioSeleccionado = null;
        jLabel6.setText("-");
        jLabel7.setText("-");
        jLabel8.setText("-");
        panelPermisosContenido.removeAll();
        panelPermisosContenido.revalidate();
        panelPermisosContenido.repaint();
    }

    private void cargarPermisosVisuales(String rolTexto) {
        int idRolLogico = 0;
        if (rolTexto.equalsIgnoreCase("ADMINISTRADOR")) {
            idRolLogico = 1;
        } else if (rolTexto.equalsIgnoreCase("CAJERO")) {
            idRolLogico = 2;
        } else if (rolTexto.equalsIgnoreCase("GERENTE")) {
            idRolLogico = 3;
        }

        panelPermisosContenido.removeAll();

        JLabel titulo = new JLabel("Capacidades del Rol: " + rolTexto);
        titulo.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(new Color(100, 100, 100));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        panelPermisosContenido.add(titulo);

        JPanel separador = new JPanel();
        separador.setMaximumSize(new Dimension(1000, 1));
        separador.setBackground(new Color(220, 220, 220));
        separador.setAlignmentX(LEFT_ALIGNMENT);
        panelPermisosContenido.add(Box.createVerticalStrut(8));
        panelPermisosContenido.add(separador);
        panelPermisosContenido.add(Box.createVerticalStrut(12));

        boolean tienePermisos = false;
        for (Permiso p : permisosLogicos) {
            if (p.getIdRol() == idRolLogico) {
                tienePermisos = true;
                JPanel pPanel = new JPanel();
                pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
                pPanel.setBackground(Color.WHITE);
                pPanel.setAlignmentX(LEFT_ALIGNMENT);
                pPanel.setMaximumSize(new Dimension(1000, 30)); // Altura fija para uniformidad

                JLabel check = new JLabel("✔");
                check.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
                check.setForeground(new Color(34, 177, 76));

                JLabel texto = new JLabel(p.getNombre());
                texto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                texto.setForeground(new Color(50, 50, 50));

                pPanel.add(check);
                pPanel.add(Box.createHorizontalStrut(10));
                pPanel.add(texto);

                panelPermisosContenido.add(pPanel);
                panelPermisosContenido.add(Box.createVerticalStrut(5));
            }
        }

        if (!tienePermisos) {
            JLabel lbl = new JLabel("Este rol no tiene permisos configurados.");
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lbl.setForeground(Color.GRAY);
            panelPermisosContenido.add(lbl);
        }

        // Importante: Forzar actualización visual
        panelPermisosContenido.revalidate();
        panelPermisosContenido.repaint();
    }

    private void cargarHistorialVisualGlobal() {
        jPanel4.removeAll();
        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Log de Cambios (Sesión Actual)");
        titulo.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(Box.createHorizontalStrut(5));
        headerPanel.add(titulo);
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(1000, 30));

        jPanel4.add(Box.createVerticalStrut(10));
        jPanel4.add(headerPanel);
        jPanel4.add(Box.createVerticalStrut(15));

        boolean hayCambios = false;

        for (int i = historialSesion.size() - 1; i >= 0; i--) {
            HistorialCambio h = historialSesion.get(i);
            hayCambios = true;

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(Color.WHITE);
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(1000, 80));

            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 1, 0, new Color(0, 102, 204)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            JLabel lblDesc = new JLabel("<html>" + h.getDescripcionCambio() + "</html>");
            lblDesc.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
            lblDesc.setForeground(new Color(33, 37, 41));
            lblDesc.setAlignmentX(LEFT_ALIGNMENT);

            String textoMotivo = (h.getMotivo() != null && !h.getMotivo().isEmpty())
                    ? "Motivo: " + h.getMotivo()
                    : "Sin motivo especificado";
            JLabel lblMotivo = new JLabel(textoMotivo);
            lblMotivo.setFont(new java.awt.Font("Segoe UI", Font.ITALIC, 11));
            lblMotivo.setForeground(new Color(100, 100, 100));
            lblMotivo.setAlignmentX(LEFT_ALIGNMENT);

            String horaStr = h.getFechaCambio().format(DateTimeFormatter.ofPattern("hh:mm a"));
            JLabel lblHora = new JLabel("🕒 " + horaStr);
            lblHora.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 10));
            lblHora.setForeground(new Color(150, 150, 150));
            lblHora.setAlignmentX(LEFT_ALIGNMENT);

            card.add(lblDesc);
            card.add(Box.createVerticalStrut(4));
            card.add(lblMotivo);
            card.add(Box.createVerticalStrut(4));
            card.add(lblHora);

            jPanel4.add(card);
            jPanel4.add(Box.createVerticalStrut(10));
        }

        if (!hayCambios) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setAlignmentX(LEFT_ALIGNMENT);
            JLabel vacio = new JLabel("No se han realizado cambios en esta sesión.");
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            vacio.setForeground(Color.GRAY);
            emptyPanel.add(Box.createVerticalStrut(20));
            emptyPanel.add(vacio);
            jPanel4.add(emptyPanel);
        }
        jPanel4.revalidate();
        jPanel4.repaint();
    }

    private void mostrarDialogoCambiarRol() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        String[] rolesDisponibles = {"ADMINISTRADOR", "GERENTE", "CAJERO"};

        String nuevoRol = (String) JOptionPane.showInputDialog(
                this, "Cambiar rol de " + usuarioSeleccionado.getNombre() + ":",
                "Gestión de Roles", JOptionPane.QUESTION_MESSAGE, null,
                rolesDisponibles, usuarioSeleccionado.getNombreRol()
        );

        if (nuevoRol != null && !nuevoRol.equals(usuarioSeleccionado.getNombreRol())) {

            String motivo = JOptionPane.showInputDialog(this,
                    "Ingrese el motivo del cambio (Opcional):",
                    "Registro de Auditoría",
                    JOptionPane.PLAIN_MESSAGE);
            if (motivo == null) {
                motivo = "Sin motivo";
            }

            boolean exito = usuarioDAO.actualizarRol(usuarioSeleccionado.getId(), nuevoRol);

            if (exito) {
                String rolAnterior = usuarioSeleccionado.getNombreRol();
                usuarioSeleccionado.setNombreRol(nuevoRol);

                String descripcion = "Usuario: " + usuarioSeleccionado.getNombre()
                        + " | Rol: " + rolAnterior + " -> " + nuevoRol;

                // Creamos el log
                HistorialCambio nuevoLog = new HistorialCambio(
                        historialSesion.size() + 1,
                        usuarioSeleccionado.getId(),
                        idAdministrador,
                        descripcion,
                        LocalDateTime.now(),
                        "Admin",
                        motivo
                );

                // Guardar en lista local
                historialSesion.add(nuevoLog);

                // Guardar en memoria compartida (Singleton)
                RepositorioLog.agregarLog(nuevoLog);

                jLabel8.setText(nuevoRol);
                cargarPermisosVisuales(nuevoRol);
                cargarHistorialVisualGlobal();
                actualizarNombreEnCombo(usuarioSeleccionado, nuevoRol);

                JOptionPane.showMessageDialog(this, "Rol actualizado correctamente.");

            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarNombreEnCombo(UsuarioADM usuario, String nuevoRol) {
        isUpdating = true;
        int index = jComboBox1.getSelectedIndex();
        String nuevoTexto = usuario.getNombre() + "  [" + nuevoRol + "]";
        String viejoTexto = (String) jComboBox1.getItemAt(index);
        mapaUsuariosCombo.remove(viejoTexto);
        mapaUsuariosCombo.put(nuevoTexto, usuario);
        jComboBox1.removeItemAt(index);
        jComboBox1.insertItemAt(nuevoTexto, index);
        jComboBox1.setSelectedIndex(index);
        isUpdating = false;
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
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                        .addComponent(CambiarRol, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(CambiarRol, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
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
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addContainerGap(38, Short.MAX_VALUE))
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
                .addContainerGap(115, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (isUpdating) {
            return;
        }

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
