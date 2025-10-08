package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.Modelo.Producto;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProductoController {

    private static final String FILE_NAME = "productos.csv";

    // Guardar todos los productos
// MÉTODO CORREGIDO - usar el DAO existente
    public void guardarProductos(List<Producto> productos) {
        // ✅ Usar el método existente del DAO que ya funciona correctamente
        // Esto actualizará la lista estática del DAO y guardará en CSV
        ProductoDAO.guardarListaProductos(productos);
    }

    // Actualizar stock y vendidos tras una compra
    public void actualizarProducto(String codigo, int cantidadVendida) {
        List<Producto> productos = cargarProductos();
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigo)) {
                p.setStock(p.getStock() - cantidadVendida);
                p.setVendidos(p.getVendidos() + cantidadVendida);
                break;
            }
        }
        guardarProductos(productos);
    }

    // Buscar producto por código
    public Producto buscarProducto(String codigo) {
        List<Producto> productos = cargarProductos();
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        return null;
    }

    private ProductoDAO productoDAO = new ProductoDAO();

    public List<Producto> cargarProductos() {
        return productoDAO.listar();
    }

    public void agregarProducto(Producto p) {
        productoDAO.agregar(p);
    }

    public void actualizarProducto(int id, int cantidadVendida) {
        productoDAO.actualizarStock(id, cantidadVendida);
    }

    public Producto buscarProductoPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    public List<Producto> productosMasVendidos() {
        return productoDAO.productosMasVendidos();
    }
}
