package edu.UPAO.proyecto.Modelo;

import java.time.LocalTime;

public class Usuario {

    private int id;                // id_usuario
    private int dni;               // dni de persona
    private boolean estado;        // Activo/Inactivo
    private String tienda;         // nombre_sucursal
    private String nombreComp;     // nombres + apellidos
    private String cargo;          // rol de empleado
    private String usuario;        // id_empleado (CHAR(8))
    private String contrasena;     // contraseña

    private LocalTime horaEntradaProg;
    private LocalTime horaSalidaProg;

    // Constructor vacío necesario
    public Usuario() {
    }

    public Usuario(int id, int dni, boolean estado, String tienda,
            String nombreComp, String cargo, String usuario, String contrasena) {
        this.id = id;
        this.dni = dni;
        this.estado = estado;
        this.tienda = tienda;
        this.nombreComp = nombreComp;
        this.cargo = cargo;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public Usuario(int id, int dni, boolean estado, String Tienda,
            String nombreComp, String cargo, String usuario, String contrasena,
            LocalTime horaEntradaProg, LocalTime horaSalidaProg) {
        this(id, dni, estado, Tienda, nombreComp, cargo, usuario, contrasena);
        this.horaEntradaProg = horaEntradaProg;
        this.horaSalidaProg = horaSalidaProg;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public String getNombreComp() {
        return nombreComp;
    }

    public void setNombreComp(String nombreComp) {
        this.nombreComp = nombreComp;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public LocalTime getHoraEntradaProg() {
        return horaEntradaProg;
    }

    public void setHoraEntradaProg(LocalTime horaEntradaProg) {
        this.horaEntradaProg = horaEntradaProg;
    }

    public LocalTime getHoraSalidaProg() {
        return horaSalidaProg;
    }

    public void setHoraSalidaProg(LocalTime horaSalidaProg) {
        this.horaSalidaProg = horaSalidaProg;
    }

    @Override
    public String toString() {
        return nombreComp + " (" + cargo + ") - " + (estado ? "Activo" : "Inactivo");
    }
}
