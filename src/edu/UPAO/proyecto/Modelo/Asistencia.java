package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {
    // --- CAMPOS ORIGINALES (NO TOCAR) ---
    private LocalDate fecha;
    private String usuario;          // id_empleado
    private LocalTime horaEntrada;   
    private LocalTime horaSalida;    

    // --- NUEVOS CAMPOS PARA EL GERENTE (ADICIONALES) ---
    private int idAsistencia;
    private int idSucursal;
    private String estado;           // RESPONSABLE, TARDE, AUSENTE
    private String nombreCompleto;   // Para mostrar en tabla
    private String cargo;            // Para mostrar en tabla
    private String turno;            // Para mostrar en tabla

    // ✅ CONSTRUCTOR VACÍO (Necesario para consultas flexibles del Gerente)
    public Asistencia() {}

    // ✅ CONSTRUCTOR ORIGINAL (Lo mantenemos para que no falle tu código anterior)
    public Asistencia(LocalDate fecha, String usuario, LocalTime horaEntrada, LocalTime horaSalida) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    // --- GETTERS Y SETTERS (Agregamos los nuevos, mantenemos los viejos) ---
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }

    // Getters/Setters Nuevos
    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}