package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    // ====================================================================
    // 1. MÉTODO AUXILIAR PARA GENERAR ID CHAR(8) (Ej: PR000001)
    // ====================================================================
    private String generarNuevoIdProveedor(Connection cn) throws SQLException {
        String sql = "SELECT MAX(id_proveedor) AS max_id FROM proveedor";
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString("max_id") != null) {
                String maxId = rs.getString("max_id");
                // Asumimos formato "PR" + 6 dígitos. Extraemos los dígitos.
                try {
                    int correlativo = Integer.parseInt(maxId.substring(2));
                    return String.format("PR%06d", correlativo + 1);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                     // Si el ID actual no tiene el formato esperado, reiniciamos o manejamos el error
                     return "PR000001"; 
                }
            }
        }
        return "PR000001"; // Primer proveedor si la tabla está vacía
    }

    // ====================================================================
    // 2. LISTAR y BUSCAR (Combinados)
    // ====================================================================
    public List<Proveedor> listar(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        // Hacemos JOIN con persona para obtener nombres y teléfonos del contacto
        String sql = "SELECT pr.id_proveedor, pr.Razon_Social, pr.ruc, pr.direccion, pr.estado, pr.id_sucursal, " +
                     "pe.dni, pe.nombres, pe.apellidos, pe.telefono " +
                     "FROM proveedor pr " +
                     "LEFT JOIN persona pe ON pr.dni = pe.dni " +
                     "WHERE pr.Razon_Social LIKE ? OR pr.ruc LIKE ? OR pe.dni LIKE ?";
        
        String parametroFiltro = "%" + (filtro == null ? "" : filtro.trim()) + "%";
        
        try (Connection cn = new Conexion().establecerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, parametroFiltro);
            ps.setString(2, parametroFiltro);
            ps.setString(3, parametroFiltro);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = new Proveedor();
                    // Datos de la tabla proveedor
                    p.setIdProveedor(rs.getString("id_proveedor"));
                    p.setRazonSocial(rs.getString("Razon_Social"));
                    p.setRuc(rs.getString("ruc"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setEstado(rs.getString("estado"));
                    p.setIdSucursal(rs.getInt("id_sucursal"));
                    // Datos de la tabla persona (asociado)
                    p.setDniAsociado(rs.getString("dni"));
                    p.setNombresContacto(rs.getString("nombres"));
                    p.setApellidosContacto(rs.getString("apellidos"));
                    p.setTelefonoContacto(rs.getString("telefono"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    // ====================================================================
    // 3. GUARDAR (Transacción Persona + Proveedor)
    // ====================================================================
    public boolean guardar(Proveedor p) {
        // NOTA IMPORTANTE SOBRE LA DB vs FORMULARIO:
        // La tabla 'persona' EXIGE 'nombres' y 'apellidos'. Tu formulario NO los pide.
        // SOLUCIÓN TEMPORAL: Usaremos la 'Razon Social' como 'nombres' y un texto fijo como 'apellidos'
        // para cumplir con la base de datos. Lo ideal sería añadir esos campos al formulario.
        
        String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, telefono, estado) "
                          + "VALUES (?, ?, '(Contacto Prov.)', ?, 'ACTIVO') " 
                          + "ON DUPLICATE KEY UPDATE telefono=VALUES(telefono)";
        
        String sqlProveedor = "INSERT INTO proveedor (id_proveedor, Razon_Social, dni, ruc, direccion, id_sucursal, estado) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false); // INICIAR TRANSACCIÓN
            
            try {
                // a) Insertar/Actualizar Persona
                try (PreparedStatement psPe = cn.prepareStatement(sqlPersona)) {
                    psPe.setString(1, p.getDniAsociado());
                    // Usamos Razón Social como nombre temporalmente porque el formulario no pide nombre de contacto
                    psPe.setString(2, p.getRazonSocial()); 
                    psPe.setString(3, p.getTelefonoContacto());
                    psPe.executeUpdate();
                }

                // b) Generar ID y Insertar Proveedor
                String nuevoId = generarNuevoIdProveedor(cn);
                try (PreparedStatement psPr = cn.prepareStatement(sqlProveedor)) {
                    psPr.setString(1, nuevoId);
                    psPr.setString(2, p.getRazonSocial());
                    psPr.setString(3, p.getDniAsociado());
                    psPr.setString(4, p.getRuc());
                    psPr.setString(5, p.getDireccion());
                    psPr.setInt(6, p.getIdSucursal()); // Sucursal del gerente que registra
                    psPr.setString(7, p.getEstado());
                    psPr.executeUpdate();
                }
                
                cn.commit(); // CONFIRMAR
                return true;
                
            } catch (SQLException ex) {
                cn.rollback(); // DESHACER
                System.err.println("Rollback por error en transacción: " + ex.getMessage());
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }

    // ====================================================================
    // 4. ACTUALIZAR (Transacción)
    // ====================================================================
    public boolean actualizar(Proveedor p) {
        String sqlPersona = "UPDATE persona SET telefono=? WHERE dni=?";
        String sqlProveedor = "UPDATE proveedor SET Razon_Social=?, ruc=?, direccion=?, estado=? WHERE id_proveedor=?";
        
        try (Connection cn = new Conexion().establecerConexion()) {
            cn.setAutoCommit(false);
            try {
                // a) Actualizar Persona (Solo teléfono según formulario actual)
                try (PreparedStatement psPe = cn.prepareStatement(sqlPersona)) {
                    psPe.setString(1, p.getTelefonoContacto());
                    psPe.setString(2, p.getDniAsociado());
                    psPe.executeUpdate();
                }
                // b) Actualizar Proveedor
                try (PreparedStatement psPr = cn.prepareStatement(sqlProveedor)) {
                    psPr.setString(1, p.getRazonSocial());
                    psPr.setString(2, p.getRuc());
                    psPr.setString(3, p.getDireccion());
                    psPr.setString(4, p.getEstado());
                    psPr.setString(5, p.getIdProveedor());
                    psPr.executeUpdate();
                }
                cn.commit();
                return true;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    // 5. Validaciones auxiliares
    public boolean existeRuc(String ruc) {
        String sql = "SELECT 1 FROM proveedor WHERE ruc = ?";
        try (Connection cn = new Conexion().establecerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             ps.setString(1, ruc);
             return rs.next();
        } catch (SQLException e) { return false; }
    }
    
    // Validar si el DNI ya existe en persona (para no sobrescribir nombres si no se desea)
    public boolean existeDniPersona(String dni) {
         String sql = "SELECT 1 FROM persona WHERE dni = ?";
         try (Connection cn = new Conexion().establecerConexion();
              PreparedStatement ps = cn.prepareStatement(sql)) {
             ps.setString(1, dni);
             try(ResultSet rs = ps.executeQuery()) { return rs.next(); }
         } catch (SQLException e) { return false; }
    }
}