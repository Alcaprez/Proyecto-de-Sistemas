package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.EmpleadoDAO;
import edu.UPAO.proyecto.DAO.UsuarioDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class panel_Cuenta extends javax.swing.JPanel {

private String idEmpleadoLogueado;
// ----------------------------
    private final Color COLOR_FONDO_GENERAL = Color.WHITE;
    // El azul vibrante de la tarjeta (referencia imagen 3)
    private final Color COLOR_TARJETA_AZUL = new Color(13, 110, 253); 
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO_TITULO_EXTERNO = new Color(50, 50, 50);
    // Color de los inputs (gris claro para resaltar sobre el azul)
    private final Color COLOR_INPUT_FONDO = new Color(230, 235, 240); 
    private final Color COLOR_INPUT_TEXTO = new Color(30, 30, 30);
    private final Color COLOR_INPUT_BLOQUEADO = new Color(200, 205, 210); // Un poco más oscuro para readonly
    // Fuentes
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FUENTE_LABEL_INTERNO = new Font("Segoe UI", Font.BOLD, 14); // Negrita y blanco
    private final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    // -----------------------------------------------------
    public panel_Cuenta(String idEmpleado) {
        this.idEmpleadoLogueado = idEmpleado;
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        initComponents(); // Carga diseño generado
        aplicarDisenoBloquesAzules(); //
        configurarCampos(); // Bloquea campos de solo lectura
        cargarInformacion(); // Llena los datos
    }
    // ----------------------
    private void aplicarDisenoBloquesAzules() {
        // 1. Fondo General Blanco
        this.setBackground(COLOR_FONDO_GENERAL);
        jTabbedPane1.setBackground(COLOR_FONDO_GENERAL);
        jTabbedPane1.setFont(FUENTE_BOTON);
        
        // Paneles contenedores principales (blancos)
        jPanel1.setBackground(COLOR_FONDO_GENERAL); // Panel Cambiar Datos
        jPanel3.setBackground(COLOR_FONDO_GENERAL); // Panel Cambiar Contraseña
        jPanel8.setBackground(COLOR_FONDO_GENERAL); 

        // 2. CONVERTIR PANELES INTERNOS EN "TARJETAS AZULES"
        // Estos son los que originalmente eran naranjas. Ahora serán el bloque azul.
        estilizarComoBloqueAzul(jPanel6); // Bloque de formulario datos
        estilizarComoBloqueAzul(jPanel7); // Bloque de confirmación
        estilizarComoBloqueAzul(jPanel9); // Bloque de cambio contraseña

        // 3. Estilizar Textos
        // Labels fuera de los bloques (Títulos grandes: Cuenta, ID)
        estilizarTituloExterno(jLabel3, lbl_cuenta, jLabel23, lbl_id, jLabel5, lbl_cuenta1, jLabel22, lbl_id1);
        
        // Labels DENTRO de los bloques azules (Deben ser BLANCOS)
        estilizarLabelInterno(jLabel12, jLabel13, jLabel14, jLabel15, jLabel16, jLabel17, 
                              jLabel18, jLabel19, jLabel20, jLabel21);

        // 4. Estilizar Inputs (Campos de texto dentro del azul)
        estilizarInput(tf_nombres, tf_apellidos, tf_dni, tf_telefono, tf_direccion, tf_sucursal,
                       tf_contraseñaActual, tf_constraseñNueva, tf_confirmacionContraseña);
        estilizarInput(pf_contraseña);

        // 5. Botón
        estilizarBoton(btn_guardar);
    }

    // --- AYUDANTES DE DISEÑO ---

    private void estilizarComoBloqueAzul(JPanel panel) {
        panel.setBackground(COLOR_TARJETA_AZUL);
        // Borde redondeado simulado (EmptyBorder para dar espacio interno + LineBorder opcional)
        // Nota: Swing puro no hace bordes redondos perfectos sin pintar, pero esto dará el margen correcto.
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
    }

    private void estilizarTituloExterno(JLabel... labels) {
        for (JLabel l : labels) {
            l.setFont(FUENTE_TITULO);
            l.setForeground(COLOR_TEXTO_TITULO_EXTERNO);
        }
    }

    private void estilizarLabelInterno(JLabel... labels) {
        for (JLabel l : labels) {
            l.setFont(FUENTE_LABEL_INTERNO);
            l.setForeground(COLOR_TEXTO_BLANCO); // Texto blanco sobre azul
        }
    }

    private void estilizarInput(JTextField... inputs) {
        // Borde vacío para padding interno
        Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        
        for (JTextField tf : inputs) {
            tf.setFont(FUENTE_INPUT);
            tf.setBackground(COLOR_INPUT_FONDO); // Fondo gris claro
            tf.setForeground(COLOR_INPUT_TEXTO); // Texto oscuro
            tf.setBorder(padding); // Sin borde negro, solo relleno
            tf.setCaretColor(Color.BLACK);
        }
    }

    private void estilizarBoton(JButton btn) {
        btn.setFont(FUENTE_BOTON);
        // Botón blanco o gris claro para contrastar con el fondo azul del panel
        btn.setBackground(Color.WHITE); 
        btn.setForeground(COLOR_TARJETA_AZUL); // Letras azules
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    // -----------------------------------------------------
    
    private void configurarCampos() {
        tf_nombres.setEditable(false);
        tf_apellidos.setEditable(false);
        tf_dni.setEditable(false);
        tf_sucursal.setEditable(false);

        java.awt.Color colorBloqueado = new java.awt.Color(230, 230, 230);
        tf_nombres.setBackground(colorBloqueado);
        tf_apellidos.setBackground(colorBloqueado);
        tf_dni.setBackground(colorBloqueado);
        tf_sucursal.setBackground(colorBloqueado);
        //----------------------
        tf_nombres.setBackground(COLOR_INPUT_BLOQUEADO);
        tf_apellidos.setBackground(COLOR_INPUT_BLOQUEADO);
        tf_dni.setBackground(COLOR_INPUT_BLOQUEADO);
        tf_sucursal.setBackground(COLOR_INPUT_BLOQUEADO);
        //-----------------------
        // Acción para guardar DATOS DE CONTACTO (Pestaña 1)
        btn_guardar.addActionListener(e -> guardarCambiosDatos());
    }
    
     private void cargarInformacion() {
        new Thread(() -> {
            EmpleadoDAO dao = new EmpleadoDAO();
            Map<String, String> datos = dao.obtenerDatosUsuario(idEmpleadoLogueado);

            SwingUtilities.invokeLater(() -> {
                if (!datos.isEmpty()) {
                    lbl_id.setText(datos.get("id"));
                    //-------------------------------
                    lbl_id1.setText(datos.get("id")); // Actualizamos ambos labels de ID
                    //-------------------------------------
                    tf_dni.setText(datos.get("dni"));
                    tf_nombres.setText(datos.get("nombres"));
                    tf_apellidos.setText(datos.get("apellidos"));
                    tf_telefono.setText(datos.get("telefono"));
                    tf_direccion.setText(datos.get("direccion"));
                    tf_sucursal.setText(datos.get("sucursal"));
                    lbl_cuenta.setText(datos.get("cargo"));
                    //---------------------------------
                    lbl_cuenta1.setText(datos.get("cargo")); // Actualizamos ambos labels de Cargo
                    //---------------------------------
                    tf_nombres.setText(datos.get("nombres"));
                    tf_apellidos.setText(datos.get("apellidos"));
                    tf_dni.setText(datos.get("dni"));
                    tf_sucursal.setText(datos.get("sucursal"));

                    tf_telefono.setText(datos.get("telefono"));
                    tf_direccion.setText(datos.get("direccion"));
                }
            });
        }).start();
    }


    private void guardarCambiosDatos() {
        String pass = new String(pf_contraseña.getPassword());
        String nuevoTel = tf_telefono.getText().trim();
        String nuevaDir = tf_direccion.getText().trim();

        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese su contraseña para confirmar cambios de datos.", "Seguridad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.validarContrasena(idEmpleadoLogueado, pass)) {
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        if (empleadoDAO.actualizarDatosContacto(idEmpleadoLogueado, nuevoTel, nuevaDir)) {
            JOptionPane.showMessageDialog(this, "✅ Datos actualizados correctamente.");
            cargarInformacion();
            pf_contraseña.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cambiarPassword() {
        // Usamos los campos que ya tienes en las variables
        String actual = tf_contraseñaActual.getText();
        String nueva = tf_constraseñNueva.getText();
        String confirm = tf_confirmacionContraseña.getText();

        if (actual.isEmpty() || nueva.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos de contraseña.");
            return;
        }

        if (!nueva.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Las nuevas contraseñas no coinciden.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.validarContrasena(idEmpleadoLogueado, actual)) {
            if (dao.cambiarContrasena(idEmpleadoLogueado, nueva)) {
                JOptionPane.showMessageDialog(this, "✅ Contraseña actualizada con éxito.");
                // Limpiar
                tf_contraseñaActual.setText("");
                tf_constraseñNueva.setText("");
                tf_confirmacionContraseña.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar en BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "La contraseña actual es incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        tf_nombres = new javax.swing.JTextField();
        tf_dni = new javax.swing.JTextField();
        tf_telefono = new javax.swing.JTextField();
        tf_direccion = new javax.swing.JTextField();
        tf_sucursal = new javax.swing.JTextField();
        tf_apellidos = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        btn_guardar = new javax.swing.JButton();
        pf_contraseña = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        lbl_cuenta = new javax.swing.JLabel();
        lbl_id = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        tf_contraseñaActual = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tf_constraseñNueva = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tf_confirmacionContraseña = new javax.swing.JTextField();
        lbl_cuenta1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbl_id1 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(0, 252, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(198, 215, 255));

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel12.setText("Nombres:");

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel13.setText("Apellidos:");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel14.setText("DNI:");

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel15.setText("Teléfono:");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel16.setText("Direccion:");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel17.setText("Sucursal:");

        tf_nombres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_nombresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel12)
                            .addComponent(jLabel15)))
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tf_nombres, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tf_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(tf_nombres, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(tf_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(tf_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(tf_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(tf_sucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 153, 0));

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel18.setText("Ingrese su contraseña para confirmar los cambios:");

        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pf_contraseña)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pf_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel3.setText("Cuenta:");

        lbl_cuenta.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        lbl_cuenta.setText("Empleado");

        lbl_id.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        lbl_id.setText("12000001");

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel23.setText("ID:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_cuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(331, 331, 331)
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_id, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(280, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lbl_cuenta)
                    .addComponent(lbl_id)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CAMBIAR DATOS", jPanel1);

        jPanel9.setBackground(new java.awt.Color(255, 153, 0));

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel19.setText("Ingrese contraseña nueva:");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel20.setText("Ingrese contraseña actual:");

        tf_constraseñNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_constraseñNuevaActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel21.setText("Confirmar nueva contraseña:");

        tf_confirmacionContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_confirmacionContraseñaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_confirmacionContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_constraseñNueva, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_contraseñaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(341, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_contraseñaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_constraseñNueva, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_confirmacionContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        lbl_cuenta1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        lbl_cuenta1.setText("Empleado");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel5.setText("Cuenta:");

        lbl_id1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        lbl_id1.setText("12000001");

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel22.setText("ID:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_cuenta1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_id1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lbl_cuenta1)
                    .addComponent(lbl_id1)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("CAMBIAR CONTRASEÑA", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tf_confirmacionContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_confirmacionContraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_confirmacionContraseñaActionPerformed

    private void tf_constraseñNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_constraseñNuevaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_constraseñNuevaActionPerformed

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void tf_nombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nombresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_nombresActionPerformed

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_guardar;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbl_cuenta;
    private javax.swing.JLabel lbl_cuenta1;
    private javax.swing.JLabel lbl_id;
    private javax.swing.JLabel lbl_id1;
    private javax.swing.JPasswordField pf_contraseña;
    private javax.swing.JTextField tf_apellidos;
    private javax.swing.JTextField tf_confirmacionContraseña;
    private javax.swing.JTextField tf_constraseñNueva;
    private javax.swing.JTextField tf_contraseñaActual;
    private javax.swing.JTextField tf_direccion;
    private javax.swing.JTextField tf_dni;
    private javax.swing.JTextField tf_nombres;
    private javax.swing.JTextField tf_sucursal;
    private javax.swing.JTextField tf_telefono;
    // End of variables declaration//GEN-END:variables
}
