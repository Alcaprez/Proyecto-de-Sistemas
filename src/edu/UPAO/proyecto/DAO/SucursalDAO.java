// SucursalDAO.java
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {
    private Connection conexion;

    public SucursalDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conectando SucursalDAO: " + e.getMessage());
        }
    }

    public List<String> obtenerSucursalesActivas() {
        List<String> sucursales = new ArrayList<>();
        String sql = "SELECT nombre_sucursal FROM sucursal WHERE estado = 'ACTIVO'";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                sucursales.add(rs.getString("nombre_sucursal"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener sucursales: " + e.getMessage());
            e.printStackTrace();
        }
        return sucursales;
    }
}