package crowdfunding.domain;

import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Doacao;
import crowdfunding.domain.entities.Recompensa;
import crowdfunding.domain.entities.StatusDoacao;
import crowdfunding.domain.valueobjects.Dinheiro;
import crowdfunding.domain.valueobjects.Prazo;
import crowdfunding.domain.valueobjects.StatusCampanha;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CampanhaTest {

    // helpers para criar objetos de teste
    private Prazo prazoAtivo() {
        return new Prazo(
                new Date(System.currentTimeMillis() - 86400000L),
                new Date(System.currentTimeMillis() + 86400000L * 30)
        );
    }

    private Campanha campanhaPadrao() {
        return new Campanha("c1", "user1", "Escola Nova", "Construir escola",
                Dinheiro.deReais(1000), prazoAtivo());
    }

    @Test
    void criaCampanhaComStatusAtiva() {
        Campanha c = campanhaPadrao();
        assertEquals(StatusCampanha.ATIVA, c.getStatus());
    }

    @Test
    void totalArrecadadoComecaEmZero() {
        Campanha c = campanhaPadrao();
        assertEquals(0, c.totalArrecadado().getCentavos());
    }

    @Test
    void recebeDoacaoESomaAoTotal() {
        Campanha c = campanhaPadrao();
        Doacao doacao = new Doacao("d1", "apoiador1", Dinheiro.deReais(200));
        c.receberDoacao(doacao);
        assertEquals(200, c.totalArrecadado().getReais(), 0.001);
    }

    @Test
    void mudaStatusParaSucessoAoBaterAMeta() {
        Campanha c = campanhaPadrao(); // meta 1000
        c.receberDoacao(new Doacao("d1", "a1", Dinheiro.deReais(600)));
        c.receberDoacao(new Doacao("d2", "a2", Dinheiro.deReais(400)));
        assertEquals(StatusCampanha.SUCESSO, c.getStatus());
    }

    @Test
    void percentualArrecadadoEhCalculadoCorretamente() {
        Campanha c = campanhaPadrao(); // meta 1000
        c.receberDoacao(new Doacao("d1", "a1", Dinheiro.deReais(250)));
        assertEquals(25.0, c.percentualArrecadado(), 0.001);
    }

    @Test
    void naoAceitaDoacaoEmCampanhaCancelada() {
        Campanha c = campanhaPadrao();
        c.cancelar();
        Exception ex = assertThrows(IllegalStateException.class,
                () -> c.receberDoacao(new Doacao("d1", "a1", Dinheiro.deReais(100))));
        assertTrue(ex.getMessage().contains("não está ativa"));
    }

    @Test
    void cancelarEstornaTodosAsDoacoesConfirmadas() {
        Campanha c = campanhaPadrao();
        Doacao d1 = new Doacao("d1", "a1", Dinheiro.deReais(100));
        Doacao d2 = new Doacao("d2", "a2", Dinheiro.deReais(200));
        c.receberDoacao(d1);
        c.receberDoacao(d2);
        c.cancelar();
        assertEquals(StatusDoacao.ESTORNADA, d1.getStatus());
        assertEquals(StatusDoacao.ESTORNADA, d2.getStatus());
        assertEquals(StatusCampanha.CANCELADA, c.getStatus());
    }

    @Test
    void naoPodeCancelarCampanhaComSucesso() {
        Campanha c = campanhaPadrao();
        c.receberDoacao(new Doacao("d1", "a1", Dinheiro.deReais(1000)));
        Exception ex = assertThrows(IllegalStateException.class, c::cancelar);
        assertTrue(ex.getMessage().contains("Não é possível cancelar"));
    }

    @Test
    void adicionaRecompensaCorretamente() {
        Campanha c = campanhaPadrao();
        Recompensa r = new Recompensa("r1", "Camiseta", "Camiseta do projeto", Dinheiro.deReais(50));
        c.adicionarRecompensa(r);
        assertEquals(1, c.getRecompensas().size());
    }

    @Test
    void naoAdicionaRecompensaDuplicada() {
        Campanha c = campanhaPadrao();
        Recompensa r = new Recompensa("r1", "Camiseta", "Camiseta", Dinheiro.deReais(50));
        c.adicionarRecompensa(r);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> c.adicionarRecompensa(r));
        assertTrue(ex.getMessage().contains("já cadastrada"));
    }

    @Test
    void recompensasDoApoiadorRetornaSomenteAsQueEleSeQualifica() {
        Campanha c = campanhaPadrao();
        c.adicionarRecompensa(new Recompensa("r1", "Camiseta", "...", Dinheiro.deReais(50)));
        c.adicionarRecompensa(new Recompensa("r2", "Boné", "...", Dinheiro.deReais(100)));
        c.adicionarRecompensa(new Recompensa("r3", "Visita", "...", Dinheiro.deReais(500)));

        c.receberDoacao(new Doacao("d1", "apoiador1", Dinheiro.deReais(100)));

        List<Recompensa> recompensas = c.recompensasDoApoiador("apoiador1");
        assertEquals(2, recompensas.size()); // se qualifica para 50 e 100, não para 500
    }

    @Test
    void lancaErroParaCampanhaSemTitulo() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Campanha("c1", "u1", "", "desc", Dinheiro.deReais(100), prazoAtivo()));
        assertTrue(ex.getMessage().contains("Título é obrigatório"));
    }

    @Test
    void lancaErroParaMetaZero() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Campanha("c1", "u1", "Titulo", "desc", Dinheiro.zero(), prazoAtivo()));
        assertTrue(ex.getMessage().contains("Meta não pode ser zero"));
    }
}
