package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.Categoria;
import edu.UPAO.proyecto.DAO.CategoriaDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DialogoNuevaCategoria extends javax.swing.JDialog {

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private boolean guardadoExitoso = false; // Para saber si recargar la pantalla anterior

    public DialogoNuevaCategoria(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Nueva Categoría");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(Color.WHITE);

        // --- Campo Nombre ---
        JLabel lblNombre = new JLabel("Nombre de la Categoría:");
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtNombre = new JTextField();
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // --- Campo Descripción ---
        JLabel lblDesc = new JLabel("Descripción (Opcional):");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        JButton btnGuardar = new JButton("Guardar Categoría");
        btnGuardar.setBackground(new Color(0, 102, 204)); // Azul
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarCategoria());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        // --- Agregar todo al panel ---
        panelPrincipal.add(lblNombre);
        panelPrincipal.add(Box.createVerticalStrut(5));
        panelPrincipal.add(txtNombre);
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(lblDesc);
        panelPrincipal.add(Box.createVerticalStrut(5));
        panelPrincipal.add(scrollDesc);
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(panelBotones);

        add(panelPrincipal);
    }

    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Categoria nuevaCategoria = new Categoria(0, nombre, descripcion);
        CategoriaDAO dao = new CategoriaDAO();

        if (dao.insertar(nuevaCategoria)) {
            JOptionPane.showMessageDialog(this, "¡Categoría guardada exitosamente!");
            guardadoExitoso = true;
            dispose(); // Cerrar ventana
        }
    }
    
    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}