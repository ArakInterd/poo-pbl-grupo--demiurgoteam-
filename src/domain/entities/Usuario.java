package crowdfunding.domain.entities;

import java.util.regex.Pattern;

public class Usuario {
    private final String id;
    private final String nome;
    private final String email;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public Usuario(String id, String nome, String email) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID do usuário é obrigatório");
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome é obrigatório");
        if (!emailValido(email)) throw new IllegalArgumentException("E-mail inválido");

        this.id = id;
        this.nome = nome.trim();
        this.email = email.toLowerCase().trim();
    }

    private boolean emailValido(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    public boolean igual(Usuario outro) {
        return this.id.equals(outro.id);
    }
}
