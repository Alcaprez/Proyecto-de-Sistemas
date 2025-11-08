package BaseDatos;

import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.SQLException; // También es bueno importar SQLException para cerrar la conexión

/**
 *
 * @author ALBERTH
 */
public class Main {

    public static void main(String[] args) {

        Conexion objetoConexion = new Conexion();

        // NO hay casteo. El método establecerConexion() ya devuelve java.sql.Connection
        Connection conexionExitosa = objetoConexion.establecerConexion();

        if (conexionExitosa != null) {
            JOptionPane.showMessageDialog(null, "¡Conexión Exitosa! 🎉",
                    "Estado de la Conexión", JOptionPane.INFORMATION_MESSAGE);

            // Opcional: Cerrar la conexión
            try {
                conexionExitosa.close();
                //JOptionPane.showMessageDialog(null, "Conexión cerrada.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage());
            }

        } else {
            JOptionPane.showMessageDialog(null, "⚠️ La conexión ha fallado. Revisa la consola y las credenciales.",
                    "Estado de la Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
