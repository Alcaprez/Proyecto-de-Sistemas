package edu.UPAO.proyecto.Util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import org.jfree.chart.JFreeChart;

public class GeneradorPDFRk {
    public void generarReporte(JTable tabla, JFreeChart grafico, String mes) {
        try {
            String ruta = "Reporte_Ranking_" + mes + ".pdf";
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Reporte de Ranking: " + mes, fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(Chunk.NEWLINE);

            // Gráfico
            if (grafico != null) {
                java.awt.image.BufferedImage img = grafico.createBufferedImage(500, 300);
                Image pdfImg = Image.getInstance(img, null);
                pdfImg.setAlignment(Element.ALIGN_CENTER);
                doc.add(pdfImg);
                doc.add(Chunk.NEWLINE);
            }

            // Tabla
            PdfPTable pdfTable = new PdfPTable(4); // 4 Columnas
            pdfTable.setWidthPercentage(100);
            
            // Cabeceras
            String[] headers = {"TOP", "LOCALES", "VENTAS", "TRANSACCIONES"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Datos
            for (int i = 0; i < tabla.getRowCount(); i++) {
                for (int j = 0; j < 4; j++) {
                    Object val = tabla.getValueAt(i, j);
                    pdfTable.addCell(val != null ? val.toString() : "");
                }
            }
            doc.add(pdfTable);
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF Exportado: " + ruta);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error PDF: " + e.getMessage());
        }
    }
}
