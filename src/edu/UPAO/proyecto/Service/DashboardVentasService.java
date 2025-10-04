package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.*;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.PuntoDiario;
import edu.UPAO.proyecto.Modelo.PuntoMensual;
import edu.UPAO.proyecto.Modelo.Venta;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DashboardVentasService {

    private final VentaDAO ventaDAO = new VentaDAO();

    /** 
     * Punto único: devuelve todo lo que la UI necesita según filtros 
     */
    public Map<String, Object> consultar(LocalDate desde, LocalDate hasta, Integer productoIdFiltro) {
        List<Venta> ventas = ventaDAO.listar(); // trae todas las ventas

        // --- Filtro por fecha ---
        if (desde != null) {
            ventas = ventas.stream()
                    .filter(v -> v.getFecha().toLocalDate().compareTo(desde) >= 0)
                    .toList();
        }
        if (hasta != null) {
            ventas = ventas.stream()
                    .filter(v -> v.getFecha().toLocalDate().compareTo(hasta) <= 0)
                    .toList();
        }

        // --- Filtro por producto ---
        if (productoIdFiltro != null) {
            int pid = productoIdFiltro;
            ventas = ventas.stream().filter(v ->
                    v.getDetalleVenta() != null &&
                    v.getDetalleVenta().stream().anyMatch(d ->
                            d.getProducto() != null && d.getProducto().getIdProducto() == pid)
            ).toList();
        }

        Map<String, Object> out = new HashMap<>();
        out.put("kpis", kpis(ventas));
        out.put("mensuales", ventasMensuales(ventas));
        out.put("diarias", ventasDiarias(ventas));
        out.put("porProducto", ventasPorProducto(ventas));
        out.put("mediosPago", mediosPago(ventas));
        return out;
    }

    // ---------- agregaciones ----------
    private KPI kpis(List<Venta> ventas) {
        int totalVentas = ventas.size();

        int productosVendidos = ventas.stream()
                .flatMap(v -> v.getDetalleVenta() == null ? Stream.<DetalleVenta>empty() : v.getDetalleVenta().stream())
                .mapToInt(DetalleVenta::getCantidad).sum();

        double gananciaTotal = ventas.stream().mapToDouble(Venta::calcularTotal).sum();
        // Si luego agregas costo real, reemplaza este margen fijo
        double gananciaBruta = gananciaTotal * 0.38;

        return new KPI(totalVentas, productosVendidos, r2(gananciaTotal), r2(gananciaBruta));
    }

    private List<PuntoMensual> ventasMensuales(List<Venta> ventas) {
        return ventas.stream()
                .collect(Collectors.groupingBy(v -> YearMonth.from(v.getFecha()),
                        Collectors.summingDouble(Venta::calcularTotal)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new PuntoMensual(e.getKey(), r2(e.getValue())))
                .toList();
    }

    private List<PuntoDiario> ventasDiarias(List<Venta> ventas) {
        return ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate(),
                        Collectors.summingDouble(Venta::calcularTotal)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new PuntoDiario(e.getKey(), r2(e.getValue())))
                .toList();
    }

    private List<Barra> ventasPorProducto(List<Venta> ventas) {
        return ventas.stream()
                .flatMap(v -> v.getDetalleVenta() == null ? Stream.<DetalleVenta>empty() : v.getDetalleVenta().stream())
                .collect(Collectors.groupingBy(
                        d -> d.getProducto() != null ? d.getProducto().getNombre() : "Producto desconocido",
                        Collectors.summingDouble(DetalleVenta::getSubtotal)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> new Barra(e.getKey(), r2(e.getValue())))
                .toList();
    }

    private List<Slice> mediosPago(List<Venta> ventas) {
        return ventas.stream()
                .collect(Collectors.groupingBy(Venta::getMetodoPago,
                        Collectors.summingDouble(Venta::calcularTotal)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new Slice(e.getKey(), r2(e.getValue())))
                .toList();
    }

    // ---------- util ----------
    private double r2(double v) { 
        return Math.round(v * 100.0) / 100.0; 
    }
}
