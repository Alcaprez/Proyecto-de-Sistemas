package edu.UPAO.proyecto.modelo;

import java.time.LocalDateTime;

public class UsuarioADM {
    private int id;
    private String nombre;
    private int idRol;
    private String nombreRol;
    private int idSucursal;
    private String nombreSucursal;
    private String estatus;
    private LocalDateTime ultimoCambio;
    
    public UsuarioADM() {
    }
    // Constructor completo
    public UsuarioADM(int id, String nombre, int idRol, String nombreRol, 
                      int idSucursal, String nombreSucursal, String estatus, 
                      LocalDateTime ultimoCambio) {
        this.id = id;
        this.nombre = nombre;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.estatus = estatus;
        this.ultimoCambio = ultimoCambio;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getIdRol() {
        return idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    
    public String getNombreRol() {
        return nombreRol;
    }
    
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    public int getIdSucursal() {
        return idSucursal;
    }
    
    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }
    
    public String getNombreSucursal() {
        return nombreSucursal;
    }
    
    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }
    
    public String getEstatus() {
        return estatus;
    }
    
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
    
    public LocalDateTime getUltimoCambio() {
        return ultimoCambio;
    }
    
    public void setUltimoCambio(LocalDateTime ultimoCambio) {
        this.ultimoCambio = ultimoCambio;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + nombreRol + ")";
    }
}
