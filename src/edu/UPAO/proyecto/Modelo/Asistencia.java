package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {
    private LocalDate fecha;
    private String usuario;          // id_empleado
    private LocalTime horaEntrada;   // real
    private LocalTime horaSalida;    // real

    public Asistencia(LocalDate fecha, String usuario, LocalTime horaEntrada, LocalTime horaSalida) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public LocalDate getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public LocalTime getHoraSalida()  { return horaSalida;  }

    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public void setHoraSalida(LocalTime horaSalida)   { this.horaSalida = horaSalida;  }
}