package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProveedorDAO {

    // Genera ID con formato correcto PROV000X
    private String generarNuevoIdProveedor(Connection cn) throws SQLException {
        String sql = "SELECT MAX(id_proveedor) AS max_id FROM proveedor";
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null && maxId.startsWith("PROV")) {
                    try {
                        String numeroStr = maxId.substring(4);
                        int correlativo = Integer.parseInt(numeroStr);
                        return String.format("PROV%04d", correlativo + 1);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear ID: " + e.getMessage());
                    }
                }
            }
        }
        return "PROV0001";
    }

    public List<Proveedor> listar(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        // CORRECCIÓN AQUÍ: Seleccionamos pr.dni en vez de pe.dni para asegurar que siempre salga
        String sql = "SELECT pr.id_proveedor, pr.Razon_Social, pr.ruc, pr.direccion, pr.estado, pr.id_sucursal, "
                + "pr.dni AS dni_proveedor, "
                + // <--- CLAVE: Tomar DNI de la tabla proveedor
                "pe.nombres, pe.apellidos, pe.telefono "
                + "FROM proveedor pr "
                + "LEFT JOIN persona pe ON pr.dni = pe.dni "
                + "WHERE pr.Razon_Social LIKE ? OR pr.ruc LIKE ? OR pr.dni LIKE ?"; // Filtramos también por pr.dni

        String parametroFiltro = "%" + (filtro == null ? "" : filtro.trim()) + "%";

        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, parametroFiltro);
            ps.setString(2, parametroFiltro);
            ps.setString(3, parametroFiltro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = new Proveedor();
                    p.setIdProveedor(rs.getString("id_proveedor"));
                    p.setRazonSocial(rs.getString("Razon_Social"));
                    p.setRuc(rs.getString("ruc"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setEstado(rs.getString("estado"));
                    p.setIdSucursal(rs.getInt("id_sucursal"));

                    // Ahora el DNI nunca será nulo porque viene de la tabla proveedor
                    p.setDniAsociado(rs.getString("dni_proveedor"));

                    // Estos datos sí pueden ser nulos si la persona no existe, controlamos con ""
                    p.setNombresContacto(rs.getString("nombres") != null ? rs.getString("nombres") : "");
                    p.setApellidosContacto(rs.getString("apellidos") != null ? rs.getString("apellidos") : "");

                    // Preferencia: Si hay teléfono en persona úsalo, si no, vacío
                    p.setTelefonoContacto(rs.getString("telefono") != null ? rs.getString("telefono") : "");

                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL listar: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar(Proveedor p) {
        // Aseguramos que la persona exista o se actualice
        String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, telefono, estado) "
                + "VALUES (?, ?, '(Contacto Prov.)', ?, 'ACTIVO') "
                + "ON DUPLICATE KEY UPDATE telefono=VALUES(telefono)";

        String sqlProveedor = "INSERT INTO proveedor (id_proveedor, Razon_Social, dni, ruc, direccion, id_sucursal, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false);
            try {
                // a) Insertar Persona
                try (PreparedStatement psPe = cn.prepareStatement(sqlPersona)) {
                    psPe.setString(1, p.getDniAsociado());
                    psPe.setString(2, p.getRazonSocial());
                    psPe.setString(3, p.getTelefonoContacto());
                    psPe.executeUpdate();
                }

                // b) Insertar Proveedor
                String nuevoId = generarNuevoIdProveedor(cn);
                try (PreparedStatement psPr = cn.prepareStatement(sqlProveedor)) {
                    psPr.setString(1, nuevoId);
                    psPr.setString(2, p.getRazonSocial());
                    psPr.setString(3, p.getDniAsociado());
                    psPr.setString(4, p.getRuc());
                    psPr.setString(5, p.getDireccion());
                    psPr.setInt(6, p.getIdSucursal());
                    psPr.setString(7, p.getEstado());
                    psPr.executeUpdate();
                }

                cn.commit();
                return true;
            } catch (SQLException ex) {
                cn.rollback();
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Proveedor p) {
        // CAMBIO CRÍTICO: Usamos INSERT... ON DUPLICATE KEY en vez de UPDATE simple.
        // Esto arregla el problema de que no se guarde el teléfono si la persona no existía antes.
        String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, telefono, estado) "
                + "VALUES (?, ?, '(Contacto Prov.)', ?, 'ACTIVO') "
                + "ON DUPLICATE KEY UPDATE telefono=VALUES(telefono)";

        String sqlProveedor = "UPDATE proveedor SET Razon_Social=?, ruc=?, direccion=?, estado=? WHERE id_proveedor=?";

        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false); // Iniciar transacción
            try {
                // a) Insertar o Actualizar Persona (Aseguramos que el contacto exista)
                try (PreparedStatement psPe = cn.prepareStatement(sqlPersona)) {
                    psPe.setString(1, p.getDniAsociado());
                    // Usamos la Razón Social como nombre temporal si se crea un contacto nuevo
                    psPe.setString(2, p.getRazonSocial());
                    psPe.setString(3, p.getTelefonoContacto());
                    psPe.executeUpdate();
                }

                // b) Actualizar datos del Proveedor
                try (PreparedStatement psPr = cn.prepareStatement(sqlProveedor)) {
                    psPr.setString(1, p.getRazonSocial());
                    psPr.setString(2, p.getRuc());
                    psPr.setString(3, p.getDireccion());
                    psPr.setString(4, p.getEstado());
                    psPr.setString(5, p.getIdProveedor());
                    psPr.executeUpdate();
                }

                cn.commit(); // Confirmar cambios
                return true;
            } catch (SQLException ex) {
                cn.rollback(); // Deshacer si falla
                System.err.println("Error SQL al actualizar: " + ex.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public boolean existeRuc(String ruc) {
        String sql = "SELECT 1 FROM proveedor WHERE ruc = ?";
        try (Connection cn = new Conexion().establecerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
