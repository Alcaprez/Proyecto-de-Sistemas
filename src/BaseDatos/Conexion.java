package BaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {
    private Connection conectar = null;

    // --- CONFIGURACIÓN PARA RAILWAY ---
    private final String usuario = "root";
    private final String contraseña = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";
    private final String servidor = "crossover.proxy.rlwy.net";
    private final String puerto = "17752";
    private final String nombre_bd = "railway";

    // ✅ URL CORREGIDA CON TIMEZONE Y OPTIMIZACIONES
    private final String url = "jdbc:mysql://" + servidor + ":" + puerto + "/" + nombre_bd 
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Lima"  // ✅ CAMBIADO: UTC → America/Lima
            + "&useLegacyDatetimeCode=false"
            + "&autoReconnect=true"
            + "&characterEncoding=UTF-8";

    public Connection establecerConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("✅ Conexión a Railway establecida - Timezone: America/Lima");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Error de conexión a Railway:\n" + e.getMessage());
            System.err.println("❌ URL intentada: " + url);
        }
        return conectar;
    }
}