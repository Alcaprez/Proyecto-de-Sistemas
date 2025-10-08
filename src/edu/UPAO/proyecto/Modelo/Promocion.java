package proyectosistemasempresariales.modelo;

public class Promocion {
    private String codigo;   // nombre del producto o código del cupón
    private String tipo;     // "producto" o "cupon"
    private double descuento; // porcentaje
    private int cantidadMinima; // NUEVO: cantidad mínima para aplicar descuento

    // Constructor para cupones
    public Promocion(String codigo, String tipo, double descuento) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.descuento = descuento;
        this.cantidadMinima = 1; // por defecto
    }

    // Constructor para productos con cantidad mínima
    public Promocion(String codigo, String tipo, double descuento, int cantidadMinima) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.descuento = descuento;
        this.cantidadMinima = cantidadMinima;
    }

    // getters
    public String getCodigo() { return codigo; }
    public String getTipo() { return tipo; }
    public double getDescuento() { return descuento; }
    public int getCantidadMinima() { return cantidadMinima; }

    @Override
    public String toString() {
        // Si es un producto con cantidad mínima
        if(tipo.equals("producto")) {
            return codigo + ";" + tipo + ";" + descuento + ";" + cantidadMinima;
        } else {
            return codigo + ";" + tipo + ";" + descuento;
        }
    }
}
