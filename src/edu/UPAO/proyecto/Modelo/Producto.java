package edu.UPAO.proyecto.Modelo;

import java.util.Date;

public class Producto {
    private int id;
    private String nombre;
    private int stockMinimo;
    private double precioCompra;
    private double precioVenta;
    private String estado;
    private Date fechaCaducidad;
    private String categoria;
    private String codigo;
    private int vendidos;
    private int stockTemporal; // ✅ NUEVO CAMPO TEMPORAL para compatibilidad

    // Constructores
    public Producto() {
        this.vendidos = 0;
        this.stockTemporal = 0;
    }

    public Producto(int id, String nombre, double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.vendidos = 0;
        this.stockTemporal = 0;
    }

    // ✅ MÉTODO TEMPORAL para compatibilidad
    public int getStock() {
        return this.stockTemporal;
    }

    // ✅ SETTER TEMPORAL
    public void setStockTemporal(int stockTemporal) {
        this.stockTemporal = stockTemporal;
    }

    // Getters y Setters (mantener todos los existentes)
    public int getId() { return id; }
    public void setId(int id) { 
        this.id = id; 
        this.codigo = String.valueOf(id);
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(Date fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getCodigo() { 
        if (codigo == null && id > 0) {
            return String.valueOf(id);
        }
        return codigo; 
    }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getVendidos() { return vendidos; }
    public void setVendidos(int vendidos) { this.vendidos = vendidos; }

    @Override
    public String toString() {
        return nombre + " (ID: " + id + ") - S/" + precioVenta + " - Stock: " + stockTemporal;
    }
}