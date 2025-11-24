package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DevolucionDAO {

    // 1. Busca los productos de una venta pasada
    public List<Object[]> buscarDetallesVenta(int idVenta) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT dv.id_producto, p.nombre, dv.cantidad, dv.precio_unitario, dv.subtotal " +
                     "FROM detalle_venta dv " +
                     "INNER JOIN producto p ON dv.id_producto = p.id_producto " +
                     "WHERE dv.id_venta = ?";
        
        try (Connection cn = new Conexion().establecerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario"),
                    rs.getDouble("subtotal")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }
        return lista;
    }

    // 2. Realiza la devolución (Mueve dinero y stock)
    public boolean procesarDevolucion(int idVenta, int idProducto, int cantidad, double montoDevolver, 
                                      String motivo, boolean regresaAStock, int idCaja, int idSucursal, String idEmpleado) {
        
        // Validaciones de seguridad antes de tocar la BD
        if (idCaja <= 0 || idSucursal <= 0) {
            JOptionPane.showMessageDialog(null, "Error crítico: No se identificó la Caja o Sucursal abierta.");
            return false;
        }

        Connection cn = new Conexion().establecerConexion();
        try {
            cn.setAutoCommit(false); // Inicio de Transacción (Todo o Nada)

            // A. Registrar en tabla devolucion
            String sqlDev = "INSERT INTO devolucion (id_venta, motivo, metodo_reembolso, total_devuelto, id_empleado, fecha_hora) VALUES (?, ?, 'EFECTIVO', ?, ?, NOW())";
            PreparedStatement psDev = cn.prepareStatement(sqlDev, Statement.RETURN_GENERATED_KEYS);
            psDev.setInt(1, idVenta);
            psDev.setString(2, motivo);
            psDev.setDouble(3, montoDevolver);
            psDev.setString(4, idEmpleado);
            psDev.executeUpdate();
            
            int idDevolucion = 0;
            ResultSet rsKeys = psDev.getGeneratedKeys();
            if (rsKeys.next()) idDevolucion = rsKeys.getInt(1);

            // B. Registrar detalle
            String sqlDet = "INSERT INTO detalle_devolucion (id_devolucion, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            double precioUnit = montoDevolver / cantidad;
            PreparedStatement psDet = cn.prepareStatement(sqlDet);
            psDet.setInt(1, idDevolucion);
            psDet.setInt(2, idProducto);
            psDet.setInt(3, cantidad);
            psDet.setDouble(4, precioUnit);
            psDet.setDouble(5, montoDevolver);
            psDet.executeUpdate();

            // C. Restar dinero de la CAJA (Salida de dinero)
            String sqlCaja = "UPDATE caja SET saldo_final = saldo_final - ? WHERE id_caja = ?";
            PreparedStatement psCaja = cn.prepareStatement(sqlCaja);
            psCaja.setDouble(1, montoDevolver);
            psCaja.setInt(2, idCaja);
            psCaja.executeUpdate();

            // D. Registrar Movimiento de Caja
            String sqlMovCaja = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_sucursal, estado) VALUES ('SALIDA', ?, NOW(), ?, ?, ?, 'ACTIVO')";
            PreparedStatement psMovCaja = cn.prepareStatement(sqlMovCaja);
            psMovCaja.setDouble(1, montoDevolver);
            psMovCaja.setString(2, "DEVOLUCION VENTA #" + idVenta);
            psMovCaja.setInt(3, idCaja);
            psMovCaja.setInt(4, idSucursal);
            psMovCaja.executeUpdate();

            // E. Devolver Stock (Solo si el producto está en buen estado)
            if (regresaAStock) {
                String sqlStock = "UPDATE inventario_sucursal SET stock_actual = stock_actual + ? WHERE id_producto = ? AND id_sucursal = ?";
                PreparedStatement psStock = cn.prepareStatement(sqlStock);
                psStock.setInt(1, cantidad);
                psStock.setInt(2, idProducto);
                psStock.setInt(3, idSucursal);
                psStock.executeUpdate();
                
                // Registrar movimiento de inventario
                String sqlMovInv = "INSERT INTO movimiento_inventario (tipo, cantidad, stock_anterior, stock_nuevo, id_producto, id_sucursal, fecha_hora) VALUES ('ENTRADA POR DEVOLUCION', ?, 0, 0, ?, ?, NOW())";
                PreparedStatement psMovInv = cn.prepareStatement(sqlMovInv);
                psMovInv.setInt(1, cantidad);
                psMovInv.setInt(2, idProducto);
                psMovInv.setInt(3, idSucursal);
                psMovInv.executeUpdate();
            }

            cn.commit(); // Confirmar cambios
            return true;

        } catch (SQLException e) {
            try { cn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { cn.close(); } catch (SQLException ex) {}
        }
    }
}