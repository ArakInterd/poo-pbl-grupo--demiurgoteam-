package crowdfunding.domain.entities;

import crowdfunding.domain.valueobjects.Dinheiro;

import java.util.Date;

public class Doacao {
    private final String id;
    private final String apoiadorId;
    private final Dinheiro valor;
    private final Date criadoEm;
    private StatusDoacao status;

    public Doacao(String id, String apoiadorId, Dinheiro valor) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID da doação é obrigatório");
        if (apoiadorId == null || apoiadorId.trim().isEmpty()) throw new IllegalArgumentException("ID do apoiador é obrigatório");
        if (valor.getCentavos() == 0) throw new IllegalArgumentException("Valor da doação não pode ser zero");

        this.id = id;
        this.apoiadorId = apoiadorId;
        this.valor = valor;
        this.status = StatusDoacao.PENDENTE;
        this.criadoEm = new Date();
    }

    public String getId() { return id; }
    public String getApoiadorId() { return apoiadorId; }
    public Dinheiro getValor() { return valor; }
    public StatusDoacao getStatus() { return status; }
    public Date getCriadoEm() { return new Date(criadoEm.getTime()); }

    public void confirmar() {
        if (this.status != StatusDoacao.PENDENTE) {
            throw new IllegalStateException("Só é possível confirmar uma doação pendente");
        }
        this.status = StatusDoacao.CONFIRMADA;
    }

    public void estornar() {
        if (this.status != StatusDoacao.CONFIRMADA) {
            throw new IllegalStateException("Só é possível estornar uma doação confirmada");
        }
        this.status = StatusDoacao.ESTORNADA;
    }

    public boolean estaConfirmada() {
        return this.status == StatusDoacao.CONFIRMADA;
    }
}
