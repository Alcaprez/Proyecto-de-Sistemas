package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.modelo.Sucursal;
import edu.UPAO.proyecto.modelo.UsuarioADM;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

public class UsuarioDAOADM {

    // 1. LISTAR TODOS (CORREGIDO: Ahora trae el NOMBRE COMPLETO)
    public List<UsuarioADM> listar() {
        return ejecutarConsulta(
            "SELECT u.id_usuario, e.dni, " +
            "CONCAT(p.nombres, ' ', p.apellidos) AS nombre_completo, " + // <--- AGREGADO
            "e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
            "FROM usuario u " +
            "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
            "INNER JOIN persona p ON e.dni = p.dni " + // <--- JOIN CON PERSONA
            "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal"
        );
    }

    // 2. LISTAR SOLO SIN SUCURSAL (CORREGIDO: Ahora trae el NOMBRE COMPLETO)
    public List<UsuarioADM> listarSinSucursal() {
        return ejecutarConsulta(
            "SELECT u.id_usuario, e.dni, " +
            "CONCAT(p.nombres, ' ', p.apellidos) AS nombre_completo, " + // <--- AGREGADO
            "e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
            "FROM usuario u " +
            "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
            "INNER JOIN persona p ON e.dni = p.dni " + // <--- JOIN CON PERSONA
            "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
            "WHERE e.id_sucursal IS NULL OR e.id_sucursal = 0"
        );
    }
    
    // 3. LISTAR USUARIOS GLOBAL (Para la vista de tabla general)
    public List<Object[]> listarUsuariosGlobal() {
        List<Object[]> lista = new ArrayList<>();
        // Reutilizamos la lógica de listar() pero retornamos Object[] para la tabla
        List<UsuarioADM> usuarios = listar();
        for (UsuarioADM u : usuarios) {
            lista.add(new Object[]{
                u.getId(),
                u.getNombre(), // Ahora sí trae el nombre real
                u.getNombreRol(),
                u.getNombreSucursal(),
                u.getEstatus()
            });
        }
        return lista;
    }

    // 4. MÉTODO COMÚN DE MAPEO
    private List<UsuarioADM> ejecutarConsulta(String sql) {
        List<UsuarioADM> lista = new ArrayList<>();
        Set<String> dnisProcesados = new HashSet<>();
        
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String dni = rs.getString("dni");
                
                // Evitar duplicados visuales
                if (dni != null && dnisProcesados.contains(dni)) {
                    continue; 
                }
                if (dni != null) dnisProcesados.add(dni);

                UsuarioADM usu = new UsuarioADM();
                usu.setId(rs.getInt("id_usuario"));
                
                // --- CORRECCIÓN CLAVE ---
                // Antes: "DNI: " + dni
                // Ahora: Nombre real de la base de datos
                String nombreReal = rs.getString("nombre_completo");
                if (nombreReal == null || nombreReal.trim().isEmpty()) {
                    usu.setNombre("DNI: " + dni); // Fallback solo si no hay nombre
                } else {
                    usu.setNombre(nombreReal); 
                }
                // ------------------------
                
                usu.setIdRol(0);
                usu.setNombreRol(rs.getString("rol"));
                usu.setIdSucursal(rs.getInt("id_sucursal"));
                
                String nomSuc = rs.getString("nombre_sucursal");
                if (nomSuc == null || nomSuc.isEmpty()) {
                     if (usu.getIdSucursal() == 1) nomSuc = "Tienda Central";
                     else if (usu.getIdSucursal() == 2) nomSuc = "Sucursal Norte";
                     else if (usu.getIdSucursal() == 3) nomSuc = "Sucursal Sur";
                     else nomSuc = "Sin Asignar";
                }
                usu.setNombreSucursal(nomSuc);
                usu.setEstatus(rs.getString("estado"));
                usu.setUltimoCambio(LocalDateTime.now());
                lista.add(usu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 5. LISTAR SUCURSALES
    public List<Sucursal> listarSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre_sucursal FROM sucursal WHERE estado = 'ABIERTO' OR estado = 'ACTIVO'";
        
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while(rs.next()){
                int id = rs.getInt("id_sucursal");
                String nombre = rs.getString("nombre_sucursal");
                lista.add(new Sucursal(id, nombre));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // 6. ACTUALIZAR SUCURSAL
    public boolean actualizarSucursal(int idUsuario, Integer idSucursal) {
        String sql = "UPDATE empleado e " +
                     "INNER JOIN usuario u ON e.id_empleado = u.id_empleado " +
                     "SET e.id_sucursal = ? " +
                     "WHERE u.id_usuario = ?";
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (idSucursal == null || idSucursal == 0) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, idSucursal);
            
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    // 7. ACTUALIZAR ROL
    public boolean actualizarRol(int idUsuario, String nuevoRol) {
        String sql = "UPDATE empleado e " +
                     "INNER JOIN usuario u ON e.id_empleado = u.id_empleado " +
                     "SET e.rol = ? " +
                     "WHERE u.id_usuario = ?";
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoRol);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 8. LISTAR EMPLEADOS PARA ASIGNACIÓN (Helper para el panel de asignación)
    public List<Object[]> listarEmpleadosSucursal() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.id_empleado, CONCAT(p.nombres, ' ', p.apellidos), " +
                     "e.rol, s.nombre_sucursal, e.id_sucursal " +
                     "FROM empleado e " +
                     "INNER JOIN persona p ON e.dni = p.dni " +
                     "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "WHERE e.estado = 'ACTIVO'";

        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while(rs.next()) {
                lista.add(new Object[]{
                    rs.getString(1), 
                    rs.getString(2), 
                    rs.getString(3), 
                    rs.getString(4) != null ? rs.getString(4) : "SIN ASIGNAR"
                });
            }
        } catch(Exception e) { e.printStackTrace(); }
        return lista;
    }
    
    // Helper
    public int obtenerIdSucursal(String nombre) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre_sucursal = ?";
        try (Connection con = new Conexion().establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) {}
        return 0;
    }
}