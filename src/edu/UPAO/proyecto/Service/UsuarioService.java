package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.Modelo.Usuario;
import edu.UPAO.proyecto.DAO.UsuarioDAO;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Login
    public Usuario login(String usuario, String contrasena) {
        return usuarioDAO.login(usuario, contrasena);
    }

    // Listar todos
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    // Buscar por ID
    public Usuario buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    // Buscar por nombre de usuario
    public Usuario buscarPorUsuario(String username) {
        return usuarioDAO.buscarPorUsuario(username);
    }

    // Agregar nuevo usuario
    public void agregarUsuario(Usuario usuario) {
        usuarioDAO.agregar(usuario);
    }

    // Eliminar usuario
    public void eliminarUsuario(int id) {
        usuarioDAO.eliminar(id);
    }

    // Cambiar estado (activar/desactivar)
    public void cambiarEstado(int id, boolean estado) {
        usuarioDAO.cambiarEstado(id, estado);
    }
}
