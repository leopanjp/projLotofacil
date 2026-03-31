package br.com.lotofacil;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public record Jogo(Set<Integer> numeros) {

    public static final int QUANTIDADE_NUMEROS = 15;
    public static final int MINIMO = 1;
    public static final int MAXIMO = 25;

    public Jogo {
        if (numeros == null) {
            throw new IllegalArgumentException("A lista de números não pode ser nula.");
        }

        if (numeros.size() != QUANTIDADE_NUMEROS) {
            throw new IllegalArgumentException("Um jogo deve ter exatamente 15 números.");
        }

        for (Integer numero : numeros) {
            if (numero == null || numero < MINIMO || numero > MAXIMO) {
                throw new IllegalArgumentException("Todos os números devem estar entre 1 e 25.");
            }
        }

        numeros = Collections.unmodifiableSet(new TreeSet<>(numeros));
    }

    @Override
    public String toString() {
        return numeros.toString();
    }
}
