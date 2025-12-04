package edu.UPAO.proyecto.Modelo;

import java.sql.Timestamp;

public class Caja {
    private int idCaja;
    private Timestamp fechaApertura;
    private Timestamp fechaCierre;
    private double saldoInicial;
    private double saldoFinal;
    
    // ✅ NUEVOS CAMPOS PARA EL ARQUEO
    private double saldoSistema; // Lo que la computadora dice que debe haber
    private double diferencia;   // La resta (Real - Sistema)
    private String observacion;  // Nota del cajero si falta dinero
    
    private String estado; // 'ABIERTA', 'ENCUADRADA', 'CERRADA'
    private int idSucursal;
    
    // Campo auxiliar para saber quién la abrió (opcional, pero útil)
    private String idEmpleado; 

    public Caja() {}

    // --- GETTERS Y SETTERS ---

    public int getIdCaja() { return idCaja; }
    public void setIdCaja(int idCaja) { this.idCaja = idCaja; }

    public Timestamp getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(Timestamp fechaApertura) { this.fechaApertura = fechaApertura; }

    public Timestamp getFechaCierre() { return fechaCierre; } // Faltaba este getter
    public void setFechaCierre(Timestamp fechaCierre) { this.fechaCierre = fechaCierre; }

    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }

    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double saldoFinal) { this.saldoFinal = saldoFinal; }

    // ✅ Getters y Setters NUEVOS (Estos solucionan tu error)
    public double getSaldoSistema() { return saldoSistema; }
    public void setSaldoSistema(double saldoSistema) { this.saldoSistema = saldoSistema; }

    public double getDiferencia() { return diferencia; }
    public void setDiferencia(double diferencia) { this.diferencia = diferencia; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    // ---------------------------------------------------------

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }
    
    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }
}