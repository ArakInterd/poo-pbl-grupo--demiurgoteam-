package crowdfunding.infrastructure.repositories;

import crowdfunding.application.interfaces.ICampanhaRepository;
import crowdfunding.domain.aggregates.Campanha;

import java.util.*;

// repositório em memória (simula um banco sem precisar de dependência externa).
public class CampanhaRepositoryMemoria implements ICampanhaRepository {
    private final Map<String, Campanha> store = new HashMap<>();

    @Override
    public void salvar(Campanha campanha) {
        store.put(campanha.getId(), campanha);
    }

    @Override
    public Optional<Campanha> buscarPorId(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Campanha> listarTodas() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deletar(String id) {
        store.remove(id);
    }
}
