package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class ResumenDiario {

    public Usuario emp;
    public long horasTrabajadas;
     Usuario usuario;
     java.time.LocalDate fecha;
    java.time.LocalTime entrada, salida;
    long minutosTrab, minTarde;
    String estado;
public ResumenDiario() {
  
}

    public ResumenDiario(Usuario emp, LocalDate fecha) {
        this.emp = emp;
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario u) {
        this.usuario = u;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate f) {
        this.fecha = f;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime t) {
        this.entrada = t;
    }

    public LocalTime getSalida() {
        return salida;
    }

    public void setSalida(LocalTime t) {
        this.salida = t;
    }

    public long getMinutosTrab() {
        return minutosTrab;
    }

    public void setMinutosTrab(long m) {
        this.minutosTrab = m;
    }

    public long getMinTarde() {
        return minTarde;
    }

    public void setMinTarde(long m) {
        this.minTarde = m;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String e) {
        this.estado = e;
    }
}
