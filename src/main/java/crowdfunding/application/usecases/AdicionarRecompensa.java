package crowdfunding.application.usecases;

import crowdfunding.application.interfaces.ICampanhaRepository;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Recompensa;
import crowdfunding.domain.valueobjects.Dinheiro;

public class AdicionarRecompensa {
    private final ICampanhaRepository campanhaRepo;

    public AdicionarRecompensa(ICampanhaRepository campanhaRepo) {
        this.campanhaRepo = campanhaRepo;
    }

    public static class Input {
        public final String recompensaId;
        public final String campanhaId;
        public final String solicitanteId;
        public final String titulo;
        public final String descricao;
        public final double valorMinimoEmReais;

        public Input(String recompensaId, String campanhaId, String solicitanteId,
                     String titulo, String descricao, double valorMinimoEmReais) {
            this.recompensaId = recompensaId;
            this.campanhaId = campanhaId;
            this.solicitanteId = solicitanteId;
            this.titulo = titulo;
            this.descricao = descricao;
            this.valorMinimoEmReais = valorMinimoEmReais;
        }
    }

    public Recompensa executar(Input input) {
        Campanha campanha = campanhaRepo.buscarPorId(input.campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));

        if (!campanha.getCriadorId().equals(input.solicitanteId)) {
            throw new IllegalArgumentException("Apenas o criador pode adicionar recompensas");
        }

        Dinheiro valorMinimo = Dinheiro.deReais(input.valorMinimoEmReais);
        Recompensa recompensa = new Recompensa(
                input.recompensaId,
                input.titulo,
                input.descricao,
                valorMinimo
        );

        campanha.adicionarRecompensa(recompensa);
        campanhaRepo.salvar(campanha);

        return recompensa;
    }
}
