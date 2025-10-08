package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Proveedor;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class ProveedorDAO {

    private static final List<Proveedor> proveedores = new ArrayList<>();
    private static int contadorId = 1; // Generador de IDs únicos

    // Agregar proveedor (con ID y fecha automáticos)
    public void agregar(Proveedor proveedor) {
        // Evitar duplicados por RUC
        if (buscarPorRuc(proveedor.getRuc()) != null) {
            throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
        }
        proveedor.setIdProveedor(contadorId++);
        if (proveedor.getFechaRegistro() == null) {
            proveedor.setFechaRegistro(LocalDate.now());
        }
        proveedores.add(proveedor);
    }

    public List<Proveedor> listar() {
        return new ArrayList<>(proveedores);
    }

    public Proveedor buscarPorId(int id) {
        return proveedores.stream()
                .filter(p -> p.getIdProveedor() == id)
                .findFirst()
                .orElse(null);
    }

    public Proveedor buscarPorRuc(String ruc) {
        return proveedores.stream()
                .filter(p -> p.getRuc().equalsIgnoreCase(ruc))
                .findFirst()
                .orElse(null);
    }

    public boolean actualizarCampos(int id, String nombre, String ruc, String telefono, String correo,
        String direccion, String contactoPrincipal) {
    Proveedor proveedor = buscarPorId(id);
    if (proveedor == null) {
        return false;
    }

    if (nombre != null && !nombre.isBlank()) proveedor.setNombre(nombre);
    if (ruc != null && !ruc.isBlank()) {
        Proveedor existente = buscarPorRuc(ruc);
        if (existente != null && existente.getIdProveedor() != id) {
            throw new IllegalArgumentException("Ya existe otro proveedor con el RUC: " + ruc);
        }
        proveedor.setRuc(ruc);
    }
    if (telefono != null && !telefono.isBlank()) proveedor.setTelefono(telefono);
    if (correo != null && !correo.isBlank()) proveedor.setCorreo(correo);
    if (direccion != null && !direccion.isBlank()) proveedor.setDireccion(direccion);
    if (contactoPrincipal != null && !contactoPrincipal.isBlank()) proveedor.setContactoPrincipal(contactoPrincipal);

    return true;
}

    // Cambiar estado explícitamente
    public void cambiarEstado(int id, boolean activo) {
        Proveedor p = buscarPorId(id);
        if (p != null) {
            p.setActivo(activo);
        }
    }
}
