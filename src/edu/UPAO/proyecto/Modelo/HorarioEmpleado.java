package edu.UPAO.proyecto.Modelo;

import java.time.LocalTime;

public class HorarioEmpleado {
    private String idEmpleado;
    private String nombreEmpleado;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

    // ✅ CONSTRUCTOR DE 4 ARGUMENTOS (Sin tolerancia)
    public HorarioEmpleado(String idEmpleado, String nombreEmpleado, LocalTime horaEntrada, LocalTime horaSalida) {
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    // Getters
    public String getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
}