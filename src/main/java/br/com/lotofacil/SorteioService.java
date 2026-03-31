package br.com.lotofacil;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class SorteioService {

    private final SecureRandom random;

    public SorteioService() {
        this(new SecureRandom());
    }

    public SorteioService(SecureRandom random) {
        this.random = random;
    }

    public Jogo gerarSorteio() {
        Set<Integer> numeros = new HashSet<>();

        while (numeros.size() < Jogo.QUANTIDADE_NUMEROS) {
            int numero = random.nextInt(Jogo.MAXIMO - Jogo.MINIMO + 1) + Jogo.MINIMO;
            numeros.add(numero);
        }

        return new Jogo(numeros);
    }
}
