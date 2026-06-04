package crowdfunding.application.usecases;

import crowdfunding.application.interfaces.ICampanhaRepository;
import crowdfunding.domain.aggregates.Campanha;

public class CancelarCampanha {
    private final ICampanhaRepository campanhaRepo;

    public CancelarCampanha(ICampanhaRepository campanhaRepo) {
        this.campanhaRepo = campanhaRepo;
    }

    public void executar(String campanhaId, String solicitanteId) {
        Campanha campanha = campanhaRepo.buscarPorId(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));

        if (!campanha.getCriadorId().equals(solicitanteId)) {
            throw new IllegalArgumentException("Apenas o criador pode cancelar a campanha");
        }

        campanha.cancelar();
        campanhaRepo.salvar(campanha);
    }
}
