package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;

public class EventoAsistencia {
    public enum Tipo { IN, OUT }

    private String usuarioId;       // clave del Usuario
    private LocalDateTime fechaHora;
    private Tipo tipo;

    public EventoAsistencia(String usuarioId, LocalDateTime fechaHora, Tipo tipo) {
        this.usuarioId = usuarioId;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
    }

    public String getUsuarioId() { return usuarioId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Tipo getTipo() { return tipo; }
}
