package edu.UPAO.proyecto.util;

import BaseDatos.Conexion;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorPDFsMasivoSimple {
    
    private static final String CARPETA_COMPROBANTES = "comprobantes/";
    private static final Font FUENTE_NORMAL = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font FUENTE_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font FUENTE_PEQUEÑA = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL); // ✅ NUEVA FUENTE
    
    public static void main(String[] args) {
        System.out.println("🚀 INICIANDO GENERACIÓN MASIVA DE PDFs...");
        
        // Crear carpeta si no existe
        File carpeta = new File(CARPETA_COMPROBANTES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        
        try {
            Connection conexion = new Conexion().establecerConexion();
            
            // ✅ OBTENER TODAS LAS VENTAS
            String sqlVentas = "SELECT id_venta, fecha_hora, total, id_cliente, id_empleado FROM venta ORDER BY id_venta";
            PreparedStatement stmtVentas = conexion.prepareStatement(sqlVentas);
            ResultSet rsVentas = stmtVentas.executeQuery();
            
            int contador = 0;
            
            while (rsVentas.next()) {
                int idVenta = rsVentas.getInt("id_venta");
                Date fechaHora = rsVentas.getTimestamp("fecha_hora");
                double total = rsVentas.getDouble("total");
                String idCliente = rsVentas.getString("id_cliente");
                String idEmpleado = rsVentas.getString("id_empleado");
                
                // ✅ CALCULAR SUBTOTAL E IGV
                double subtotal = total / 1.18;
                double igv = subtotal * 0.18;
                
                // ✅ GENERAR PDF
                String nombreArchivo = generarPDF(idVenta, fechaHora, total, subtotal, igv, idCliente, idEmpleado);
                
                if (nombreArchivo != null) {
                    contador++;
                    System.out.println("✅ PDF " + contador + ": Venta " + idVenta + " -> " + nombreArchivo);
                }
            }
            
            conexion.close();
            
            System.out.println("\n🎉 GENERACIÓN COMPLETADA!");
            System.out.println("📊 Total de PDFs generados: " + contador);
            System.out.println("📁 Carpeta: " + new File(CARPETA_COMPROBANTES).getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generarPDF(int idVenta, Date fechaHora, double total, double subtotal, double igv, 
                                   String idCliente, String idEmpleado) {
        String serie = "B001";
        String numero = String.format("%08d", idVenta); // Usar ID de venta como número
        String nombreArchivo = CARPETA_COMPROBANTES + "VENTA_" + idVenta + "_" + serie + "-" + numero + ".pdf";
        
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();
            
            // TÍTULO
            Paragraph titulo = new Paragraph("BOLETA DE VENTA", FUENTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            // NÚMERO
            Paragraph numeroParrafo = new Paragraph("N°: " + serie + "-" + numero, FUENTE_NEGRITA);
            numeroParrafo.setAlignment(Element.ALIGN_CENTER);
            document.add(numeroParrafo);
            
            document.add(new Paragraph(" "));
            
            // INFORMACIÓN DE LA EMPRESA
            document.add(new Paragraph("MINIMARKET UPAO", FUENTE_NEGRITA));
            document.add(new Paragraph("RUC: 20123456789"));
            document.add(new Paragraph("Av. Universitaria 123 - Trujillo"));
            document.add(new Paragraph("Tel: (044) 123456"));
            
            document.add(new Paragraph(" "));
            
            // INFORMACIÓN DE LA VENTA
            document.add(new Paragraph("ID Venta: " + idVenta));
            document.add(new Paragraph("Cliente: " + idCliente));
            document.add(new Paragraph("Empleado: " + idEmpleado));
            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaHora)));
            
            document.add(new Paragraph(" "));
            
            // ✅ OBTENER DETALLES DE LA VENTA
            String detallesProductos = obtenerDetallesVenta(idVenta);
            document.add(new Paragraph("PRODUCTOS:"));
            document.add(new Paragraph(detallesProductos));
            
            document.add(new Paragraph(" "));
            
            // TOTALES
            document.add(new Paragraph("Subtotal: S/ " + String.format("%.2f", subtotal)));
            document.add(new Paragraph("IGV (18%): S/ " + String.format("%.2f", igv)));
            
            Paragraph totalParrafo = new Paragraph("TOTAL: S/ " + String.format("%.2f", total), FUENTE_NEGRITA);
            totalParrafo.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParrafo);
            
            document.add(new Paragraph(" "));
            
            Paragraph gracias = new Paragraph("¡Gracias por su compra!");
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);
            
            // ✅ CORREGIDO: Usar la fuente pequeña en lugar de setFontSize
            Paragraph nota = new Paragraph("Comprobante generado automáticamente - " + 
                                         new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), FUENTE_PEQUEÑA);
            nota.setAlignment(Element.ALIGN_CENTER);
            document.add(nota);
            
            document.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            System.err.println("❌ Error generando PDF para venta " + idVenta + ": " + e.getMessage());
            return null;
        }
    }
    
    private static String obtenerDetallesVenta(int idVenta) {
        StringBuilder detalles = new StringBuilder();
        
        try {
            Connection conexion = new Conexion().establecerConexion();
            String sql = "SELECT p.nombre, dv.cantidad, dv.precio_unitario, dv.subtotal " +
                        "FROM detalle_venta dv " +
                        "JOIN producto p ON dv.id_producto = p.id_producto " +
                        "WHERE dv.id_venta = ?";
            
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio_unitario");
                double subtotal = rs.getDouble("subtotal");
                
                String linea = String.format("  - %s x%d = S/ %.2f\n", nombre, cantidad, subtotal);
                detalles.append(linea);
            }
            
            conexion.close();
            
        } catch (Exception e) {
            detalles.append("  - No se pudieron obtener los detalles\n");
        }
        
        return detalles.toString();
    }
}