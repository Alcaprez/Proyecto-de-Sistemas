package edu.UPAO.proyecto;

import proyectosistemasempresariales.modelo.Promocion;
import edu.UPAO.proyecto.Modelo.Producto;
import java.io.*;
import java.util.*;
import edu.UPAO.proyecto.Modelo.VentaItem;

public class PromocionController {

    private static final String FILE_NAME = "promociones.txt";

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

            double subtotal = prod.getPrecioVenta()* item.getCantidad();

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

    // Aplicar cupón al total
    public double aplicarCupon(String cuponIngresado, double total) {
        List<Promocion> promos = cargarPromociones();
        for (Promocion promo : promos) {
            if (promo.getTipo().equals("cupon")
                    && promo.getCodigo().equalsIgnoreCase(cuponIngresado)) {
                return total * (1 - promo.getDescuento() / 100);
            }
        }
        return total;
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

}
