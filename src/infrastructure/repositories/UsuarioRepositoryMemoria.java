package crowdfunding.infrastructure.repositories;

import crowdfunding.application.interfaces.IUsuarioRepository;
import crowdfunding.domain.entities.Usuario;

import java.util.*;

public class UsuarioRepositoryMemoria implements IUsuarioRepository {
    private final Map<String, Usuario> store = new HashMap<>();

    @Override
    public void salvar(Usuario usuario) {
        store.put(usuario.getId(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equals(email.toLowerCase().trim()))
                .findFirst();
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(store.values());
    }
}
