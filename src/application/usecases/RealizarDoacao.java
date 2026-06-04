package crowdfunding.application.usecases;

import crowdfunding.application.interfaces.ICampanhaRepository;
import crowdfunding.application.interfaces.IUsuarioRepository;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Doacao;
import crowdfunding.domain.entities.Usuario;
import crowdfunding.domain.valueobjects.Dinheiro;

public class RealizarDoacao {
    private final ICampanhaRepository campanhaRepo;
    private final IUsuarioRepository usuarioRepo;

    public RealizarDoacao(ICampanhaRepository campanhaRepo, IUsuarioRepository usuarioRepo) {
        this.campanhaRepo = campanhaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public static class Input {
        public final String doacaoId;
        public final String campanhaId;
        public final String apoiadorId;
        public final double valorEmReais;

        public Input(String doacaoId, String campanhaId, String apoiadorId, double valorEmReais) {
            this.doacaoId = doacaoId;
            this.campanhaId = campanhaId;
            this.apoiadorId = apoiadorId;
            this.valorEmReais = valorEmReais;
        }
    }

    public Doacao executar(Input input) {
        Campanha campanha = campanhaRepo.buscarPorId(input.campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));

        Usuario apoiador = usuarioRepo.buscarPorId(input.apoiadorId)
                .orElseThrow(() -> new IllegalArgumentException("Apoiador não encontrado"));

        Dinheiro valor = Dinheiro.deReais(input.valorEmReais);
        Doacao doacao = new Doacao(input.doacaoId, input.apoiadorId, valor);

        campanha.receberDoacao(doacao);
        campanhaRepo.salvar(campanha);

        return doacao;
    }
}
