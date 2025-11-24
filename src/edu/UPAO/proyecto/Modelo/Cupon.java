package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;

public class Cupon {

    public enum TipoDescuento {
        PERCENT, FLAT
    }

    private String codigo;
    private TipoDescuento tipo;
    private double valor;
    private String descripcion;   // ✅ Antes skuAplicado, ahora Descripción
    private double minimoCompra;
    private LocalDate inicio;
    private LocalDate fin;
    private boolean activo;
    private int maxUsos;
    private int usos;

    public Cupon() {
    }

    public Cupon(String codigo, TipoDescuento tipo, double valor, String descripcion,
            double minimoCompra, LocalDate inicio, LocalDate fin,
            boolean activo, int maxUsos, int usos) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.minimoCompra = minimoCompra;
        this.inicio = inicio;
        this.fin = fin;
        this.activo = activo;
        this.maxUsos = maxUsos;
        this.usos = usos;
    }

    // Lógica simplificada: Aplica a todo el carrito si cumple condiciones
    public double calcularDescuento(double subtotal) {
        if (tipo == TipoDescuento.PERCENT) {
            return subtotal * (valor / 100.0);
        } else {
            return Math.min(valor, subtotal); // No descontar más del total
        }
    }

    public boolean isVigente(LocalDate hoy) {
        if (!activo) {
            return false;
        }
        if (inicio != null && hoy.isBefore(inicio)) {
            return false;
        }
        if (fin != null && hoy.isAfter(fin)) {
            return false;
        }
        if (maxUsos > 0 && usos >= maxUsos) {
            return false;
        }
        return true;
    }

    public boolean cumpleMinimo(double subtotal) {
        return subtotal >= minimoCompra;
    }

    // Getters y Setters actualizados
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoDescuento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDescuento tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    } // ✅ Getter Descripción

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getMinimoCompra() {
        return minimoCompra;
    }

    public void setMinimoCompra(double minimoCompra) {
        this.minimoCompra = minimoCompra;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public void setFin(LocalDate fin) {
        this.fin = fin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getMaxUsos() {
        return maxUsos;
    }

    public void setMaxUsos(int maxUsos) {
        this.maxUsos = maxUsos;
    }

    public int getUsos() {
        return usos;
    }

    public void setUsos(int usos) {
        this.usos = usos;
    }
}
