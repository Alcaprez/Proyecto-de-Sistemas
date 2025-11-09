package edu.UPAO.proyecto.Modelo;

import java.util.Date;

public class Producto {

    private int id;
    private String nombre;
    private int stock;
    private int stockMinimo;
    private double precioCompra;
    private double precioVenta;
    private String estado;
    private Date fechaCaducidad;
    private String categoria;
    private String codigo; // Usaremos el ID como código temporal

    // Constructores
    public Producto() {
    }

    public Producto(int id, String nombre, int stock, double precioVenta) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.precioVenta = precioVenta;
    }

// Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.codigo = String.valueOf(id); // Auto-generar código desde ID
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        if (codigo == null && id > 0) {
            return String.valueOf(id);
        }
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return nombre + " (ID: " + id + ") - S/" + precioVenta;
    }
}
