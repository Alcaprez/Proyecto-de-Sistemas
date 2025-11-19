
package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.ProductoCaducidad;
import edu.UPAO.proyecto.DAO.CaducidadCellRenderer;
import edu.UPAO.proyecto.DAO.Categoria;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.table.DefaultTableModel;
import static edu.UPAO.proyecto.DAO.ColorADM.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.BorderFactory;

public class VENTAS_Admin extends javax.swing.JPanel {
    
    private DefaultTableModel modeloTabla;
    private List<ProductoCaducidad> listaProductos;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Categoria> listaCategorias;
    private Categoria categoriaSeleccionada;
    private JPanel panelCategorias; // Reemplazará jList1
    private int nextCategoriaId = 1;
    private DefaultTableModel modeloTablaProductos;
    private ButtonGroup grupoOpciones;
    
    
    public VENTAS_Admin() {
        initComponents();
        inicializarTabla();
        configurarComboBoxes();
        cargarDatosEjemplo();
        actualizarMetricas();
        configurarBuscador();
        inicializarInventarioTienda();
        configurarTextField2();
        inicializarGestionProductos();
    }
    
    /////////////////////////////////////////////////////
    // MÉTODOS DE INVENTARIO DE TIENDA
    /////////////////////////////////////////////////////
    
    private void inicializarInventarioTienda() {
        listaCategorias = new ArrayList<>();
        configurarPanelCategorias();
        jPanel9.setLayout(new BoxLayout(jPanel9, BoxLayout.Y_AXIS));
        configurarPanelProductos();
        cargarCategoriasEjemplo();
        actualizarVistaCategorias();
    }

    private void configurarPanelProductos() {
        jPanel9.setBackground(Color.WHITE);
        jScrollPane3.getViewport().setBackground(Color.WHITE);
    }
    
    private void configurarPanelCategorias() {
        panelCategorias = new JPanel();
        panelCategorias.setLayout(new BoxLayout(panelCategorias, BoxLayout.Y_AXIS));
        panelCategorias.setBackground(Color.WHITE);
        jScrollPane2.setViewportView(panelCategorias);
    }

    private void cargarCategoriasEjemplo() {
        Categoria golosinas = new Categoria(nextCategoriaId++, "Golosinas", 
            "Caramelos, chicles y dulces variados");
        golosinas.agregarSubcategoria("Caramelos duros");
        golosinas.agregarSubcategoria("Gomas de mascar");
        golosinas.agregarSubcategoria("Paletas");
        listaCategorias.add(golosinas);
        
        Categoria bebidas = new Categoria(nextCategoriaId++, "Bebidas", 
            "Refrescos, jugos y bebidas frías");
        bebidas.agregarSubcategoria("Gaseosas");
        bebidas.agregarSubcategoria("Jugos naturales");
        bebidas.agregarSubcategoria("Agua mineral");
        bebidas.agregarSubcategoria("Energizantes");
        listaCategorias.add(bebidas);
        
        Categoria snacks = new Categoria(nextCategoriaId++, "Snacks Salados", 
            "Papas, maíz y frituras diversas");
        snacks.agregarSubcategoria("Papas fritas");
        snacks.agregarSubcategoria("Chizitos");
        snacks.agregarSubcategoria("Nachos");
        listaCategorias.add(snacks);
        
        Categoria chocolates = new Categoria(nextCategoriaId++, "Chocolates", 
            "Barras de chocolate y productos derivados");
        chocolates.agregarSubcategoria("Chocolate con leche");
        chocolates.agregarSubcategoria("Chocolate amargo");
        listaCategorias.add(chocolates);
    }

    private void actualizarVistaCategorias() {
        panelCategorias.removeAll();
        panelCategorias.add(Box.createVerticalStrut(10));
        
        for (Categoria categoria : listaCategorias) {
            JPanel cardCategoria = crearCardCategoria(categoria);
            panelCategorias.add(cardCategoria);
            panelCategorias.add(Box.createVerticalStrut(10));
        }
        
        panelCategorias.revalidate();
        panelCategorias.repaint();
    }

    private JPanel crearCardCategoria(Categoria categoria) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(300, 90));
        card.setPreferredSize(new Dimension(300, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        if (categoria.equals(categoriaSeleccionada)) {
            card.setBackground(CARD_SELECCIONADA);
        } else {
            card.setBackground(CARD_FONDO);
        }
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setOpaque(false);
        
        JLabel lblNombre = new JLabel(categoria.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(TITULO);
        
        JLabel lblDescripcion = new JLabel("<html>" + categoria.getDescripcion() + "</html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(DESCRIPCION);
        
        JLabel lblSubcategorias = new JLabel(categoria.getCantidadSubcategorias() + " subcategorías");
        lblSubcategorias.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubcategorias.setForeground(SUBCATEGORIAS);
        
        panelInfo.add(lblNombre);
        panelInfo.add(Box.createVerticalStrut(3));
        panelInfo.add(lblDescripcion);
        panelInfo.add(Box.createVerticalStrut(3));
        panelInfo.add(lblSubcategorias);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotones.setOpaque(false);
        
        JButton btnEditar = new JButton("✏");
        btnEditar.setFont(new Font("Arial Unicode MS", Font.BOLD, 14));
        btnEditar.setForeground(new Color(255, 140, 0));
        btnEditar.setBorderPainted(false);
        btnEditar.setContentAreaFilled(false);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setToolTipText("Editar categoría");
        btnEditar.addActionListener(e -> editarCategoria(categoria));
        
        JButton btnEliminar = new JButton("✖");
        btnEliminar.setFont(new Font("Arial Unicode MS", Font.BOLD, 14));
        btnEliminar.setForeground(new Color(220, 53, 69));
        btnEliminar.setBorderPainted(false);
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setToolTipText("Eliminar categoría");
        btnEliminar.addActionListener(e -> eliminarCategoria(categoria));
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        
        card.add(panelInfo, BorderLayout.CENTER);
        card.add(panelBotones, BorderLayout.EAST);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarCategoria(categoria);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!categoria.equals(categoriaSeleccionada)) {
                    card.setBackground(CARD_HOVER);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!categoria.equals(categoriaSeleccionada)) {
                    card.setBackground(CARD_FONDO);
                }
            }
        });
        
        return card;
    }

    private void seleccionarCategoria(Categoria categoria) {
        categoriaSeleccionada = categoria;
        actualizarVistaCategorias();
        actualizarVistaProductos();
    }

    private void actualizarVistaProductos() {
        jPanel9.removeAll();
        
        if (categoriaSeleccionada == null) {
            JLabel lblVacio = new JLabel("Selecciona una categoría");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblVacio.setForeground(Color.GRAY);
            jPanel9.add(lblVacio);
        } else {
            jLabel6.setText("Productos: " + categoriaSeleccionada.getNombre());
            
            jPanel9.add(Box.createVerticalStrut(10));
            
            for (String subcategoria : categoriaSeleccionada.getSubcategorias()) {
                JPanel cardProducto = crearCardProducto(subcategoria);
                jPanel9.add(cardProducto);
                jPanel9.add(Box.createVerticalStrut(8));
            }
        }
        
        jPanel9.revalidate();
        jPanel9.repaint();
    }
    
    private JPanel crearCardProducto(String nombreProducto) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setMaximumSize(new Dimension(350, 45));
        card.setPreferredSize(new Dimension(350, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setBackground(PRODUCTO_FONDO);
        
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelInfo.setOpaque(false);
        
        JLabel lblNombre = new JLabel(nombreProducto);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombre.setForeground(PRODUCTO_TEXTO);
        
        int cantidadEjemplo = 15 + (int)(Math.random() * 85);
        JLabel lblCantidad = new JLabel("(" + cantidadEjemplo + " unid.)");
        lblCantidad.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCantidad.setForeground(new Color(128, 128, 128));
        
        panelInfo.add(lblNombre);
        panelInfo.add(lblCantidad);
        
        JButton btnEliminar = new JButton("✖");
        btnEliminar.setFont(new Font("Arial Unicode MS", Font.BOLD, 14));
        btnEliminar.setForeground(new Color(220, 53, 69));
        btnEliminar.setBorderPainted(false);
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setToolTipText("Eliminar producto");
        btnEliminar.addActionListener(e -> eliminarSubcategoria(nombreProducto));
        
        card.add(panelInfo, BorderLayout.CENTER);
        card.add(btnEliminar, BorderLayout.EAST);
        
        return card;
    }

    private void agregarCategoria() {
        JTextField txtNombre = new JTextField(20);
        JTextField txtDescripcion = new JTextField(30);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, 
            "Nueva Categoría", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre de la categoría no puede estar vacío.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            for (Categoria c : listaCategorias) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    JOptionPane.showMessageDialog(this, 
                        "Ya existe una categoría con ese nombre.",
                        "Nombre duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            Categoria nuevaCategoria = new Categoria(nextCategoriaId++, nombre, descripcion);
            listaCategorias.add(nuevaCategoria);
            actualizarVistaCategorias();
            
            JOptionPane.showMessageDialog(this, 
                "Categoría creada exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarSubcategoria() {
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona una categoría primero.",
                "Ninguna categoría seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreProducto = jTextField2.getText().trim();
        
        if (nombreProducto.isEmpty() || nombreProducto.equals("Nuevo Producto")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingresa un nombre para el producto.",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (categoriaSeleccionada.getSubcategorias().contains(nombreProducto)) {
            JOptionPane.showMessageDialog(this, 
                "Este producto ya existe en la categoría.",
                "Producto duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        categoriaSeleccionada.agregarSubcategoria(nombreProducto);
        jTextField2.setText("Nuevo Producto");
        actualizarVistaCategorias();
        actualizarVistaProductos();
        
        JOptionPane.showMessageDialog(this, 
            "Producto agregado exitosamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarCategoria(Categoria categoria) {
        JTextField txtNombre = new JTextField(categoria.getNombre(), 20);
        JTextField txtDescripcion = new JTextField(categoria.getDescripcion(), 30);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, 
            "Editar Categoría", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevaDescripcion = txtDescripcion.getText().trim();
            
            if (nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre no puede estar vacío.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            for (Categoria c : listaCategorias) {
                if (!c.equals(categoria) && c.getNombre().equalsIgnoreCase(nuevoNombre)) {
                    JOptionPane.showMessageDialog(this, 
                        "Ya existe una categoría con ese nombre.",
                        "Nombre duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            categoria.setNombre(nuevoNombre);
            categoria.setDescripcion(nuevaDescripcion);
            actualizarVistaCategorias();
            actualizarVistaProductos();
            
            JOptionPane.showMessageDialog(this, 
                "Categoría actualizada exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void eliminarCategoria(Categoria categoria) {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la categoría '" + categoria.getNombre() + 
            "' y todas sus subcategorías (" + categoria.getCantidadSubcategorias() + ")?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            listaCategorias.remove(categoria);
            
            if (categoria.equals(categoriaSeleccionada)) {
                categoriaSeleccionada = null;
            }
            
            actualizarVistaCategorias();
            actualizarVistaProductos();
            
            JOptionPane.showMessageDialog(this, 
                "Categoría eliminada exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void eliminarSubcategoria(String nombreProducto) {
        if (categoriaSeleccionada == null) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el producto '" + nombreProducto + "'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            categoriaSeleccionada.eliminarSubcategoria(nombreProducto);
            actualizarVistaCategorias();
            actualizarVistaProductos();
            
            JOptionPane.showMessageDialog(this, 
                "Producto eliminado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /////////////////////////////////////////////////////
    // MÉTODOS DE GESTIÓN DE CADUCIDAD
    /////////////////////////////////////////////////////
    
    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"Producto (Lote/SKU)", "Proveedor", 
                        "Fecha de Caducidad", "Días Restantes", "Cantidad"}, 0
        ) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(modeloTabla);
        
        CaducidadCellRenderer renderer = new CaducidadCellRenderer();
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        jTable1.setRowHeight(30);
        listaProductos = new ArrayList<>();
    }
    
    private void configurarComboBoxes() {
        Estado.removeAllItems();
        Estado.addItem("Todos");
        Estado.addItem("Vencidos");
        Estado.addItem("Vencen en 7 días");
        Estado.addItem("Vencen en 30 días");
        Estado.addItem("Normal");
        
        FiltroProveedor.removeAllItems();
        FiltroProveedor.addItem("Todos");
        FiltroProveedor.addItem("Proveedor A");
        FiltroProveedor.addItem("Proveedor B");
        FiltroProveedor.addItem("Proveedor C");
    }
    
    private void cargarDatosEjemplo() {
        listaProductos.add(new ProductoCaducidad(
            "PMI-03", "Yogurt de Molde Integral", "Proveedor A",
            LocalDate.of(2026, 1, 9), 250));
        
        listaProductos.add(new ProductoCaducidad(
            "LAC-15", "Leche Descremada", "Proveedor B",
            LocalDate.now().plusDays(5), 180));
        
        listaProductos.add(new ProductoCaducidad(
            "QUE-22", "Queso Fresco", "Proveedor A",
            LocalDate.now().minusDays(2), 50));
        
        listaProductos.add(new ProductoCaducidad(
            "MAN-08", "Mantequilla", "Proveedor C",
            LocalDate.now().plusDays(25), 120));
        
        actualizarTabla();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        
        for (ProductoCaducidad producto : listaProductos) {
            modeloTabla.addRow(new Object[]{
                producto.getNombreProducto() + " (" + producto.getLote() + ")",
                producto.getProveedor(),
                producto.getFechaCaducidad().format(formatter),
                producto.getDiasRestantes(),
                producto.getCantidad()
            });
        }
    }
    
    private void buscarProducto() {
        String textoBusqueda = jTextField1.getText().trim().toLowerCase();
        
        if (textoBusqueda.isEmpty() || 
            textoBusqueda.equals("buscar productos por nombre/sku.....")) {
            actualizarTabla();
            return;
        }
        
        modeloTabla.setRowCount(0);
        
        for (ProductoCaducidad producto : listaProductos) {
            String nombreLote = (producto.getNombreProducto() + " " + producto.getLote()).toLowerCase();
            
            if (nombreLote.contains(textoBusqueda)) {
                modeloTabla.addRow(new Object[]{
                    producto.getNombreProducto() + " (" + producto.getLote() + ")",
                    producto.getProveedor(),
                    producto.getFechaCaducidad().format(formatter),
                    producto.getDiasRestantes(),
                    producto.getCantidad()
                });
            }
        }
    }
    
    private void configurarBuscador() {
        jTextField1.setForeground(Color.GRAY);
        
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().equals("Buscar productos por Nombre/SKU.....")) {
                    jTextField1.setText("");
                    jTextField1.setForeground(Color.BLACK);
                }
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Buscar productos por Nombre/SKU.....");
                    jTextField1.setForeground(Color.GRAY);
                }
            }
        });
        
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarProducto();
            }
        });
    }
    
    private void actualizarMetricas() {
    int vencidos = 0;
    int vencen7Dias = 0;
    int vencen30Dias = 0;
    double valorRiesgo = 0.0;
    
    for (ProductoCaducidad p : listaProductos) {
        long dias = p.getDiasRestantes();
        if (dias < 0) {
            vencidos++;
            valorRiesgo += p.getCantidad() * 5.0;
        } else if (dias <= 7) {
            vencen7Dias++;
        } else if (dias <= 30) {
            vencen30Dias++;
        }
    }
    
    // CORRECCIÓN: Usar jLabel1 en lugar de jLabel2 para el primer label
    jLabel1.setText("<html><center>PRODUCTOS VENCIDOS<br><b style='font-size:18px'>" 
                   + vencidos + " UNDS</b></center></html>");
    jLabel2.setText("<html><center>VENCEN EN 7 DÍAS<br><b style='font-size:18px'>" 
                   + vencen7Dias + " UNDS</b></center></html>");
    jLabel3.setText("<html><center>VENCEN EN 30 DÍAS<br><b style='font-size:18px'>" 
                   + vencen30Dias + " UNDS</b></center></html>");
    jLabel4.setText("<html><center>VALOR EN RIESGO<br><b style='font-size:18px'>$" 
                   + String.format("%.2f", valorRiesgo) + "</b></center></html>");
    
    jPanel6.setBackground(new Color(220, 53, 69));
    jPanel5.setBackground(new Color(255, 193, 7));
    jPanel4.setBackground(new Color(255, 235, 156));
    jPanel7.setBackground(new Color(173, 216, 230));
    
    jLabel1.setForeground(Color.WHITE);
    jLabel2.setForeground(Color.BLACK);
    jLabel3.setForeground(Color.BLACK);
    jLabel4.setForeground(Color.BLACK);
}
    
    private void filtrarProductos() {
    String estadoSeleccionado = (String) Estado.getSelectedItem();
    String proveedorSeleccionado = (String) FiltroProveedor.getSelectedItem();
    
    modeloTabla.setRowCount(0);
    
    // Contadores para actualizar métricas
    int vencidosTotal = 0;
    int vencen7DiasTotal = 0;
    int vencen30DiasTotal = 0;
    double valorRiesgoTotal = 0.0;
    
    for (ProductoCaducidad producto : listaProductos) {
        boolean cumpleEstado = true;
        boolean cumpleProveedor = true;
        
        long dias = producto.getDiasRestantes();
        
        // Actualizar contadores totales (independiente del filtro)
        if (dias < 0) {
            vencidosTotal++;
            valorRiesgoTotal += producto.getCantidad() * 5.0;
        } else if (dias <= 7) {
            vencen7DiasTotal++;
        } else if (dias <= 30) {
            vencen30DiasTotal++;
        }
        
        // Aplicar filtro de estado
        if (!"Todos".equals(estadoSeleccionado)) {
            cumpleEstado = false;
            
            switch (estadoSeleccionado) {
                case "Vencidos":
                    cumpleEstado = dias < 0;
                    break;
                case "Vencen en 7 días":
                    cumpleEstado = dias >= 0 && dias <= 7;
                    break;
                case "Vencen en 30 días":
                    cumpleEstado = dias > 7 && dias <= 30;
                    break;
                case "Normal":
                    cumpleEstado = dias > 30;
                    break;
            }
        }
        
        // Aplicar filtro de proveedor
        if (!"Todos".equals(proveedorSeleccionado)) {
            cumpleProveedor = producto.getProveedor().equals(proveedorSeleccionado);
        }
        
        // Agregar a la tabla si cumple ambos filtros
        if (cumpleEstado && cumpleProveedor) {
            modeloTabla.addRow(new Object[]{
                producto.getNombreProducto() + " (" + producto.getLote() + ")",
                producto.getProveedor(),
                producto.getFechaCaducidad().format(formatter),
                producto.getDiasRestantes(),
                producto.getCantidad()
            });
        }
    }
    
    // ACTUALIZAR LAS MÉTRICAS después de filtrar
    jLabel1.setText("<html><center>PRODUCTOS VENCIDOS<br><b style='font-size:18px'>" 
                   + vencidosTotal + " UNDS</b></center></html>");
    jLabel2.setText("<html><center>VENCEN EN 7 DÍAS<br><b style='font-size:18px'>" 
                   + vencen7DiasTotal + " UNDS</b></center></html>");
    jLabel3.setText("<html><center>VENCEN EN 30 DÍAS<br><b style='font-size:18px'>" 
                   + vencen30DiasTotal + " UNDS</b></center></html>");
    jLabel4.setText("<html><center>VALOR EN RIESGO<br><b style='font-size:18px'>$" 
                   + String.format("%.2f", valorRiesgoTotal) + "</b></center></html>");
}
    
    private void configurarTextField2() {
    jTextField2.setPreferredSize(new Dimension(150, 25));
    jTextField2.setMinimumSize(new Dimension(150, 25));
    jTextField2.setMaximumSize(new Dimension(200, 25));
    jTextField2.setForeground(Color.GRAY);
    
    jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (jTextField2.getText().equals("Nuevo Producto")) {
                jTextField2.setText("");
                jTextField2.setForeground(Color.BLACK);
            }
        }
        
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (jTextField2.getText().trim().isEmpty()) {
                jTextField2.setText("Nuevo Producto");
                jTextField2.setForeground(Color.GRAY);
            }
        }
    });
}
    
    private void inicializarGestionProductos() {
    // Configurar la tabla de productos
    String[] columnasProductos = {"Nombre", "Precio", "Stock", "Código", "Categoría"};
    modeloTablaProductos = new DefaultTableModel(columnasProductos, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    tabla_productos_gestion.setModel(modeloTablaProductos);
    tabla_productos_gestion.setRowHeight(25);
    
    // Configurar ancho de columnas
    tabla_productos_gestion.getColumnModel().getColumn(0).setPreferredWidth(250); // Nombre
    tabla_productos_gestion.getColumnModel().getColumn(1).setPreferredWidth(80);  // Precio
    tabla_productos_gestion.getColumnModel().getColumn(2).setPreferredWidth(60);  // Stock
    tabla_productos_gestion.getColumnModel().getColumn(3).setPreferredWidth(100); // Código
    tabla_productos_gestion.getColumnModel().getColumn(4).setPreferredWidth(120); // Categoría
    
    // Configurar ComboBox de categorías
    cb_categoria_gestion.removeAllItems();
    cb_categoria_gestion.addItem("Golosinas");
    cb_categoria_gestion.addItem("Bebidas");
    cb_categoria_gestion.addItem("Snacks Salados");
    cb_categoria_gestion.addItem("Chocolates");
    cb_categoria_gestion.addItem("Lácteos");
    cb_categoria_gestion.addItem("Panadería");
    
    // Limpiar campos iniciales
    tf_nombre_gestion.setText("");
    tf_precio_gestion.setText("");
    tf_stock_gestion.setText("");
    tf_codigoProducto_gestion.setText("");
    tf_busqueda_gestion.setText("");
    
    // Listener para selección de fila en la tabla
    tabla_productos_gestion.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            cargarProductoSeleccionadoGestion();
        }
    });
    
    // Cargar productos de ejemplo
    cargarProductosEjemploGestion();
}

private void cargarProductosEjemploGestion() {
    modeloTablaProductos.setRowCount(0);
    
    modeloTablaProductos.addRow(new Object[]{"Coca Cola 500ml", 3.50, 50, "BEB-001", "Bebidas"});
    modeloTablaProductos.addRow(new Object[]{"Papas Lays Clásicas", 2.00, 80, "SNK-001", "Snacks Salados"});
    modeloTablaProductos.addRow(new Object[]{"Chocolate Sublime", 1.50, 120, "CHO-001", "Chocolates"});
    modeloTablaProductos.addRow(new Object[]{"Galletas Oreo", 4.00, 60, "GOL-001", "Golosinas"});
    modeloTablaProductos.addRow(new Object[]{"Leche Gloria", 5.50, 40, "LAC-001", "Lácteos"});
}

private void cargarProductoSeleccionadoGestion() {
    int fila = tabla_productos_gestion.getSelectedRow();
    if (fila != -1) {
        tf_nombre_gestion.setText(tabla_productos_gestion.getValueAt(fila, 0).toString());
        tf_precio_gestion.setText(tabla_productos_gestion.getValueAt(fila, 1).toString());
        tf_stock_gestion.setText(tabla_productos_gestion.getValueAt(fila, 2).toString());
        tf_codigoProducto_gestion.setText(tabla_productos_gestion.getValueAt(fila, 3).toString());
        cb_categoria_gestion.setSelectedItem(tabla_productos_gestion.getValueAt(fila, 4).toString());
    }
}

private void limpiarCamposProductosGestion() {
    tf_nombre_gestion.setText("");
    tf_precio_gestion.setText("");
    tf_stock_gestion.setText("");
    tf_codigoProducto_gestion.setText("");
    cb_categoria_gestion.setSelectedIndex(0);
    tabla_productos_gestion.clearSelection();
}

private boolean validarCamposProductosGestion() {
    if (tf_nombre_gestion.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el nombre del producto", 
            "Campo vacío", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    try {
        double precio = Double.parseDouble(tf_precio_gestion.getText().trim());
        if (precio <= 0) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", 
                "Precio inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Ingrese un precio válido", 
            "Error de formato", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    try {
        int stock = Integer.parseInt(tf_stock_gestion.getText().trim());
        if (stock < 0) {
            JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", 
                "Stock inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Ingrese un stock válido", 
            "Error de formato", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    if (tf_codigoProducto_gestion.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese un código para el producto", 
            "Código vacío", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    return true;
}

private String generarCodigoPorCategoriaGestion(String categoria) {
    String prefijo = "";
    switch (categoria) {
        case "Golosinas": prefijo = "GOL"; break;
        case "Bebidas": prefijo = "BEB"; break;
        case "Snacks Salados": prefijo = "SNK"; break;
        case "Chocolates": prefijo = "CHO"; break;
        case "Lácteos": prefijo = "LAC"; break;
        case "Panadería": prefijo = "PAN"; break;
        default: prefijo = "PRO"; break;
    }
    
    int numero = (int)(Math.random() * 999) + 1;
    return prefijo + "-" + String.format("%03d", numero);
}

private void buscarProductoGestion() {
    String textoBusqueda = tf_busqueda_gestion.getText().trim().toLowerCase();
    
    if (textoBusqueda.isEmpty()) {
        cargarProductosEjemploGestion();
        return;
    }
    
    modeloTablaProductos.setRowCount(0);
    
    // Aquí deberías buscar en tu lista real de productos
    // Por ahora, filtramos los datos de ejemplo
    Object[][] productosEjemplo = {
        {"Coca Cola 500ml", 3.50, 50, "BEB-001", "Bebidas"},
        {"Papas Lays Clásicas", 2.00, 80, "SNK-001", "Snacks Salados"},
        {"Chocolate Sublime", 1.50, 120, "CHO-001", "Chocolates"},
        {"Galletas Oreo", 4.00, 60, "GOL-001", "Golosinas"},
        {"Leche Gloria", 5.50, 40, "LAC-001", "Lácteos"}
    };
    
    for (Object[] producto : productosEjemplo) {
        String nombre = producto[0].toString().toLowerCase();
        String codigo = producto[3].toString().toLowerCase();
        
        if (nombre.contains(textoBusqueda) || codigo.contains(textoBusqueda)) {
            modeloTablaProductos.addRow(producto);
        }
    }
}
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("VENTAS", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producto (Lote/SKU)", "Provvedor", "Fecha de Caducidad", "Días Restantes", "Cantidad"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(229, 229, 229)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DEVOLUCIONES", jPanel2);

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

        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
