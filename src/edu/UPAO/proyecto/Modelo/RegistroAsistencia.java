/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.UPAO.proyecto.Modelo;
import java.time.LocalDateTime;

public class RegistroAsistencia {
    private String idEmpleado;
    private String nombreEmpleado;
    private String tipo; // Entrada o Salida
    private LocalDateTime fechaHora;
    private String estado; // NORMAL, TARDANZA, SALIDA_TEMPRANA

    public RegistroAsistencia(String idEmpleado, String nombreEmpleado, String tipo, LocalDateTime fechaHora, String estado) {
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public String getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return "RegistroAsistencia{" +
                "idEmpleado='" + idEmpleado + '\'' +
                ", nombreEmpleado='" + nombreEmpleado + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}
