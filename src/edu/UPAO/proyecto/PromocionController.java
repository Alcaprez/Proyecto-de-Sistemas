package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.CuponDAO;
import edu.UPAO.proyecto.Modelo.Cupon;
import proyectosistemasempresariales.modelo.Promocion;
import edu.UPAO.proyecto.Modelo.Producto;
import java.io.*;
import java.util.*;
import edu.UPAO.proyecto.Modelo.VentaItem;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;

public class PromocionController {

    private static final String FILE_NAME = "promociones.txt";

    private CuponDAO cuponDAO;

    public PromocionController() {
        cuponDAO = new CuponDAO();
    }

    public double aplicarCupon(String codigo, double total) {
        List<Cupon> cupones = cuponDAO.cargarCupones();
        LocalDate hoy = LocalDate.now();

        for (Cupon c : cupones) {
            System.out.println("📌 Probando cupón: " + c.getCodigo()
                    + " | inicio=" + c.getInicio()
                    + " | fin=" + c.getFin()
                    + " | hoy=" + hoy);

            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                if (!c.isActivo()) {
                    System.out.println("❌ Cupón inactivo");
                    return total;
                }
                if (hoy.isBefore(c.getInicio()) || hoy.isAfter(c.getFin())) {
                    System.out.println("❌ Fuera de rango de fechas");
                    return total;
                }
                System.out.println("✅ Cupón válido, aplica " + (c.getDescuento() * 100) + "%");
                return total * (1 - c.getDescuento());
            }
        }

        return total; // cupón no encontrado
    }

    public List<Promocion> cargarPromociones() {
        List<Promocion> promos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 3) {
                    promos.add(new Promocion(data[0], data[1], Double.parseDouble(data[2])));
                } else if (data.length == 4) {
                    promos.add(new Promocion(data[0], data[1], Double.parseDouble(data[2]), Integer.parseInt(data[3])));
                }
            }
        } catch (IOException e) {
            System.out.println("Archivo de promociones vacío o no encontrado.");
        }
        return promos;
    }

    // Guardar lista de promociones en archivo
    public void guardarPromociones(List<Promocion> promos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Promocion p : promos) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

// Nuevo método para aplicar promociones automáticas por cantidad mínima
    public void aplicarPromocionesProductos(List<VentaItem> carrito, List<Producto> productos) {
        List<Promocion> promos = cargarPromociones();

        for (VentaItem item : carrito) {
            // buscar producto completo
            Producto prod = null;
            for (Producto p : productos) {
                if (p.getNombre().equalsIgnoreCase(item.getNombre())) {
                    prod = p;
                    break;
                }
            }
            if (prod == null) {
                continue;
            }

            double subtotal = prod.getPrecioVenta() * item.getCantidad();

            for (Promocion promo : promos) {
                if (promo.getTipo().equals("producto")
                        && promo.getCodigo().equalsIgnoreCase(prod.getNombre())
                        && item.getCantidad() >= promo.getCantidadMinima()) {

                    subtotal = subtotal * (1 - promo.getDescuento() / 100.0);
                }
            }

            item.setSubtotal(subtotal); // actualizar subtotal del item
        }
    }

    public void aplicarPromocionesAutomaticas(List<VentaItem> carrito, List<Producto> productos) {
        List<Promocion> promos = cargarPromociones();

        for (VentaItem item : carrito) {
            for (Promocion promo : promos) {
                if (promo.getTipo().equals("producto")
                        && item.getNombre().equalsIgnoreCase(promo.getCodigo())) {

                    if (item.getCantidad() >= promo.getCantidadMinima()) {
                        // Aplicar descuento
                        double precioOriginal = 0;
                        for (Producto p : productos) {
                            if (p.getNombre().equalsIgnoreCase(item.getNombre())) {
                                precioOriginal = p.getPrecioVenta();
                                break;
                            }
                        }
                        double nuevoSubtotal = item.getCantidad() * precioOriginal * (1 - promo.getDescuento() / 100);
                        item.setSubtotal(nuevoSubtotal);
                    }
                }
            }
        }
    }
    
        // Descuentos automáticos por reglas de negocio
    public double calcularDescuentoReglas(DefaultTableModel carrito) {
        double descuento = 0.0;

        for (int i = 0; i < carrito.getRowCount(); i++) {
            String nombreProducto = carrito.getValueAt(i, 0).toString(); // ✅ columna nombre
            int cantidad = Integer.parseInt(carrito.getValueAt(i, 1).toString()); // ✅ columna cantidad
            double subtotal = Double.parseDouble(carrito.getValueAt(i, 3).toString()); // ✅ columna subtotal

            // 🔥 Regla: si son 6 o más gaseosas → 10% descuento
            if (nombreProducto.toLowerCase().contains("gaseosa") && cantidad >= 6) {
                descuento += subtotal * 0.10;
            }

            // 👉 Aquí puedes ir agregando más reglas, ejemplo:
            // if (nombreProducto.equalsIgnoreCase("Arroz 5kg") && cantidad >= 3) {
            //     descuento += subtotal * 0.15;
            // }
        }

        return descuento;
    }
    

}
