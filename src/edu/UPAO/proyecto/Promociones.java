package edu.UPAO.proyecto;

import edu.UPAO.proyecto.Modelo.Producto;
import java.util.List;

public class Promociones {

    // Aplica descuentos automáticos sobre productos específicos
    public static void aplicarPromociones(List<Producto> productos) {
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase("Coca-Cola 1L") && p.getStock() >= 3) {
                p.setPrecioVenta(p.getPrecioVenta()* 0.85); // 15% off
            } else if (p.getNombre().equalsIgnoreCase("Arroz 5kg") && p.getStock() >= 2) {
                p.setPrecioVenta(p.getPrecioVenta()* 0.90); // 10% off
            }
        }
    }

    // Aplica cupón sobre el total
    public static double aplicarCupon(String codigo, double total) {
        if ("DESCUENTO10".equalsIgnoreCase(codigo)) {
            return total * 0.90; // 10% off
        }
        return total;
    }
}
