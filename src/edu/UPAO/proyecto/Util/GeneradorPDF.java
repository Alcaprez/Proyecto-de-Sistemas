package edu.UPAO.proyecto.util;

//IMPORTACIONES para iText 5.5.13.2
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Modelo.DetalleVenta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorPDF {
    
    private static final String CARPETA_COMPROBANTES = "comprobantes/";
    
    // ✅ FUENTES para iText 5
    private static final Font FUENTE_NORMAL = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font FUENTE_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    
    static {
        // Crear carpeta si no existe
        File carpeta = new File(CARPETA_COMPROBANTES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
            System.out.println("📁 Carpeta comprobantes creada");
        }
    }
    
    // ✅ GENERAR BOLETA CON iText 5.5.13.2
    public static String generarBoleta(Venta venta, String numeroComprobante) {
        String serie = "B001";
        String nombreArchivo = CARPETA_COMPROBANTES + "BOLETA_" + serie + "-" + numeroComprobante + ".pdf";
        
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();
            
            // TÍTULO
            Paragraph titulo = new Paragraph("BOLETA DE VENTA", FUENTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            // NÚMERO
            Paragraph numero = new Paragraph("N°: " + serie + "-" + numeroComprobante, FUENTE_NEGRITA);
            numero.setAlignment(Element.ALIGN_CENTER);
            document.add(numero);
            
            document.add(new Paragraph(" ")); // Espacio
            
            // INFORMACIÓN DE LA EMPRESA
            document.add(new Paragraph("MINIMARKET KUKAY®", FUENTE_NEGRITA));
            document.add(new Paragraph("RUC: 20123456789"));
            document.add(new Paragraph("Av. Universitaria 123 - Trujillo"));
            document.add(new Paragraph("Tel: (044) 123456"));
            
            document.add(new Paragraph(" "));
            
            // INFORMACIÓN DEL CLIENTE Y FECHA
            document.add(new Paragraph("Cliente: " + venta.getDniCliente()));
            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
            document.add(new Paragraph(" "));
            
            // TABLA DE PRODUCTOS
            PdfPTable tabla = new PdfPTable(4); // 4 columnas
            tabla.setWidthPercentage(100); // Ancho 100%
            
            // Encabezados de la tabla
            agregarCelda(tabla, "DESCRIPCIÓN", true);
            agregarCelda(tabla, "CANT", true);
            agregarCelda(tabla, "P. UNIT", true);
            agregarCelda(tabla, "TOTAL", true);
            
            // Detalles de productos
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                agregarCelda(tabla, detalle.getProducto().getNombre(), false);
                agregarCelda(tabla, String.valueOf(detalle.getCantidad()), false);
                agregarCelda(tabla, "S/ " + String.format("%.2f", detalle.getPrecioUnitario()), false);
                agregarCelda(tabla, "S/ " + String.format("%.2f", detalle.getSubtotal()), false);
            }
            
            document.add(tabla);
            document.add(new Paragraph(" "));
            
            // TOTALES
            document.add(new Paragraph("Subtotal: S/ " + String.format("%.2f", venta.getSubtotal())));
            document.add(new Paragraph("IGV (18%): S/ " + String.format("%.2f", venta.getIgv())));
            
            Paragraph total = new Paragraph("TOTAL: S/ " + String.format("%.2f", venta.getTotal()), FUENTE_NEGRITA);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            document.add(new Paragraph(" "));
            
            Paragraph gracias = new Paragraph("¡Gracias por su compra!");
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);
            
            document.close();
            
            System.out.println("✅ Boleta PDF generada: " + nombreArchivo);
            return nombreArchivo;
            
        } catch (DocumentException | IOException e) {
            System.err.println("❌ Error generando PDF: " + e.getMessage());
            return null;
        }
    }
    
    // ✅ MÉTODO AUXILIAR PARA AGREGAR CELDAS A LA TABLA
    private static void agregarCelda(PdfPTable tabla, String texto, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, isHeader ? FUENTE_NEGRITA : FUENTE_NORMAL));
        if (isHeader) {
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
        }
        tabla.addCell(cell);
    }
}