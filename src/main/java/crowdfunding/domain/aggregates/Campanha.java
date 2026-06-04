package crowdfunding.domain.aggregates;

import crowdfunding.domain.valueobjects.Dinheiro;
import crowdfunding.domain.valueobjects.Prazo;
import crowdfunding.domain.valueobjects.StatusCampanha;
import crowdfunding.domain.entities.Recompensa;
import crowdfunding.domain.entities.Doacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Campanha é o aggregate root — controla tudo que envolve uma campanha
public class Campanha {
    private final String id;
    private final String criadorId;
    private final String titulo;
    private final String descricao;
    private final Dinheiro meta;
    private final Prazo prazo;
    private StatusCampanha status;
    private final List<Recompensa> recompensas;
    private final List<Doacao> doacoes;

    public Campanha(
            String id,
            String criadorId,
            String titulo,
            String descricao,
            Dinheiro meta,
            Prazo prazo
    ) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID da campanha é obrigatório");
        if (criadorId == null || criadorId.trim().isEmpty()) throw new IllegalArgumentException("ID do criador é obrigatório");
        if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Título é obrigatório");
        if (descricao == null || descricao.trim().isEmpty()) throw new IllegalArgumentException("Descrição é obrigatória");
        if (meta.getCentavos() == 0) throw new IllegalArgumentException("Meta não pode ser zero");

        this.id = id;
        this.criadorId = criadorId;
        this.titulo = titulo.trim();
        this.descricao = descricao.trim();
        this.meta = meta;
        this.prazo = prazo;
        this.status = StatusCampanha.ATIVA;
        this.recompensas = new ArrayList<>();
        this.doacoes = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getCriadorId() { return criadorId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Dinheiro getMeta() { return meta; }
    public Prazo getPrazo() { return prazo; }
    public StatusCampanha getStatus() { return status; }
    public List<Recompensa> getRecompensas() { return Collections.unmodifiableList(recompensas); }
    public List<Doacao> getDoacoes() { return Collections.unmodifiableList(doacoes); }

    public Dinheiro totalArrecadado() {
        return doacoes.stream()
                .filter(Doacao::estaConfirmada)
                .reduce(Dinheiro.zero(), (acc, d) -> acc.somar(d.getValor()), (a, b) -> a.somar(b));
    }

    public double percentualArrecadado() {
        if (meta.getCentavos() == 0) return 0;
        return (double) totalArrecadado().getCentavos() / meta.getCentavos() * 100;
    }

    public boolean metaBatida() {
        return totalArrecadado().ehMaiorOuIgualA(meta);
    }

    public void adicionarRecompensa(Recompensa recompensa) {
        if (this.status != StatusCampanha.ATIVA) {
            throw new IllegalStateException("Não é possível adicionar recompensas em campanha inativa");
        }
        boolean jaExiste = recompensas.stream().anyMatch(r -> r.getId().equals(recompensa.getId()));
        if (jaExiste) throw new IllegalArgumentException("Recompensa já cadastrada nessa campanha");
        recompensas.add(recompensa);
    }

    public void receberDoacao(Doacao doacao) {
        if (this.status != StatusCampanha.ATIVA) {
            throw new IllegalStateException("Campanha não está ativa para receber doações");
        }
        if (prazo.estaExpirado()) {
            // prazo venceu, encerra automaticamente
            encerrarPorPrazo();
            throw new IllegalStateException("Campanha expirada, não aceita mais doações");
        }
        doacao.confirmar();
        doacoes.add(doacao);

        // verifica se bateu a meta
        if (metaBatida()) {
            this.status = StatusCampanha.SUCESSO;
        }
    }

    public void cancelar() {
        if (this.status == StatusCampanha.CANCELADA) {
            throw new IllegalStateException("Campanha já está cancelada");
        }
        if (this.status == StatusCampanha.SUCESSO) {
            throw new IllegalStateException("Não é possível cancelar uma campanha com sucesso");
        }
        // estorna todas as doações confirmadas
        doacoes.stream()
                .filter(Doacao::estaConfirmada)
                .forEach(Doacao::estornar);

        this.status = StatusCampanha.CANCELADA;
    }

    public void encerrarPorPrazo() {
        if (this.status != StatusCampanha.ATIVA) return;
        this.status = StatusCampanha.EXPIRADA;
    }

    public List<Recompensa> recompensasDoApoiador(String apoiadorId) {
        // pega o total doado pelo apoiador
        Dinheiro totalDoApoiador = doacoes.stream()
                .filter(d -> d.getApoiadorId().equals(apoiadorId) && d.estaConfirmada())
                .reduce(Dinheiro.zero(), (acc, d) -> acc.somar(d.getValor()), (a, b) -> a.somar(b));

        // retorna as recompensas que ele se qualifica
        List<Recompensa> qualificadas = new ArrayList<>();
        for (Recompensa r : recompensas) {
            if (totalDoApoiador.ehMaiorOuIgualA(r.getValorMinimo())) {
                qualificadas.add(r);
            }
        }
        return qualificadas;
    }
}
