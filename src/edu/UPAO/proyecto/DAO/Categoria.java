package edu.UPAO.proyecto.DAO; // O package edu.UPAO.proyecto.Modelo; si lo mueves

import java.util.ArrayList;
import java.util.List;

public class Categoria {

    private int id;
    private String nombre;
    private String descripcion;
    private List<String> subcategorias;

    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategorias = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<String> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public void agregarSubcategoria(String subcategoria) {
        if (!this.subcategorias.contains(subcategoria)) {
            this.subcategorias.add(subcategoria);
        }
    }

    public void eliminarSubcategoria(String subcategoria) {
        this.subcategorias.remove(subcategoria);
    }

    public int getCantidadSubcategorias() {
        return subcategorias.size();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
