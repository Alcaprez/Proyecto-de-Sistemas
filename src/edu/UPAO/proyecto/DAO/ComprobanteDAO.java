package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.util.GeneradorPDF;
import java.sql.*;

public class ComprobanteDAO {
    private Connection conexion;

    public ComprobanteDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("✅ ComprobanteDAO conectado");
        } catch (Exception e) {
            System.err.println("❌ Error conectando ComprobanteDAO: " + e.getMessage());
        }
    }

    // ✅ OBTENER PRÓXIMO NÚMERO
    public String obtenerProximoNumero(String tipoComprobante) {
        String sql = "SELECT numero_actual FROM serie_comprobante WHERE tipo_comprobante = ? AND estado = 'ACTIVO'";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tipoComprobante);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int numeroActual = rs.getInt("numero_actual");
                String numeroFormateado = String.format("%08d", numeroActual);
                System.out.println("🔢 Próximo número: " + tipoComprobante + " - " + numeroFormateado);
                return numeroFormateado;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo próximo número: " + e.getMessage());
        }
        return "00000001"; // Valor por defecto
    }

    // ✅ ACTUALIZAR NÚMERO
    public void actualizarNumeroActual(String tipoComprobante) {
        String sql = "UPDATE serie_comprobante SET numero_actual = numero_actual + 1 WHERE tipo_comprobante = ? AND estado = 'ACTIVO'";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tipoComprobante);
            stmt.executeUpdate();
            System.out.println("✅ Número actualizado para: " + tipoComprobante);
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando número: " + e.getMessage());
        }
    }

    // ✅ REGISTRAR COMPROBANTE (AHORA RECIBE VENTA)
    public boolean registrarComprobante(int idVenta, String tipo, Venta venta) {
        String serie = tipo.equals("BOLETA") ? "B001" : "F001";
        String numero = obtenerProximoNumero(tipo);
        
        // Generar PDF
        String archivoPdf = GeneradorPDF.generarBoleta(venta, numero);
        
        if (archivoPdf == null) {
            System.err.println("❌ No se pudo generar el PDF");
            return false;
        }
        
        String sql = "INSERT INTO comprobante (id_venta, tipo, serie, numero, archivo_pdf) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            stmt.setString(2, tipo);
            stmt.setString(3, serie);
            stmt.setString(4, numero);
            stmt.setString(5, archivoPdf);
            
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                // Actualizar el número solo si se registró correctamente
                actualizarNumeroActual(tipo);
                System.out.println("✅ Comprobante registrado: " + tipo + " " + serie + "-" + numero);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error registrando comprobante: " + e.getMessage());
        }
        return false;
    }

    // ✅ OBTENER COMPROBANTE POR ID VENTA
    public String obtenerArchivoComprobante(int idVenta) {
        String sql = "SELECT archivo_pdf FROM comprobante WHERE id_venta = ? AND estado = 'ACTIVO'";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("archivo_pdf");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo comprobante: " + e.getMessage());
        }
        return null;
    }
}