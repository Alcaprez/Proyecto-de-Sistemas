package edu.UPAO.proyecto.Modelo;

import java.util.Date;
import java.util.List;

public class Compra {
    private int idCompra;
    private Date fechaHora;
    private double total;
    private String idProveedor;
    private String idEmpleado;
    private int idSucursal;
    private String estado;
    private List<DetalleCompra> detalles;

    // Constructores
    public Compra() {}

    public Compra(Date fechaHora, double total, String idProveedor, String idEmpleado, int idSucursal) {
        this.fechaHora = fechaHora;
        this.total = total;
        this.idProveedor = idProveedor;
        this.idEmpleado = idEmpleado;
        this.idSucursal = idSucursal;
        this.estado = "ACTIVO";
    }

    // Getters y Setters
    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getIdProveedor() { return idProveedor; }
    public void setIdProveedor(String idProveedor) { this.idProveedor = idProveedor; }

    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }
}