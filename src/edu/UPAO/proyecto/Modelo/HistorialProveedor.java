package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;

public class HistorialProveedor {
    private int idHistorial;
    private int idProveedor;
    private String evento;
    private String descripcion;
    private LocalDateTime fecha;

    public HistorialProveedor(int idHistorial, int idProveedor, String evento, String descripcion, LocalDateTime fecha) {
        this.idHistorial = idHistorial;
        this.idProveedor = idProveedor;
        this.evento = evento;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public HistorialProveedor(int idProveedor, String evento, String descripcion) {
        this(0, idProveedor, evento, descripcion, LocalDateTime.now());
    }

    // Getters y setters
    public int getIdHistorial() { return idHistorial; }
    public void setIdHistorial(int idHistorial) { this.idHistorial = idHistorial; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
