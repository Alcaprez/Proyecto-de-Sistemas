// SucursalDAO.java - CORREGIDO
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.modelo.Sucursal;
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

// 1. Obtener el presupuesto actual de la sucursal
    public double obtenerPresupuesto(int idSucursal) {
        String sql = "SELECT presupuesto FROM sucursal WHERE id_sucursal = ?";
        try (java.sql.Connection cn = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("presupuesto");
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo presupuesto: " + e.getMessage());
        }
        return 0.0;
    }

// 2. Actualizar el presupuesto (sumar o restar dinero)
    public boolean actualizarPresupuesto(int idSucursal, double monto, boolean esIngreso) {
        // Si esIngreso es true, SUMA. Si es false, RESTA.
        String operacion = esIngreso ? "+" : "-";
        String sql = "UPDATE sucursal SET presupuesto = presupuesto " + operacion + " ? WHERE id_sucursal = ?";

        try (java.sql.Connection cn = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDouble(1, monto);
            ps.setInt(2, idSucursal);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error actualizando presupuesto: " + e.getMessage());
            return false;
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

    // Método para obtener el ID de una sucursal dado su nombre
    public int obtenerIdPorNombre(String nombreSucursal) {
        int id = -1;
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre_sucursal = ?";

        try (java.sql.Connection cn = new BaseDatos.Conexion().establecerConexion(); java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreSucursal);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id_sucursal");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar ID de sucursal por nombre: " + e.getMessage());
        }
        return id;
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

    public List<Sucursal> listar() {
        List<Sucursal> lista = new ArrayList<>();
        // Usamos el nombre exacto de la columna en tu base de datos: nombre_sucursal
        String sql = "SELECT id_sucursal, nombre_sucursal, direccion, estado FROM sucursal WHERE estado = 'ACTIVO'";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal s = new Sucursal();
                s.setId_sucursal(rs.getInt("id_sucursal"));

                // Aquí mapeamos la columna 'nombre_sucursal' al atributo 'nombre' de tu clase
                s.setNombre(rs.getString("nombre_sucursal"));

                s.setDireccion(rs.getString("direccion"));
                s.setEstado(rs.getString("estado"));

                lista.add(s);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar sucursales: " + e.getMessage());
        }
        return lista;
    }
}
