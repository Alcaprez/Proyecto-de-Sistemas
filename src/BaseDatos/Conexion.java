package BaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    Connection conectar = null;

    // 1. Credenciales
    String usuario = "sql10806610";
    String contraseña = "58BSn3cxmk";

    // 2. Información del Servidor
    String servidor = "sql10.freesqldatabase.com"; 
    String puerto = "3306";
    String nombre_bd = "sql10806610"; 

    // 3. URL de Conexión (La parte crucial)
    // El formato JDBC es: jdbc:mysql://[servidor]:[puerto]/[nombre_bd]
    String url = "jdbc:mysql://" + servidor + ":" + puerto + "/" + nombre_bd;

    public Connection establecerConexion() {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");

            conectar = DriverManager.getConnection(url, usuario, contraseña);
         
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos: " + e.getMessage());
        }
        return conectar;
    }
}
