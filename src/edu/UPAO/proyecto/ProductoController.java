package edu.UPAO.proyecto;

import edu.UPAO.proyecto.DAO.ProductoDAO;
import edu.UPAO.proyecto.Modelo.Producto;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProductoController {

    private ProductoDAO productoDAO;

    public ProductoController() {
        this.productoDAO = new ProductoDAO();
    }

    // ✅ MÉTODO CARGAR PRODUCTOS - SIMPLIFICADO
    public List<Producto> cargarProductos() {
        return productoDAO.listar();
    }

    // ✅ MÉTODO BUSCAR POR CÓDIGO - SIMPLIFICADO
    public Producto buscarProducto(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    // ✅ MÉTODO BUSCAR POR CÓDIGO (alias)
    public Producto buscarProductoPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    // ✅ MÉTODO ACTUALIZAR PRODUCTO - CORREGIDO
    public void actualizarProducto(String codigo, int cantidadVendida) {
        Producto producto = productoDAO.buscarPorCodigo(codigo);
        if (producto != null) {
            // Actualizar en BD
            productoDAO.actualizarStock(producto.getIdProducto(), cantidadVendida);
        }
    }

    // ✅ MÉTODO AGREGAR PRODUCTO
    public void agregarProducto(Producto p) {
        productoDAO.agregar(p);
    }

    // ✅ MÉTODO PRODUCTOS MÁS VENDIDOS
    public List<Producto> productosMasVendidos() {
        return productoDAO.productosMasVendidos();
    }

}
