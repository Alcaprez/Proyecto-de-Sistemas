package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraGlobalDAO {

    // LISTAR COMPRAS CON FILTROS (VERSIÓN SEGURA: LEFT JOIN)
    public List<Object[]> listarComprasGlobales(String nombreSucursal) {
        List<Object[]> lista = new ArrayList<>();
        
        // CAMBIO IMPORTANTE: Usamos LEFT JOIN para que NO se oculten compras si falta el empleado o proveedor
        String sql = "SELECT c.id_compra, c.fecha_hora, " +
                     "COALESCE(s.nombre_sucursal, 'Sin Sucursal') as nombre_sucursal, " +
                     "COALESCE(p.Razon_Social, 'Proveedor Eliminado') as Razon_Social, " +
                     "COALESCE(e.dni, 'Sistema') as dni, " + // Si no encuentra empleado, pone 'Sistema'
                     "c.total, c.estado " +
                     "FROM compra c " +
                     "LEFT JOIN sucursal s ON c.id_sucursal = s.id_sucursal " +
                     "LEFT JOIN proveedor p ON c.id_proveedor = p.id_proveedor " +
                     "LEFT JOIN empleado emp ON c.id_empleado = emp.id_empleado " +
                     "LEFT JOIN persona e ON emp.dni = e.dni " + 
                     "WHERE (? = 'TODAS' OR s.nombre_sucursal = ?) " +
                     "ORDER BY c.fecha_hora DESC";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String filtro = (nombreSucursal == null || nombreSucursal.equals("TODAS LAS TIENDAS")) ? "TODAS" : nombreSucursal;
            
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_compra"),
                    rs.getTimestamp("fecha_hora"),
                    rs.getString("nombre_sucursal"),
                    rs.getString("Razon_Social"),
                    rs.getString("dni"), 
                    "S/ " + String.format("%.2f", rs.getDouble("total")),
                    rs.getString("estado")
                });
            }
        } catch (Exception e) {
            System.err.println("Error listando compras globales: " + e.getMessage());
            e.printStackTrace(); // Mira la consola si sale error aquí
        }
        return lista;
    }
}