package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.Modelo.Producto;
import edu.UPAO.proyecto.Util.Validaciones;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private ProductoDAO productoDAO = new ProductoDAO();

    // Listar todos los productos
    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }

    // Buscar producto por ID
    public Producto buscarPorId(int id) {
        return productoDAO.buscarPorId(id);
    }

    // Buscar producto por código
    public Producto buscarPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    // ✅ Validar producto antes de agregarlo
    private List<String> validarProducto(Producto p) {
        List<String> errores = new ArrayList<>();
        if (p == null) {
            errores.add("El producto no puede ser nulo.");
            return errores;
        }

        if (p.getNombre() == null || p.getNombre().isBlank() ||
                !Validaciones.isAlphaSpaceAccents(p.getNombre())) {
            errores.add("Nombre inválido: solo letras y espacios.");
        }

        if (p.getCodigo() == null || !Validaciones.isAlphanumeric(p.getCodigo())) {
            errores.add("Código inválido: solo letras, números y guiones.");
        }

        if (!Validaciones.isPositiveNumber(p.getPrecioVenta())) {
            errores.add("Precio de venta debe ser mayor que 0.");
        }
        
        if (!Validaciones.isNonNegativeInt(p.getStock())) {
            errores.add("Stock no puede ser negativo.");
        }

        if (!Validaciones.isNonNegativeInt(p.getStockMinimo())) {
            errores.add("Stock mínimo no puede ser negativo.");
        }

        // Validar fechas
        if (p.getFechaIngreso() != null && !Validaciones.isValidDate(p.getFechaIngreso(), "yyyy-MM-dd")) {
            errores.add("Fecha de ingreso inválida (formato: yyyy-MM-dd).");
        }

        if (p.getFechaVencimiento() != null && !Validaciones.isValidDate(p.getFechaVencimiento(), "yyyy-MM-dd")) {
            errores.add("Fecha de vencimiento inválida (formato: yyyy-MM-dd).");
        }

        // Comparar fechas
        if (p.getFechaIngreso() != null && p.getFechaVencimiento() != null &&
                Validaciones.isValidDate(p.getFechaIngreso(),"yyyy-MM-dd") &&
                Validaciones.isValidDate(p.getFechaVencimiento(),"yyyy-MM-dd")) {
            LocalDate ing = LocalDate.parse(p.getFechaIngreso(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate ven = LocalDate.parse(p.getFechaVencimiento(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (ven.isBefore(ing)) {
                errores.add("La fecha de vencimiento no puede ser anterior a la de ingreso.");
            }
        }

        return errores;
    }

    // Agregar producto con validación completa
    public boolean agregarProducto(Producto producto) {
        // Validar campos
        List<String> errores = validarProducto(producto);
        if (!errores.isEmpty()) {
            System.out.println("❌ No se pudo agregar el producto. Errores:");
            errores.forEach(e -> System.out.println(" - " + e));
            return false;
        }

        // Validar duplicados
        Producto existente = productoDAO.buscarPorCodigo(producto.getCodigo());
        if (existente != null) {
            System.out.println("⚠️ Ya existe un producto con el código: " + producto.getCodigo());
            return false;
        }

        productoDAO.agregar(producto);
        System.out.println("✅ Producto agregado: " + producto.getNombre());
        return true;
    }

    // Eliminar producto por ID
    public void eliminarProducto(int id) {
        productoDAO.eliminar(id);
        System.out.println("🗑 Producto eliminado con ID: " + id);
    }

    // Verificar si un producto está en stock mínimo
    public boolean verificarStockMinimo(int id) {
        boolean bajoStock = productoDAO.estaBajoStock(id);
        if (bajoStock) {
            Producto p = productoDAO.buscarPorId(id);
            System.out.println("⚠️ El producto " + p.getNombre() +
                    " está en stock mínimo (" + p.getStock() + ").");
        }
        return bajoStock;
    }
}

