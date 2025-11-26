package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion; // ✅ Importante: Tu clase de conexión
import java.sql.Connection; // ✅ CORREGIDO: java.sql, NO com.sun.jdi...
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CategoriaDAO {

    // Método para insertar
    public boolean insertar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";

        // ✅ El try-with-resources ahora funcionará porque Connection es el tipo correcto
        try (Connection con = new Conexion().establecerConexion(); 
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

    // Método para listar
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY nombre ASC";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando categorías: " + e.getMessage());
        }
        return lista;
    }
}