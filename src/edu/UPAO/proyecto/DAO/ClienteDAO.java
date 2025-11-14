package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Util.TextUtils;
import static edu.UPAO.proyecto.Util.TextUtils.capitalizarTexto;
import java.sql.*;

public class ClienteDAO {

    private Connection conexion;

    public ClienteDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("❌ Error al conectar ClienteDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ MÉTODO CORREGIDO - Sin id_persona
    public void registrarClienteSiNoExiste(String dni, String nombres, String apellidos) {
        // Primero verificar si ya existe
        if (existeCliente(dni)) {
            System.out.println("✅ Cliente ya existe con DNI: " + dni);
            return;
        }

        Connection conn = null;
        try {
            conn = new Conexion().establecerConexion();
            conn.setAutoCommit(false);

            // 1. Registrar en persona (si no existe)
            if (!existePersona(dni)) {
                String sqlPersona = "INSERT INTO persona (dni, nombres, apellidos, estado) VALUES (?, ?, ?, 'ACTIVO')";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPersona)) {
                    stmt.setString(1, dni);
                    stmt.setString(2, TextUtils.capitalizarTexto(nombres));
                    stmt.setString(3, TextUtils.capitalizarTexto(apellidos));
                    stmt.executeUpdate();
                    System.out.println("✅ Persona registrada: " + dni);
                }
            }

            // 2. Registrar en cliente (usando dni como id_cliente)
            String sqlCliente = "INSERT INTO cliente (id_cliente, dni) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                stmt.setString(1, dni);  // id_cliente = dni
                stmt.setString(2, dni);  // dni = dni
                stmt.executeUpdate();
                System.out.println("✅ Cliente registrado - ID: " + dni);
            }

            conn.commit();
            System.out.println("🎉 Cliente registrado exitosamente: " + nombres + " " + apellidos);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            System.err.println("❌ Error registrando cliente: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private boolean existePersona(String dni) {
        String sql = "SELECT 1 FROM persona WHERE dni = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error verificando persona: " + e.getMessage());
            return false;
        }
    }

    private boolean existeCliente(String dni) {
        String sql = "SELECT 1 FROM cliente WHERE dni = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error verificando cliente: " + e.getMessage());
            return false;
        }
    }

    // ✅ INSERTAR EN TABLA PERSONA - CORREGIDO (sin GeneratedKeys)
    private void insertarPersona(String dni, String nombres, String apellidos) throws SQLException {
        // Insertar sin esperar ID generado
        String sql = "INSERT INTO persona (dni, nombres, apellidos, estado) VALUES (?, ?, ?, 'ACTIVO')";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            stmt.setString(2, nombres);
            stmt.setString(3, apellidos);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar persona, ninguna fila afectada.");
            }
            // ✅ ELIMINADO: No intentamos obtener GeneratedKeys
        }
    }

    // ✅ INSERTAR EN TABLA CLIENTE - CORREGIDO
    private void insertarCliente(String dni) throws SQLException {
        // Generar id_cliente a partir del DNI
        String idCliente = generarIdCliente(dni);

        String sql = "INSERT INTO cliente (id_cliente, dni) VALUES (?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idCliente);
            stmt.setString(2, dni);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar cliente, ninguna fila afectada.");
            }
        }
    }

    // ✅ GENERAR ID_CLIENTE (CHAR(8))
    private String generarIdCliente(String dni) {
        // Si el DNI tiene 8 caracteres, usarlo directamente
        if (dni.length() == 8) {
            return dni;
        }
        // Si no, generar formato CLI + números
        return "CLI" + String.format("%05d", (int) (Math.random() * 10000));
    }

    // ✅ OBTENER ID_CLIENTE PARA LA VENTA - CORREGIDO
    public String obtenerIdClienteParaVenta(String dni) {
        String sql = "SELECT id_cliente FROM cliente WHERE dni = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("id_cliente");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo ID cliente: " + e.getMessage());
        }

        // Si no encuentra, usar cliente genérico
        return "00000000";
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

    public String[] obtenerNombresApellidosPorDNI(String dni) {
        String sql = "SELECT p.nombres, p.apellidos, c.id_cliente "
                + "FROM persona p "
                + "INNER JOIN cliente c ON p.dni = c.dni "
                + "WHERE p.dni = ? AND p.estado = 'ACTIVO'";

        System.out.println("🔍 Ejecutando consulta cliente para DNI: " + dni);

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                String idCliente = rs.getString("id_cliente");

                nombres = TextUtils.capitalizarTexto(nombres);
                apellidos = TextUtils.capitalizarTexto(apellidos);

                System.out.println("✅ Cliente encontrado - ID: " + idCliente
                        + ", Nombre: " + nombres + " " + apellidos);

                return new String[]{nombres, apellidos};
            } else {
                System.out.println("❌ No se encontró cliente con DNI: " + dni);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en obtenerNombresApellidosPorDNI: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
