
package edu.UPAO.proyecto.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboradBienvenida extends javax.swing.JPanel {

    private Image backgroundImage;
    
    public DashboradBienvenida() {
        initComponents();
        // 1. Configuración del Panel
        setLayout(new BorderLayout());
        setOpaque(false); // Importante para que se vea el fondo que pintaremos

        // 2. Cargar la Imagen
        try {
            // IMPORTANTE: Asegúrate de que la ruta y el nombre sean correctos.
            // La ruta empieza con '/' y se refiere a la carpeta 'src' de tu proyecto.
            ImageIcon icon = new ImageIcon(getClass().getResource("/frame/imagenes/fondo_bienvenida.png"));
            backgroundImage = icon.getImage();
        } catch (Exception e) {
            System.err.println("Error: No se pudo cargar la imagen del dashboard. Verifica la ruta.");
            // Color de respaldo por si falla la imagen
            setBackground(new Color(245, 245, 245)); 
            setOpaque(true);
        }

        // 3. Crear el Título de Bienvenida
        JLabel lblTitulo = new JLabel("¡BIENVENIDO!", SwingConstants.CENTER);
        // Usamos una fuente grande y elegante, y el color verde oscuro de tu paleta
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 48));
        lblTitulo.setForeground(new Color(13, 59, 40)); 
        
        // Agregamos un margen superior para que no quede pegado al techo
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 0, 0, 0));

        // Agregamos el título en la parte superior (Norte) del panel
        add(lblTitulo, BorderLayout.NORTH);
    }

    
    @SuppressWarnings("unchecked")
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            // Activar suavizado para que la imagen se vea bien al estirarse
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // Dibujar la imagen estirada para cubrir todo el panel
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
