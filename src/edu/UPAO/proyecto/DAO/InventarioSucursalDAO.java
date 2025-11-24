// edu.UPAO.proyecto.DAO.InventarioSucursalDAO.java
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.InventarioSucursal;
import java.sql.*;

public class InventarioSucursalDAO {
    private Connection conexion;

    public InventarioSucursalDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conectando InventarioSucursalDAO: " + e.getMessage());
        }
    }

    // OBTENER STOCK DE UN PRODUCTO EN UNA SUCURSAL
    public int obtenerStock(int idProducto, int idSucursal) {
        String sql = "SELECT stock_actual FROM inventario_sucursal WHERE id_producto = ? AND id_sucursal = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setInt(2, idSucursal);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("stock_actual");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo stock: " + e.getMessage());
        }
        return 0; // Si no existe registro, stock es 0
    }

    // ✅ ACTUALIZAR STOCK EN SUCURSAL
    public boolean actualizarStock(int idProducto, int idSucursal, int nuevoStock) {
        String sql = "UPDATE inventario_sucursal SET stock_actual = ? WHERE id_producto = ? AND id_sucursal = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, nuevoStock);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, idSucursal);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando stock en sucursal: " + e.getMessage());
            return false;
        }
    }

    // ✅ CREAR REGISTRO DE INVENTARIO SI NO EXISTE
    public boolean crearRegistroInventario(int idProducto, int idSucursal, int stockInicial) {
        String sql = "INSERT INTO inventario_sucursal (id_producto, id_sucursal, stock_actual) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, stockInicial);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error creando registro inventario: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    // Si idSucursal es 0, busca en TODAS las sucursales (Para Admin)
    // Si idSucursal > 0, busca solo en esa sucursal (Para Cajero)
    public java.util.List<String> obtenerAlertasBajoStock(int idSucursal) {
        java.util.List<String> alertas = new java.util.ArrayList<>();
        
        String sql = "SELECT p.nombre, s.nombre_sucursal, i.stock_actual, p.stock_minimo " +
                     "FROM inventario_sucursal i " +
                     "JOIN producto p ON i.id_producto = p.id_producto " +
                     "JOIN sucursal s ON i.id_sucursal = s.id_sucursal " +
                     "WHERE i.stock_actual <= p.stock_minimo " +
                     "AND p.estado = 'ACTIVO' " + // Solo productos activos
                     (idSucursal > 0 ? "AND i.id_sucursal = ? " : "") + // Filtro opcional por sucursal
                     "ORDER BY i.stock_actual ASC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (idSucursal > 0) {
                stmt.setInt(1, idSucursal);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String nombreProd = rs.getString("nombre");
                String sucursal = rs.getString("nombre_sucursal");
                int actual = rs.getInt("stock_actual");
                int minimo = rs.getInt("stock_minimo");
                
                // Formato del mensaje: "Coca Cola (Laredo) - Actual: 2 / Mín: 10"
                String mensaje = String.format("⚠️ %s (%s)\n   📦 Stock: %d  |  📉 Mínimo: %d", 
                        nombreProd, sucursal, actual, minimo);
                alertas.add(mensaje);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error buscando alertas de stock: " + e.getMessage());
        }
        return alertas;
    }
    
}

