package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.ProveedorDAO;
import edu.UPAO.proyecto.Modelo.HistorialProveedor;
import edu.UPAO.proyecto.Modelo.Proveedor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProveedorService {

    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final List<HistorialProveedor> historial = new ArrayList<>();
    public ProveedorService() {
        // Datos de prueba (ID se asigna automático en el DAO)
        registrarProveedor(new Proveedor("Distribuidora Norte SAC", "20123456789", "Calle A 123", "044-123456", "contacto@nortesac.com", "Carlos Díaz", true));
        registrarProveedor(new Proveedor("Servicios Industriales del Sur", "20987654321", "Av. B 456", "054-654321", "ventas@indsur.com", "María López", true));
        registrarProveedor(new Proveedor("Insumos Médicos Andinos", "20456789123", "Jr. C 789", "01-4567890", "soporte@insumed.com", "José Ramos", false));
        registrarProveedor(new Proveedor("TecnoGlobal Importaciones", "20111222334", "Av. D 101", "01-7654321", "info@tecnoglobal.pe", "Ana Torres", true));
        registrarProveedor(new Proveedor("AgroExport Trujillo", "20678912345", "Calle E 202", "044-987654", "ventas@agroexporttrujillo.com", "Luis Gómez", true));
    }
    public List<HistorialProveedor> obtenerHistorialPorProveedor(int idProveedor) {
    return historial.stream()
            .filter(h -> h.getIdProveedor() == idProveedor)
            .toList();
    }
    public List<HistorialProveedor> obtenerHistorialCompleto() {
    return new ArrayList<>(historial);
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
            System.out.println("Proveedor actualizado parcialmente (ID " + id + ")");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(" Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }
    private void registrarEventoHistorial(int idProveedor, String tipoEvento, String detalle) {
    historial.add(new HistorialProveedor(idProveedor, tipoEvento, detalle));
    System.out.println("Historial registrado → " + tipoEvento + " | " + detalle);
    }
    // Cambiar estado explícitamente (por RUC)
    public boolean cambiarEstadoProveedorPorRuc(String ruc, boolean activo, String motivo) {
    Proveedor p = proveedorDAO.buscarPorRuc(ruc);
    if (p != null) {
        p.setActivo(activo);
        // Registrar evento en historial
        String tipoEvento = activo ? "ACTIVACION" : "DESACTIVACION";
        registrarEventoHistorial(p.getIdProveedor(), tipoEvento, motivo);

        System.out.println("Estado cambiado para proveedor RUC " + ruc + " → " 
                           + (activo ? "Activo" : "Inactivo") 
                           + ". Motivo: " + motivo);
        return true;
    }
    return false;
    }
    public void registrarCompraOServicio(int idProveedor, String detalle, String tipoEvento,
                                     LocalDateTime fechaPeticion, LocalDateTime fechaLlegada, double monto) {
    historial.add(new HistorialProveedor(idProveedor, tipoEvento, detalle, fechaPeticion, fechaLlegada, monto));
    }
    public void registrarEventoSimple(int idProveedor, String tipo, String motivo) {
    historial.add(new HistorialProveedor(idProveedor, tipo, motivo));
}
}
