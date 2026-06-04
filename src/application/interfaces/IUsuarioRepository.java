package crowdfunding.application.interfaces;

import crowdfunding.domain.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    void salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(String id);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> listarTodos();
}
