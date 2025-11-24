package edu.UPAO.proyecto.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CategoriaDAO {
    // Usamos las mismas credenciales que tienes en tu proyecto
    private final String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    private final String usuario = "root";
    private final String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

    // 1. INSERTAR NUEVA CATEGORÍA
    public boolean insertar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
        
        try (Connection con = DriverManager.getConnection(url, usuario, password);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar categoría: " + e.getMessage());
            return false;
        }
    }

    // 2. LISTAR CATEGORÍAS (Para recargar la vista después de guardar)
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY nombre ASC";
        
        try (Connection con = DriverManager.getConnection(url, usuario, password);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // Asegúrate de que el constructor de Categoria coincida
                lista.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listando categorías: " + e.getMessage());
        }
        return lista;
    }
    
    // Aquí podrías agregar métodos eliminar() y actualizar() en el futuro
}