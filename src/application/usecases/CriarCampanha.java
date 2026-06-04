package crowdfunding.application.usecases;

import crowdfunding.application.interfaces.ICampanhaRepository;
import crowdfunding.application.interfaces.IUsuarioRepository;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Usuario;
import crowdfunding.domain.valueobjects.Dinheiro;
import crowdfunding.domain.valueobjects.Prazo;

import java.util.Date;

public class CriarCampanha {
    private final ICampanhaRepository campanhaRepo;
    private final IUsuarioRepository usuarioRepo;

    public CriarCampanha(ICampanhaRepository campanhaRepo, IUsuarioRepository usuarioRepo) {
        this.campanhaRepo = campanhaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public static class Input {
        public final String id;
        public final String criadorId;
        public final String titulo;
        public final String descricao;
        public final double metaEmReais;
        public final Date dataInicio;
        public final Date dataFim;

        public Input(String id, String criadorId, String titulo, String descricao,
                     double metaEmReais, Date dataInicio, Date dataFim) {
            this.id = id;
            this.criadorId = criadorId;
            this.titulo = titulo;
            this.descricao = descricao;
            this.metaEmReais = metaEmReais;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
        }
    }

    public Campanha executar(Input input) {
        Usuario criador = usuarioRepo.buscarPorId(input.criadorId)
                .orElseThrow(() -> new IllegalArgumentException("Criador não encontrado"));

        Dinheiro meta = Dinheiro.deReais(input.metaEmReais);
        Prazo prazo = new Prazo(input.dataInicio, input.dataFim);

        Campanha campanha = new Campanha(
                input.id,
                input.criadorId,
                input.titulo,
                input.descricao,
                meta,
                prazo
        );

        campanhaRepo.salvar(campanha);
        return campanha;
    }
}
