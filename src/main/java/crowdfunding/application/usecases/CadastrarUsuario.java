package crowdfunding.application.usecases;

import crowdfunding.application.interfaces.IUsuarioRepository;
import crowdfunding.domain.entities.Usuario;

public class CadastrarUsuario {
    private final IUsuarioRepository usuarioRepo;

    public CadastrarUsuario(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public static class Input {
        public final String id;
        public final String nome;
        public final String email;

        public Input(String id, String nome, String email) {
            this.id = id;
            this.nome = nome;
            this.email = email;
        }
    }

    public Usuario executar(Input input) {
        boolean emailEmUso = usuarioRepo.buscarPorEmail(input.email).isPresent();
        if (emailEmUso) throw new IllegalArgumentException("E-mail já cadastrado");

        Usuario usuario = new Usuario(input.id, input.nome, input.email);
        usuarioRepo.salvar(usuario);
        return usuario;
    }
}
