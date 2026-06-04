package crowdfunding.presentation.cli;

import crowdfunding.application.usecases.*;
import crowdfunding.domain.aggregates.Campanha;
import crowdfunding.domain.entities.Doacao;
import crowdfunding.domain.entities.Recompensa;
import crowdfunding.domain.entities.Usuario;
import crowdfunding.infrastructure.repositories.CampanhaRepositoryMemoria;
import crowdfunding.infrastructure.repositories.UsuarioRepositoryMemoria;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    // repositórios em memória (persistência durante a sessão)
    private static final CampanhaRepositoryMemoria campanhaRepo = new CampanhaRepositoryMemoria();
    private static final UsuarioRepositoryMemoria usuarioRepo = new UsuarioRepositoryMemoria();

    private static final CadastrarUsuario cadastrarUsuario = new CadastrarUsuario(usuarioRepo);
    private static final CriarCampanha criarCampanha = new CriarCampanha(campanhaRepo, usuarioRepo);
    private static final RealizarDoacao realizarDoacao = new RealizarDoacao(campanhaRepo, usuarioRepo);
    private static final CancelarCampanha cancelarCampanha = new CancelarCampanha(campanhaRepo);
    private static final AdicionarRecompensa adicionarRecompensa = new AdicionarRecompensa(campanhaRepo);

    private static final Scanner scanner = new Scanner(System.in);
    private static int idCounter = 1;

    private static String newId() {
        return "id-" + idCounter++;
    }

    private static String prompt(String question) {
        System.out.print(question);
        return scanner.nextLine();
    }

    private static void linha() {
        System.out.println("-".repeat(50));
    }

    private static void exibirMenu() {
        System.out.println("\n=== CROWDFUNDING SOCIAL ===");
        System.out.println("1. Cadastrar usuário");
        System.out.println("2. Criar campanha");
        System.out.println("3. Listar campanhas");
        System.out.println("4. Fazer doação");
        System.out.println("5. Cancelar campanha");
        System.out.println("6. Adicionar recompensa");
        System.out.println("7. Ver recompensas do apoiador");
        System.out.println("0. Sair");
    }

    private static void handleCadastrarUsuario() {
        String nome = prompt("Nome: ");
        String email = prompt("E-mail: ");
        try {
            Usuario u = cadastrarUsuario.executar(new CadastrarUsuario.Input(newId(), nome, email));
            System.out.println("✓ Usuário \"" + u.getNome() + "\" cadastrado. ID: " + u.getId());
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void handleCriarCampanha() {
        String criadorId = prompt("ID do criador: ");
        String titulo = prompt("Título: ");
        String descricao = prompt("Descrição: ");
        String meta = prompt("Meta em reais (ex: 1000): ");
        String diasAtivo = prompt("Durar quantos dias: ");
        try {
            Date dataInicio = new Date();
            Date dataFim = new Date(System.currentTimeMillis() + Long.parseLong(diasAtivo) * 86400000L);
            Campanha c = criarCampanha.executar(new CriarCampanha.Input(
                    newId(),
                    criadorId,
                    titulo,
                    descricao,
                    Double.parseDouble(meta),
                    dataInicio,
                    dataFim
            ));
            System.out.println("✓ Campanha \"" + c.getTitulo() + "\" criada. ID: " + c.getId());
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void handleListarCampanhas() {
        List<Campanha> campanhas = campanhaRepo.listarTodas();
        if (campanhas.isEmpty()) {
            System.out.println("Nenhuma campanha cadastrada.");
            return;
        }
        for (Campanha c : campanhas) {
            linha();
            System.out.println("ID: " + c.getId() + " | " + c.getTitulo());
            System.out.printf("Status: %s | Meta: %s | Arrecadado: %s (%.1f%%)%n",
                    c.getStatus(), c.getMeta(), c.totalArrecadado(), c.percentualArrecadado());
        }
        linha();
    }

    private static void handleRealizarDoacao() {
        String campanhaId = prompt("ID da campanha: ");
        String apoiadorId = prompt("ID do apoiador: ");
        String valor = prompt("Valor em reais: ");
        try {
            Doacao d = realizarDoacao.executar(new RealizarDoacao.Input(
                    newId(), campanhaId, apoiadorId, Double.parseDouble(valor)
            ));
            System.out.println("✓ Doação de " + d.getValor() + " confirmada!");
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void handleCancelarCampanha() {
        String campanhaId = prompt("ID da campanha: ");
        String solicitanteId = prompt("Seu ID (deve ser o criador): ");
        try {
            cancelarCampanha.executar(campanhaId, solicitanteId);
            System.out.println("✓ Campanha cancelada. Doações estornadas.");
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void handleAdicionarRecompensa() {
        String campanhaId = prompt("ID da campanha: ");
        String solicitanteId = prompt("Seu ID (deve ser o criador): ");
        String titulo = prompt("Título da recompensa: ");
        String descricao = prompt("Descrição: ");
        String valorMin = prompt("Valor mínimo para ganhar (em reais): ");
        try {
            Recompensa r = adicionarRecompensa.executar(new AdicionarRecompensa.Input(
                    newId(), campanhaId, solicitanteId, titulo, descricao, Double.parseDouble(valorMin)
            ));
            System.out.println("✓ Recompensa \"" + r.getTitulo() + "\" adicionada.");
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void handleRecompensasApoiador() {
        String campanhaId = prompt("ID da campanha: ");
        String apoiadorId = prompt("ID do apoiador: ");
        Campanha campanha = campanhaRepo.buscarPorId(campanhaId).orElse(null);
        if (campanha == null) {
            System.out.println("Campanha não encontrada.");
            return;
        }
        List<Recompensa> recompensas = campanha.recompensasDoApoiador(apoiadorId);
        if (recompensas.isEmpty()) {
            System.out.println("Nenhuma recompensa disponível para este apoiador.");
            return;
        }
        System.out.println("Recompensas disponíveis:");
        for (Recompensa r : recompensas) {
            System.out.println("  - " + r.getTitulo() + ": " + r.getDescricao() + " (mín: " + r.getValorMinimo() + ")");
        }
    }

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Crowdfunding Social!");
        System.out.println("(dados ficam em memória durante a sessão)\n");

        while (true) {
            exibirMenu();
            String opcao = prompt("\nEscolha: ").trim();

            switch (opcao) {
                case "1": handleCadastrarUsuario(); break;
                case "2": handleCriarCampanha(); break;
                case "3": handleListarCampanhas(); break;
                case "4": handleRealizarDoacao(); break;
                case "5": handleCancelarCampanha(); break;
                case "6": handleAdicionarRecompensa(); break;
                case "7": handleRecompensasApoiador(); break;
                case "0":
                    System.out.println("Até mais!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}
