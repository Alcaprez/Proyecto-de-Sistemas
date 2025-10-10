package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.CuponDAO;
import edu.UPAO.proyecto.Modelo.Cupon;
import edu.UPAO.proyecto.Modelo.VentaItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.table.DefaultTableModel;

public class PromocionController {

    // Cajero: aplicar cupón (Menu2/ventas)
    public static double aplicarCupon(String codigo, List<VentaItem> items, double subtotal) {
        if (codigo == null || codigo.isBlank()) return 0.0;
        Optional<Cupon> opt = CuponDAO.buscarPorCodigo(codigo.trim());
        if (opt.isEmpty()) return 0.0;
        Cupon c = opt.get();
        if (!c.isVigente(LocalDate.now())) return 0.0;
        return c.calcularDescuento(items, subtotal);
    }

    // Gerente: CRUD para pantalla Promociones
    public static java.util.List<Cupon> listarCupones() {
        return CuponDAO.listar();
    }

    public static void crearActualizarCupon(Cupon c) {
        CuponDAO.upsert(c);
    }

    public static void activar(String codigo) {
        CuponDAO.buscarPorCodigo(codigo).ifPresent(c -> {
            c.setActivo(true);
            CuponDAO.upsert(c);
        });
    }

    public static void desactivar(String codigo) {
        CuponDAO.buscarPorCodigo(codigo).ifPresent(c -> {
            c.setActivo(false);
            CuponDAO.upsert(c);
        });
    }

    public static void eliminar(String codigo) {
        CuponDAO.eliminar(codigo);
    }
    public static double calcularDescuentoReglas(DefaultTableModel carrito) {
    double descuentoTotal = 0.0;

    if (carrito == null) return 0.0;

    for (int i = 0; i < carrito.getRowCount(); i++) {
        // Suponiendo que la columna 3 de la tabla es el subtotal
        Object valor = carrito.getValueAt(i, 3);
        if (valor != null) {
            try {
                double subtotal = Double.parseDouble(valor.toString());

                // ⚠️ Aquí puedes poner tus reglas automáticas:
                // Ejemplo: 10% descuento si el subtotal > 100
                if (subtotal > 100) {
                    descuentoTotal += subtotal * 0.10;
                }

            } catch (NumberFormatException e) {
                System.err.println("Error leyendo subtotal en fila " + i + ": " + valor);
            }
        }
    }
    return descuentoTotal;
}
}
