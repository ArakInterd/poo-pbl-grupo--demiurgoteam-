package crowdfunding.domain;

import crowdfunding.domain.entities.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void criaUsuarioValido() {
        Usuario u = new Usuario("1", "João Silva", "joao@email.com");
        assertEquals("1", u.getId());
        assertEquals("João Silva", u.getNome());
        assertEquals("joao@email.com", u.getEmail());
    }

    @Test
    void normalizaEmailParaMinusculo() {
        Usuario u = new Usuario("1", "João", "JOAO@EMAIL.COM");
        assertEquals("joao@email.com", u.getEmail());
    }

    @Test
    void lancaErroParaEmailInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario("1", "João", "emailinvalido"));
        assertTrue(ex.getMessage().contains("E-mail inválido"));
    }

    @Test
    void lancaErroParaNomeVazio() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario("1", "", "joao@email.com"));
        assertTrue(ex.getMessage().contains("Nome é obrigatório"));
    }

    @Test
    void lancaErroParaIdVazio() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Usuario("", "João", "joao@email.com"));
        assertTrue(ex.getMessage().contains("ID"));
    }

    @Test
    void igualRetornaTrueParaMesmoId() {
        Usuario u1 = new Usuario("1", "João", "joao@email.com");
        Usuario u2 = new Usuario("1", "Outro Nome", "outro@email.com");
        assertTrue(u1.igual(u2));
    }

    @Test
    void igualRetornaFalseParaIdsDiferentes() {
        Usuario u1 = new Usuario("1", "João", "joao@email.com");
        Usuario u2 = new Usuario("2", "João", "joao2@email.com");
        assertFalse(u1.igual(u2));
    }
}
