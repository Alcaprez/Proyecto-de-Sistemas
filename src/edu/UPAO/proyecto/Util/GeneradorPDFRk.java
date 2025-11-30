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
        public void generarReporteCompleto(JTable tabla,
                                       JFreeChart chartDiario,
                                       JFreeChart chartMensual,
                                       JFreeChart chartProductos,
                                       JFreeChart chartMediosPago,
                                       String mes) {
        try {
            String ruta = "Reporte_Ranking_" + mes + ".pdf";
            // Horizontal para que entren mejor los gráficos
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // ===== TÍTULO =====
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Reporte general de ventas: " + mes, fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(Chunk.NEWLINE);

            // ===== BLOQUE DE GRÁFICOS (2 x 2) =====
            PdfPTable tablaGraficos = new PdfPTable(2);
            tablaGraficos.setWidthPercentage(100);

            agregarGraficoACelda(tablaGraficos, chartDiario,     "Ventas diarias");
            agregarGraficoACelda(tablaGraficos, chartMensual,    "Ventas mensuales");
            agregarGraficoACelda(tablaGraficos, chartProductos,  "Ventas por producto (Top 5)");
            agregarGraficoACelda(tablaGraficos, chartMediosPago, "Medios de pago");

            doc.add(tablaGraficos);
            doc.add(Chunk.NEWLINE);

            // ===== TABLA RESUMEN (KPI + ranking) =====
            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);

            String[] headers = {"TOP", "LOCALES", "VENTAS", "TRANSACCIONES"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11))
                );
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

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
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper privado para no repetir código
    private void agregarGraficoACelda(PdfPTable tabla, JFreeChart grafico, String titulo) throws Exception {
        PdfPCell cell;

        if (grafico == null) {
            cell = new PdfPCell(new Phrase("Sin datos para " + titulo));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            return;
        }

        java.awt.image.BufferedImage img = grafico.createBufferedImage(400, 250);
        Image pdfImg = Image.getInstance(img, null);

        cell = new PdfPCell();
        cell.setPadding(5f);

        Paragraph tituloPar = new Paragraph(
                titulo,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
        );
        tituloPar.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(tituloPar);
        pdfImg.setAlignment(Image.ALIGN_CENTER);
        cell.addElement(pdfImg);

        tabla.addCell(cell);
    }

}
