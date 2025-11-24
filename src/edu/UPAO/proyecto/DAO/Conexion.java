
package edu.UPAO.proyecto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // URL formada con tu HOST, PUERTO y nombre de BD (railway)
    private static final String URL = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    private static final String USER = "root"; 
    private static final String PASS = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU"; 

    public static Connection getConexion() {
        try {
            // Carga el driver (asegúrate de tener el jar de MySQL en librerías)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
}