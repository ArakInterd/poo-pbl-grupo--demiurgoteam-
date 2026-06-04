package crowdfunding.domain;

import crowdfunding.domain.entities.Doacao;
import crowdfunding.domain.entities.StatusDoacao;
import crowdfunding.domain.valueobjects.Dinheiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoacaoTest {
    private final Dinheiro valor = Dinheiro.deReais(50);

    @Test
    void criaDoacaoComStatusPendente() {
        Doacao d = new Doacao("1", "user1", valor);
        assertEquals(StatusDoacao.PENDENTE, d.getStatus());
    }

    @Test
    void confirmarMudaStatusParaConfirmada() {
        Doacao d = new Doacao("1", "user1", valor);
        d.confirmar();
        assertEquals(StatusDoacao.CONFIRMADA, d.getStatus());
        assertTrue(d.estaConfirmada());
    }

    @Test
    void estornarMudaStatusParaEstornada() {
        Doacao d = new Doacao("1", "user1", valor);
        d.confirmar();
        d.estornar();
        assertEquals(StatusDoacao.ESTORNADA, d.getStatus());
    }

    @Test
    void naoPodeConfirmarDuasVezes() {
        Doacao d = new Doacao("1", "user1", valor);
        d.confirmar();
        Exception ex = assertThrows(IllegalStateException.class, d::confirmar);
        assertTrue(ex.getMessage().contains("Só é possível confirmar uma doação pendente"));
    }

    @Test
    void naoPodeEstornarSemConfirmarAntes() {
        Doacao d = new Doacao("1", "user1", valor);
        Exception ex = assertThrows(IllegalStateException.class, d::estornar);
        assertTrue(ex.getMessage().contains("Só é possível estornar uma doação confirmada"));
    }

    @Test
    void naoPodeCriarDoacaoComValorZero() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Doacao("1", "user1", Dinheiro.zero()));
        assertTrue(ex.getMessage().contains("zero"));
    }
}
