package edu.UPAO.proyecto.Modelo;

public class RankingFinanciero {
    private String sede;
    private double ventas;
    private double transacciones;
    private int ranking; // se calcula según promedio

    public RankingFinanciero(String sede, double ventas, double transacciones) {
        this.sede = sede;
        this.ventas = ventas;
        this.transacciones = transacciones;
    }

    public String getSede() {
        return sede;
    }

    public double getVentas() {
        return ventas;
    }

    public double getTransacciones() {
        return transacciones;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    // Promedio simple para calcular ranking
    public double getPromedio() {
        return (ventas + transacciones) / 2.0;
    }
}



