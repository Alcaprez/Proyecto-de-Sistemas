package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
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
    
    // ✅ MÉTODO ACTUALIZADO - recibe nombres y apellidos
    public void registrarClienteSiNoExiste(String dni, String nombres, String apellidos) {
        // Primero verificar si el cliente ya existe
        if (existeCliente(dni)) {
            System.out.println("✅ Cliente con DNI " + dni + " ya existe");
            return;
        }
        
        // Si no existe, insertar en persona y cliente
        try {
            conexion.setAutoCommit(false);
            
            // 1. Insertar en tabla persona
            int idPersona = insertarPersona(dni, nombres, apellidos);
            
            // 2. Insertar en tabla cliente
            insertarCliente(dni, idPersona);
            
            conexion.commit();
            System.out.println("✅ Cliente registrado: " + nombres + " " + apellidos + " (DNI: " + dni + ")");
            
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error al registrar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // ✅ VERIFICAR SI EL CLIENTE EXISTE
    private boolean existeCliente(String dni) {
        String sql = "SELECT COUNT(*) as count FROM persona p " +
                    "INNER JOIN cliente c ON p.id_persona = c.id_persona " +
                    "WHERE p.dni = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ✅ INSERTAR EN TABLA PERSONA - CON NOMBRES Y APELLIDOS
    private int insertarPersona(String dni, String nombres, String apellidos) throws SQLException {
        String sql = "INSERT INTO persona (dni, nombres, apellidos, estado) VALUES (?, ?, ?, 'ACTIVO')";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dni);
            stmt.setString(2, nombres);
            stmt.setString(3, apellidos);
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar persona, ninguna fila afectada.");
            }
            
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Error al obtener ID de persona generado.");
            }
        }
    }
    
    // ✅ INSERTAR EN TABLA CLIENTE
    private void insertarCliente(String dni, int idPersona) throws SQLException {
        // Usar el dni como id_cliente (formato CHAR(8))
        String idCliente = dni.length() <= 8 ? dni : dni.substring(0, 8);
        
        String sql = "INSERT INTO cliente (id_cliente_CHAR, id_persona) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idCliente);
            stmt.setInt(2, idPersona);
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar cliente, ninguna fila afectada.");
            }
        }
    }
    
    // ✅ OBTENER ID_CLIENTE PARA LA VENTA
    public String obtenerIdClienteParaVenta(String dni) {
        // Para la tabla venta, usar el DNI como id_cliente (CHAR(8))
        return dni.length() <= 8 ? dni : dni.substring(0, 8);
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