package edu.UPAO.proyecto.Modelo;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Cupon {

    public enum TipoDescuento {
        PERCENT, FLAT
    }

    private String codigo;
    private TipoDescuento tipo;   // PERCENT = %, FLAT = monto fijo
    private double valor;         // si PERCENT => 0..100 ; si FLAT => monto en moneda
    private String skuAplicado;   // null o "" = aplica al carrito completo
    private double minimoCompra;  // 0 si no hay mínimo
    private LocalDate inicio;     // null = sin inicio
    private LocalDate fin;        // null = sin fin
    private boolean activo;
    private int maxUsos;          // 0 = ilimitado
    private int usos;             // contador (simple)

    public Cupon() {
    }

    public Cupon(String codigo, TipoDescuento tipo, double valor, String skuAplicado,
            double minimoCompra, LocalDate inicio, LocalDate fin,
            boolean activo, int maxUsos, int usos) {
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

    // Necesita tu clase VentaItem real. Si no la tienes, avísame y te doy una mínima.
    public double calcularDescuento(List<VentaItem> items, double subtotal) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        if (!cumpleMinimo(subtotal)) {
            return 0.0;
        }

        double base = 0.0;
        if (skuAplicado == null) {
            base = subtotal;
        } else {
            for (VentaItem it : items) {
                if (it.getNombre().equalsIgnoreCase(skuAplicado)) {
                    base += it.getSubtotal();
                }
            }

            if (base <= 0) {
                return 0.0;
            }
        }

        if (tipo == TipoDescuento.PERCENT) {
            return base * (valor / 100.0);
        } else {
            return Math.min(valor, base);
        }
    }

    public String toCsvLine() {
        String sku = (skuAplicado == null ? "" : skuAplicado);
        String inicioStr = (inicio == null ? "" : inicio.toString());
        String finStr = (fin == null ? "" : fin.toString());
        return String.join(",",
                escape(codigo),
                tipo.name(),
                String.valueOf(valor),
                escape(sku),
                String.valueOf(minimoCompra),
                inicioStr,
                finStr,
                String.valueOf(activo),
                String.valueOf(maxUsos),
                String.valueOf(usos)
        );
    }

    public static String csvHeader() {
        return "codigo,tipo,valor,sku_aplicado,minimo_compra,inicio,fin,activo,max_usos,usos";
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // Getters/Setters
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

    public String getSkuAplicado() {
        return skuAplicado;
    }

    public void setSkuAplicado(String skuAplicado) {
        this.skuAplicado = skuAplicado;
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
