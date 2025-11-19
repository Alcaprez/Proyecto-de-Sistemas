
package edu.UPAO.proyecto.Modelo;

import java.util.Date;

public class InventarioSucursal {
    private int idInventarioSucursal;
    private int idProducto;
    private int idSucursal;
    private int stockActual;
    private Date fechaCaducidad;
    
    // Constructores
    public InventarioSucursal() {}
    
    public InventarioSucursal(int idProducto, int idSucursal, int stockActual) {
        this.idProducto = idProducto;
        this.idSucursal = idSucursal;
        this.stockActual = stockActual;
    }
    
    // Getters y Setters
    public int getIdInventarioSucursal() { return idInventarioSucursal; }
    public void setIdInventarioSucursal(int idInventarioSucursal) { this.idInventarioSucursal = idInventarioSucursal; }
    
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    
    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }
    
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    
    public Date getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(Date fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
    
    // Métodos de negocio
    public void disminuirStock(int cantidad) {
        this.stockActual -= cantidad;
    }
    
    public void aumentarStock(int cantidad) {
        this.stockActual += cantidad;
    }
    
    public boolean tieneStockSuficiente(int cantidad) {
        return this.stockActual >= cantidad;
    }
}