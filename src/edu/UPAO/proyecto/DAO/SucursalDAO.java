// SucursalDAO.java - CORREGIDO
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
            System.out.println("Conectado");
        } catch (Exception e) {
            System.err.println("Error conectando DAO: " + e.getMessage());
        }
    }
        

    public List<String> obtenerSucursalesActivas() {
        List<String> sucursales = new ArrayList<>();
        // ✅ CORREGIDO: usar 'nombre_sucursal' que es el nombre real en tu BD
        String sql = "SELECT nombre_sucursal FROM sucursal WHERE estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sucursales.add(rs.getString("nombre_sucursal"));
            }
            System.out.println("✅ Sucursales encontradas: " + sucursales.size());
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener sucursales: " + e.getMessage());
            e.printStackTrace();
        }
        return sucursales;
    }

    public int obtenerIdSucursalPorNombre(String nombreSucursal) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre_sucursal = ? AND estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombreSucursal);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_sucursal");
            } else {
                System.err.println("❌ Sucursal no encontrada: " + nombreSucursal);
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de sucursal: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // ✅ NUEVO MÉTODO: Obtener todas las sucursales con ID
    public List<Object[]> obtenerSucursalesConId() {
        List<Object[]> sucursales = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre, direccion FROM sucursal WHERE estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] sucursal = {
                    rs.getInt("id_sucursal"),
                    rs.getString("nombre"),
                    rs.getString("direccion")
                };
                sucursales.add(sucursal);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener sucursales con ID: " + e.getMessage());
            e.printStackTrace();
        }
        return sucursales;
    }
}
