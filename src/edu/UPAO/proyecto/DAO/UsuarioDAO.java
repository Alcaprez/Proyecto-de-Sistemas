package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection conexion;

    public UsuarioDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            System.out.println("Conectado");
        } catch (Exception e) {
            System.err.println("Error conectando DAO: " + e.getMessage());
        }
    }


    public boolean probarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("✅ Conexión a BD establecida correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en conexión: " + e.getMessage());
        }
        return false;
    }

    public Usuario autenticar(String idEmpleado, String contrasena) {
        System.out.println("🔐 Intentando autenticar: " + idEmpleado);

        String sql = "SELECT u.id_usuario, u.id_empleado, u.contraseña, u.estado, "
                + "e.dni, e.rol, e.id_sucursal, "
                + "p.nombres, p.apellidos, "
                + "s.nombre_sucursal "
                + "FROM usuario u "
                + "INNER JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE u.id_empleado = ? AND u.contraseña = ? AND u.estado = 'ACTIVO'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            stmt.setString(2, contrasena);

            System.out.println("📝 Ejecutando consulta de autenticación...");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ USUARIO AUTENTICADO CORRECTAMENTE");

                Usuario user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setUsuario(rs.getString("id_empleado"));
                user.setCargo(rs.getString("rol")); // ✅ Esto debería traer "GERENTE" o "CAJERO"
                user.setNombreComp(rs.getString("nombres") + " " + rs.getString("apellidos"));
                user.setTienda(rs.getString("nombre_sucursal"));
                user.setContrasena(rs.getString("contraseña"));
                user.setDni(rs.getInt("dni"));
                user.setIdSucursal(rs.getInt("id_sucursal"));
                user.setEstado(true);

                System.out.println("📋 Datos del usuario:");
                System.out.println("   - ID: " + user.getId());
                System.out.println("   - Empleado: " + user.getUsuario());
                System.out.println("   - Nombre: " + user.getNombreComp());
                System.out.println("   - Cargo: " + user.getCargo());
                System.out.println("   - Sucursal: " + user.getTienda());
                System.out.println("   - DNI: " + user.getDni());

                return user;
            } else {
                System.out.println("❌ Credenciales incorrectas o usuario inactivo");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL en autenticación: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void diagnosticarProblema(String idEmpleado, String contrasena) {
        try {
            System.out.println("🔍 DIAGNÓSTICO DEL PROBLEMA:");

            // 1. Verificar usuario básico
            String sqlUser = "SELECT * FROM usuario WHERE id_empleado = ?";
            PreparedStatement stmt = conexion.prepareStatement(sqlUser);
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Usuario existe en tabla 'usuario':");
                System.out.println("   - ID Empleado: " + rs.getString("id_empleado"));
                System.out.println("   - Estado: " + rs.getString("estado"));
                System.out.println("   - Contraseña BD: '" + rs.getString("contraseña") + "'");
                System.out.println("   - Contraseña ingresada: '" + contrasena + "'");
                System.out.println("   - Coinciden: " + rs.getString("contraseña").equals(contrasena));

                // 2. Verificar empleado
                String sqlEmp = "SELECT e.*, s.nombre_sucursal FROM empleado e "
                        + "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                        + "WHERE e.id_empleado = ?";
                PreparedStatement stmtEmp = conexion.prepareStatement(sqlEmp);
                stmtEmp.setString(1, idEmpleado);
                ResultSet rsEmp = stmtEmp.executeQuery();

                if (rsEmp.next()) {
                    System.out.println("✅ Empleado encontrado en tabla 'empleado':");
                    System.out.println("   - ID Sucursal: " + rsEmp.getInt("id_sucursal"));
                    System.out.println("   - Sucursal: " + rsEmp.getString("nombre_sucursal"));
                    System.out.println("   - Rol: " + rsEmp.getString("rol"));
                } else {
                    System.out.println("❌ Empleado NO encontrado en tabla 'empleado'");
                    System.out.println("💡 SOLUCIÓN: Necesitas insertar este usuario en la tabla empleado");
                }
            } else {
                System.out.println("❌ Usuario NO existe en tabla 'usuario'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void verificarUsuarioSimple(String idEmpleado, String contrasena) {
        try {
            // Verificar si el usuario existe
            String sqlUser = "SELECT * FROM usuario WHERE id_empleado = ?";
            PreparedStatement stmt = conexion.prepareStatement(sqlUser);
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("ℹ️ Usuario encontrado en BD:");
                System.out.println("   - ID Empleado: " + rs.getString("id_empleado"));
                System.out.println("   - Contraseña en BD: '" + rs.getString("contraseña") + "'");
                System.out.println("   - Contraseña ingresada: '" + contrasena + "'");
                System.out.println("   - Estado: " + rs.getString("estado"));
                System.out.println("   - Coincidencia contraseña: " + rs.getString("contraseña").equals(contrasena));
            } else {
                System.out.println("❌ No existe usuario con ID: " + idEmpleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    // ---------- API PRINCIPAL ----------
    /**
     * Lee usuarios desde data/empleados.csv. Si no existe, devuelve lista
     * vacía.
     */
    private String esc(String s) {
        if (s == null) {
            return "";
        }
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean parseBool(String s) {
        String v = s.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("activo") || v.equals("habilitado");
    }

    private LocalTime parseTime(String s) {
        s = s == null ? "" : s.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void verificarDatosUsuario(String idEmpleado) {
        // ✅ CORREGIDO: usar 'contraseña' (con ñ) en la consulta
        String sql = "SELECT u.id_empleado, u.contraseña, u.estado, "
                + "e.dni, e.rol, e.id_sucursal, "
                + "p.nombres, p.apellidos, "
                + "s.nombre_sucursal "
                + "FROM usuario u "
                + "LEFT JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "LEFT JOIN persona p ON e.dni = p.dni "
                + "LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE u.id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ USUARIO ENCONTRADO EN BD:");
                System.out.println("   - ID Empleado: " + rs.getString("id_empleado"));
                System.out.println("   - Contraseña en BD: " + rs.getString("contraseña"));
                System.out.println("   - Estado: " + rs.getString("estado"));
                System.out.println("   - DNI: " + rs.getString("dni"));
                System.out.println("   - Rol: " + rs.getString("rol"));
                System.out.println("   - Nombre: " + rs.getString("nombres") + " " + rs.getString("apellidos"));
                System.out.println("   - Sucursal: " + rs.getString("nombre_sucursal"));

                // Verificar si la contraseña es incorrecta
                if ("123456".equals(rs.getString("contraseña"))) {
                    System.out.println("ℹ️  La contraseña por defecto (123456) está configurada");
                }
            } else {
                System.out.println("❌ NO SE ENCONTRÓ EL USUARIO EN BD: " + idEmpleado);
                // Crear usuario por defecto si no existe
                crearUsuarioSiNoExiste(idEmpleado);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en verificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearUsuarioSiNoExiste(String idEmpleado) {
        System.out.println("🔄 Intentando crear usuario: " + idEmpleado);

        // Primero verificar si el empleado existe
        String sqlEmpleado = "SELECT e.id_empleado, e.dni, e.rol, e.id_sucursal, "
                + "p.nombres, p.apellidos, s.nombre_sucursal "
                + "FROM empleado e "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE e.id_empleado = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sqlEmpleado)) {
            stmt.setString(1, idEmpleado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Empleado encontrado, creando usuario...");

                // Insertar usuario con contraseña por defecto
                String sqlInsert = "INSERT INTO usuario (id_empleado, contraseña, estado) VALUES (?, '123456', 'ACTIVO')";
                try (PreparedStatement stmtInsert = conexion.prepareStatement(sqlInsert)) {
                    stmtInsert.setString(1, idEmpleado);
                    int filas = stmtInsert.executeUpdate();

                    if (filas > 0) {
                        System.out.println("✅ Usuario creado exitosamente: " + idEmpleado);
                        System.out.println("   - Contraseña por defecto: 123456");
                        System.out.println("   - Estado: ACTIVO");
                    }
                }
            } else {
                System.out.println("❌ No se puede crear usuario: Empleado no existe: " + idEmpleado);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creando usuario: " + e.getMessage());
        }
    }

    // En UsuarioDAO.java - método opcional para validar sucursal
    public boolean verificarSucursalUsuario(String idEmpleado, String nombreSucursal) {
        String sql = "SELECT COUNT(*) as count "
                + "FROM usuario u "
                + "INNER JOIN empleado e ON u.id_empleado = e.id_empleado "
                + "INNER JOIN sucursal s ON e.id_sucursal = s.id_sucursal "
                + "WHERE u.id_empleado = ? AND s.nombre_sucursal = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idEmpleado);
            stmt.setString(2, nombreSucursal);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando sucursal: " + e.getMessage());
        }
        return false;
    }
}
