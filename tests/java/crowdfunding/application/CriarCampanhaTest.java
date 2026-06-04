package crowdfunding.application;

import crowdfunding.application.usecases.CadastrarUsuario;
import crowdfunding.application.usecases.CriarCampanha;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.valueobjects.StatusCampanha;
import crowdfunding.infrastructure.repositories.CampanhaRepositoryMemoria;
import crowdfunding.infrastructure.repositories.UsuarioRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CriarCampanhaTest {
    private CampanhaRepositoryMemoria campanhaRepo;
    private UsuarioRepositoryMemoria usuarioRepo;
    private CriarCampanha criarCampanha;
    private CadastrarUsuario cadastrarUsuario;

    private Date amanha() { return new Date(System.currentTimeMillis() + 86400000L); }
    private Date emUmMes() { return new Date(System.currentTimeMillis() + 86400000L * 30); }

    @BeforeEach
    void setUp() {
        campanhaRepo = new CampanhaRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        criarCampanha = new CriarCampanha(campanhaRepo, usuarioRepo);
        cadastrarUsuario = new CadastrarUsuario(usuarioRepo);
    }

    @Test
    void criaCampanhaComSucessoParaCriadorExistente() {
        cadastrarUsuario.executar(new CadastrarUsuario.Input("u1", "Ana", "ana@email.com"));

        Campanha campanha = criarCampanha.executar(new CriarCampanha.Input(
                "c1", "u1", "Horta Comunitária", "Projeto de horta no bairro",
                500, amanha(), emUmMes()
        ));

        assertEquals(StatusCampanha.ATIVA, campanha.getStatus());
        assertEquals(500, campanha.getMeta().getReais(), 0.001);
        assertTrue(campanhaRepo.buscarPorId("c1").isPresent());
    }

    @Test
    void lancaErroSeCriadorNaoExiste() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> criarCampanha.executar(new CriarCampanha.Input(
                        "c1", "nao-existe", "Teste", "Desc",
                        100, amanha(), emUmMes()
                )));
        assertTrue(ex.getMessage().contains("Criador não encontrado"));
    }
}
