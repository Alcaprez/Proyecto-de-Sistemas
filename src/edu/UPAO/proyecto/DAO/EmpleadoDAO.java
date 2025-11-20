// edu.UPAO.proyecto.DAO.EmpleadoDAO.java
package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}