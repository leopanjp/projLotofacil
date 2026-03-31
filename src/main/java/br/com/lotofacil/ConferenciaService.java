package br.com.lotofacil;

public class ConferenciaService {

    public static final int ACERTO_MINIMO_PREMIO = 11;

    public ResultadoConferencia conferir(Jogo aposta, Jogo sorteio) {
        int acertos = (int) aposta.numeros().stream()
                .filter(sorteio.numeros()::contains)
                .count();

        boolean premiado = acertos >= ACERTO_MINIMO_PREMIO;
        return new ResultadoConferencia(acertos, premiado);
    }
}
