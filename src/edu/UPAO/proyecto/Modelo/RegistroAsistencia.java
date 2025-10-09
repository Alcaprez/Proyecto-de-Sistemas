package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroAsistencia {
    private String idEmpleado;
    private String nombreEmpleado;
    private String tipo; // ENTRADA o SALIDA
    private LocalDateTime fechaHora;
    private String estado; // NORMAL, TARDANZA, SALIDA_TEMPRANA, etc.

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RegistroAsistencia(String idEmpleado, String nombreEmpleado, String tipo,
                              LocalDateTime fechaHora, String estado) {
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // Getters y Setters
    public String getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    // ✅ Convertir a CSV
    public String toCSV() {
        return idEmpleado + ";" + nombreEmpleado + ";" + tipo + ";" +
               fechaHora.format(formatter) + ";" + estado;
    }

    // ✅ Convertir desde CSV
    public static RegistroAsistencia fromCSV(String linea) {
        try {
            String[] partes = linea.split(";");
            if (partes.length == 5) {
                return new RegistroAsistencia(
                        partes[0],
                        partes[1],
                        partes[2],
                        LocalDateTime.parse(partes[3], formatter),
                        partes[4]
                );
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error leyendo línea de asistencia: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return tipo + " - " + fechaHora.format(formatter) + " (" + estado + ")";
    }
}
