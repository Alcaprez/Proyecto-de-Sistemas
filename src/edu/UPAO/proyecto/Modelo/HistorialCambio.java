
package edu.UPAO.proyecto.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialCambio {
    private int id;
    private int idUsuario;
    private int idAdministrador;
    private String descripcionCambio;
    private LocalDateTime fechaCambio;
    private String nombreAdministrador;
    private String motivo;
    
    // Constructor
    public HistorialCambio(int id, int idUsuario, int idAdministrador, 
                          String descripcionCambio, LocalDateTime fechaCambio,
                          String nombreAdministrador, String motivo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idAdministrador = idAdministrador;
        this.descripcionCambio = descripcionCambio;
        this.fechaCambio = fechaCambio;
        this.nombreAdministrador = nombreAdministrador;
        this.motivo = motivo;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public int getIdAdministrador() {
        return idAdministrador;
    }
    
    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }
    
    public String getDescripcionCambio() {
        return descripcionCambio;
    }
    
    public void setDescripcionCambio(String descripcionCambio) {
        this.descripcionCambio = descripcionCambio;
    }
    
    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
    
    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
    
    public String getNombreAdministrador() {
        return nombreAdministrador;
    }
    
    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    // Método útil para mostrar la fecha formateada
    public String getFechaCambioFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCambio.format(formatter);
    }
    
    @Override
    public String toString() {
        return fechaCambio + " - " + descripcionCambio;
    }
}