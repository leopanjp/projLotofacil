package br.com.lotofacil.db;

import java.time.LocalDate;
import java.util.List;

public record ConcursoComDezenas(
        int numeroConcurso,
        LocalDate dataApuracao,
        List<Integer> dezenas
) {
}
