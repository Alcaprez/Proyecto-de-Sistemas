
package edu.UPAO.proyecto.dao;

import edu.UPAO.proyecto.modelo.Sucursal;
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

    // 1. LISTAR TODOS
    public List<UsuarioADM> listar() {
        return ejecutarConsulta("SELECT u.id_usuario, e.dni, e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
                                "FROM usuario u " +
                                "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
                                "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal");
    }

    // 2. LISTAR SOLO SIN SUCURSAL
    public List<UsuarioADM> listarSinSucursal() {
        return ejecutarConsulta("SELECT u.id_usuario, e.dni, e.rol, e.id_sucursal, s.nombre_sucursal, u.estado " +
                                "FROM usuario u " +
                                "INNER JOIN empleado e ON u.id_empleado = e.id_empleado " +
                                "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                                "WHERE e.id_sucursal IS NULL");
    }

    // 3. MÉTODO COMÚN: AHORA FILTRA POR DNI (CONTENIDO VISUAL)
    private List<UsuarioADM> ejecutarConsulta(String sql) {
        List<UsuarioADM> lista = new ArrayList<>();
        
        // CAMBIO CLAVE: Usamos un Set de Strings para guardar DNIs
        Set<String> dnisProcesados = new HashSet<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Obtenemos el DNI primero
                String dni = rs.getString("dni");
                
                // SI YA VIMOS ESTE DNI, SALTAMOS AL SIGUIENTE.
                // Esto elimina duplicados visuales aunque tengan IDs diferentes.
                if (dni != null && dnisProcesados.contains(dni)) {
                    continue; 
                }
                if (dni != null) dnisProcesados.add(dni);

                UsuarioADM usu = new UsuarioADM();
                usu.setId(rs.getInt("id_usuario"));
                usu.setNombre("DNI: " + dni); 
                
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
            e.printStackTrace();
        }
        return lista;
    }

    // 4. LISTAR SUCURSALES
    public List<Sucursal> listarSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre_sucursal FROM sucursal";
        Set<Integer> idsSuc = new HashSet<>(); // Filtro ID para sucursales
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while(rs.next()){
                int id = rs.getInt("id_sucursal");
                if(idsSuc.contains(id)) continue;
                idsSuc.add(id);
                
                String nombre = rs.getString("nombre_sucursal");
                if (id == 1) nombre = "Tienda Central"; 
                else if (id == 2) nombre = "Sucursal Norte";
                else if (id == 3) nombre = "Sucursal Sur";
                

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 5. ACTUALIZAR SUCURSAL
    public boolean actualizarSucursal(int idUsuario, Integer idSucursal) {
        String sql = "UPDATE empleado e " +
                     "INNER JOIN usuario u ON e.id_empleado = u.id_empleado " +
                     "SET e.id_sucursal = ? " +
                     "WHERE u.id_usuario = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (idSucursal == null || idSucursal == 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, idSucursal);
            }
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 6. ACTUALIZAR ROL
    public boolean actualizarRol(int idUsuario, String nuevoRol) {
        String sql = "UPDATE empleado e " +
                     "INNER JOIN usuario u ON e.id_empleado = u.id_empleado " +
                     "SET e.rol = ? " +
                     "WHERE u.id_usuario = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoRol);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}