package edu.UPAO.proyecto.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int idVenta;
    private LocalDateTime fecha;
    private String idEmpleado;           // ✅ CAMBIADO: de int cajeroId a String idEmpleado
    private String metodoPago;
    private List<DetalleVenta> detalleVenta;
    private String dniCliente;
    private double subtotal;
    private double igv;
    private double total;
    private int idSucursal;

    // Constructor vacío
    public Venta() {
        this.detalleVenta = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    // ✅ CONSTRUCTOR COMPLETO CORREGIDO - ahora recibe idEmpleado
    public Venta(int idVenta, String idEmpleado, String metodoPago, List<DetalleVenta> detalleVenta) {
        this.idVenta = idVenta;
        this.idEmpleado = idEmpleado;    // ✅ INICIALIZAR idEmpleado
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

    // ✅ ELIMINADO: getCajeroId() y setCajeroId() - ya no existen
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

    // ✅ CORREGIDO: toFileLine() - reemplazado cajeroId por idEmpleado
    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFechaFormateada()).append(";")
                .append(idVenta).append(";")
                .append(idEmpleado).append(";") // ✅ CAMBIADO: cajeroId → idEmpleado
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

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    // ✅ CORREGIDO: toString() - reemplazado cajeroId por idEmpleado
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOLETA DE VENTA ===\n")
                .append("Venta ID: ").append(idVenta).append("\n")
                .append("Fecha: ").append(getFechaFormateada()).append("\n")
                .append("Empleado ID: ").append(idEmpleado).append("\n") // ✅ CAMBIADO: cajeroId → idEmpleado
                .append("Método de pago: ").append(metodoPago).append("\n\n")
                .append("Detalle:\n");

        for (DetalleVenta d : detalleVenta) {
            sb.append(d.toString()).append("\n");
        }

        sb.append("\nTOTAL: S/ ").append(calcularTotal()).append("\n")
                .append("=======================\n");

        return sb.toString();
    }

    // ✅ Calcular el subtotal (sin IGV)
    public double getSubtotal() {
        return this.subtotal; // Devuelve el valor establecido, no recalcula
    }

    public double getIGV() {
        return this.igv; // Devuelve el valor establecido
    }

    public double getTotal() {
        return this.total; // Devuelve el valor establecido
    }

    // ✅ CORREGIDO: generarComprobante() - reemplazado cajeroId por idEmpleado
    public String generarComprobante() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("         COMPROBANTE DE PAGO\n");
        sb.append("=========================================\n");
        sb.append("N° Venta: ").append(idVenta).append("\n");
        sb.append("Empleado ID: ").append(idEmpleado).append("\n")
                .append("Fecha: ").append(getFechaFormateada()).append("\n");
        sb.append("Método Pago: ").append(metodoPago).append("\n");
        sb.append("-----------------------------------------\n");

        // Detalles de productos
        for (DetalleVenta detalle : detalleVenta) {
            String nombreProducto = (detalle.getProducto() != null)
                    ? detalle.getProducto().getNombre() : "Producto no disponible";

            // Truncar nombres muy largos
            if (nombreProducto.length() > 20) {
                nombreProducto = nombreProducto.substring(0, 17) + "...";
            }

            sb.append(String.format("%-20s %2d x S/%-6.2f S/%-7.2f\n",
                    nombreProducto,
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal()));
        }

        sb.append("-----------------------------------------\n");

        // ✅ USAR LOS VALORES ESTABLECIDOS (CORREGIDOS) EN LUGAR DE RECALCULAR
        sb.append(String.format("SUBTOTAL: S/%-25.2f\n", this.subtotal));  // Usar this.subtotal
        sb.append(String.format("IGV (18%%): S/%-24.2f\n", this.igv));     // Usar this.igv  
        sb.append(String.format("TOTAL: S/%-27.2f\n", this.total));        // Usar this.total

        sb.append("=========================================\n");
        sb.append("         ¡GRACIAS POR SU COMPRA!\n");
        sb.append("=========================================\n");

        return sb.toString();
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
