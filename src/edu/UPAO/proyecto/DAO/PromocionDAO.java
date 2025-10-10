package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Cupon;
import edu.UPAO.proyecto.Modelo.Promocion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter para mantener compatibilidad con PromocionService.
 * Internamente usa CuponDAO y mapea entre Promocion <-> Cupon.
 */
public class PromocionDAO {

    // ---------- Singleton ----------
    private static final PromocionDAO INSTANCE = new PromocionDAO();
    public static PromocionDAO getInstance() { return INSTANCE; }
    private PromocionDAO() {}

    // ---------- API esperada por tu Service ----------
    public List<Promocion> listar() {
        List<Cupon> cupones = CuponDAO.listar();
        List<Promocion> out = new ArrayList<>();
        for (Cupon c : cupones) {
            out.add(toPromocion(c));
        }
        return out;
    }

    public void guardar(Promocion p) {
        Cupon c = toCupon(p);
        CuponDAO.upsert(c);
    }

    public void eliminar(String codigo) {
        CuponDAO.eliminar(codigo);
    }

    public Optional<Promocion> buscarPorCodigo(String codigo) {
        return CuponDAO.buscarPorCodigo(codigo).map(this::toPromocion);
    }

    // ---------- Mappers ----------
    private Promocion toPromocion(Cupon c) {
        return new Promocion(
                c.getCodigo(),
                (c.getTipo() == Cupon.TipoDescuento.PERCENT
                        ? Promocion.Tipo.PERCENT
                        : Promocion.Tipo.FLAT),
                c.getValor(),
                c.getSkuAplicado(),
                c.getMinimoCompra(),
                c.getInicio(),
                c.getFin(),
                c.isActivo(),
                c.getMaxUsos(),
                c.getUsos()
        );
    }

    private Cupon toCupon(Promocion p) {
        Cupon.TipoDescuento tipo = (p.getTipo() == Promocion.Tipo.PERCENT)
                ? Cupon.TipoDescuento.PERCENT
                : Cupon.TipoDescuento.FLAT;

        return new Cupon(
                p.getCodigo(),
                tipo,
                p.getValor(),
                p.getSkuAplicado(),
                p.getMinimoCompra(),
                p.getInicio(),
                p.getFin(),
                p.isActivo(),
                p.getMaxUsos(),
                p.getUsos()
        );
    }
}
