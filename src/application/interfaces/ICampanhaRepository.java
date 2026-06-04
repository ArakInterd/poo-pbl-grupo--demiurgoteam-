package crowdfunding.application.interfaces;

import crowdfunding.domain.aggregates.Campanha;

import java.util.List;
import java.util.Optional;

// contrato que qualquer repositório de campanha deve seguir
public interface ICampanhaRepository {
    void salvar(Campanha campanha);
    Optional<Campanha> buscarPorId(String id);
    List<Campanha> listarTodas();
    void deletar(String id);
}
