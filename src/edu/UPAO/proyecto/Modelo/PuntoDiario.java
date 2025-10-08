/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;

public class PuntoDiario {
    private LocalDate dia;
    private double total;

    public PuntoDiario(LocalDate dia, double total) {
        this.dia = dia;
        this.total = total;
    }

    public LocalDate getdia() { return dia; }
    public double getTotal() { return total; }

    @Override
    public String toString() {
        return dia + " → " + total;
    }
}

