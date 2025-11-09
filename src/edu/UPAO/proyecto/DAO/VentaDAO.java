package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import BaseDatos.Conexion;

// ✅ IMPORTACIONES NECESARIAS
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private Connection conexion;
    private ProductoDAO productoDAO;
    
    public VentaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            this.productoDAO = new ProductoDAO();
        } catch (Exception e) {
            System.err.println("❌ Error al conectar VentaDAO: " + e.getMessage());
        }
    }
    
    // ✅ MÉTODO REGISTRAR VENTA COMPLETO CON TRANSACCIÓN
    public boolean registrarVenta(Venta venta) {
        String sqlVenta = "INSERT INTO venta (fecha_hora, total, id_empleado, id_metodo_pago) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, id_venta, id_producto) VALUES (?, ?, ?, ?, ?)";
        
        try {
            conexion.setAutoCommit(false); // Iniciar transacción
            
            // 1. INSERTAR VENTA PRINCIPAL
            PreparedStatement stmtVenta = conexion.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            stmtVenta.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            stmtVenta.setDouble(2, venta.calcularTotal());
            stmtVenta.setInt(3, venta.getCajeroId());
            stmtVenta.setInt(4, 1); // método pago temporal (efectivo)
            
            stmtVenta.executeUpdate();
            
            // 2. OBTENER ID GENERADO
            int idVenta = 0;
            ResultSet generatedKeys = stmtVenta.getGeneratedKeys();
            if (generatedKeys.next()) {
                idVenta = generatedKeys.getInt(1);
            }
            
            // 3. INSERTAR DETALLES Y ACTUALIZAR STOCK
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                // Insertar detalle
                PreparedStatement stmtDetalle = conexion.prepareStatement(sqlDetalle);
                stmtDetalle.setInt(1, detalle.getCantidad());
                stmtDetalle.setDouble(2, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(3, detalle.getSubtotal());
                stmtDetalle.setInt(4, idVenta);
                stmtDetalle.setInt(5, obtenerIdProducto(detalle.getProducto().getCodigo()));
                stmtDetalle.executeUpdate();
                
                // Actualizar stock
                productoDAO.actualizarStock(obtenerIdProducto(detalle.getProducto().getCodigo()), detalle.getCantidad());
            }
            
            conexion.commit(); // ✅ CONFIRMAR TRANSACCIÓN
            return true;
            
        } catch (SQLException e) {
            try {
                conexion.rollback(); // ❌ REVERTIR EN ERROR
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error registrando venta: " + e.getMessage());
            return false;
        } finally {
            try {
                conexion.setAutoCommit(true); // Restaurar auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // ✅ MÉTODO AUXILIAR PARA OBTENER ID PRODUCTO
    private int obtenerIdProducto(String codigo) {
        if (codigo.startsWith("P")) {
            try {
                return Integer.parseInt(codigo.substring(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
    
    // ✅ MANTENER MÉTODOS EXISTENTES PARA COMPATIBILIDAD
    public List<Venta> listar() {
        return new ArrayList<>(); // Temporal
    }
}