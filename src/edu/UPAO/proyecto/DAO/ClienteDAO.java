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
    
    // ✅ MÉTODO CORREGIDO - Sin id_persona
    public void registrarClienteSiNoExiste(String dni, String nombres, String apellidos) {
        // Primero verificar si el cliente ya existe
        if (existeCliente(dni)) {
            System.out.println("✅ Cliente con DNI " + dni + " ya existe");
            return;
        }
        
        // Si no existe, insertar en persona y cliente
        try {
            conexion.setAutoCommit(false);
            
            // 1. Insertar en tabla persona (sin id_persona)
            insertarPersona(dni, nombres, apellidos);
            
            // 2. Insertar en tabla cliente
            insertarCliente(dni);
            
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
    
    // ✅ VERIFICAR SI EL CLIENTE EXISTE - CORREGIDO (sin id_persona)
    private boolean existeCliente(String dni) {
        // Consulta simplificada - solo verificar por DNI
        String sql = "SELECT COUNT(*) as count FROM cliente WHERE dni = ?";
        
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
        return "CLI" + String.format("%05d", (int)(Math.random() * 10000));
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
}