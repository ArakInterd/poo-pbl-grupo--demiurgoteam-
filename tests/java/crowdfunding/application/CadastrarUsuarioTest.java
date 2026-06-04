package crowdfunding.application;

import crowdfunding.application.usecases.CadastrarUsuario;
import crowdfunding.domain.entities.Usuario;
import crowdfunding.infrastructure.repositories.UsuarioRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CadastrarUsuarioTest {
    private UsuarioRepositoryMemoria usuarioRepo;
    private CadastrarUsuario cadastrarUsuario;

    @BeforeEach
    void setUp() {
        usuarioRepo = new UsuarioRepositoryMemoria();
        cadastrarUsuario = new CadastrarUsuario(usuarioRepo);
    }

    @Test
    void cadastraUsuarioComSucesso() {
        Usuario usuario = cadastrarUsuario.executar(new CadastrarUsuario.Input("u1", "Pedro", "pedro@email.com"));
        assertEquals("Pedro", usuario.getNome());
        assertTrue(usuarioRepo.buscarPorId("u1").isPresent());
    }

    @Test
    void naoPermiteEmailDuplicado() {
        cadastrarUsuario.executar(new CadastrarUsuario.Input("u1", "Pedro", "pedro@email.com"));
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> cadastrarUsuario.executar(new CadastrarUsuario.Input("u2", "Pedro2", "pedro@email.com")));
        assertTrue(ex.getMessage().contains("E-mail já cadastrado"));
    }
}
