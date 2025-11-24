// edu.UPAO.proyecto.Modelo.Empleado.java
package edu.UPAO.proyecto.Modelo;

public class Empleado {

    private String idEmpleado;
    private String dni;
    private int idSucursal;
    private String cargo; 
    private String estado;
    private double sueldo;
    
    
    private String nombres;
    private String apellidos;
    private String nombreSucursal; 
    private String telefono;
    private String correo;

    // Constructor vacío
    public Empleado() {
    }

    // Constructor completo (puedes mantener el que tenías o actualizarlo)
    public Empleado(String idEmpleado, String dni, int idSucursal, String cargo, String estado, double sueldo) {
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.idSucursal = idSucursal;
        this.cargo = cargo;
        this.estado = estado;
        this.sueldo = sueldo;
    }

    // --- GETTERS Y SETTERS DE LOS CAMPOS NUEVOS ---
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    // --- GETTERS Y SETTERS ORIGINALES (Déjalos como estaban) ---
    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getSueldo() { return sueldo; }
    public void setSueldo(double sueldo) { this.sueldo = sueldo; }
}