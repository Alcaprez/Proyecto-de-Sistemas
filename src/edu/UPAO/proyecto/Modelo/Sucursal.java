package edu.UPAO.proyecto.modelo;

public class Sucursal {
    
    private int id_sucursal;
    private String nombre;      // En BD es 'nombre_sucursal'
    private String direccion;
    private String estado;

    public Sucursal() {
    }
    
    public Sucursal(int id_sucursal, String nombre) {
        this.id_sucursal = id_sucursal;
        this.nombre = nombre;
        this.direccion = ""; // Valores por defecto para evitar nulos
        this.estado = "";
    }

    public Sucursal(int id_sucursal, String nombre, String direccion, String estado) {
        this.id_sucursal = id_sucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.estado = estado;
    }


    public int getId() { 
        return id_sucursal; 
    }


    // Getters y Setters originales (puedes mantenerlos por compatibilidad)

    public void setId(int id_sucursal) { // También es bueno cambiar el setter
        this.id_sucursal = id_sucursal; 
    }

    public void setId_sucursal(int id_sucursal) { this.id_sucursal = id_sucursal; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        return nombre; // Para que el ComboBox muestre el nombre
    }
}