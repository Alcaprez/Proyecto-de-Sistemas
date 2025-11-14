// edu.UPAO.proyecto.Modelo.Empleado.java
package edu.UPAO.proyecto.Modelo;

public class Empleado {

    private String idEmpleado;
    private String dni;
    private int idSucursal;
    private String cargo;
    private String estado;
    private double sueldo;

    // Constructores
    public Empleado() {
    }

    public Empleado(String idEmpleado, String dni, int idSucursal, String cargo, String estado, double sueldo) {
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.idSucursal = idSucursal;
        this.cargo = cargo;
        this.estado = estado;
        this.sueldo = sueldo;
    }

    // Getters y Setters
    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getSueldo() {
        return sueldo;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }
}
