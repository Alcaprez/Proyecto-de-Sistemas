package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.VentaDAO;
import edu.UPAO.proyecto.Modelo.DetalleVenta;
import edu.UPAO.proyecto.Modelo.Venta;
import edu.UPAO.proyecto.Util.Validaciones; // 👈 tu clase de validaciones

import java.util.ArrayList;
import java.util.List;

public class VentaService {

    private VentaDAO ventaDAO = new VentaDAO();
    private InventarioSucursalDAO inventarioSucursalDAO = new InventarioSucursalDAO(); // ✅ NUEVO

    // Validar la venta antes de registrar
    private List<String> validarVenta(List<DetalleVenta> detalles, String metodoPago, String idEmpleado, int idSucursal) {
        List<String> errores = new ArrayList<>();

        if (detalles == null || detalles.isEmpty()) {
            errores.add("La venta debe tener al menos un producto.");
            return errores;
        }

        for (DetalleVenta d : detalles) {
            if (d.getProducto() == null) {
                errores.add("El detalle contiene un producto nulo.");
                continue;
            }

            if (!Validaciones.isPositiveInt(d.getCantidad())) {
                errores.add("Cantidad inválida para el producto " + d.getProducto().getNombre());
            }

            if (!Validaciones.isPositiveNumber(d.getPrecioUnitario())) {
                errores.add("Precio unitario inválido para " + d.getProducto().getNombre());
            }

            // ✅ VALIDACIÓN CORREGIDA: Stock por sucursal
            int idProducto = d.getProducto().getId();
            int stockDisponible = inventarioSucursalDAO.obtenerStock(idProducto, idSucursal);

            if (d.getCantidad() > stockDisponible) {
                errores.add("Stock insuficiente para " + d.getProducto().getNombre()
                        + " (stock disponible en sucursal: " + stockDisponible + ").");
            }
        }

        if (metodoPago == null || metodoPago.isBlank()) {
            errores.add("Debe especificar un método de pago.");
        }

        return errores;
    }

    // Registrar una nueva venta con validaciones
    public boolean registrarVenta(int cajeroId, List<DetalleVenta> detalles, String metodoPago) {
        List<String> errores = validarVenta(detalles, metodoPago);

        if (!errores.isEmpty()) {
            System.out.println("❌ No se pudo registrar la venta. Errores:");
            errores.forEach(e -> System.out.println(" - " + e));
            return false;
        }

        ventaDAO.registrarVenta(cajeroId, detalles, metodoPago);
        System.out.println("✅ Venta registrada correctamente.");
        return true;
    }

    // Listar todas las ventas
    public List<Venta> listarVentas() {
        return ventaDAO.listar();
    }

    // Buscar una venta por su ID
    public Venta buscarPorId(int idVenta) {
        return ventaDAO.buscarPorId(idVenta);
    }

    // Eliminar una venta
    public boolean eliminarVenta(int idVenta) {
        return ventaDAO.eliminarVenta(idVenta);
    }

    // Obtener el total de una venta específica
    public double obtenerTotalVenta(int idVenta) {
        Venta v = ventaDAO.buscarPorId(idVenta);
        if (v != null) {
            return v.calcularTotal();
        }
        return 0.0;
    }
}
