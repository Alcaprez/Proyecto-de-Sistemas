package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.Categoria;
import edu.UPAO.proyecto.DAO.CategoriaDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DialogoNuevaCategoria extends javax.swing.JDialog {

    private JTextField txtNombre;
    // Eliminamos txtDescripcion porque no existe en la BD
    private boolean guardadoExitoso = false; 

    public DialogoNuevaCategoria(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Nueva Categoría");
        setSize(400, 200); // Reducimos altura ya que quitamos descripción
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
        panelPrincipal.add(Box.createVerticalStrut(20)); // Espacio extra
        panelPrincipal.add(panelBotones);

        add(panelPrincipal);
    }

    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- CORRECCIÓN AQUÍ ---
        // Usamos el constructor nuevo que solo pide (id, nombre)
        Categoria nuevaCategoria = new Categoria(0, nombre);
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