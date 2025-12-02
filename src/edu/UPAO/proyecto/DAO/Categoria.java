package edu.UPAO.proyecto.DAO; // Asegúrate que el package sea el correcto según tu proyecto

import java.util.ArrayList;
import java.util.List;

public class Categoria {

    private int id;
    private String nombre;
    // Eliminamos 'descripcion' porque no existe en la base de datos
    private List<String> subcategorias;

    // Constructor corregido: solo pide ID y Nombre
    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    // Eliminamos los getters y setters de descripcion

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