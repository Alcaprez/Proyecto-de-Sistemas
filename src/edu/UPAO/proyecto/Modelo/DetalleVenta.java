package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DetalleVenta {
    private Producto producto;     // Producto vendido
    private int cantidad;          // Cantidad vendida
    private double precioUnitario; // Precio unitario (puede ser producto.getPrecioVenta())
    private LocalDateTime fecha;   // Fecha y hora de la venta

    // Constructor vacío
    public DetalleVenta() {
        this.fecha = LocalDateTime.now();
    }

    // Constructor para una línea de venta
    public DetalleVenta(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fecha = LocalDateTime.now();
    }

    // Getters y setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    // Subtotal de esta línea
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    // Formato bonito de fecha
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }

    // Convertir línea a string para mostrar
    @Override
    public String toString() {
        return producto.getNombre() + " x" + cantidad +
               " | Precio: S/ " + precioUnitario +
               " | Subtotal: S/ " + getSubtotal();
    }

    // Convertir línea a archivo (fecha;producto;cantidad;precioUnitario;subtotal)
    public String toFileLine() {
        return getFechaFormateada() + ";" +
               producto.getCodigo() + ";" +
               producto.getNombre() + ";" +
               cantidad + ";" +
               String.format("%.2f", precioUnitario) + ";" +
               String.format("%.2f", getSubtotal());
    }
       
}

