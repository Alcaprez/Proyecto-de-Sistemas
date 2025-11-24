
package edu.UPAO.proyecto.modelo;

public class Permiso {
    private int id;
    private int idRol;
    private String modulo;
    private String nombre;
    
    // Constructor
    public Permiso(int id, int idRol, String modulo, String nombre) {
        this.id = id;
        this.idRol = idRol;
        this.modulo = modulo;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdRol() {
        return idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    
    public String getModulo() {
        return modulo;
    }
    
    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
        return modulo + " - " + nombre;
    }
}