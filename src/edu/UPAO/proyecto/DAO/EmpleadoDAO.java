// edu.UPAO.proyecto.DAO.EmpleadoDAO.java
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    private Connection conexion;

    public EmpleadoDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conectando EmpleadoDAO: " + e.getMessage());
        }
    }

    // Obtener empleado por ID
    public Empleado obtenerPorId(String idEmpleado) {
        String sql = "SELECT e.id_empleado, e.dni, e.id_sucursal, e.cargo, e.estado, e.sueldo, " +
                    "p.nombres, p.apellidos " +
                    "FROM empleado e " +
                    "INNER JOIN persona p ON e.dni = p.dni " +
                    "WHERE e.id_empleado = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Empleado emp = new Empleado();
                emp.setIdEmpleado(rs.getString("id_empleado"));
                emp.setDni(rs.getString("dni"));
                emp.setIdSucursal(rs.getInt("id_sucursal"));
                emp.setCargo(rs.getString("cargo"));
                emp.setEstado(rs.getString("estado"));
                emp.setSueldo(rs.getDouble("sueldo"));
                return emp;
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo empleado: " + e.getMessage());
        }
        return null;
    }

    // Actualizar sueldo
    public boolean actualizarSueldo(String idEmpleado, double nuevoSueldo) {
        String sql = "UPDATE empleado SET sueldo = ? WHERE id_empleado = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoSueldo);
            stmt.setString(2, idEmpleado);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando sueldo: " + e.getMessage());
        }
        return false;
    }

    // Listar todos los empleados
    public List<Empleado> listarTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.dni, e.id_sucursal, e.cargo, e.estado, e.sueldo, " +
                    "p.nombres, p.apellidos " +
                    "FROM empleado e " +
                    "INNER JOIN persona p ON e.dni = p.dni " +
                    "WHERE e.estado = 'ACTIVO'";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Empleado emp = new Empleado();
                emp.setIdEmpleado(rs.getString("id_empleado"));
                emp.setDni(rs.getString("dni"));
                emp.setIdSucursal(rs.getInt("id_sucursal"));
                emp.setCargo(rs.getString("cargo"));
                emp.setEstado(rs.getString("estado"));
                emp.setSueldo(rs.getDouble("sueldo"));
                empleados.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error listando empleados: " + e.getMessage());
        }
        return empleados;
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
}