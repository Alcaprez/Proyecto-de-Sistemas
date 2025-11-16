package edu.UPAO.proyecto.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.util.Map;

public class GraficosUtil {
    
    public static JPanel crearGraficoBarrasVentas(Map<String, Double> datos, String titulo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            dataset.addValue(entry.getValue(), "Ventas", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            titulo,
            "Sucursales",
            "Ventas (S/)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    public static JPanel crearGraficoPastelDistribucion(Map<String, Double> datos, String titulo) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            titulo,
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    public static JPanel crearGraficoLineasTendencia(Map<String, Double> datos, String titulo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            dataset.addValue(entry.getValue(), "Ventas", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            titulo,
            "Período",
            "Ventas (S/)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
}