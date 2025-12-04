package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.EmpleadoDAO;
import edu.UPAO.proyecto.DAO.ProductoDAO;
// AGREGAR ESTOS IMPORTS DEBAJO DE LOS QUE YA TIENES
import edu.UPAO.proyecto.DAO.CajaDAO;          // <--- FALTABA ESTE
import edu.UPAO.proyecto.LoginController;      // <--- FALTABA ESTE
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import edu.UPAO.proyecto.Modelo.Producto;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Menu2 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu2.class.getName());
    private String idEmpleado;
    private int idSucursal;

    private int idCaja;

    public Menu2(String idEmpleado) {
        initComponents();
         //btn_validar.addActionListener(e -> onValidarCupon());
        this.idEmpleado = idEmpleado;
        this.idSucursal = obtenerSucursalEmpleado(idEmpleado);
        this.idCaja = idCaja;
        System.out.println("Cajero - Empleado: " + this.idEmpleado + ", Sucursal: " + this.idSucursal);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Sistema Kuyay - Menú Principal");

        inicializarComponentes();
        verificarStockCajero(idEmpleado); 
        // AGREGAR ESTA LÍNEA AQUÍ:
        aplicarDisenoWeb(); 

        verificarStockCajero(idEmpleado);
}
    //------------------------
    // =========================================================================
    //  ÁREA DE DISEÑO WEB (AGREGAR AL FINAL DE LA CLASE)
    // =========================================================================
    private void aplicarDisenoWeb() {
        // 1. Colores y Fuentes Estilo Web
        java.awt.Color blancoInfinito = java.awt.Color.WHITE;
        java.awt.Color grisClaroHeader = new java.awt.Color(248, 249, 250); // Color humo para cabeceras
        java.awt.Color grisBorde = new java.awt.Color(230, 230, 230);
        java.awt.Font fuenteWeb = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
        java.awt.Font fuenteBold = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);

        // 2. Fondo Blanco Infinito (Resetear fondos de paneles principales)
        this.getContentPane().setBackground(blancoInfinito);
        // Y REEMPLÁZALA por esta:
        jPanel1.setBackground(new java.awt.Color(255, 153, 0)); // Color Naranja de la marca
        jPanel2.setBackground(blancoInfinito); // Navbar
        // 👇 AGREGA ESTA LÍNEA EXACTAMENTE AQUÍ 👇
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 200, 200)));
        panelFormulario.setBackground(blancoInfinito);
        jPanel7.setBackground(blancoInfinito); // Buscador
        jPanel8.setBackground(blancoInfinito); // Tabla productos
        panel.setBackground(blancoInfinito);   // Panel carrito (Izquierda)

        // 3. Estilizar la Cabecera (Eliminar imagen de fondo naranja si existía y dejarlo limpio)
        // Si deseas mantener el logo, no toques los labels, pero el fondo será blanco.
        jSeparator1.setForeground(grisBorde);
        jSeparator1.setBackground(grisBorde);

        // 4. Estilizar Botones de Navegación (Efecto Hover Línea Amarilla)
        javax.swing.JButton[] botonesMenu = {btn_cuenta, btn_ventas, btn_devolver, btn_compras};
        for (javax.swing.JButton btn : botonesMenu) {
            estilizarBotonNavegacion(btn);
        }
        // El toggle button requiere un trato similar
        estilizarBotonNavegacion(tb_entrada);

        // 5. Estilizar Tablas (Estilo Web: filas altas, sin grillas verticales)
        estilizarTablaWeb(tablaProductos);
        estilizarTablaWeb(miniTabla);

        // 6. Línea Divisoria Vertical (Separador entre Carrito y Productos)
        // Agregamos un borde derecho al panel del carrito
        panel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 2, new java.awt.Color(220, 220, 220)));
        
        // 7. Estilizar Inputs y Buscador
        txtBuscarCodigo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,200,200)), 
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // 8. Botones de Acción (Estilo Flat)
        estilizarBotonAccion(btn_SKU, new java.awt.Color(255, 193, 7)); // Amarillo
        estilizarBotonAccion(btn_buscar, new java.awt.Color(255, 193, 7));
        estilizarBotonAccion(btn_Siguiente, new java.awt.Color(40, 167, 69)); // Verde
        estilizarBotonAccion(btn_agregar, new java.awt.Color(40, 167, 69));
        estilizarBotonAccion(btn_cancelar, new java.awt.Color(220, 53, 69)); // Rojo
        estilizarBotonAccion(btn_salir, new java.awt.Color(220, 53, 69));
        
        // Ajuste visual del separador del header
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, grisBorde));
    }

    private void estilizarBotonNavegacion(javax.swing.AbstractButton btn) {
        // 1. Configuración Visual Base
        btn.setContentAreaFilled(false); // Quitar fondo gris por defecto
        btn.setFocusPainted(false);      // Quitar cuadro de enfoque
        btn.setOpaque(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // IMPORTANTE: Esto debe estar en TRUE para que la línea se dibuje
        btn.setBorderPainted(true); 

        // 2. Definir Fuente y Colores
        java.awt.Font fuente = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);
        java.awt.Color colorNormal = new java.awt.Color(0, 0, 0);     // Negro
        java.awt.Color colorHover = new java.awt.Color(255, 193, 7);  // Amarillo/Naranja (Tu color deseado)

        btn.setFont(fuente);
        btn.setForeground(colorNormal);

        // 3. BORDE INVISIBLE (ESTADO NORMAL)
        // Usamos un borde compuesto: 
        // - Exterior: Borde Mate de 3px abajo (pero transparente/invisible por ahora)
        // - Interior: Espacio vacío (padding) para separar el texto de la línea
        javax.swing.border.Border bordeInvisible = javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(255, 255, 255, 0)), // Línea Transparente
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 2, 10) // Relleno interno
        );
        
        btn.setBorder(bordeInvisible);

        // 4. EVENTOS DEL MOUSE (HOVER)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Cambiar color de texto
                btn.setForeground(colorHover);
                
                // Cambiar el borde transparente por uno de color SÓLIDO
                btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, colorHover), // <--- AQUÍ APARECE LA LÍNEA
                    javax.swing.BorderFactory.createEmptyBorder(5, 10, 2, 10) // Mismo relleno para que no se mueva el texto
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Volver al estado normal
                btn.setForeground(colorNormal);
                btn.setBorder(bordeInvisible);
            }
        });
    }

    private void estilizarTablaWeb(javax.swing.JTable tabla) {
        tabla.setRowHeight(40); // Filas más altas
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new java.awt.Color(230, 230, 230));
        tabla.setSelectionBackground(new java.awt.Color(232, 240, 254)); // Azul muy suave al seleccionar
        tabla.setSelectionForeground(java.awt.Color.BLACK);
        tabla.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        
        // Cabecera
        javax.swing.table.JTableHeader header = tabla.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new java.awt.Color(250, 250, 250)); // Fondo gris muy claro
                setForeground(new java.awt.Color(100, 100, 100)); // Texto gris
                setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(230, 230, 230)));
                return this;
            }
        });
        
        // Scroll pane limpio
        if (tabla.getParent() instanceof javax.swing.JViewport) {
            javax.swing.JScrollPane scroll = (javax.swing.JScrollPane) tabla.getParent().getParent();
            scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            scroll.getViewport().setBackground(java.awt.Color.WHITE);
        }
    }
    
    private void estilizarBotonAccion(javax.swing.JButton btn, java.awt.Color colorFondo) {
        btn.setBackground(colorFondo);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    //---------------------------
    
    private int obtenerIdCajaAbierta() {
        int idCaja = -1;
        String sql = "SELECT id_caja FROM caja WHERE id_empleado = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";

        try (java.sql.Connection cn = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, this.idEmpleado); // Usamos el ID del empleado logueado
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idCaja = rs.getInt("id_caja");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar caja abierta: " + e.getMessage());
        }
        return idCaja;
    }

    private void verificarStockCajero(String idEmpleado) {
        // Usar un hilo secundario para no congelar la ventana mientras carga
        new Thread(() -> {
            try {
                // 1. Obtener la sucursal del empleado
                edu.UPAO.proyecto.DAO.EmpleadoDAO empleadoDAO = new edu.UPAO.proyecto.DAO.EmpleadoDAO();
                int idSucursal = empleadoDAO.obtenerSucursalEmpleado(idEmpleado);

                // 2. Buscar alertas para ESA sucursal
                edu.UPAO.proyecto.DAO.InventarioSucursalDAO inventarioDAO = new edu.UPAO.proyecto.DAO.InventarioSucursalDAO();
                java.util.List<String> alertas = inventarioDAO.obtenerAlertasBajoStock(idSucursal);

                // 3. Mostrar si hay alertas
                if (!alertas.isEmpty()) {
                    StringBuilder mensaje = new StringBuilder("⚠️ ALERTA DE STOCK BAJO ⚠️\n\n");
                    // Mostrar solo los primeros 10 para no saturar la pantalla
                    int limite = Math.min(alertas.size(), 10);
                    for (int i = 0; i < limite; i++) {
                        mensaje.append(alertas.get(i)).append("\n\n");
                    }

                    if (alertas.size() > 10) {
                        mensaje.append("... y ").append(alertas.size() - 10).append(" productos más.");
                    }

                    mensaje.append("\nPor favor, notifique al administrador.");

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(this,
                                mensaje.toString(),
                                "Stock Crítico en Sucursal",
                                javax.swing.JOptionPane.WARNING_MESSAGE);
                    });
                }
            } catch (Exception e) {
                System.err.println("Error verificando stock cajero: " + e.getMessage());
            }
        }).start();
    }

    public void finalizarVenta() {
        // 1. Limpiar campos de texto
        txtBuscarCodigo.setText("");
        txtCupon.setText("");
        txtObservaciones.setText("");

        // 2. Reiniciar etiquetas y spinners
        lbl_subtotal.setText("Subtotal: S/ 0.00");
        lbl_descuento.setText("Descuento: S/ 0.00");
        resultadoTotal.setText("S/ 0.00");
        spCantidad.setValue(1);

        // 3. Vaciar la tabla del carrito
        javax.swing.table.DefaultTableModel modeloCarrito = (javax.swing.table.DefaultTableModel) miniTabla.getModel();
        modeloCarrito.setRowCount(0);

        // 4. Desactivar controles opcionales
        rb_cupon.setSelected(false);
        txtCupon.setEnabled(false);
        rb_observacion.setSelected(false);
        txtObservaciones.setEnabled(false);

        // 5. 🔥 RECARGAR EL STOCK DE LA TABLA DE PRODUCTOS (Actualizar vista)
        cargarProductosEnTabla();

        System.out.println("🔄 Venta finalizada: Interfaz limpia y stock actualizado.");
    }

    private void inicializarComponentes() {
        inicializarTablaProductos();
        txtObservaciones.setEnabled(false);
        txtCupon.setEnabled(false);

        // Configurar spinners
        spCantidad.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        sp_item.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        // Listeners
        configurarListeners();

        // Cargar productos iniciales
        cargarProductosEnTabla();
    }

    private void configurarListeners() {
        // Mouse wheel para spinners
        sp_item.addMouseWheelListener(e -> {
            int rot = e.getWheelRotation();
            int val = (int) sp_item.getValue();
            if (rot < 0) {
                sp_item.setValue(Math.max(1, val + 1));
            } else if (rot > 0) {
                sp_item.setValue(Math.max(1, val - 1));
            }
        });

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                spCantidad.setValue(1);
            }
        });

        // ✅ CORREGIDO: MouseWheelListener para cantidad
        spCantidad.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            int valorActual = (int) spCantidad.getValue();

            int fila = tablaProductos.getSelectedRow();
            if (fila == -1) {
                return;
            }

            String codigo = tablaProductos.getValueAt(fila, 0).toString();
            ProductoDAO productoDAO = new ProductoDAO();

            // ✅ USAR NUEVO MÉTODO CON SUCURSAL
            Producto producto = productoDAO.buscarPorCodigo(codigo, this.idSucursal);

            if (producto == null) {
                productoDAO.cerrarConexion();
                return;
            }

            int stock = producto.getStock();
            int nuevoValor = valorActual;

            if (notches < 0 && valorActual < stock) {
                nuevoValor++;
            } else if (notches > 0 && valorActual > 1) {
                nuevoValor--;
            }

            spCantidad.setValue(nuevoValor);
            productoDAO.cerrarConexion();
        });

        miniTabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && miniTabla.getSelectedRow() != -1) {
                sp_item.setValue(1);
            }
        });

        // ✅ CORREGIDO: DocumentListener para búsqueda
        txtBuscarCodigo.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String texto = txtBuscarCodigo.getText();
                List<Producto> filtrados = buscarProductosPorNombre(texto);

                DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
                modelo.setRowCount(0);

                for (Producto p : filtrados) {
                    modelo.addRow(new Object[]{
                        p.getCodigo(),
                        p.getNombre(),
                        p.getPrecioVenta(),
                        p.getStock()
                    });
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        // Ocultar columna código en miniTabla
        miniTabla.getColumnModel().getColumn(4).setMinWidth(0);
        miniTabla.getColumnModel().getColumn(4).setMaxWidth(0);
        miniTabla.getColumnModel().getColumn(4).setWidth(0);
    }

    private int obtenerSucursalEmpleado(String idEmpleado) {
        try {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            int sucursal = empleadoDAO.obtenerSucursalEmpleado(idEmpleado);
            empleadoDAO.cerrarConexion();
            return sucursal;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo sucursal: " + e.getMessage());
            return 1; // Por defecto
        }
    }

    public String getIdEmpleado() {
        return this.idEmpleado;
    }

    public void cargarProductosEnTabla() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.listarPorSucursal(this.idSucursal);

            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            for (Producto p : productos) {
                modelo.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getPrecioVenta(),
                    p.getStock()
                });
            }

            dao.cerrarConexion();
            System.out.println("Productos cargados para sucursal: " + this.idSucursal);

        } catch (Exception e) {
            System.err.println("ERROR en cargarProductosEnTabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializarTablaProductos() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Precio", "Stock"}, 0
        );
        tablaProductos.setModel(modelo);
    }

    private void onValidarCupon() {
        try {
            String cupon = txtCupon.getText().trim(); // o txtBuscarCodigo si ese es tu campo
            if (cupon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un cupón primero.");
                return;
            }

            // items del carrito desde tu JTable (miniTabla)
            java.util.List<edu.UPAO.proyecto.Modelo.VentaItem> itemsDelCarrito = obtenerItemsDelCarrito();

            double subtotal = calcularSubtotalCarrito();

            // aplicar cupon (método estático, NO necesitas new PromocionController())
            double descuento = edu.UPAO.proyecto.PromocionController.aplicarCupon(
                    cupon, itemsDelCarrito, subtotal
            );
            double totalConCupon = subtotal - descuento;

            // feedback + labels
            if (descuento > 0) {
                double porcentaje = (subtotal > 0) ? (descuento / subtotal) * 100 : 0;
                JOptionPane.showMessageDialog(this, "✓ Cupón válido: " + (int) porcentaje + "% aplicado.");
            } else {
                JOptionPane.showMessageDialog(this, "✗ Cupón inválido, caducado o inactivo.");
            }

            lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
            lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
            resultadoTotal.setText(String.format("S/ %.2f", totalConCupon));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al validar cupón: " + ex.getMessage());
        }
    }

    // helper robusto para números (soporta "15", "15.0", "15,0")
    private double parseNum(Object v) {
        if (v == null) {
            return 0.0;
        }
        String s = String.valueOf(v).trim().replace(",", ".");
        if (s.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private List<edu.UPAO.proyecto.Modelo.VentaItem> obtenerItemsDelCarrito() {
        List<edu.UPAO.proyecto.Modelo.VentaItem> items = new ArrayList<>();
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        for (int i = 0; i < carrito.getRowCount(); i++) {
            String nombre = String.valueOf(carrito.getValueAt(i, 0)); // Nombre
            double cantD = parseNum(carrito.getValueAt(i, 1));       // Cantidad (col 1)
            int cantidad = (int) Math.round(cantD <= 0 ? 0 : cantD);
            double precioUnit = parseNum(carrito.getValueAt(i, 2));       // P/U (col 2)
            double subtotal = parseNum(carrito.getValueAt(i, 3));       // Subtotal (col 3)

            if (precioUnit <= 0 && cantidad > 0) {
                precioUnit = subtotal / cantidad;
            }

            items.add(new edu.UPAO.proyecto.Modelo.VentaItem(nombre, cantidad, precioUnit));
        }
        return items;
    }

    //-------------------------------------------------

    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btn_salir = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblFrase = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tb_entrada = new javax.swing.JToggleButton();
        btn_devolver = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btn_ventas = new javax.swing.JButton();
        btn_cuenta = new javax.swing.JButton();
        btn_compras = new javax.swing.JButton();
        panelFormulario = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btn_agregar = new javax.swing.JButton();
        spCantidad = new javax.swing.JSpinner();
        jButton11 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        btn_SKU = new javax.swing.JButton();
        btn_cancelar = new javax.swing.JButton();
        btn_Siguiente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        txtCupon = new javax.swing.JTextField();
        rb_observacion = new javax.swing.JRadioButton();
        rb_cupon = new javax.swing.JRadioButton();
        lbl_total = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        sp_item = new javax.swing.JSpinner();
        btn_actualizarItem = new javax.swing.JButton();
        btn_eliminarItem = new javax.swing.JButton();
        btn_validar = new javax.swing.JButton();
        lbl_descuento = new javax.swing.JLabel();
        lbl_subtotal = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        miniTabla = new javax.swing.JTable();
        resultadoTotal = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarCodigo = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        btn_buscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 153, 0));

        btn_salir.setText("SALIR");
        btn_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salirActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/frame/imagenes/miniLogo.png"))); // NOI18N

        lblFrase.setFont(new java.awt.Font("Harlow Solid Italic", 0, 36)); // NOI18N
        lblFrase.setForeground(new java.awt.Color(193, 28, 28));
        lblFrase.setText("Todo lo que necesitas al alcance");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(57, 57, 57)
                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                .addComponent(btn_salir, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_salir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lblFrase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1380, 100));

        jPanel2.setBackground(new java.awt.Color(179, 9, 1));

        tb_entrada.setBackground(new java.awt.Color(118, 158, 139));
        tb_entrada.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tb_entrada.setText("R. ENT / SAL");
        tb_entrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tb_entradaActionPerformed(evt);
            }
        });

        btn_devolver.setBackground(new java.awt.Color(113, 153, 143));
        btn_devolver.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_devolver.setText("HACER DEVOLUCION");
        btn_devolver.setPreferredSize(new java.awt.Dimension(129, 30));
        btn_devolver.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                btn_devolverComponentMoved(evt);
            }
        });
        btn_devolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_devolverActionPerformed(evt);
            }
        });

        btn_ventas.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_ventas.setText("VENTAS");
        btn_ventas.setPreferredSize(new java.awt.Dimension(58, 30));
        btn_ventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ventasActionPerformed(evt);
            }
        });

        btn_cuenta.setBackground(new java.awt.Color(186, 224, 186));
        btn_cuenta.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_cuenta.setText("MI CUENTA");
        btn_cuenta.setPreferredSize(new java.awt.Dimension(76, 30));
        btn_cuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cuentaActionPerformed(evt);
            }
        });

        btn_compras.setBackground(new java.awt.Color(113, 153, 143));
        btn_compras.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_compras.setText("COMPRAS");
        btn_compras.setPreferredSize(new java.awt.Dimension(129, 30));
        btn_compras.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                btn_comprasComponentMoved(evt);
            }
        });
        btn_compras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_comprasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btn_cuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tb_entrada, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_ventas, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_devolver, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_compras, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(646, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_entrada, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btn_cuenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ventas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_devolver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_compras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(115, 115, 115)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(685, 685, 685))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 104, 1380, 50));

        panelFormulario.setPreferredSize(new java.awt.Dimension(1400, 646));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_agregar.setBackground(new java.awt.Color(0, 153, 0));
        btn_agregar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_agregar.setForeground(new java.awt.Color(255, 255, 255));
        btn_agregar.setText("AGREGAR");
        btn_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregarActionPerformed(evt);
            }
        });
        jPanel8.add(btn_agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 530, 132, 48));
        jPanel8.add(spCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 120, 48));

        jButton11.setBackground(new java.awt.Color(110, 149, 106));
        jButton11.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("GESTIONAR CAJA");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 530, 200, 50));

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(500);
        }

        jPanel8.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 980, 490));

        panel.setBackground(new java.awt.Color(255, 255, 255));

        btn_SKU.setBackground(new java.awt.Color(110, 149, 106));
        btn_SKU.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        btn_SKU.setForeground(new java.awt.Color(255, 255, 255));
        btn_SKU.setText("ESCANEAR SKU");
        btn_SKU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SKUActionPerformed(evt);
            }
        });

        btn_cancelar.setBackground(new java.awt.Color(255, 0, 0));
        btn_cancelar.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_cancelar.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelar.setText("CANCELAR");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });

        btn_Siguiente.setBackground(new java.awt.Color(0, 153, 0));
        btn_Siguiente.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btn_Siguiente.setForeground(new java.awt.Color(255, 255, 255));
        btn_Siguiente.setText("SIGUIENTE");
        btn_Siguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SiguienteActionPerformed(evt);
            }
        });

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(5);
        jScrollPane1.setViewportView(txtObservaciones);

        txtCupon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCuponActionPerformed(evt);
            }
        });

        rb_observacion.setText("Observación");
        rb_observacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_observacionActionPerformed(evt);
            }
        });

        rb_cupon.setText("Cupón:");
        rb_cupon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_cuponActionPerformed(evt);
            }
        });

        lbl_total.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lbl_total.setText("TOTAL:");

        btn_actualizarItem.setText("Actualizar");
        btn_actualizarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarItemActionPerformed(evt);
            }
        });

        btn_eliminarItem.setText("Eliminar");
        btn_eliminarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(sp_item, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_actualizarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_eliminarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sp_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_actualizarItem)
                    .addComponent(btn_eliminarItem))
                .addContainerGap())
        );

        btn_validar.setText("VALIDAR CUP");
        btn_validar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_validarActionPerformed(evt);
            }
        });

        lbl_descuento.setText("Descuento: S./ 0.00");

        lbl_subtotal.setText("Subtotal: S./ 0.00");

        miniTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Cantidad", "P/U", "Subtotal", "CODIGO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(miniTabla);
        if (miniTabla.getColumnModel().getColumnCount() > 0) {
            miniTabla.getColumnModel().getColumn(0).setResizable(false);
            miniTabla.getColumnModel().getColumn(0).setPreferredWidth(220);
            miniTabla.getColumnModel().getColumn(1).setResizable(false);
            miniTabla.getColumnModel().getColumn(2).setResizable(false);
            miniTabla.getColumnModel().getColumn(3).setResizable(false);
            miniTabla.getColumnModel().getColumn(4).setResizable(false);
        }

        resultadoTotal.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        resultadoTotal.setText("S/.  0.00");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addComponent(rb_observacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(rb_cupon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCupon, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_validar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(btn_SKU, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelLayout.createSequentialGroup()
                                        .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lbl_total, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(resultadoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_Siguiente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbl_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_SKU, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(rb_observacion))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_subtotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rb_cupon)
                    .addComponent(txtCupon, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_validar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_total, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultadoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Siguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("PRODUCTO:");

        txtBuscarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarCodigoActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(110, 149, 106));
        jButton12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("FRECUENTES");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        btn_buscar.setBackground(new java.awt.Color(110, 149, 106));
        btn_buscar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btn_buscar.setForeground(new java.awt.Color(255, 255, 255));
        btn_buscar.setText("BUSCAR");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarCodigo)
                        .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(panelFormulario, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 152, 1380, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private List<Producto> buscarProductosPorNombre(String texto) {
        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.buscarPorNombre(texto, this.idSucursal);
        dao.cerrarConexion();
        return productos;
    }

    public void vaciarCarrito() {
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        modeloCarrito.setRowCount(0);
        spCantidad.setValue(1);

        lbl_subtotal.setText("Subtotal: S/ 0.00");
        lbl_descuento.setText("Descuento: S/ 0.00");
        resultadoTotal.setText("S/ 0.00");
        txtObservaciones.setText("");
        txtCupon.setText("");
    }

    public void actualizarTotal() {
        javax.swing.table.DefaultTableModel modeloCarrito = (javax.swing.table.DefaultTableModel) miniTabla.getModel();
        double subtotal = 0.0;

        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            Object valorSubtotal = modeloCarrito.getValueAt(i, 3);
            if (valorSubtotal != null) {
                try {
                    subtotal += Double.parseDouble(valorSubtotal.toString());
                } catch (NumberFormatException e) {
                    System.err.println("⚠ Error en fila " + i);
                }
            }
        }

        // ---------------------------------------------------------
        // 🔴 CÓDIGO VIEJO (CAUSANTE DEL ERROR): COMENTAR O BORRAR
        // PromocionController promoCtrl = new PromocionController();
        // double descuento = promoCtrl.calcularDescuentoReglas(modeloCarrito);
        // ---------------------------------------------------------
        // ✅ CÓDIGO NUEVO:
        // Por ahora ponemos 0 en automático. El descuento real se aplica
        // cuando presionas el botón "VALIDAR CUPÓN".
        double descuento = 0.0;

        // Si quisieras que el descuento del cupón se mantenga al agregar productos,
        // necesitarías una variable global 'descuentoActual', pero por ahora esto
        // elimina los errores.
        double total = subtotal - descuento;

        lbl_subtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
        lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
        resultadoTotal.setText("S/ " + String.format("%.2f", total));
    }

    private double calcularSubtotalCarrito() {
        double subtotalGeneral = 0.0;
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        for (int i = 0; i < carrito.getRowCount(); i++) {
            subtotalGeneral += parseNum(carrito.getValueAt(i, 3)); // Subtotal en col 3
        }
        return subtotalGeneral;
    }

    private boolean contieneSalteado(String texto, String patron) {
        int j = 0;
        for (int i = 0; i < texto.length() && j < patron.length(); i++) {
            if (texto.charAt(i) == patron.charAt(j)) {
                j++;
            }
        }
        return j == patron.length();
    }

    private void btn_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salirActionPerformed
        // 1. Obtener turno (Noche o Día)
        String turnoActual = obtenerTurnoActual();

        // 2. Verificar Rol y Turno
        // Si es NOCHE o es GERENTE, obligamos a hacer el arqueo (Cierre de caja diario)
        if (turnoActual.equalsIgnoreCase("NOCHE") || LoginController.getTipoUsuario(idEmpleado).equalsIgnoreCase("GERENTE")) {

            CajaDAO cajaDAO = new CajaDAO();
            edu.UPAO.proyecto.Modelo.Caja caja = cajaDAO.obtenerCajaAbierta(idSucursal);

            if (caja != null) {
                // Abrir ventana de conteo de dinero (Arqueo)
                DialogoArqueoCaja arqueo = new DialogoArqueoCaja(this, true, caja.getIdCaja());
                arqueo.setVisible(true);

                // Si el arqueo terminó bien, cerramos la ventana del menú y volvemos al login
                if (arqueo.cajaCerradaExito) {
                    this.dispose();
                    new LoginjFrame().setVisible(true);
                }
            } else {
                // Si por error no había caja abierta, salimos normal
                this.dispose();
                new LoginjFrame().setVisible(true);
            }

        } else {
            // ==> TURNOS MAÑANA / TARDE (O NOCHE ANTES DEL CIERRE)
            // Solo cierran sesión, la caja sigue abierta para el siguiente compañero

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea cerrar su sesión? (La caja permanecerá abierta para el siguiente turno)",
                    "Salida de Turno",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginjFrame().setVisible(true);
            }
        }
    }

// Método auxiliar sugerido para determinar si corresponde cerrar
    private String obtenerTurnoActual() {
        java.time.LocalTime horaActual = java.time.LocalTime.now();

        // Si son más de las 19:00 (7 PM), lo consideramos turno NOCHE (cierre de día)
        if (horaActual.isAfter(java.time.LocalTime.of(19, 0))) {
            return "NOCHE";
        }
        return "DIA";
    }//GEN-LAST:event_btn_salirActionPerformed

    private void irALogin() {
        LoginjFrame login = new LoginjFrame();
        login.setVisible(true);
        this.dispose();
    }

    private void cerrarCajaAutomatica() {
        try {
            edu.UPAO.proyecto.DAO.CajaDAO cajaDAO = new edu.UPAO.proyecto.DAO.CajaDAO();

            // 1. Buscar mi caja abierta
            int idCaja = obtenerIdCajaAbierta(); // Método que ya creamos en la respuesta anterior

            if (idCaja != -1) {
                // 2. Calcular saldo final real
                double saldoFinal = cajaDAO.obtenerSaldoAcumuladoHistorico(this.idSucursal);

                // 3. Cerrar en BD
                boolean exito = cajaDAO.cerrarCaja(idCaja, saldoFinal);

                if (exito) {
                    System.out.println("✅ CAJA CERRADA CORRECTAMENTE. Saldo Final: " + saldoFinal);
                    JOptionPane.showMessageDialog(this, "Turno finalizado. Caja cerrada con: S/ " + saldoFinal);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar caja automática: " + e.getMessage());
        }
    }

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        String texto = txtBuscarCodigo.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un texto para buscar.");
            return;
        }

        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.buscarPorNombre(texto, this.idSucursal);

        DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
        modelo.setRowCount(0);

        for (Producto p : productos) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getPrecioVenta(), p.getStock()});
        }

        dao.cerrarConexion();

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron productos con: '" + texto + "'");
        }
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        try {
            // ✅ CORREGIDO: Usar nuestro método que sí considera sucursal
            cargarProductosEnTabla();
            JOptionPane.showMessageDialog(this,
                    "Productos cargados para sucursal " + this.idSucursal);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void txtBuscarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarCodigoActionPerformed

    }//GEN-LAST:event_txtBuscarCodigoActionPerformed

    private void btn_validarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_validarActionPerformed
        try {
            String codigo = txtCupon.getText().trim();
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un cupón.");
                return;
            }

            java.util.Optional<edu.UPAO.proyecto.Modelo.Cupon> opt
                    = edu.UPAO.proyecto.DAO.CuponDAO.buscarPorCodigo(codigo);

            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Cupón no existe.");
                return;
            }

            edu.UPAO.proyecto.Modelo.Cupon cupon = opt.get();
            double subtotal = calcularSubtotalCarrito();

            // Validaciones
            if (!cupon.isVigente(java.time.LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "⛔ Cupón vencido o inactivo.");
                return;
            }
            if (!cupon.cumpleMinimo(subtotal)) {
                JOptionPane.showMessageDialog(this, "⚠️ Compra mínima: S/. " + cupon.getMinimoCompra());
                return;
            }

            // ✅ CORREGIDO: Solo pasamos subtotal (eliminamos 'items')
            double descuento = cupon.calcularDescuento(subtotal);

            if (descuento > 0) {
                JOptionPane.showMessageDialog(this, "✅ ¡Descuento aplicado! S/. " + descuento);
                lbl_descuento.setText("Descuento: S/ " + String.format("%.2f", descuento));

                double total = Math.max(0, subtotal - descuento);
                resultadoTotal.setText("S/ " + String.format("%.2f", total));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_validarActionPerformed

    private void btn_eliminarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarItemActionPerformed
        int fila = miniTabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel modelo = (DefaultTableModel) miniTabla.getModel();
            modelo.removeRow(fila);
            actualizarTotal(); // recalcula el total después de eliminar
        }
    }//GEN-LAST:event_btn_eliminarItemActionPerformed

    private void btn_actualizarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarItemActionPerformed
        int fila = miniTabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar.");
            return;
        }

        int nuevaCantidad = (int) sp_item.getValue();
        if (nuevaCantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) miniTabla.getModel();
        String codigo = modelo.getValueAt(fila, 4).toString();

        ProductoDAO productoDAO = new ProductoDAO();

        // ✅ USAR NUEVO MÉTODO CON SUCURSAL
        Producto producto = productoDAO.buscarPorCodigo(codigo, this.idSucursal);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el producto en la base de datos.");
            productoDAO.cerrarConexion();
            return;
        }

        if (nuevaCantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
            productoDAO.cerrarConexion();
            return;
        }

        modelo.setValueAt(nuevaCantidad, fila, 1);
        modelo.setValueAt(producto.getPrecioVenta(), fila, 2);
        modelo.setValueAt(producto.getPrecioVenta() * nuevaCantidad, fila, 3);

        actualizarTotal();
        productoDAO.cerrarConexion();
    }//GEN-LAST:event_btn_actualizarItemActionPerformed

    private void rb_cuponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_cuponActionPerformed
        txtCupon.setEnabled(rb_cupon.isSelected());
    }//GEN-LAST:event_rb_cuponActionPerformed

    private void rb_observacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_observacionActionPerformed
        txtObservaciones.setEnabled(rb_observacion.isSelected());
    }//GEN-LAST:event_rb_observacionActionPerformed

    private void txtCuponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCuponActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCuponActionPerformed

    private void btn_SiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SiguienteActionPerformed
        DefaultTableModel carrito = (DefaultTableModel) miniTabla.getModel();

        // Crear un clon del carrito
        DefaultTableModel carritoClonado = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "P/U", "Subtotal", "Código"}, 0);

        for (int i = 0; i < carrito.getRowCount(); i++) {
            Object[] fila = new Object[carrito.getColumnCount()];
            for (int j = 0; j < carrito.getColumnCount(); j++) {
                fila[j] = carrito.getValueAt(i, j);
            }
            carritoClonado.addRow(fila);
        }

        // Obtener los valores actuales de los labels
        String subtotal = lbl_subtotal.getText().replace("Subtotal:", "").trim();
        String descuento = lbl_descuento.getText().replace("Descuento:", "").trim();
        String total = resultadoTotal.getText().trim();

        // ✅ OBTENER OBSERVACIONES DEL TEXTAREA
        String observaciones = txtObservaciones.getText().trim();

        // ✅ PASAR TODOS LOS PARÁMETROS INCLUYENDO OBSERVACIONES
        jFrame_GenerarBoleta boletaFrame = new jFrame_GenerarBoleta(
                this,
                carritoClonado,
                subtotal,
                descuento,
                total,
                this.idEmpleado,
                this.idSucursal,
                observaciones // ✅ NUEVO PARÁMETRO
        );

        boletaFrame.setLocationRelativeTo(this);
        boletaFrame.setVisible(true);
    }//GEN-LAST:event_btn_SiguienteActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        modeloCarrito.setRowCount(0); // vacía el carrito
        spCantidad.setValue(1);
        sp_item.setValue(1);

        lbl_subtotal.setText("Subtotal: S/ 0.00");
        lbl_descuento.setText("Descuento: S/ 0.00");
        resultadoTotal.setText("S/ 0.00");

        txtObservaciones.setText("");
        txtCupon.setText("");
    }//GEN-LAST:event_btn_cancelarActionPerformed

    private void btn_SKUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SKUActionPerformed
        btn_SKU.setText("Escaneando...");
        btn_SKU.setEnabled(false);

        Timer t = new Timer(2000, e -> {
            ProductoDAO productoDAO = new ProductoDAO();
            List<Producto> productos = productoDAO.listarPorSucursal(this.idSucursal);

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos cargados.");
            } else {
                Random rand = new Random();
                Producto producto = productos.get(rand.nextInt(productos.size()));
                int cantidad = 1;

                if (producto.getStock() <= 0) {
                    JOptionPane.showMessageDialog(this, "❌ El producto " + producto.getNombre() + " no tiene stock.");
                } else {
                    DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();

                    if (cantidad > producto.getStock()) {
                        JOptionPane.showMessageDialog(this,
                                "⚠ Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
                    } else {
                        double precioUnitario = producto.getPrecioVenta();
                        double subtotal = precioUnitario * cantidad;

                        modeloCarrito.addRow(new Object[]{
                            producto.getNombre(),
                            cantidad,
                            precioUnitario,
                            subtotal,
                            producto.getCodigo()
                        });

                        actualizarTotal();
                        JOptionPane.showMessageDialog(this, "✅ Producto escaneado: " + producto.getNombre());
                    }
                }
            }

            productoDAO.cerrarConexion();
            btn_SKU.setText("ESCANEAR SKU");
            btn_SKU.setEnabled(true);
        });

        t.setRepeats(false);
        t.start();
    }//GEN-LAST:event_btn_SKUActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        GestionCajaFrame cajaFrame = new GestionCajaFrame(this.idSucursal);
        cajaFrame.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btn_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregarActionPerformed
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto primero.");
            return;
        }

        String codigo = tablaProductos.getValueAt(filaSeleccionada, 0).toString().trim();
        ProductoDAO productoDAO = new ProductoDAO();

        // ✅ USAR NUEVO MÉTODO CON SUCURSAL
        Producto producto = productoDAO.buscarPorCodigo(codigo, this.idSucursal);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, "❌ No se encontró el producto");
            productoDAO.cerrarConexion();
            return;
        }

        int cantidad = (int) spCantidad.getValue();

        if (cantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Stock insuficiente. Solo hay " + producto.getStock() + " unidades.");
            productoDAO.cerrarConexion();
            return;
        }

        double precioUnitario = producto.getPrecioVenta();
        double subtotal = precioUnitario * cantidad;

        DefaultTableModel modeloCarrito = (DefaultTableModel) miniTabla.getModel();
        modeloCarrito.addRow(new Object[]{
            producto.getNombre(),
            cantidad,
            precioUnitario,
            subtotal,
            producto.getCodigo()
        });

        actualizarTotal();
        productoDAO.cerrarConexion();
        System.out.println("✅ Producto agregado - Stock: " + producto.getStock());
    }//GEN-LAST:event_btn_agregarActionPerformed

    private void btn_cuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cuentaActionPerformed
// 1. Crear una ventana nueva (JFrame)
        javax.swing.JFrame ventanaCuenta = new javax.swing.JFrame("Mi Cuenta - Kuyay");

        // 2. Crear tu panel pasándole el ID
        panel_Cuenta miPanel = new panel_Cuenta(this.idEmpleado); // Usamos el ID que ya tiene Menu2

        // 3. Configurar la ventana
        ventanaCuenta.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE); // Solo cerrar esta ventana, no toda la app
        ventanaCuenta.setContentPane(miPanel); // Metemos el panel dentro
        ventanaCuenta.pack(); // Ajustar tamaño automáticamente al contenido del panel
        ventanaCuenta.setLocationRelativeTo(this); // Centrar sobre el menú

        // 4. Mostrar
        ventanaCuenta.setVisible(true);
    }//GEN-LAST:event_btn_cuentaActionPerformed

    private void btn_ventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ventasActionPerformed
        HistorialVentasFrame historial = new HistorialVentasFrame(this.idEmpleado);
        historial.setVisible(true);
    }//GEN-LAST:event_btn_ventasActionPerformed

    private void btn_devolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_devolverActionPerformed
        int idCajaActual = obtenerIdCajaAbierta();

        // 2. Validamos que exista una caja abierta
        if (idCajaActual == -1) {
            JOptionPane.showMessageDialog(this,
                    "No tienes una caja abierta actualmente.\nNo se pueden realizar devoluciones de dinero.",
                    "Caja Cerrada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Abrimos la ventana de devoluciones pasándole todos los datos
        DialogoDevolucionVenta dialogo = new DialogoDevolucionVenta(
                this, // Ventana padre (Menu2)
                true, // Modal (bloquea la ventana de atrás)
                idCajaActual, // ID Caja recuperado de la BD
                this.idSucursal, // ID Sucursal que ya tenías en Menu2
                this.idEmpleado // ID Empleado que ya tenías en Menu2
        );

        dialogo.setVisible(true);

        // Opcional: Recargar productos por si hubo devolución a stock
        cargarProductosEnTabla();
    }//GEN-LAST:event_btn_devolverActionPerformed

    private void btn_devolverComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_btn_devolverComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_devolverComponentMoved

    private void tb_entradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tb_entradaActionPerformed
        jFrame_Asistncias jFrame_Asistncias1;
        jFrame_Asistncias1 = new jFrame_Asistncias();
        jFrame_Asistncias1.setVisible(true);
    }//GEN-LAST:event_tb_entradaActionPerformed

    private void btn_comprasComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_btn_comprasComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_comprasComponentMoved

    private void btn_comprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_comprasActionPerformed
        // 1. Validar si hay caja abierta (Reutilizando tu método existente)
        int idCajaActual = obtenerIdCajaAbierta(); // O usar this.idCaja si ya lo guardaste en el constructor

        if (idCajaActual == -1 || idCajaActual == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay caja abierta. No se puede sacar dinero.",
                    "Error de Caja", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Abrir la ventana de Gastos
        DialogoRegistrarGasto dialogo = new DialogoRegistrarGasto(
                this,
                true,
                idCajaActual,
                this.idSucursal
        );

        dialogo.setVisible(true);

        // Opcional: Si tienes un label que muestra el saldo actual en pantalla, aquí podrías actualizarlo
        // actualizarLabelSaldo();
    }//GEN-LAST:event_btn_comprasActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* ✅ CAMBIO: Abrir LoginjFrame en lugar de Menu2 directamente */
        java.awt.EventQueue.invokeLater(() -> {
            LoginjFrame login = new LoginjFrame();
            login.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_SKU;
    private javax.swing.JButton btn_Siguiente;
    private javax.swing.JButton btn_actualizarItem;
    private javax.swing.JButton btn_agregar;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_compras;
    private javax.swing.JButton btn_cuenta;
    private javax.swing.JButton btn_devolver;
    private javax.swing.JButton btn_eliminarItem;
    private javax.swing.JButton btn_salir;
    private javax.swing.JButton btn_validar;
    private javax.swing.JButton btn_ventas;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFrase;
    private javax.swing.JLabel lbl_descuento;
    private javax.swing.JLabel lbl_subtotal;
    private javax.swing.JLabel lbl_total;
    private javax.swing.JTable miniTabla;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JRadioButton rb_cupon;
    private javax.swing.JRadioButton rb_observacion;
    private javax.swing.JLabel resultadoTotal;
    private javax.swing.JSpinner spCantidad;
    private javax.swing.JSpinner sp_item;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JToggleButton tb_entrada;
    private javax.swing.JTextField txtBuscarCodigo;
    private javax.swing.JTextField txtCupon;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
