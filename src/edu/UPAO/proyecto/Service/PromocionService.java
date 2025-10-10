package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.PromocionDAO;
import edu.UPAO.proyecto.Modelo.Promocion;

import java.util.List;
import java.util.Optional;

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

    public Optional<Promocion> validarCupon(String codigo) {
        return dao.buscarPorCodigo(codigo)
                 .filter(p -> {
                     // vigencia simple (igual que en Cupon.isVigente)
                     java.time.LocalDate hoy = java.time.LocalDate.now();
                     if (!p.isActivo()) return false;
                     if (p.getInicio() != null && hoy.isBefore(p.getInicio())) return false;
                     if (p.getFin() != null && hoy.isAfter(p.getFin())) return false;
                     if (p.getMaxUsos() > 0 && p.getUsos() >= p.getMaxUsos()) return false;
                     return true;
                 });
    }
}