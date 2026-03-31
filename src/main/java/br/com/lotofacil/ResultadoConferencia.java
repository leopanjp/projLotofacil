package br.com.lotofacil;

public record ResultadoConferencia(int acertos, boolean premiado) {

    @Override
    public String toString() {
        return "Acertos: " + acertos + (premiado ? " (Premiado)" : " (Não premiado)");
    }
}
