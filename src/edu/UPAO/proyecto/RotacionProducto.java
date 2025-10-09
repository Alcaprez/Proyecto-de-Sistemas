package edu.UPAO.proyecto;

public class RotacionProducto {
    private String producto;
    private String sede;
    private double precioUnitario;
    private int ventas;

    public RotacionProducto(String producto, String sede, double precioUnitario, int ventas) {
        this.producto = producto;
        this.sede = sede;
        this.precioUnitario = precioUnitario;
        this.ventas = ventas;
    }

    public String getProducto() { return producto; }
    public String getSede() { return sede; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getVentas() { return ventas; }
}




