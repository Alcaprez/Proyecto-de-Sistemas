package edu.UPAO.proyecto.Modelo;

public class VentaItem {
    private String nombre;
    private int cantidad;
    private double subtotal;

    public VentaItem(String nombre, int cantidad, double subtotal) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return nombre + "|" + cantidad + "|" + String.format("%.2f", subtotal);
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
        
}
