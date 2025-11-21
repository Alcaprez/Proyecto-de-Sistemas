package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Promocion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromocionDAO {
    private Connection conexion;

    public PromocionDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            // Silencio para no molestar con ventanas
            System.err.println("Error conexión PromocionDAO: " + e.getMessage());
        }
    }

    public List<Promocion> obtenerPromocionesActivas() {
        List<Promocion> lista = new ArrayList<>();
        // ✅ Consulta corregida: NO incluye 'vendidos' ni tablas extrañas
        String sql = "SELECT * FROM promocion WHERE estado = 'ACTIVO' AND CURDATE() BETWEEN fecha_inicio AND fecha_fin";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Promocion p = new Promocion();
                p.setIdPromocion(rs.getInt("id_promocion"));
                p.setNombre(rs.getString("nombre"));
                p.setTipo(rs.getString("tipo"));
                p.setValor(rs.getDouble("valor"));
                // Agrega más campos solo si existen en tu tabla 'promocion'
                lista.add(p);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error leyendo promociones antiguas: " + e.getMessage());
        }
        return lista;
    }
}