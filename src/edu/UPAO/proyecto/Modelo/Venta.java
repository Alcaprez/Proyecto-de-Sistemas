package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int idVenta;
    private LocalDateTime fecha;
    private int cajeroId;
    private String metodoPago;
    private List<DetalleVenta> detalleVenta;

    // Constructor vacío
    public Venta() {
        this.detalleVenta = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    // Constructor completo
    public Venta(int idVenta, int cajeroId, String metodoPago, List<DetalleVenta> detalleVenta) {
        this.idVenta = idVenta;
        this.cajeroId = cajeroId;
        this.metodoPago = metodoPago;
        this.detalleVenta = detalleVenta != null ? detalleVenta : new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(int cajeroId) {
        this.cajeroId = cajeroId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }

    public void setDetalleVenta(List<DetalleVenta> detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    // ✅ Calcular total automáticamente sumando subtotales de detalleVenta
    public double calcularTotal() {
        return detalleVenta.stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
    }

    // ✅ Formatear fecha bonita para mostrar
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }

    // ✅ Agregar un detalle de venta
    public void agregarDetalle(DetalleVenta detalle) {
        if (this.detalleVenta == null) {
            this.detalleVenta = new ArrayList<>();
        }
        this.detalleVenta.add(detalle);
    }

    // ✅ Convertir la venta completa a línea de archivo
    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFechaFormateada()).append(";")
                .append(idVenta).append(";")
                .append(cajeroId).append(";")
                .append(metodoPago).append(";")
                .append(String.format("%.2f", calcularTotal())).append(";");

        for (int i = 0; i < detalleVenta.size(); i++) {
            if (i > 0) {
                sb.append("|"); // separador más seguro
            }
            sb.append(detalleVenta.get(i).toFileLine());
        }

        return sb.toString();
    }

    // ✅ Representación amigable de la venta
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOLETA DE VENTA ===\n")
                .append("Venta ID: ").append(idVenta).append("\n")
                .append("Fecha: ").append(getFechaFormateada()).append("\n")
                .append("Cajero ID: ").append(cajeroId).append("\n")
                .append("Método de pago: ").append(metodoPago).append("\n\n")
                .append("Detalle:\n");

        for (DetalleVenta d : detalleVenta) {
            sb.append(d.toString()).append("\n");
        }

        sb.append("\nTOTAL: S/ ").append(calcularTotal()).append("\n")
                .append("=======================\n");

        return sb.toString();
    }
}
