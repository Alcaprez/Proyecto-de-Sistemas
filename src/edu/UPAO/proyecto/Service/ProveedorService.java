package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.HistorialProveedorDAO;
import edu.UPAO.proyecto.DAO.ProveedorDAO;
import edu.UPAO.proyecto.Modelo.HistorialProveedor;
import edu.UPAO.proyecto.Modelo.Proveedor;
import java.time.LocalDateTime;
import java.util.List;

public class ProveedorService {

    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final HistorialProveedorDAO historialDAO = new HistorialProveedorDAO();
    public ProveedorService() {
    }
    public List<HistorialProveedor> obtenerHistorialPorProveedor(int idProveedor) {
        return historialDAO.listarPorProveedor(idProveedor);
    }
    public List<HistorialProveedor> obtenerHistorialCompleto() {
        return historialDAO.listar();
    }
    // Listar proveedores
    public List<Proveedor> listarProveedores() {
        return proveedorDAO.listar();
    }

    // Buscar proveedor por ID
    public Proveedor buscarPorId(int id) {
        return proveedorDAO.buscarPorId(id);
    }

    // Buscar proveedor por nombre
    public Proveedor buscarPorNombre(String nombre) {
        return proveedorDAO.listar().stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
    // Buscar proveedor por RUC
    public Proveedor buscarPorRuc(String ruc) {
        return proveedorDAO.buscarPorRuc(ruc);
    }

    // Registrar un nuevo proveedor (validando RUC duplicado y formato)
    public boolean registrarProveedor(Proveedor proveedor) {
        if (!proveedor.getRuc().matches("\\d{11}")) {
            System.out.println("El RUC debe tener 11 dígitos numericos.");
            return false;
        }
        if (proveedorDAO.buscarPorRuc(proveedor.getRuc()) != null) {
            System.out.println("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
            return false;
        }
        proveedorDAO.agregar(proveedor);
         // Registrar evento automático en historial
         historialDAO.registrarEvento(
                proveedor.getIdProveedor(),
                "REGISTRO",
                "Proveedor agregado correctamente"
        );
        System.out.println("Proveedor agregado: " + proveedor.getNombre());
        return true;
    }

    // Actualizar proveedor
    public boolean actualizarProveedorCampos(int id, String nombre, String ruc, String telefono,
            String correo, String direccion, String contactoPrincipal) {
        try {
            boolean actualizado = proveedorDAO.actualizarCampos(id, nombre, ruc, telefono, correo, direccion, contactoPrincipal);
            if (!actualizado) {
                System.out.println("No se encontró un proveedor con ID: " + id);
                return false;
            }
            historialDAO.registrarEvento(
                    id,
                    "ACTUALIZACION",
                    "Se actualizaron campos del proveedor"
            );
            System.out.println("Proveedor actualizado parcialmente (ID " + id + ")");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(" Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }
    // Cambiar estado explícitamente (por RUC)
    public boolean cambiarEstadoProveedorPorRuc(String ruc, boolean activo, String motivo) {
    Proveedor p = proveedorDAO.buscarPorRuc(ruc);
        if (p != null) {
            p.setActivo(activo);
            proveedorDAO.cambiarEstado(p.getIdProveedor(), activo);

            historialDAO.registrarEvento(
                    p.getIdProveedor(),
                    activo ? "ACTIVACION" : "DESACTIVACION",
                    motivo
            );

            System.out.println("🔄 Estado cambiado para proveedor RUC " + ruc +
                    " → " + (activo ? "Activo" : "Inactivo") +
                    ". Motivo: " + motivo);
            return true;
        }
        return false;
    }
    // 🔹 Registrar compra o servicio
    public void registrarCompraOServicio(int idProveedor, String detalle, String tipoEvento,
                                         LocalDateTime fechaPeticion, LocalDateTime fechaLlegada, double monto) {
        String descripcion = detalle + " | Monto: " + monto + " | Llegada: " + fechaLlegada;
        historialDAO.registrarEvento(idProveedor, tipoEvento, descripcion);
    }

    // 🔹 Registrar evento simple
    public void registrarEventoSimple(int idProveedor, String tipo, String motivo) {
        historialDAO.registrarEvento(idProveedor, tipo, motivo);
    }
}
