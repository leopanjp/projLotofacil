package br.com.lotofacil.analise;

import br.com.lotofacil.db.ConcursoComDezenas;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AnaliseRepeticaoServiceTest {

    @Test
    void deveGerarCsvComAnaliseDeRepeticao() throws IOException {
        List<ConcursoComDezenas> concursos = List.of(
                new ConcursoComDezenas(1, LocalDate.of(2026, 1, 1), List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)),
                new ConcursoComDezenas(2, LocalDate.of(2026, 1, 2), List.of(1,2,3,4,5,16,17,18,19,20,21,22,23,24,25)),
                new ConcursoComDezenas(3, LocalDate.of(2026, 1, 3), List.of(1,2,3,6,7,8,9,10,11,12,13,14,15,24,25))
        );

        Path csv = Path.of("target", "analise-repeticao-test.csv");
        Files.deleteIfExists(csv);

        AnaliseRepeticaoService service = new AnaliseRepeticaoService();
        service.gerarCsvAnalise(concursos, csv);

        String conteudo = Files.readString(csv);
        assertTrue(conteudo.contains("REPETICAO_CONSECUTIVA;1;2;1-2-3-4-5;5"));
        assertTrue(conteudo.contains("DEZENA_EM_SEQUENCIA;;;dezena_01;3"));
        assertTrue(conteudo.contains("CONJUNTO_FREQUENTE_TAM_2"));
        assertTrue(conteudo.contains("CONJUNTO_FREQUENTE_TAM_3"));
    }

    @Test
    void deveGerarCsvInformativoQuandoNaoHaDadosSuficientes() throws IOException {
        List<ConcursoComDezenas> concursos = List.of(
                new ConcursoComDezenas(1, LocalDate.of(2026, 1, 1), List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15))
        );

        Path csv = Path.of("target", "analise-repeticao-insuficiente-test.csv");
        Files.deleteIfExists(csv);

        AnaliseRepeticaoService service = new AnaliseRepeticaoService();
        service.gerarCsvAnalise(concursos, csv);

        String conteudo = Files.readString(csv);
        assertTrue(conteudo.contains("INFO;;;dados_insuficientes;minimo_2_concursos"));
    }
}
