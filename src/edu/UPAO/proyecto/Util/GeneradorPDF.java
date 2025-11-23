package edu.UPAO.proyecto.Util; // <--- AQUÍ ESTABA EL ERROR (Debe ser 'Util' con mayúscula)

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize; // Importante para reporte horizontal
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
import javax.swing.JTable;
import javax.swing.JOptionPane;

public class GeneradorPDF {
    
    private static final String CARPETA_COMPROBANTES = "comprobantes/";
    
    // FUENTES
    private static final Font FUENTE_NORMAL = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font FUENTE_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    
    static {
        // Crear carpeta si no existe
        File carpeta = new File(CARPETA_COMPROBANTES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
    
    // --- MÉTODO 1: GENERAR BOLETA (El que ya tenías) ---
    public static String generarBoleta(Venta venta, String numeroComprobante) {
        String serie = "B001";
        String nombreArchivo = CARPETA_COMPROBANTES + "BOLETA_" + serie + "-" + numeroComprobante + ".pdf";
        
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();
            
            Paragraph titulo = new Paragraph("BOLETA DE VENTA", FUENTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            Paragraph numero = new Paragraph("N°: " + serie + "-" + numeroComprobante, FUENTE_NEGRITA);
            numero.setAlignment(Element.ALIGN_CENTER);
            document.add(numero);
            
            document.add(new Paragraph(" ")); 
            
            document.add(new Paragraph("MINIMARKET KUKAY®", FUENTE_NEGRITA));
            document.add(new Paragraph("RUC: 20123456789"));
            document.add(new Paragraph("Av. Universitaria 123 - Trujillo"));
            
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Cliente: " + venta.getDniCliente()));
            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
            document.add(new Paragraph(" "));
            
            PdfPTable tabla = new PdfPTable(4); 
            tabla.setWidthPercentage(100); 
            
            agregarCelda(tabla, "DESCRIPCIÓN", true);
            agregarCelda(tabla, "CANT", true);
            agregarCelda(tabla, "P. UNIT", true);
            agregarCelda(tabla, "TOTAL", true);
            
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                agregarCelda(tabla, detalle.getProducto().getNombre(), false);
                agregarCelda(tabla, String.valueOf(detalle.getCantidad()), false);
                agregarCelda(tabla, "S/ " + String.format("%.2f", detalle.getPrecioUnitario()), false);
                agregarCelda(tabla, "S/ " + String.format("%.2f", detalle.getSubtotal()), false);
            }
            
            document.add(tabla);
            document.add(new Paragraph(" "));
            
            Paragraph total = new Paragraph("TOTAL: S/ " + String.format("%.2f", venta.getTotal()), FUENTE_NEGRITA);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            document.add(new Paragraph("¡Gracias por su compra!", FUENTE_NORMAL));
            
            document.close();
            return nombreArchivo;
            
        } catch (DocumentException | IOException e) {
            System.err.println("❌ Error generando PDF: " + e.getMessage());
            return null;
        }
    }

    // --- MÉTODO 2: GENERAR REPORTE DESDE JTABLE (El nuevo para Cupones) ---
    public static void generarReporteDesdeTabla(JTable tabla, String tituloReporte) {
        String nombreArchivo = "Reporte_" + tituloReporte.replaceAll(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document(PageSize.A4.rotate()); // Hoja Horizontal

        try {
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();

            Paragraph titulo = new Paragraph(tituloReporte.toUpperCase(), FUENTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), FUENTE_NORMAL));
            document.add(new Paragraph(" ")); 

            PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Encabezados
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                agregarCelda(pdfTable, tabla.getColumnName(i), true);
            }

            // Datos
            for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                    Object valor = tabla.getValueAt(rows, cols);
                    agregarCelda(pdfTable, valor == null ? "" : valor.toString(), false);
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(null, "¡Reporte PDF generado!\nArchivo: " + nombreArchivo);
            
            try {
                java.awt.Desktop.getDesktop().open(new File(nombreArchivo));
            } catch (Exception e) { }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
    }
    
    // MÉTODOS AUXILIARES
    private static void agregarCelda(PdfPTable tabla, String texto, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, isHeader ? FUENTE_NEGRITA : FUENTE_NORMAL));
        if (isHeader) {
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
        }
        tabla.addCell(cell);
    }
}