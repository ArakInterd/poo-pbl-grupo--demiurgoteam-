package crowdfunding.domain;

import crowdfunding.domain.valueobjects.Dinheiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DinheiroTest {

    @Test
    void criaValorEmReaisCorretamente() {
        Dinheiro d = Dinheiro.deReais(10.5);
        assertEquals(1050, d.getCentavos());
        assertEquals(10.5, d.getReais());
    }

    @Test
    void criaValorEmCentavosCorretamente() {
        Dinheiro d = Dinheiro.deCentavos(250);
        assertEquals(250, d.getCentavos());
    }

    @Test
    void zeroRetornaValorZero() {
        Dinheiro d = Dinheiro.zero();
        assertEquals(0, d.getCentavos());
    }

    @Test
    void somaDoisValoresCorretamente() {
        Dinheiro a = Dinheiro.deReais(10);
        Dinheiro b = Dinheiro.deReais(5);
        assertEquals(1500, a.somar(b).getCentavos());
    }

    @Test
    void subtraiDoisValoresCorretamente() {
        Dinheiro a = Dinheiro.deReais(10);
        Dinheiro b = Dinheiro.deReais(3);
        assertEquals(700, a.subtrair(b).getCentavos());
    }

    @Test
    void lancaErroAoSubtrairMaisDoQuetem() {
        Dinheiro a = Dinheiro.deReais(5);
        Dinheiro b = Dinheiro.deReais(10);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> a.subtrair(b));
        assertTrue(ex.getMessage().contains("Subtração resultaria em valor negativo"));
    }

    @Test
    void lancaErroParaValorNegativo() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> Dinheiro.deCentavos(-1));
        assertTrue(ex.getMessage().contains("não pode ser negativo"));
    }

    @Test
    void ehMaiorOuIgualAFuncionaCorretamente() {
        Dinheiro a = Dinheiro.deReais(10);
        Dinheiro b = Dinheiro.deReais(10);
        Dinheiro c = Dinheiro.deReais(5);
        assertTrue(a.ehMaiorOuIgualA(b));
        assertTrue(a.ehMaiorOuIgualA(c));
        assertFalse(c.ehMaiorOuIgualA(a));
    }

    @Test
    void igualFuncionaCorretamente() {
        assertTrue(Dinheiro.deReais(10).igual(Dinheiro.deReais(10)));
        assertFalse(Dinheiro.deReais(10).igual(Dinheiro.deReais(9)));
    }

    @Test
    void toStringFormataCorretamente() {
        assertEquals("R$ 10,50", Dinheiro.deReais(10.5).toString().replace(".", ","));
    }

    @Test
    void evitaProblemaClassicoDeFloat() {
        // evita problema clássico de float (0.1 + 0.2)
        Dinheiro a = Dinheiro.deReais(0.1);
        Dinheiro b = Dinheiro.deReais(0.2);
        assertEquals(0.3, a.somar(b).getReais(), 0.0001);
    }
}
