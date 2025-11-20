package edu.UPAO.proyecto.Modelo;

import java.time.LocalTime;

public class HorarioEmpleado {

    private String idEmpleado;
    private String nombreEmpleado;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private int minutosTolerancia;

    public HorarioEmpleado(String idEmpleado, String nombreEmpleado, LocalTime horaEntrada, LocalTime horaSalida, int minutosTolerancia) {
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.minutosTolerancia = minutosTolerancia;
    }

    // Getters y Setters
    public String getIdEmpleado() {
        return idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public int getMinutosTolerancia() {
        return minutosTolerancia;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public void setMinutosTolerancia(int minutosTolerancia) {
        this.minutosTolerancia = minutosTolerancia;
    }

    public String toCSV() {
        return idEmpleado + ";" + nombreEmpleado + ";" + horaEntrada + ";" + horaSalida + ";" + minutosTolerancia;
    }

    public static HorarioEmpleado fromCSV(String csvLine) {
        try {
            String[] partes = csvLine.split(";");
            if (partes.length == 5) {
                return new HorarioEmpleado(
                        partes[0], // idEmpleado
                        partes[1], // nombreEmpleado
                        LocalTime.parse(partes[2]), // horaEntrada
                        LocalTime.parse(partes[3]), // horaSalida
                        Integer.parseInt(partes[4]) // minutosTolerancia
                );
            }
        } catch (Exception e) {
            System.err.println("Error parseando horario: " + e.getMessage());
        }
        return null;
    }
}
