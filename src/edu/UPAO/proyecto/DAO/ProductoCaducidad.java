
package edu.UPAO.proyecto.DAO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductoCaducidad {
    private String lote;
    private String nombreProducto;
    private String proveedor;
    private LocalDate fechaCaducidad;
    private int cantidad;
    
    public ProductoCaducidad(String lote, String nombreProducto, String proveedor, 
                             LocalDate fechaCaducidad, int cantidad) {
        this.lote = lote;
        this.nombreProducto = nombreProducto;
        this.proveedor = proveedor;
        this.fechaCaducidad = fechaCaducidad;
        this.cantidad = cantidad;
    }
    
    // Calcula los días restantes hasta caducidad
    public long getDiasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);
    }
    
    // Determina el estado del producto
    public String getEstado() {
        long dias = getDiasRestantes();
        if (dias < 0) return "VENCIDO";
        if (dias <= 7) return "7_DIAS";
        if (dias <= 30) return "30_DIAS";
        return "NORMAL";
    }
    
    // Getters
    public String getLote() { return lote; }
    public String getNombreProducto() { return nombreProducto; }
    public String getProveedor() { return proveedor; }
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public int getCantidad() { return cantidad; }
    
    // Setters
    public void setLote(String lote) { this.lote = lote; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public void setFechaCaducidad(LocalDate fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
