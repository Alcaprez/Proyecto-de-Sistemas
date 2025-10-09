package edu.UPAO.proyecto.Service;


import edu.UPAO.proyecto.DAO.PromocionDAO;

import java.util.*;
import proyectosistemasempresariales.modelo.Promocion;

public class PromocionService {
    private final PromocionDAO dao = PromocionDAO.getInstance();

    public List<Promocion> listar() {
        return dao.listar();
    }

    public void guardar(Promocion p) {
        dao.guardar(p);
    }

    public void eliminar(String codigo) {
        dao.eliminar(codigo);
    }

    // Verifica si existe un cupón válido
    public Optional<Promocion> validarCupon(String codigo) {
        return dao.buscarPorCodigo(codigo)
                  .filter(p -> p.getTipo().equalsIgnoreCase("cupon"));
    }
    // Aplica descuentos por producto según cantidad
    public double aplicarDescuentoPorProducto(String producto, int cantidad, double subtotal) {
        return dao.listar().stream()
            .filter(p -> p.getTipo().equalsIgnoreCase("producto"))
            .filter(p -> p.getCodigo().equalsIgnoreCase(producto))
            .filter(p -> cantidad >= p.getCantidadMinima())
            .mapToDouble(p -> subtotal * (p.getDescuento() / 100.0))
            .sum();
    }
 

}
