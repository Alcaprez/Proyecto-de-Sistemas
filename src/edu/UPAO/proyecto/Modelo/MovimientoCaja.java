package edu.UPAO.proyecto.Modelo;

import java.util.Date;

public class MovimientoCaja {

    private int idMovimientoCaja;
    private String tipo; // INGRESO, GASTO, COMPRA, VENTA
    private double monto;
    private Date fechaHora;
    private String descripcion;
    private int idCaja;
    private Integer idVenta;
    private Integer idCompra;
    private int idSucursal;
    private String estado;

    // Constructores
    public MovimientoCaja() {
    }

    public MovimientoCaja(String tipo, double monto, String descripcion, int idCaja, int idSucursal) {
        this.tipo = tipo;
        this.monto = monto;
        this.fechaHora = new Date();
        this.descripcion = descripcion;
        this.idCaja = idCaja;
        this.idSucursal = idSucursal;
        this.estado = "ACTIVO";
    }

    // Getters y Setters
    public int getIdMovimientoCaja() {
        return idMovimientoCaja;
    }

    public void setIdMovimientoCaja(int idMovimientoCaja) {
        this.idMovimientoCaja = idMovimientoCaja;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
