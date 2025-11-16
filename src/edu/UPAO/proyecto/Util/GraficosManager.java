package edu.UPAO.proyecto.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GraficosManager {
    

    
    public static ChartPanel crearGraficoPastel(Map<String, Double> datos, String titulo) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart3D(
            titulo,
            dataset,
            true,    // leyenda
            true,    // tooltips
            false    // URLs
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
    
    public static ChartPanel crearGraficoLineas(Map<String, Double> datos, String titulo, 
                                               String ejeX, String ejeY) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            dataset.addValue(entry.getValue(), "Ventas", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            titulo,
            ejeX,
            ejeY,
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        
        return chartPanel;
    }
}