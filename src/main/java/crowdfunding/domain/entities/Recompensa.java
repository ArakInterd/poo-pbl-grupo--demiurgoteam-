package crowdfunding.domain.entities;

import crowdfunding.domain.valueobjects.Dinheiro;

public class Recompensa {
    private final String id;
    private final String titulo;
    private final String descricao;
    private final Dinheiro valorMinimo;

    public Recompensa(String id, String titulo, String descricao, Dinheiro valorMinimo) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID da recompensa é obrigatório");
        if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Título da recompensa é obrigatório");
        if (descricao == null || descricao.trim().isEmpty()) throw new IllegalArgumentException("Descrição da recompensa é obrigatória");

        this.id = id;
        this.titulo = titulo.trim();
        this.descricao = descricao.trim();
        this.valorMinimo = valorMinimo;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Dinheiro getValorMinimo() { return valorMinimo; }
}
