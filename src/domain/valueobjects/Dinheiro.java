package crowdfunding.domain.valueobjects;

// representa um valor monetário, imutável para evitar problemas com float
public final class Dinheiro {
    private final long centavos;

    private Dinheiro(long centavos) {
        if (centavos < 0) {
            throw new IllegalArgumentException("Valor monetário não pode ser negativo");
        }
        this.centavos = centavos;
    }

    public static Dinheiro deCentavos(long centavos) {
        return new Dinheiro(centavos);
    }

    public static Dinheiro deReais(double reais) {
        // arredonda para evitar erro de float (ex: 0.1 + 0.2)
        return new Dinheiro(Math.round(reais * 100));
    }

    public static Dinheiro zero() {
        return new Dinheiro(0);
    }

    public long getCentavos() {
        return centavos;
    }

    public double getReais() {
        return centavos / 100.0;
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(this.centavos + outro.centavos);
    }

    public Dinheiro subtrair(Dinheiro outro) {
        long resultado = this.centavos - outro.centavos;
        if (resultado < 0) {
            throw new IllegalArgumentException("Subtração resultaria em valor negativo");
        }
        return new Dinheiro(resultado);
    }

    public boolean ehMaiorOuIgualA(Dinheiro outro) {
        return this.centavos >= outro.centavos;
    }

    public boolean ehMenorQue(Dinheiro outro) {
        return this.centavos < outro.centavos;
    }

    public boolean igual(Dinheiro outro) {
        return this.centavos == outro.centavos;
    }

    @Override
    public String toString() {
        return String.format("R$ %.2f", getReais());
    }
}
