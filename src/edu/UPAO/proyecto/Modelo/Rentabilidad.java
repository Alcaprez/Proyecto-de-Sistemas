package edu.UPAO.proyecto.Modelo;

public class Rentabilidad {
    private String sede;
    private double ventas;
    private double costos;

    public Rentabilidad(String sede, double ventas, double costos) {
        this.sede = sede;
        this.ventas = ventas;
        this.costos = costos;
    }

    public String getSede() {
        return sede;
    }

    public double getVentas() {
        return ventas;
    }

    public double getCostos() {
        return costos;
    }

    // 🟢 Margen en soles (diferencia)
    public double getMargenUtilidadSoles() {
        return ventas - costos;
    }

    // 🟢 Margen porcentual (%)
    public double getMargenUtilidadPorcentaje() {
        if (ventas == 0) return 0;
        return ((ventas - costos) / ventas) * 100;
    }
}





