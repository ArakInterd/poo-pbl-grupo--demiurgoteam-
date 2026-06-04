# Crowdfunding Social — POO/DDD em Java

Plataforma de financiamento coletivo para projetos sociais.

## Como rodar

**Requisitos:** Java 17+ e Maven 3.8+

```bash
# rodar todos os testes
mvn test

# rodar com cobertura (JaCoCo)
mvn test jacoco:report

# compilar e gerar o jar
mvn package

# interface de linha de comando
mvn exec:java -Dexec.mainClass="crowdfunding.presentation.cli.Main"
# ou após o package:
java -jar target/crowdfunding-1.0.0.jar
```

## Estrutura do projeto

```
src/
  main/java/crowdfunding/
    domain/               ← núcleo DDD: entidades, value objects, aggregates
      aggregates/
        Campanha.java     ← Aggregate Root
      entities/
        Usuario.java
        Doacao.java
        Recompensa.java
        StatusDoacao.java
      valueobjects/
        Dinheiro.java     ← imutável, evita problemas de float
        Prazo.java        ← imutável, valida datas
        StatusCampanha.java ← enum de estados possíveis
    application/
      usecases/           ← casos de uso da aplicação
      interfaces/         ← contratos dos repositórios
    infrastructure/
      repositories/       ← implementações em memória (persistência)
    presentation/
      cli/                ← interface interativa no terminal (bônus)

  test/java/crowdfunding/
    domain/               ← testes das regras de negócio (TDD)
    application/          ← testes dos casos de uso
```

## Decisões de design

**`Dinheiro` como Value Object:** todos os valores monetários usam centavos inteiros internamente. Evita erros clássicos de ponto flutuante (`0.1 + 0.2 ≠ 0.3`).

**`Campanha` como Aggregate Root:** nenhum código externo mexe diretamente em `Doacao` ou `Recompensa` — tudo passa pela `Campanha`, que garante consistência de estado.

**Estados da campanha:** `ATIVA → SUCESSO | EXPIRADA | CANCELADA`. Transições protegidas por regras no próprio domínio.

**Repositórios via interface:** a camada de aplicação depende de `ICampanhaRepository`, não de uma implementação concreta — facilita trocar por banco real depois.

## Integrantes
* João Pedro Araújo de Alcântara (ArakInterd)
* Arthur Carvalho Thaumaturgo (ArDev4)
* Caio Vitor de Lima Lopes (caiovitorlima)
