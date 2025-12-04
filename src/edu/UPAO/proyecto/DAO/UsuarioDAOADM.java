package edu.UPAO.proyecto.dao;

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

    // 1. LISTAR TODOS (CON EL NOMBRE CORRECTO)
    public List<UsuarioADM> listar() {
        // CAMBIO CLAVE: Usamos 'p.nombres' (plural)
        return ejecutarConsulta(
            "SELECT u.id_usuario, p.nombres, p.apellidos, e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
            "FROM usuario u " +
            "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
            "INNER JOIN persona p ON e.dni = p.dni " + 
            "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal"
        );
    }

    // 2. LISTAR SOLO SIN SUCURSAL
    public List<UsuarioADM> listarSinSucursal() {
        return ejecutarConsulta(
            "SELECT u.id_usuario, p.nombres, p.apellidos, e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
            "FROM usuario u " +
            "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
            "INNER JOIN persona p ON e.dni = p.dni " +
            "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
            "WHERE e.id_sucursal IS NULL"
        );
    }

    // 3. MÉTODO COMÚN
    private List<UsuarioADM> ejecutarConsulta(String sql) {
        List<UsuarioADM> lista = new ArrayList<>();
        Set<Integer> idsProcesados = new HashSet<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                if (idsProcesados.contains(id)) continue;
                idsProcesados.add(id);

                UsuarioADM usu = new UsuarioADM();
                usu.setId(id);
                
                // --- CORRECCIÓN: Usamos 'nombres' ---
                String nombre = rs.getString("nombres"); // Ahora sí coincidirá con tu BD
                String apellido = rs.getString("apellidos");
                
                if (nombre == null) nombre = "SinNombre";
                if (apellido == null) apellido = "";
                
                usu.setNombre(nombre + " " + apellido); 
                // ------------------------------------
                
                usu.setIdRol(0);
                usu.setNombreRol(rs.getString("rol"));
                
                // Lógica Sucursal
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
            System.out.println("Error SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 4. LISTAR SUCURSALES
    public List<Sucursal> listarSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre_sucursal FROM sucursal";
        Set<Integer> idsSuc = new HashSet<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while(rs.next()){
                int id = rs.getInt("id_sucursal");
                if(idsSuc.contains(id)) continue;
                idsSuc.add(id);
                
                String nombre = rs.getString("nombre_sucursal");
                lista.add(new Sucursal(id, nombre, "", "")); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // 5. ACTUALIZAR SUCURSAL
    public boolean actualizarSucursal(int idUsuario, Integer idSucursal) {
        String sql = "UPDATE empleado e INNER JOIN usuario u ON e.id_empleado = u.id_empleado SET e.id_sucursal = ? WHERE u.id_usuario = ?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (idSucursal == null || idSucursal == 0) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, idSucursal);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
    
    // 6. ACTUALIZAR ROL
    public boolean actualizarRol(int idUsuario, String nuevoRol) {
        String sql = "UPDATE empleado e INNER JOIN usuario u ON e.id_empleado = u.id_empleado SET e.rol = ? WHERE u.id_usuario = ?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoRol);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}