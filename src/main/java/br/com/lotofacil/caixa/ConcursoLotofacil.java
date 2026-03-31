package br.com.lotofacil.caixa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ConcursoLotofacil(
        int numero,
        LocalDate dataApuracao,
        List<Integer> dezenas,
        BigDecimal valorArrecadado,
        boolean acumulado,
        BigDecimal valorEstimadoProximoConcurso
) {
}
