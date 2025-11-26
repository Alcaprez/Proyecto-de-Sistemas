package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap; // Importante para mantener orden
import java.util.List;
import java.util.Map;

public class DevolucionCompraDAO {

    // 1. Listar Devoluciones para la Tabla
    public List<Object[]> listarDevoluciones(String proveedor) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.id_compra, c.fecha_hora, s.nombre_sucursal, p.Razon_Social, "
                + "COUNT(dc.id_producto) as items, SUM(dc.cantidad) as unidades, c.total, c.estado "
                + "FROM compra c "
                + "JOIN sucursal s ON c.id_sucursal = s.id_sucursal "
                + "JOIN proveedor p ON c.id_proveedor = p.id_proveedor "
                + "LEFT JOIN detalle_compra dc ON c.id_compra = dc.id_compra "
                + "WHERE (c.estado = 'DEVUELTA' OR c.estado = 'ANULADA') ";

        if (proveedor != null && !proveedor.equals("TODOS")) {
            sql += " AND p.Razon_Social = ? ";
        }

        sql += " GROUP BY c.id_compra ORDER BY c.fecha_hora DESC";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            if (proveedor != null && !proveedor.equals("TODOS")) {
                ps.setString(1, proveedor);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_compra"),
                    rs.getTimestamp("fecha_hora"),
                    rs.getString("nombre_sucursal"),
                    rs.getString("Razon_Social"),
                    rs.getInt("items") + " Prods",
                    rs.getInt("unidades"),
                    "S/ " + String.format("%.2f", rs.getDouble("total")),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listar devoluciones: " + e.getMessage());
        }
        return lista;
    }

    // 2. Datos para Gráfico Circular
    public Map<String, Integer> obtenerDistribucionPorSucursal() {
        Map<String, Integer> datos = new HashMap<>();
        // Aceptamos DEVUELTA o ANULADA
        String sql = "SELECT s.nombre_sucursal, COUNT(*) as total "
                + "FROM compra c "
                + "JOIN sucursal s ON c.id_sucursal = s.id_sucursal "
                + "WHERE c.estado IN ('DEVUELTA', 'ANULADA') "
                + "GROUP BY s.nombre_sucursal";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                datos.put(rs.getString(1), rs.getInt(2));
            }

            // MENSAJE DE CONTROL EN CONSOLA
            System.out.println("📊 Gráfico Circular: Se encontraron " + datos.size() + " sucursales con devoluciones.");
            if (datos.isEmpty()) {
                System.out.println("⚠️ AVISO: No hay compras con estado 'DEVUELTA' o 'ANULADA' en la BD.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    // 3. Datos para Gráfico de Barras
    public Map<String, Double> obtenerMontoPorProveedor() {
        Map<String, Double> datos = new LinkedHashMap<>();

        String sql = "SELECT p.Razon_Social, SUM(c.total) as monto "
                + "FROM compra c "
                + "JOIN proveedor p ON c.id_proveedor = p.id_proveedor "
                + "WHERE c.estado IN ('DEVUELTA', 'ANULADA') "
                + "GROUP BY p.Razon_Social "
                + "ORDER BY monto DESC LIMIT 5";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                datos.put(rs.getString(1), rs.getDouble(2));
            }
            System.out.println("📊 Gráfico Barras: Se encontraron " + datos.size() + " proveedores con devoluciones.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }
}
