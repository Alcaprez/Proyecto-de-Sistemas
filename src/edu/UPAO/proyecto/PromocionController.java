package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.PromocionDAO;
import edu.UPAO.proyecto.Modelo.Promocion;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PromocionController {

    private PromocionDAO promocionDAO;

    public PromocionController() {
        this.promocionDAO = new PromocionDAO();
    }

    public double calcularDescuentoReglas(DefaultTableModel modeloCarrito) {
        double descuentoTotal = 0.0;
        try {
            List<Promocion> promociones = promocionDAO.obtenerPromocionesActivas();
            
            // Lógica simple de ejemplo para evitar errores
            for (Promocion promo : promociones) {
                // Aquí iría tu lógica antigua de comparación
            }
        } catch (Exception e) {
            // ✅ Solo imprimimos en consola, NO mostramos JOptionPane
            System.err.println("Error calculando reglas antiguas: " + e.getMessage());
        }
        return descuentoTotal;
    }
    
    // Método estático para compatibilidad si lo usabas
    public static double aplicarCupon(String codigo, List<edu.UPAO.proyecto.Modelo.VentaItem> items, double subtotal) {
        return 0.0; // Ya no se usa, ahora usas CuponDAO directamente
    }
}