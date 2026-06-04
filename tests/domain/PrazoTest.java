package crowdfunding.domain;

import crowdfunding.domain.valueobjects.Prazo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PrazoTest {
    private final Date ontem = new Date(System.currentTimeMillis() - 86400000L);
    private final Date amanha = new Date(System.currentTimeMillis() + 86400000L);
    private final Date depoisDeAmanha = new Date(System.currentTimeMillis() + 2 * 86400000L);

    @Test
    void criaPrazoValido() {
        Prazo prazo = new Prazo(ontem, amanha);
        assertEquals(ontem, prazo.getInicio());
    }

    @Test
    void lancaErroQuandoFimEhAntesDoInicio() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new Prazo(amanha, ontem));
        assertTrue(ex.getMessage().contains("posterior"));
    }

    @Test
    void estaAtivoRetornaTrueParaPrazoEmAndamento() {
        Prazo prazo = new Prazo(ontem, amanha);
        assertTrue(prazo.estaAtivo());
    }

    @Test
    void estaExpiradoRetornaTrueParaPrazoVencido() {
        Prazo prazo = new Prazo(ontem, new Date(System.currentTimeMillis() - 100));
        assertTrue(prazo.estaExpirado());
    }

    @Test
    void diasRestantesRetornaZeroParaPrazoVencido() {
        Prazo prazo = new Prazo(ontem, new Date(System.currentTimeMillis() - 100));
        assertEquals(0, prazo.diasRestantes());
    }

    @Test
    void diasRestantesRetornaNumeroPositivoParaPrazoAtivo() {
        Prazo prazo = new Prazo(ontem, depoisDeAmanha);
        assertTrue(prazo.diasRestantes() > 0);
    }
}
