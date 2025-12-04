
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CategoriaDAO {

// Método para insertar
    public boolean insertar(Categoria categoria) {
        // CORREGIDO: Solo insertamos 'nombre'
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection con = new Conexion().establecerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            
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
        // CORREGIDO: Eliminada la columna 'descripcion' del SELECT
        // Aseguramos usar 'id_categoria' que es el nombre real en tu BD
        String sql = "SELECT id_categoria, nombre FROM categoria ORDER BY nombre ASC";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Usamos el constructor nuevo que solo acepta (id, nombre)
                lista.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando categorías: " + e.getMessage());
        }
        return lista;
    }
}
