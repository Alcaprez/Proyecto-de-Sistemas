package edu.UPAO.proyecto.app;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogoNuevaMerma extends javax.swing.JDialog {

    private DefaultTableModel modelo;
    // Ajusta tus credenciales si cambiaron
    private final String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    private final String usuario = "root";
    private final String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

    public DialogoNuevaMerma(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configurarTabla();
        this.setLocationRelativeTo(parent);
        this.setTitle("Registro de Mermas y Bajas");
    }

    private void configurarTabla() {
        modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Código", "Producto", "Stock Actual", "Fecha Venc."}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tblProductos.setModel(modelo);
        // Ocultar columna ID visualmente pero mantener el dato
        tblProductos.getColumnModel().getColumn(0).setMinWidth(0);
        tblProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblProductos.getColumnModel().getColumn(0).setWidth(0);
    }

    // --- LÓGICA DE BÚSQUEDA ---
    private void buscarProducto() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe algo para buscar.");
            return;
        }

        modelo.setRowCount(0);
        String sql = "SELECT p.id_producto, p.codigo, p.nombre, i.stock_actual, i.fecha_caducidad " +
                     "FROM producto p " +
                     "INNER JOIN inventario_sucursal i ON p.id_producto = i.id_producto " +
                     "WHERE (p.nombre LIKE ? OR p.codigo LIKE ?) AND i.id_sucursal = 1 AND i.stock_actual > 0";

        try (Connection con = DriverManager.getConnection(url, usuario, password);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getInt("stock_actual"),
                    rs.getDate("fecha_caducidad")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    // --- LÓGICA DE REGISTRO DE MERMA ---
    private void registrarMerma() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }

        // 1. Obtener datos de la selección
        int idProducto = Integer.parseInt(tblProductos.getValueAt(fila, 0).toString());
        String nombreProd = tblProductos.getValueAt(fila, 2).toString();
        int stockActual = Integer.parseInt(tblProductos.getValueAt(fila, 3).toString());

        // 2. Validar cantidad a retirar
        String cantStr = txtCantidad.getText().trim();
        if (cantStr.isEmpty() || !cantStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Ingresa una cantidad válida.");
            return;
        }
        int cantidad = Integer.parseInt(cantStr);

        if (cantidad <= 0 || cantidad > stockActual) {
            JOptionPane.showMessageDialog(this, "La cantidad no puede ser cero ni mayor al stock actual (" + stockActual + ").");
            return;
        }

        String motivo = cboMotivo.getSelectedItem().toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de dar de baja " + cantidad + " unidades de:\n" + nombreProd + "\nPor motivo: " + motivo + "?",
            "Confirmar Merma", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // 3. Transacción en Base de Datos
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            con.setAutoCommit(false); // Iniciar transacción

            // A. Restar del Inventario
            String sqlUpdate = "UPDATE inventario_sucursal SET stock_actual = stock_actual - ? WHERE id_producto = ? AND id_sucursal = 1";
            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, cantidad);
                psUpd.setInt(2, idProducto);
                psUpd.executeUpdate();
            }

            // B. Registrar en Kardex / Historial (Movimiento)
            // Asumiendo que tu tabla 'movimiento_inventario' tiene estas columnas. Si no, ajusta el SQL.
            String sqlMov = "INSERT INTO movimiento_inventario (fecha_hora, tipo, cantidad, stock_anterior, stock_nuevo, estado, id_producto, id_sucursal) " +
                            "VALUES (NOW(), ?, ?, ?, ?, ?, ?, 1)";
            try (PreparedStatement psMov = con.prepareStatement(sqlMov)) {
                psMov.setString(1, "SALIDA MERMA (" + motivo + ")"); // Tipo con motivo incluido
                psMov.setInt(2, cantidad);
                psMov.setInt(3, stockActual);
                psMov.setInt(4, stockActual - cantidad);
                psMov.setString(5, "PROCESADO");
                psMov.setInt(6, idProducto);
                psMov.executeUpdate();
            }

            con.commit(); // Confirmar cambios
            JOptionPane.showMessageDialog(this, "Merma registrada correctamente.");
            
            // Limpiar y recargar
            txtCantidad.setText("");
            buscarProducto(); // Para ver el stock actualizado en la tabla

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- DISEÑO DE LA INTERFAZ (GENERADO MANUALMENTE) ---
    private void initComponents() {
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panelCentral = new JPanel(new BorderLayout());
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));

        // Componentes de Búsqueda
        JLabel lblBuscar = new JLabel("Buscar Producto:");
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(e -> buscarProducto()); // Enter para buscar
        JButton btnBuscar = new JButton("Buscar 🔍");
        btnBuscar.addActionListener(e -> buscarProducto());

        panelSuperior.add(lblBuscar);
        panelSuperior.add(txtBuscar);
        panelSuperior.add(btnBuscar);
        panelSuperior.setBackground(Color.WHITE);

        // Tabla
        tblProductos = new JTable();
        JScrollPane scroll = new JScrollPane(tblProductos);
        panelCentral.add(scroll, BorderLayout.CENTER);

        // Componentes de Registro (Abajo)
        JLabel lblCant = new JLabel("Cant. a Retirar:");
        txtCantidad = new JTextField(5);
        
        JLabel lblMotivo = new JLabel("Motivo:");
        cboMotivo = new JComboBox<>(new String[]{"VENCIMIENTO", "DAÑO / ROTURA", "ROBO / PÉRDIDA", "CONSUMO INTERNO"});
        
        JButton btnGuardar = new JButton("⚠️ CONFIRMAR MERMA");
        btnGuardar.setBackground(new Color(220, 53, 69)); // Rojo alerta
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.addActionListener(e -> registrarMerma());

        panelInferior.add(lblCant);
        panelInferior.add(txtCantidad);
        panelInferior.add(lblMotivo);
        panelInferior.add(cboMotivo);
        panelInferior.add(btnGuardar);
        panelInferior.setBackground(new Color(245, 245, 245));

        // Armar ventana
        this.setLayout(new BorderLayout());
        this.add(panelSuperior, BorderLayout.NORTH);
        this.add(panelCentral, BorderLayout.CENTER);
        this.add(panelInferior, BorderLayout.SOUTH);
        
        this.setSize(700, 450);
    }

    // Variables
    private JTextField txtBuscar;
    private JTable tblProductos;
    private JTextField txtCantidad;
    private JComboBox<String> cboMotivo;
}