package crowdfunding.application;

import crowdfunding.application.usecases.*;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Doacao;
import crowdfunding.domain.valueobjects.StatusCampanha;
import crowdfunding.infrastructure.repositories.CampanhaRepositoryMemoria;
import crowdfunding.infrastructure.repositories.UsuarioRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RealizarDoacaoTest {
    private CampanhaRepositoryMemoria campanhaRepo;
    private UsuarioRepositoryMemoria usuarioRepo;
    private RealizarDoacao realizarDoacao;
    private CadastrarUsuario cadastrarUsuario;
    private CriarCampanha criarCampanha;

    @BeforeEach
    void setUp() {
        campanhaRepo = new CampanhaRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        realizarDoacao = new RealizarDoacao(campanhaRepo, usuarioRepo);
        cadastrarUsuario = new CadastrarUsuario(usuarioRepo);
        criarCampanha = new CriarCampanha(campanhaRepo, usuarioRepo);

        cadastrarUsuario.executar(new CadastrarUsuario.Input("criador1", "Carlos", "carlos@email.com"));
        cadastrarUsuario.executar(new CadastrarUsuario.Input("apoiador1", "Bia", "bia@email.com"));
        criarCampanha.executar(new CriarCampanha.Input(
                "c1", "criador1", "Biblioteca Popular", "Montar biblioteca no bairro",
                1000,
                new Date(System.currentTimeMillis() - 86400000L),
                new Date(System.currentTimeMillis() + 86400000L * 30)
        ));
    }

    @Test
    void realizaDoacaoComSucesso() {
        Doacao doacao = realizarDoacao.executar(new RealizarDoacao.Input("d1", "c1", "apoiador1", 200));
        assertTrue(doacao.estaConfirmada());
        assertEquals(200, doacao.getValor().getReais(), 0.001);
    }

    @Test
    void campanhaAtingeMetaAposDoacaoSuficiente() {
        realizarDoacao.executar(new RealizarDoacao.Input("d1", "c1", "apoiador1", 1000));
        Campanha campanha = campanhaRepo.buscarPorId("c1").get();
        assertEquals(StatusCampanha.SUCESSO, campanha.getStatus());
    }

    @Test
    void lancaErroSeCampanhaNaoExiste() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> realizarDoacao.executar(new RealizarDoacao.Input("d1", "inexistente", "apoiador1", 50)));
        assertTrue(ex.getMessage().contains("Campanha não encontrada"));
    }

    @Test
    void lancaErroSeApoiadorNaoExiste() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> realizarDoacao.executar(new RealizarDoacao.Input("d1", "c1", "fantasma", 50)));
        assertTrue(ex.getMessage().contains("Apoiador não encontrado"));
    }

    @Nested
    class CancelarCampanhaTest {
        private CancelarCampanha cancelarCampanha;

        @BeforeEach
        void setUp() {
            cancelarCampanha = new CancelarCampanha(campanhaRepo);
        }

        @Test
        void criadorCancelaCampanhaComSucesso() {
            cancelarCampanha.executar("c1", "criador1");
            Campanha campanha = campanhaRepo.buscarPorId("c1").get();
            assertEquals(StatusCampanha.CANCELADA, campanha.getStatus());
        }

        @Test
        void naoCriadorNaoPodeCancelar() {
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> cancelarCampanha.executar("c1", "outro-user"));
            assertTrue(ex.getMessage().contains("Apenas o criador"));
        }
    }
}
