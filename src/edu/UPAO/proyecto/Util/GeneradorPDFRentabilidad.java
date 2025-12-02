package edu.UPAO.proyecto.Util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image; // Importante para el gráfico
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage; // Para convertir el gráfico
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.JFreeChart;

public class GeneradorPDFRentabilidad {

    // Ahora el método recibe 'JFreeChart chart' también
    public static void generarPDF(JTable table, JFreeChart chart, String tituloReporte) {
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Rentabilidad");
        fileChooser.setSelectedFile(new java.io.File(tituloReporte + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) filePath += ".pdf";

            // Usamos hoja horizontal (Rotate) para que quepa bien el gráfico y la tabla
            Document document = new Document(PageSize.A4.rotate());

            try {
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // 1. TÍTULO
                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph("Reporte Financiero de Rentabilidad", fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);

                Paragraph fecha = new Paragraph("Generado el: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
                fecha.setAlignment(Element.ALIGN_CENTER);
                fecha.setSpacingAfter(10);
                document.add(fecha);

                // 2. AGREGAR EL GRÁFICO AL PDF
                if (chart != null) {
                    // Convertimos el JFreeChart a una imagen PNG en memoria
                    int width = 500;
                    int height = 300;
                    BufferedImage bufferedImage = chart.createBufferedImage(width, height);
                    Image pdfImage = Image.getInstance(bufferedImage, null);
                    
                    pdfImage.setAlignment(Element.ALIGN_CENTER);
                    pdfImage.setSpacingAfter(20); // Espacio entre gráfico y tabla
                    document.add(pdfImage);
                }

                // 3. AGREGAR LA TABLA
                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Encabezados
                Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i), fontHeader));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(new BaseColor(46, 139, 87)); // Verde Mar (mismo que tu gráfico)
                    cell.setPadding(8);
                    pdfTable.addCell(cell);
                }

                // Datos
                Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
                for (int rows = 0; rows < table.getRowCount(); rows++) {
                    for (int cols = 0; cols < table.getColumnCount(); cols++) {
                        Object valor = table.getValueAt(rows, cols);
                        String texto = (valor == null) ? "" : valor.toString();
                        
                        PdfPCell cell = new PdfPCell(new Phrase(texto, fontData));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();
                JOptionPane.showMessageDialog(null, "Reporte exportado en:\n" + filePath);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}