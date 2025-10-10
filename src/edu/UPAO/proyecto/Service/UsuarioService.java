package edu.UPAO.proyecto.Service;

import edu.UPAO.proyecto.DAO.UsuarioDAO;
import edu.UPAO.proyecto.Modelo.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    // 🔹 tu método original (para compatibilidad con el código existente)
    public List<Usuario> listarUsuarios() {
        return dao.listar();
    }

    // 🔹 lo mismo con otro nombre más genérico
    public List<Usuario> listarTodos() {
        return dao.listar();
    }

    // 🔹 filtrado para gestión de empleados (sin GERENTE)
    public List<Usuario> listarParaGestion() {
        return dao.listar().stream()
                .filter(u -> ! "GERENTE".equalsIgnoreCase(u.getCargo()))
                .collect(Collectors.toList());
    }

    // 🔹 inicializa CSV si no existe
    public void seedIfMissing() {
        dao.seedIfMissing();
    }
}
