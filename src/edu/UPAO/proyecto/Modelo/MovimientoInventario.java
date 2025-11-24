// edu.UPAO.proyecto.Modelo.MovimientoInventario.java
package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {

    private int idMovimientoInventario;
    private LocalDateTime fechaHora;
    private String tipo; // "ENTRADA", "SALIDA", "AJUSTE"
    private int cantidad;
    private int stockAnterior;
    private int stockNuevo;
    private String estado;
    private int idProducto;
    private int idSucursal;

    public MovimientoInventario() {
    }

    public MovimientoInventario(String tipo, int cantidad, int stockAnterior,
            int stockNuevo, String estado, int idProducto, int idSucursal) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.estado = estado;
        this.idProducto = idProducto;
        this.idSucursal = idSucursal; // ✅ INICIALIZAR
        this.fechaHora = LocalDateTime.now();
    }

    public int getIdMovimientoInventario() {
        return idMovimientoInventario;
    }

    public void setIdMovimientoInventario(int idMovimientoInventario) {
        this.idMovimientoInventario = idMovimientoInventario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(int stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public int getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(int stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }
}
