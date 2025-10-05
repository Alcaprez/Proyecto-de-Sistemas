package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static List<Producto> productos = new ArrayList<>();
    private static final String FILE_NAME = "productos.csv";

    static {
        // Intentar cargar desde CSV
        List<Producto> cargados = leerDesdeCSV();
        if (cargados.isEmpty()) {
            // Si no hay CSV, usamos datos simulados
            productos.add(new Producto(
                    1, "B001", "Gaseosa Inca Kola 500ml", "Bebidas",
                    3.50, 20, 5, 0, "2025-01-01", "2026-01-01"
            ));
            productos.add(new Producto(
                    2, "S001", "Galletas Oreo", "Snacks",
                    2.00, 50, 10, 0, "2025-01-01", "2025-06-01"
            ));
            productos.add(new Producto(
                    3, "H001", "Shampoo Sedal 200ml", "Higiene",
                    7.50, 15, 3, 0, "2025-02-15", "2026-02-15"
            ));
            // Guardar iniciales en CSV
            guardarEnCSV();
        } else {
            productos = cargados;
        }
    }

    // =======================
    // Métodos CSV
    // =======================
    public static List<Producto> leerDesdeCSV() {
        List<Producto> lista = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return lista; // vacío si no existe
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim().replace("\uFEFF", ""); // limpiar BOM
                if (primeraLinea && linea.toLowerCase().startsWith("id;")) {
                    primeraLinea = false;
                    continue;
                }
                primeraLinea = false;

                String[] partes = linea.split(";");
                if (partes.length < 10) {
                    System.out.println("⚠ Línea inválida: " + linea);
                    continue;
                }

                try {
                    Producto p = new Producto(
                            Integer.parseInt(partes[0]), // idProducto
                            partes[1], // codigo
                            partes[2], // nombre
                            partes[3], // categoria
                            Double.parseDouble(partes[4]), // precioVenta
                            Integer.parseInt(partes[5]), // stock
                            Integer.parseInt(partes[6]), // stockMinimo
                            Integer.parseInt(partes[7]), // vendidos
                            partes[8], // fechaIngreso
                            partes[9] // fechaVencimiento
                    );
                    lista.add(p);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error en conversión numérica: " + linea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static void guardarEnCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Encabezado
            pw.println("id;codigo;nombre;categoria;precio;stock;stockMinimo;vendidos;fechaIngreso;fechaVencimiento");

            for (Producto p : productos) {
                pw.println(p.getIdProducto() + ";" + p.getCodigo() + ";" + p.getNombre() + ";"
                        + p.getCategoria() + ";" + p.getPrecioVenta() + ";" + p.getStock() + ";"
                        + p.getStockMinimo() + ";" + p.getVendidos() + ";"
                        + p.getFechaIngreso() + ";" + p.getFechaVencimiento());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =======================
    // Métodos ya existentes
    // =======================
    public List<Producto> listar() {
        return productos;
    }

    public Producto buscarPorId(int id) {
        return productos.stream()
                .filter(p -> p.getIdProducto() == id)
                .findFirst()
                .orElse(null);
    }

    public Producto buscarPorCodigo(String codigo) {
        return productos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
    }

    public void agregar(Producto producto) {
        productos.add(producto);
        guardarEnCSV();
    }

    public void eliminar(int id) {
        productos.removeIf(p -> p.getIdProducto() == id);
        guardarEnCSV();
    }

    public void actualizarStock(int id, int cantidadVendida) {
        Producto p = buscarPorId(id);
        if (p != null) {
            int nuevoStock = p.getStock() - cantidadVendida;
            p.setStock(Math.max(nuevoStock, 0));
            guardarEnCSV();
        }
    }

    public boolean estaBajoStock(int id) {
        Producto p = buscarPorId(id);
        return p != null && p.getStock() <= p.getStockMinimo();
    }

    public List<Producto> productosMasVendidos() {
        productos.sort((a, b) -> Integer.compare(b.getVendidos(), a.getVendidos()));
        return productos;
    }

    public Producto buscarPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (Producto p : productos) {
            if (nombre.equalsIgnoreCase(p.getNombre())) {
                return p;
            }
        }
        return null;
    }

}
