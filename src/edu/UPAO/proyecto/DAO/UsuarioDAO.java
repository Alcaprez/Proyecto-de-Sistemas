package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static List<Usuario> usuarios = new ArrayList<>();

    static {
        // Usuarios de prueba iniciales
        usuarios.add(new Usuario(1, 12345678, true, "TiendaApurimac", "Juan Pérez", "CAJERO", "cajero1", "1234"));
        usuarios.add(new Usuario(2, 87654321, true, "Tienda Trujillo", "María López", "ADMIN", "admin1", "admin"));
        usuarios.add(new Usuario(3, 11112222, false, "Tienda Lima", "Carlos Ruiz", "CAJERO", "cajero2", "0000"));
    }

    // Listar todos los usuarios
    public List<Usuario> listar() {
        return usuarios;
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Buscar usuario por username
    public Usuario buscarPorUsuario(String username) {
        return usuarios.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    // Validar login
    public Usuario login(String usuario, String contrasena) {
        return usuarios.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(usuario)
                        && u.getContrasena().equals(contrasena)
                        && u.isEstado())
                .findFirst()
                .orElse(null);
    }

    // Agregar usuario
    public void agregar(Usuario usuario) {
        usuarios.add(usuario);
    }

    // Eliminar usuario por ID
    public void eliminar(int id) {
        usuarios.removeIf(u -> u.getId() == id);
    }

    // Activar o desactivar usuario
    public void cambiarEstado(int id, boolean nuevoEstado) {
        Usuario u = buscarPorId(id);
        if (u != null) {
            u.setEstado(nuevoEstado);
        }
    }
 }