package edu.UPAO.proyecto.Modelo;

public class DetalleCompra {
    private int idDetalleCompra;
    private int idCompra;
    private int idProducto;
    private int cantidad;
    private double precioCompra;
    private double subtotal;

    // Constructores
    public DetalleCompra() {}

    public DetalleCompra(int idProducto, int cantidad, double precioCompra) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.subtotal = cantidad * precioCompra;
    }

    // Getters y Setters
    public int getIdDetalleCompra() { return idDetalleCompra; }
    public void setIdDetalleCompra(int idDetalleCompra) { this.idDetalleCompra = idDetalleCompra; }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        this.subtotal = this.cantidad * this.precioCompra;
    }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { 
        this.precioCompra = precioCompra;
        this.subtotal = this.cantidad * this.precioCompra;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}