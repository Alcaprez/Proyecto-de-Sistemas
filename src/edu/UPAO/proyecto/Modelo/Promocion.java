package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;

public class Promocion {

    public enum Tipo { PERCENT, FLAT } // % o monto fijo

    private String codigo;
    private Tipo tipo;
    private double valor;
    private String skuAplicado;   // null si aplica a todo el carrito
    private double minimoCompra;  // 0 = sin mínimo
    private LocalDate inicio;     // null = sin inicio
    private LocalDate fin;        // null = sin fin
    private boolean activo;
    private int maxUsos;          // 0 = ilimitado
    private int usos;

    public Promocion() {}

    public Promocion(String codigo, Tipo tipo, double valor, String skuAplicado, double minimoCompra,
                     LocalDate inicio, LocalDate fin, boolean activo, int maxUsos, int usos) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = valor;
        this.skuAplicado = (skuAplicado == null || skuAplicado.isBlank()) ? null : skuAplicado;
        this.minimoCompra = minimoCompra;
        this.inicio = inicio;
        this.fin = fin;
        this.activo = activo;
        this.maxUsos = maxUsos;
        this.usos = usos;
    }

    // Getters/Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getSkuAplicado() { return skuAplicado; }
    public void setSkuAplicado(String skuAplicado) { this.skuAplicado = skuAplicado; }
    public double getMinimoCompra() { return minimoCompra; }
    public void setMinimoCompra(double minimoCompra) { this.minimoCompra = minimoCompra; }
    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }
    public LocalDate getFin() { return fin; }
    public void setFin(LocalDate fin) { this.fin = fin; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getMaxUsos() { return maxUsos; }
    public void setMaxUsos(int maxUsos) { this.maxUsos = maxUsos; }
    public int getUsos() { return usos; }
    public void setUsos(int usos) { this.usos = usos; }
}
