package edu.UPAO.proyecto.Modelo;

import java.sql.Timestamp;

public class Caja {
    private int idCaja;
    private Timestamp fechaApertura;
    private Timestamp fechaCierre;
    private double saldoInicial;
    private double saldoFinal;
    private String estado; // 'ABIERTA', 'CERRADA'
    private int idSucursal;

    // Constructores, Getters y Setters
    public Caja() {}

    public int getIdCaja() { return idCaja; }
    public void setIdCaja(int idCaja) { this.idCaja = idCaja; }
    public Timestamp getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(Timestamp fechaApertura) { this.fechaApertura = fechaApertura; }
    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }
    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double saldoFinal) { this.saldoFinal = saldoFinal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }
}