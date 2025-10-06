package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;

public class Cupon {

    private String codigo;
    private double descuento;
    private boolean activo;
    private LocalDate inicio;
    private LocalDate fin;

    public Cupon(String codigo, double descuento, boolean activo, LocalDate inicio, LocalDate fin) {
        this.codigo = codigo;
        this.descuento = descuento;
        this.activo = activo;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getDescuento() {
        return descuento;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }
}
