package edu.UPAO.proyecto.Modelo;

public class Producto {

    private int idProducto;         // Identificador único
    private String codigo;          // Código interno
    private String nombre;          // Nombre del producto
    private String categoria;       // Categoría del producto

    private double precioVenta;     // Precio de venta

    private int stock;              // Stock actual
    private int stockMinimo;        // Stock mínimo permitido
    private int vendidos;           // Cantidad de productos vendidos

    private String fechaIngreso;    // Fecha en que ingresó
    private String fechaVencimiento;// Fecha de vencimiento (si aplica)

    // Constructores
    
    public Producto() {
    }

    public Producto(String nombre, double precioVenta, int stock, String codigo) {
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.codigo = codigo;
        
        // Valores por defecto
        this.idProducto = 0;
        this.categoria = "Sin categoría";
        this.stockMinimo = 0;
        this.vendidos = 0;
        this.fechaIngreso = "";
        this.fechaVencimiento = "";
    }
    public Producto(int idProducto, String codigo, String nombre, String categoria,double precioVenta,
            int stock, int stockMinimo, int vendidos,
            String fechaIngreso, String fechaVencimiento) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.vendidos = vendidos;
        this.fechaIngreso = fechaIngreso;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getVendidos() {
        return vendidos;
    }

    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    // Métodos útiles
    public boolean estaBajoStock() {
        return stock <= stockMinimo;
    }

    // Para guardar en archivo (CSV)
    public String toCSV() {
        return nombre + ";" + precioVenta + ";" + stock + ";" + codigo + ";" + vendidos;
    }

    // Para mostrar en pantalla
    @Override
    public String toString() {
        return idProducto + " - " + nombre + " | Precio: S/ " + precioVenta + " | Stock: " + stock;
    }
}
