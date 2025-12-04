package edu.UPAO.proyecto.app;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class PrincipalAdministrador extends javax.swing.JFrame {

    private String idEmpleado;
    private String nombreUsuario;
    private int idSucursal;
    private DashboradBienvenida dashboradBienvenida;
    public PrincipalAdministrador() {
        initComponents();
        MostrarPanel(new DashboradBienvenida());
        
        // --- AGREGA ESTO AQUÍ ---
        try {
            edu.UPAO.proyecto.DAO.EmpleadoDAO empleadoDAO = new edu.UPAO.proyecto.DAO.EmpleadoDAO();
            // Obtenemos el ID de la sucursal y lo guardamos en la variable global
            this.idSucursal = empleadoDAO.obtenerSucursalEmpleado(this.idEmpleado);
        } catch (Exception e) {
            System.err.println("Error al obtener sucursal: " + e.getMessage());
            this.idSucursal = 0; // Valor por defecto si falla
        }
        verificarStockSucursal();
    }

    public PrincipalAdministrador(String idEmpleado, String nombreUsuario) {
        // Inicializar componentes visuales primero
        initComponents();
        MostrarPanel(new DashboradBienvenida());
        aplicarDisenoModerno();
        // Guardar los datos recibidos
        this.idEmpleado = idEmpleado;
        this.nombreUsuario = nombreUsuario;

        // Opcional: Poner el nombre en el título de la ventana o en una etiqueta
        this.setTitle("Panel Administrador - Usuario: " + nombreUsuario);

        verificarStockSucursal();
    }
    
    private void verificarStockSucursal() {
        new Thread(() -> {
            try {
                // 1. Obtener la sucursal de ESTE administrador
                edu.UPAO.proyecto.DAO.EmpleadoDAO empleadoDAO = new edu.UPAO.proyecto.DAO.EmpleadoDAO();
                int idSucursal = empleadoDAO.obtenerSucursalEmpleado(this.idEmpleado);
                
                // 2. Buscar alertas SOLO de esa sucursal (pasamos el ID)
                edu.UPAO.proyecto.DAO.InventarioSucursalDAO inventarioDAO = new edu.UPAO.proyecto.DAO.InventarioSucursalDAO();
                java.util.List<String> alertas = inventarioDAO.obtenerAlertasBajoStock(idSucursal);
                
                if (!alertas.isEmpty()) {
                    StringBuilder mensaje = new StringBuilder("⚠️ ALERTA DE STOCK (Tu Sucursal)\n\n");
                    
                    int limite = Math.min(alertas.size(), 15);
                    for (int i = 0; i < limite; i++) {
                        mensaje.append(alertas.get(i)).append("\n");
                    }
                    
                    if (alertas.size() > 15) {
                        mensaje.append("\n... y ").append(alertas.size() - 15).append(" más.");
                    }
                    
                    mensaje.append("\n\nSe requiere reposición urgente en esta sede.");

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(this, 
                            new javax.swing.JScrollPane(new javax.swing.JTextArea(mensaje.toString(), 15, 40)), 
                            "Gestión de Inventario - Local", 
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    });
                }
            } catch (Exception e) {
                System.err.println("Error stock admin: " + e.getMessage());
            }
        }).start();
    }

    private void MostrarPanel(JPanel p) {
// Configurar el panel nuevo
        p.setOpaque(true);
        // Limpiar el contenedor
        content.removeAll();
        // Agregar y estirar automáticamente
        content.add(p, java.awt.BorderLayout.CENTER);
        // Refrescar visualización
        content.revalidate();
        content.repaint();
    }
    
    // =======================================================
    // MÉTODOS DE DISEÑO (ESTILO MODERNO)
    // =======================================================
    
    private void aplicarDisenoModerno() {
        // 1. EL ENCABEZADO (Amarillo/Dorado con texto centrado)
        // Color amarillo similar a la imagen 1
        jPanel4.setBackground(new Color(255, 204, 51));
        // Configuramos el texto "ADMINISTRADOR"
        lblFrase.setText("ADMINISTRADOR");
        lblFrase.setForeground(new Color(20, 60, 40)); // Verde oscuro para el texto
        lblFrase.setFont(new Font("Serif", Font.BOLD, 44)); // Fuente elegante
        lblFrase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        // 2. EL MENÚ LATERAL (Fondo verde oscuro)
        jPanel1.setBackground(new Color(13, 59, 40)); // Verde oscuro profundo
        
        // 3. LOS BOTONES (Colores vibrantes y forma redondeada)
        // Tesorería -> Naranja
        estilizarBoton(btn_tesoreria, new Color(211, 84, 0)); 
        
        // Ventas -> Verde Hoja
        estilizarBoton(btn_venta, new Color(39, 174, 96)); 
        
        // Compras -> Rojo Intenso
        estilizarBoton(btn_compras, new Color(192, 57, 43)); 
        
        // Almacén -> Morado/Violeta
        estilizarBoton(btn_almacenes, new Color(142, 68, 173)); 
        
        // Personal -> Verde Azulado (Teal) - Este es el que está activo en tu ejemplo
        estilizarBoton(btn_personal, new Color(22, 160, 133));
        
        // Cuenta -> Azul
        estilizarBoton(btn_cuenta, new Color(41, 128, 185));
        
        // Mis Asistencias -> Gris azulado (para diferenciarlo)
        estilizarBoton(btnMarcarAsistencia, new Color(90, 100, 110));

        // Cerrar Sesión -> Texto amarillo, fondo transparente
        btn_cerrarSesion.setContentAreaFilled(false);
        btn_cerrarSesion.setBorderPainted(false);
        btn_cerrarSesion.setForeground(new Color(255, 204, 51));
        btn_cerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_cerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Método auxiliar para dar estilo, forma y efectos modernos (Hover/Press)
    private void estilizarBoton(JButton btn, Color colorFondo) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorFondo);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        
        // IMPORTANTE: Habilitar la detección del mouse
        btn.setRolloverEnabled(true); 

        // Aplicamos el borde para el margen interno
        btn.setBorder(new BordeRedondeado(20)); 
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        // UI Personalizado con Lógica de Estados (Hover y Click)
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Obtenemos el modelo del botón para saber su estado
                javax.swing.AbstractButton b = (javax.swing.AbstractButton) c;
                javax.swing.ButtonModel model = b.getModel();
                
                // Lógica de colores dinámica
                Color colorActual = c.getBackground();
                
                if (model.isPressed()) {
                    // Si se presiona: Color más oscuro
                    colorActual = colorActual.darker(); 
                } else if (model.isRollover()) {
                    // Si el mouse pasa por encima: Color más brillante (Iluminación)
                    colorActual = colorActual.brighter(); 
                }
                
                g2.setColor(colorActual);
                
                // Rellenar forma redondeada con el color calculado
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20); 
                
                // Llamamos a pintar el texto original encima
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    // Clase interna para el borde (necesaria para el espaciado correcto)
    class BordeRedondeado implements javax.swing.border.Border {
        private int radio;
        BordeRedondeado(int radio) { this.radio = radio; }
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            return new java.awt.Insets(10, 20, 10, 20); // Margen interno (padding)
        }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            // No pintamos borde linea, solo usamos esto para el margen
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btn_venta = new javax.swing.JButton();
        btn_almacenes = new javax.swing.JButton();
        btn_compras = new javax.swing.JButton();
        btn_personal = new javax.swing.JButton();
        btn_cuenta = new javax.swing.JButton();
        btn_tesoreria = new javax.swing.JButton();
        btn_cerrarSesion = new javax.swing.JButton();
        btnMarcarAsistencia = new javax.swing.JButton();
        content = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblFrase = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(6);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jPanel1.setBackground(new java.awt.Color(0, 102, 51));

        btn_venta.setBackground(new java.awt.Color(0, 102, 51));
        btn_venta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_venta.setForeground(new java.awt.Color(255, 255, 255));
        btn_venta.setText("VENTAS");
        btn_venta.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_venta.setContentAreaFilled(false);
        btn_venta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventaActionPerformed(evt);
            }
        });

        btn_almacenes.setBackground(new java.awt.Color(0, 102, 51));
        btn_almacenes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_almacenes.setForeground(new java.awt.Color(255, 255, 255));
        btn_almacenes.setText("ALMACEN");
        btn_almacenes.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_almacenes.setContentAreaFilled(false);
        btn_almacenes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_almacenesActionPerformed(evt);
            }
        });

        btn_compras.setBackground(new java.awt.Color(0, 102, 51));
        btn_compras.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_compras.setForeground(new java.awt.Color(255, 255, 255));
        btn_compras.setText("COMPRAS");
        btn_compras.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_compras.setContentAreaFilled(false);
        btn_compras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_comprasActionPerformed(evt);
            }
        });

        btn_personal.setBackground(new java.awt.Color(0, 102, 51));
        btn_personal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_personal.setForeground(new java.awt.Color(255, 255, 255));
        btn_personal.setText("PERSONAL");
        btn_personal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_personal.setContentAreaFilled(false);
        btn_personal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_personalActionPerformed(evt);
            }
        });

        btn_cuenta.setBackground(new java.awt.Color(0, 102, 51));
        btn_cuenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_cuenta.setForeground(new java.awt.Color(255, 255, 255));
        btn_cuenta.setText("CUENTA");
        btn_cuenta.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_cuenta.setContentAreaFilled(false);
        btn_cuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cuentaActionPerformed(evt);
            }
        });

        btn_tesoreria.setBackground(new java.awt.Color(0, 102, 51));
        btn_tesoreria.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_tesoreria.setForeground(new java.awt.Color(255, 255, 255));
        btn_tesoreria.setText("TESORERIA");
        btn_tesoreria.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btn_tesoreria.setContentAreaFilled(false);
        btn_tesoreria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tesoreriaActionPerformed(evt);
            }
        });

        btn_cerrarSesion.setBackground(new java.awt.Color(0, 102, 51));
        btn_cerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_cerrarSesion.setForeground(new java.awt.Color(255, 204, 0));
        btn_cerrarSesion.setText("Cerrar sesion");
        btn_cerrarSesion.setBorderPainted(false);
        btn_cerrarSesion.setContentAreaFilled(false);
        btn_cerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cerrarSesionActionPerformed(evt);
            }
        });

        btnMarcarAsistencia.setBackground(new java.awt.Color(0, 102, 51));
        btnMarcarAsistencia.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMarcarAsistencia.setForeground(new java.awt.Color(255, 255, 255));
        btnMarcarAsistencia.setText("MIS ASISTENCIAS");
        btnMarcarAsistencia.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnMarcarAsistencia.setContentAreaFilled(false);
        btnMarcarAsistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarAsistenciaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_venta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btn_almacenes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_compras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_personal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_cuenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_tesoreria, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnMarcarAsistencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(btn_cerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btn_tesoreria, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_compras, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_almacenes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_personal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_cuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMarcarAsistencia, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_cerrarSesion)
                .addGap(20, 20, 20))
        );

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1197, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 609, Short.MAX_VALUE)
        );

        content.add(jLayeredPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(255, 153, 0));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/miniLogo.png"))); // NOI18N

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 36)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addGap(72, 72, 72)
                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tesoreriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tesoreriaActionPerformed
        TESORERIA_Admin paneltesoreria = new TESORERIA_Admin();
        MostrarPanel(paneltesoreria);
    }//GEN-LAST:event_btn_tesoreriaActionPerformed

    private void btn_personalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_personalActionPerformed
        CONTROL_PERSONAL panelPersonal = new CONTROL_PERSONAL();
        MostrarPanel(panelPersonal);
    }//GEN-LAST:event_btn_personalActionPerformed

    private void btn_comprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_comprasActionPerformed
        try {
            // Cambiamos el cursor a "cargando" para dar feedback visual
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

            // 1. Instanciamos el panel (ahora capturamos si falla aquí)
            COMPRAS_Admin panelCompras = new COMPRAS_Admin(idSucursal);

            // IMPORTANTE: Pasar el ID del empleado o sucursal si es necesario
            // panelCompras.setIdSucursal(this.idSucursalAdministrador); // (Opcional si implementas el setter)
            // 2. Mostramos el panel
            MostrarPanel(panelCompras);

        } catch (Exception e) {
            // Si falla, mostramos el error real
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el panel de Compras.\nError: " + e.getMessage(),
                    "Error Crítico",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            // Restauramos el cursor
            this.setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btn_comprasActionPerformed

    private void btn_ventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventaActionPerformed
        VENTAS_Admin panelventas = new VENTAS_Admin();
        MostrarPanel(panelventas);
    }//GEN-LAST:event_btn_ventaActionPerformed

    private void btn_almacenesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_almacenesActionPerformed
        ALMACEN_Admin panelAlmacen = new ALMACEN_Admin();
        MostrarPanel(panelAlmacen);
    }//GEN-LAST:event_btn_almacenesActionPerformed

    private void btn_cuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cuentaActionPerformed
        // 1. Validar que tenemos el ID del administrador (por si acaso)
        String idParaEnviar = (this.idEmpleado != null) ? this.idEmpleado : "12000001";

        // 2. Instanciar el panel pasándole el ID
        panel_Cuenta panelCuenta = new panel_Cuenta(idParaEnviar);

        // 3. Mostrarlo en el contenido
        MostrarPanel(panelCuenta);
    }//GEN-LAST:event_btn_cuentaActionPerformed

    private void btn_cerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cerrarSesionActionPerformed
// Cierra la ventana actual
        this.dispose();

        // Abre el login de nuevo
        LoginjFrame login = new LoginjFrame();
        login.setVisible(true);
    }//GEN-LAST:event_btn_cerrarSesionActionPerformed

    private void btnMarcarAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarAsistenciaActionPerformed
        String idAdmin = this.idEmpleado; // O la variable donde guardes el ID
        String nombreAdmin = this.nombreUsuario; // O la variable donde guardes el Nombre

        // 2. Validar que no sean nulos (por seguridad)
        if (idAdmin == null || idAdmin.isEmpty()) {
            // Fallback por si es una prueba rápida
            idAdmin = "ADMIN001";
            nombreAdmin = "Administrador";
        }

        // 3. Abrir la ventanita de asistencia usando tu método estático existente
        //
        jFrame_Asistncias.mostrarRegistroAsistencia(idAdmin, nombreAdmin);
    }//GEN-LAST:event_btnMarcarAsistenciaActionPerformed

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
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalAdministrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMarcarAsistencia;
    private javax.swing.JButton btn_almacenes;
    private javax.swing.JButton btn_cerrarSesion;
    private javax.swing.JButton btn_compras;
    private javax.swing.JButton btn_cuenta;
    private javax.swing.JButton btn_personal;
    private javax.swing.JButton btn_tesoreria;
    private javax.swing.JButton btn_venta;
    private javax.swing.JPanel content;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblFrase;
    // End of variables declaration//GEN-END:variables
}
