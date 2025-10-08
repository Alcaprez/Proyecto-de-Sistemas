package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;

/**
 *
 * @author Fabri
 */
public class HistorialProveedor {

    private int idProveedor;     // Referencia al Proveedor
    private String tipoEvento;   // "COMPRA", "ACTIVACION", "DESACTIVACION"
    private String detalle;      // Motivo o descripción
    private LocalDateTime fecha; 
    private LocalDateTime fechaPeticion;  
    private LocalDateTime fechaLlegada;   
    private double monto; 

    // Constructor para eventos simples (activación, desactivación)
    public HistorialProveedor(int idProveedor, String tipoEvento, String detalle) {
        this.idProveedor = idProveedor;
        this.tipoEvento = tipoEvento;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
    }
     // Constructor para compras o servicios
    public HistorialProveedor(int idProveedor, String tipoEvento, String detalle, 
                              LocalDateTime fechaPeticion, LocalDateTime fechaLlegada, double monto) {
        this(idProveedor, tipoEvento, detalle);
        this.fechaPeticion = fechaPeticion;
        this.fechaLlegada = fechaLlegada;
        this.monto = monto;
    }

    // Getters
    public int getIdProveedor() { return idProveedor; }
    public String getTipoEvento() { return tipoEvento; }
    public String getDetalle() { return detalle; }
    public LocalDateTime getFecha() { return fecha; }
    public LocalDateTime getFechaPeticion() { return fechaPeticion; }
    public LocalDateTime getFechaLlegada() { return fechaLlegada; }
    public double getMonto() { return monto; }
    @Override
    public String toString() {
        return "HistorialProveedor{"
                + "idProveedor=" + idProveedor
                + ", tipoEvento='" + tipoEvento + '\''
                + ", detalle='" + detalle + '\''
                + ", fecha=" + fecha
                + '}';
    }
}
