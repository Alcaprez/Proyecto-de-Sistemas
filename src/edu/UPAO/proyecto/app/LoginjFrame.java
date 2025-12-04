package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.SucursalDAO;
import edu.UPAO.proyecto.DAO.UsuarioDAO;
import edu.UPAO.proyecto.LoginController;
import edu.UPAO.proyecto.Modelo.Usuario;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

/**
 *
 * @author WIN-10
 */
public class LoginjFrame extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public LoginjFrame() {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Lima"));
        initComponents();
        setLocationRelativeTo(null);
        cargarSucursales(); // Nueva línea para cargar sucursales
        aplicarEstiloModerno();
        cb_sucursales.setVisible(rootPaneCheckingEnabled);
    }

    private void cargarSucursales() {
        try {
            SucursalDAO sucursalDAO = new SucursalDAO();
            List<String> sucursales = sucursalDAO.obtenerSucursalesActivas();

            // Limpiar el ComboBox
            cb_sucursales.removeAllItems();

            if (sucursales.isEmpty()) {
                cb_sucursales.addItem("No hay sucursales disponibles");
                System.out.println("⚠️ No se encontraron sucursales activas");
            } else {
                for (String sucursal : sucursales) {
                    cb_sucursales.addItem(sucursal);
                }
                System.out.println("✅ Sucursales cargadas: " + sucursales.size());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar sucursales: " + e.getMessage());
            cb_sucursales.addItem("Error cargando sucursales");
        }
    }
    // =========================================================================
    //  DISEÑO MODERNO CORREGIDO (TEXTO IZQUIERDA + BOTONES MEJORADOS)
    // =========================================================================

    private void aplicarEstiloModerno() {
        // 1. COLORES
        Color colorNaranja = new Color(255, 153, 0);
        Color colorBlanco = Color.WHITE;

        // 2. CONFIGURACIÓN DE PANELES
        Right.setBackground(colorBlanco); // Logo en fondo blanco
        Left.setBackground(colorNaranja); // Formulario en fondo naranja
        jPanel1.setBackground(colorNaranja);

        // 3. ESTILIZAR LABELS
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        jLabel1.setForeground(colorBlanco);
        jLabel1.setText("INICIO DE SESIÓN");

        estilizarLabel(jLabel2, colorBlanco);
        estilizarLabel(jLabel3, colorBlanco);

        // 4. ESTILIZAR INPUTS (Corrección: Texto a la izquierda)
        estilizarInput(tf_identificacion);
        estilizarInput(tf_contraseña);

        // 5. BOTÓN INGRESAR (Corrección: Sin borde feo)
        btn_login.setBackground(colorBlanco);
        btn_login.setForeground(colorNaranja);
        btn_login.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // AQUÍ QUITAMOS EL BORDE BLANCO FINO
        btn_login.setBorder(null); // Sin borde
        btn_login.setBorderPainted(false);

        btn_login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_login.setFocusPainted(false);

        // Efecto Hover suave
        btn_login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn_login.setBackground(new Color(245, 245, 245)); // Gris muy claro
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn_login.setBackground(colorBlanco);
            }
        });

        // 6. BOTÓN OLVIDÉ CONTRASEÑA (Corrección: Que parezca botón)
        // Lo haremos un botón "fantasma" o con fondo sutil
        btn_olivdeContraseña.setBackground(new Color(204, 102, 0)); // Un naranja más oscuro (Sombra)
        btn_olivdeContraseña.setForeground(Color.WHITE);
        btn_olivdeContraseña.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Hacemos que tenga fondo para que parezca botón
        btn_olivdeContraseña.setContentAreaFilled(true);
        btn_olivdeContraseña.setBorderPainted(false); // Sin borde linea, solo color de fondo
        btn_olivdeContraseña.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_olivdeContraseña.setFocusPainted(false);

        // 7. COMBOBOX
        cb_sucursales.setBackground(colorBlanco);
        cb_sucursales.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    // Método auxiliar para Labels
    private void estilizarLabel(javax.swing.JLabel lbl, Color color) {
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
    }

    // Método auxiliar para Inputs (Text Fields) CORREGIDO
    private void estilizarInput(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBackground(new Color(255, 153, 0));

        // CORRECCIÓN 1: TEXTO A LA IZQUIERDA
        tf.setHorizontalAlignment(JTextField.LEFT);

        // CORRECCIÓN 2: BORDE INFERIOR + PADDING (Sangría)
        // Creamos un borde compuesto: Línea abajo + Espacio vacío a la izquierda
        javax.swing.border.Border lineaInferior = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE);
        javax.swing.border.Border espacioIzquierda = BorderFactory.createEmptyBorder(0, 5, 0, 0); // 5 pixeles de margen

        tf.setBorder(BorderFactory.createCompoundBorder(lineaInferior, espacioIzquierda));
    }

    //----------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Right = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Left = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tf_identificacion = new javax.swing.JTextField();
        tf_contraseña = new javax.swing.JPasswordField();
        btn_login = new javax.swing.JButton();
        btn_olivdeContraseña = new javax.swing.JButton();
        cb_sucursales = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOGIN");

        jPanel1.setBackground(new java.awt.Color(255, 153, 51));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel1.setLayout(null);

        Right.setBackground(new java.awt.Color(255, 255, 255));
        Right.setPreferredSize(new java.awt.Dimension(400, 500));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/INICIO DE SESION-1.png"))); // NOI18N

        javax.swing.GroupLayout RightLayout = new javax.swing.GroupLayout(Right);
        Right.setLayout(RightLayout);
        RightLayout.setHorizontalGroup(
            RightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RightLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel4)
                .addContainerGap(66, Short.MAX_VALUE))
        );
        RightLayout.setVerticalGroup(
            RightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RightLayout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(jLabel4)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        jPanel1.add(Right);
        Right.setBounds(0, 0, 400, 440);

        Left.setBackground(new java.awt.Color(255, 153, 0));
        Left.setMinimumSize(new java.awt.Dimension(400, 500));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 51));
        jLabel1.setText("INICIO DE SESION");

        jLabel2.setText("IDENTIFICACION");

        jLabel3.setText("CONTRASEÑA:");

        tf_identificacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_identificacionActionPerformed(evt);
            }
        });

        tf_contraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_contraseñaActionPerformed(evt);
            }
        });

        btn_login.setBackground(new java.awt.Color(0, 153, 153));
        btn_login.setForeground(new java.awt.Color(255, 255, 255));
        btn_login.setText("INGRESAR");
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });

        btn_olivdeContraseña.setBackground(new java.awt.Color(255, 153, 0));
        btn_olivdeContraseña.setForeground(new java.awt.Color(204, 0, 0));
        btn_olivdeContraseña.setText("Olvide mi contraseña");
        btn_olivdeContraseña.setBorderPainted(false);
        btn_olivdeContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_olivdeContraseñaActionPerformed(evt);
            }
        });

        cb_sucursales.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_sucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_sucursalesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LeftLayout = new javax.swing.GroupLayout(Left);
        Left.setLayout(LeftLayout);
        LeftLayout.setHorizontalGroup(
            LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftLayout.createSequentialGroup()
                .addGroup(LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LeftLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_olivdeContraseña)
                            .addGroup(LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(tf_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tf_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LeftLayout.createSequentialGroup()
                                    .addComponent(cb_sucursales, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btn_login, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(LeftLayout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jLabel1)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        LeftLayout.setVerticalGroup(
            LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabel1)
                .addGap(63, 63, 63)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(LeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cb_sucursales)
                    .addComponent(btn_login, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btn_olivdeContraseña)
                .addContainerGap(146, Short.MAX_VALUE))
        );

        jPanel1.add(Left);
        Left.setBounds(400, 0, 400, 502);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 776, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tf_contraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_contraseñaActionPerformed
        realizarLogin();
    }//GEN-LAST:event_tf_contraseñaActionPerformed

    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        realizarLogin();
    }//GEN-LAST:event_btn_loginActionPerformed

    private void btn_olivdeContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_olivdeContraseñaActionPerformed
        JOptionPane.showMessageDialog(this,
                "Contacte al administrador del sistema para recuperar su contraseña.\n"
                + "Teléfono: 123-456-789\n"
                + "Email: soporte@kuyay.com",
                "Recuperar Contraseña",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btn_olivdeContraseñaActionPerformed

    private void tf_identificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_identificacionActionPerformed
        // Al presionar Enter en identificación, pasar a contraseña
        tf_contraseña.requestFocus();
    }//GEN-LAST:event_tf_identificacionActionPerformed

    private void cb_sucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_sucursalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_sucursalesActionPerformed

    private void abrirPanelSegunRol(Usuario usuario) {
        String rol = usuario.getCargo().toUpperCase();
        String nombreUsuario = usuario.getNombreComp();
        String idEmpleado = usuario.getUsuario();

        String mensajeBienvenida = "¡Bienvenido " + nombreUsuario + "!";

        try {
            switch (rol) {
                case "GERENTE":
                    JOptionPane.showMessageDialog(this, mensajeBienvenida, "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                    // ✅ CAMBIO AQUÍ: Pasamos los datos al constructor nuevo
                    PrincipalGerente principalGerente = new PrincipalGerente(idEmpleado, nombreUsuario);

                    principalGerente.setVisible(true);
                    break;

                case "ADMINISTRADOR":
                    JOptionPane.showMessageDialog(this, mensajeBienvenida, "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                    // --- CAMBIO AQUÍ: Pasamos idEmpleado y nombreUsuario al constructor ---
                    PrincipalAdministrador principalAdministrador = new PrincipalAdministrador(idEmpleado, nombreUsuario);
                    // -----------------------------------------------------------------------

                    principalAdministrador.setLocationRelativeTo(null);
                    principalAdministrador.setVisible(true);
                    break;

                case "CAJERO":
                    JOptionPane.showMessageDialog(this, mensajeBienvenida, "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);                    // Abrir menú principal de cajero
                    Menu2 menuPrincipal = new Menu2(idEmpleado);
                    menuPrincipal.setVisible(true);

                    // Abrir ventana de asistencia para cajero
                    //jFrame_Asistncias asistencia = new jFrame_Asistncias(nombreUsuario);
                    //asistencia.setVisible(true);
                    break;

                default:
                    JOptionPane.showMessageDialog(this,
                            "Rol no reconocido: " + rol,
                            "Error de Sistema", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            // Cerrar ventana de login después de abrir el panel correspondiente
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el panel: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void realizarLogin() {
        String usuario = tf_identificacion.getText().trim();
        String contrasena = new String(tf_contraseña.getPassword());
        String sucursalSeleccionada = (String) cb_sucursales.getSelectedItem();

        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Usuario ingresado: " + usuario);
        System.out.println("Contraseña ingresada: " + contrasena);
        System.out.println("Sucursal seleccionada: " + sucursalSeleccionada);

        // Validar campos vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos",
                    "Campos Incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar que se haya seleccionado una sucursal válida
        if (sucursalSeleccionada == null || sucursalSeleccionada.equals("No hay sucursales disponibles")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una sucursal válida",
                    "Sucursal Requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar formato (8 dígitos)
        if (!usuario.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this,
                    "El usuario debe ser un número de 8 dígitos",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que sea usuario interno (10, 11, 12)
        int id = Integer.parseInt(usuario);
        int primerosDosDigitos = id / 1000000;

        if (primerosDosDigitos < 10 || primerosDosDigitos > 12) {
            JOptionPane.showMessageDialog(this,
                    "❌ Acceso denegado.\nSolo personal autorizado puede ingresar al sistema.\n\n"
                    + "Tipos de usuario permitidos:\n"
                    + "• 10xxxxxx - Gerentes\n"
                    + "• 11xxxxxx - Administradores\n"
                    + "• 12xxxxxx - Cajeros",
                    "Acceso Restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Intentar autenticación
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Verificar conexión primero (opcional, para debug)
        usuarioDAO.verificarDatosUsuario(usuario);

        Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, contrasena);

        if (usuarioAutenticado != null) {
            String rol = usuarioAutenticado.getCargo().toUpperCase();

            if (rol.equals("CAJERO")) {
                // 1. Validación de Horario (Existente)
                boolean enTurno = LoginController.esHorarioValido(usuarioAutenticado.getUsuario());
                if (!enTurno) {
                    JOptionPane.showMessageDialog(this,
                            "⛔ ACCESO DENEGADO POR HORARIO\nNo estás en tu turno asignado.",
                            "Fuera de Turno", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 2. 👇 LÓGICA REAL PARA OBTENER EL ID DE LA SUCURSAL 👇
                String nombreSucursal = cb_sucursales.getSelectedItem().toString();
                int idSucursalReal = -1;

                try {
                    SucursalDAO sucursalDAO = new SucursalDAO();
                    // Aquí llamamos al método que acabamos de crear en el Paso 1
                    idSucursalReal = sucursalDAO.obtenerIdPorNombre(nombreSucursal);
                } catch (Exception e) {
                    System.err.println("Error buscando sucursal: " + e.getMessage());
                }

                // Validamos que se haya encontrado la sucursal
                if (idSucursalReal == -1) {
                    JOptionPane.showMessageDialog(this, "Error crítico: No se pudo identificar la sucursal seleccionada en la BD.");
                    return;
                }

                System.out.println("🏢 Sucursal detectada: " + nombreSucursal + " (ID: " + idSucursalReal + ")");

                // 3. 👇 ABRIR CAJA AUTOMÁTICAMENTE CON EL ID REAL 👇
                gestionarAperturaCajaAutomatica(usuarioAutenticado.getUsuario(), idSucursalReal);

                edu.UPAO.proyecto.DAO.AsistenciaDAO asisDao = new edu.UPAO.proyecto.DAO.AsistenciaDAO();
                asisDao.registrarMarca(usuarioAutenticado.getUsuario(), idSucursalReal, "ENTRADA");

            }

            System.out.println("🎉 Login exitoso - Redirigiendo a: " + usuarioAutenticado.getCargo());
            abrirPanelSegunRol(usuarioAutenticado);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos",
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            tf_contraseña.setText("");
            tf_identificacion.requestFocus();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginjFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginjFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginjFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginjFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Lima"));
                new LoginjFrame().setVisible(true);
            }
        });
    }

    // =========================================================================
    //  👇 REEMPLAZA TU MÉTODO ANTIGUO POR ESTE NUEVO BLOQUE 👇
    // =========================================================================
    // En edu.UPAO.proyecto.app.LoginjFrame
// En LoginjFrame.java
    private void gestionarAperturaCajaAutomatica(String idEmpleado, int idSucursal) {
        edu.UPAO.proyecto.DAO.CajaDAO cajaDAO = new edu.UPAO.proyecto.DAO.CajaDAO();
        edu.UPAO.proyecto.DAO.SucursalDAO sucursalDAO = new edu.UPAO.proyecto.DAO.SucursalDAO();

        // ---------------------------------------------------------
        // 1. DETECTAR Y CORREGIR OLVIDOS DEL DÍA ANTERIOR
        // ---------------------------------------------------------
        edu.UPAO.proyecto.Modelo.Caja cajaVieja = cajaDAO.obtenerCajaPendienteAnterior(idSucursal);

        if (cajaVieja != null) {
            System.out.println("⚠️ ALERTA: Se detectó una caja del día anterior sin cerrar (Estado: " + cajaVieja.getEstado() + ")");

            double montoRecuperar = 0.0;

            // Caso A: El cajero SÍ hizo el encuadre, pero el Admin olvidó cerrar
            if ("ENCUADRADA".equals(cajaVieja.getEstado())) {
                montoRecuperar = cajaVieja.getSaldoFinal(); // Recuperamos lo que contó el cajero
            } // Caso B: Nadie cerró nada (Ni cajero ni Admin) - CRÍTICO
            else {
                // Asumimos el saldo del sistema para no perder el rastro, o 0 si queremos ser estrictos.
                // Lo ideal es recuperar el saldo teórico calculado.
                montoRecuperar = cajaDAO.calcularSaldoTeorico(cajaVieja.getIdCaja());
            }

            // AUTO-CIERRE: Mover dinero al presupuesto y cerrar caja vieja
            boolean devolucion = sucursalDAO.actualizarPresupuesto(idSucursal, montoRecuperar, true); // true = Ingreso
            boolean cierre = cajaDAO.cerrarCajaDefinitivaAdmin(cajaVieja.getIdCaja()); // Usamos el método que creamos para el Admin

            if (devolucion && cierre) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ AVISO DE SEGURIDAD:\n"
                        + "La caja del día anterior no fue cerrada correctamente por el Administrador.\n"
                        + "El sistema ha realizado el CIERRE AUTOMÁTICO recuperando S/ " + montoRecuperar + "\n"
                        + "al presupuesto de la tienda para permitir la operación de hoy.");
            }
        }

        // ---------------------------------------------------------
        // 2. FLUJO NORMAL: ABRIR CAJA DE HOY (O unirse a ella)
        // ---------------------------------------------------------
        edu.UPAO.proyecto.Modelo.Caja cajaDia = cajaDAO.obtenerCajaAbierta(idSucursal);

        if (cajaDia == null) {
            // ... (Aquí va el código que ya tenías para pedir el 5% y abrir) ...
            // COPIA PEGA LA LÓGICA DE APERTURA QUE TE DI EN LA RESPUESTA ANTERIOR
            // (Calcular 5%, restar presupuesto, abrir caja nueva)

            double presupuestoActual = sucursalDAO.obtenerPresupuesto(idSucursal);
            double saldoInicial = presupuestoActual * 0.05; // 5%

            if (sucursalDAO.actualizarPresupuesto(idSucursal, saldoInicial, false)) {
                if (cajaDAO.abrirCaja(idSucursal, saldoInicial, idEmpleado, "DIA_COMPLETO")) {
                    JOptionPane.showMessageDialog(this, "☀️ Caja del día aperturada (Saldo inicial 5%: S/ " + String.format("%.2f", saldoInicial) + ")");
                }
            }
        } else {
            System.out.println("ℹ️ Uniéndose a la caja del día existente.");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Left;
    private javax.swing.JPanel Right;
    private javax.swing.JButton btn_login;
    private javax.swing.JButton btn_olivdeContraseña;
    private javax.swing.JComboBox<String> cb_sucursales;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField tf_contraseña;
    private javax.swing.JTextField tf_identificacion;
    // End of variables declaration//GEN-END:variables
}
