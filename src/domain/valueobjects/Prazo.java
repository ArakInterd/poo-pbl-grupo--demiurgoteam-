package crowdfunding.domain.valueobjects;

import java.util.Date;

// prazo de uma campanha, imutável
public final class Prazo {
    private final Date inicio;
    private final Date fim;

    public Prazo(Date inicio, Date fim) {
        if (!fim.after(inicio)) {
            throw new IllegalArgumentException("Data de fim deve ser posterior à data de início");
        }
        this.inicio = new Date(inicio.getTime());
        this.fim = new Date(fim.getTime());
    }

    public Date getInicio() {
        return new Date(inicio.getTime());
    }

    public Date getFim() {
        return new Date(fim.getTime());
    }

    public boolean estaAtivo(Date agora) {
        return !agora.before(inicio) && !agora.after(fim);
    }

    public boolean estaAtivo() {
        return estaAtivo(new Date());
    }

    public boolean estaExpirado(Date agora) {
        return agora.after(fim);
    }

    public boolean estaExpirado() {
        return estaExpirado(new Date());
    }

    public int diasRestantes(Date agora) {
        if (estaExpirado(agora)) return 0;
        long diff = fim.getTime() - agora.getTime();
        return (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
    }

    public int diasRestantes() {
        return diasRestantes(new Date());
    }
}
