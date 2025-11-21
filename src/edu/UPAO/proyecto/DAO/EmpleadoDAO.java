package edu.UPAO.proyecto.DAO;
import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    private Connection conexion;

    public EmpleadoDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("✅ EmpleadoDAO conectado");
        } catch (Exception e) {
            System.err.println("❌ Error conectando EmpleadoDAO: " + e.getMessage());
        }
    }

    // ✅ OBTENER SUCURSAL DEL EMPLEADO
    public int obtenerSucursalEmpleado(String idEmpleado) {
        String sql = "SELECT e.id_sucursal, s.nombre_sucursal " +
                     "FROM empleado e " +
                     "JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "WHERE e.id_empleado = ? AND e.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idSucursal = rs.getInt("id_sucursal");
                String nombreSucursal = rs.getString("nombre_sucursal");
                System.out.println("📍 Empleado " + idEmpleado + " asignado a: " + nombreSucursal + " (ID: " + idSucursal + ")");
                return idSucursal;
            } else {
                System.err.println("❌ No se encontró sucursal para empleado: " + idEmpleado);
                return 1; // Sucursal por defecto
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo sucursal del empleado: " + e.getMessage());
            return 1; // Sucursal por defecto en caso de error
        }
    }

    // ✅ VERIFICAR DATOS DEL EMPLEADO
    public void verificarDatosEmpleado(String idEmpleado) {
        String sql = "SELECT e.id_empleado, p.nombres, p.apellidos, s.nombre_sucursal, e.rol " +
                     "FROM empleado e " +
                     "JOIN persona p ON e.dni = p.dni " +
                     "JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                     "WHERE e.id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("👤 DATOS EMPLEADO:");
                System.out.println("   - ID: " + rs.getString("id_empleado"));
                System.out.println("   - Nombre: " + rs.getString("nombres") + " " + rs.getString("apellidos"));
                System.out.println("   - Sucursal: " + rs.getString("nombre_sucursal"));
                System.out.println("   - Rol: " + rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error verificando datos empleado: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

public List<Empleado> listarEmpleadosDetallado() {
    List<Empleado> lista = new ArrayList<>();
    
    // SQL CORREGIDO: Usamos 'nombre_sucursal' en lugar de 'nombre'
    String sql = "SELECT e.id_empleado, p.nombres, p.apellidos, p.dni, " +
                 "       p.telefono, p.correo, s.nombre_sucursal, " + // <--- Aquí estaba el error
                 "       e.rol, e.estado, e.sueldo " +
                 "FROM empleado e " +
                 "INNER JOIN persona p ON e.dni = p.dni " +
                 "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal " +
                 "WHERE e.rol <> 'GERENTE' " + // Filtramos al gerente si quieres
                 "ORDER BY p.apellidos ASC";

    try (Connection con = new Conexion().establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Empleado emp = new Empleado();
            // Datos propios de Empleado
            emp.setIdEmpleado(rs.getString("id_empleado"));
            emp.setDni(rs.getString("dni"));
            emp.setCargo(rs.getString("rol"));
            emp.setEstado(rs.getString("estado"));
            emp.setSueldo(rs.getDouble("sueldo"));
            
            // Datos traídos de Persona y Sucursal (Ahora sí existen los setters)
            emp.setNombres(rs.getString("nombres"));
            emp.setApellidos(rs.getString("apellidos"));
            emp.setTelefono(rs.getString("telefono"));
            emp.setCorreo(rs.getString("correo"));
            emp.setNombreSucursal(rs.getString("nombre_sucursal"));

            lista.add(emp);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al listar empleados en DAO: " + e.getMessage());
    }
    return lista;
}
// Agrega esto en EmpleadoDAO.java
public Empleado buscarPersonaPorDni(String dni) {
    Empleado personaEncontrada = null;
    String sql = "SELECT nombres, apellidos, telefono, correo FROM persona WHERE dni = ?";

    try (Connection con = new Conexion().establecerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, dni);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            personaEncontrada = new Empleado();
            personaEncontrada.setDni(dni);
            personaEncontrada.setNombres(rs.getString("nombres"));
            personaEncontrada.setApellidos(rs.getString("apellidos"));
            personaEncontrada.setTelefono(rs.getString("telefono"));
            personaEncontrada.setCorreo(rs.getString("correo"));
        }
    } catch (SQLException e) {
        System.err.println("Error al buscar persona: " + e.getMessage());
    }
    return personaEncontrada;
}
}