package br.com.lotofacil.analise;

import br.com.lotofacil.db.ConcursoComDezenas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AnaliseRepeticaoService {

    public void gerarCsvAnalise(List<ConcursoComDezenas> concursos, Path destinoCsv) {
        List<String> linhas = new ArrayList<>();
        linhas.add("tipo;concurso_base;concurso_comparado;detalhe;valor");

        if (concursos.size() < 2) {
            linhas.add("INFO;;;dados_insuficientes;minimo_2_concursos");
            salvarCsv(destinoCsv, linhas);
            return;
        }

        adicionarAnaliseConsecutiva(concursos, linhas);
        adicionarAnaliseDezenasEmSequencia(concursos, linhas);
        adicionarConjuntosFrequentes(concursos, linhas, 2, 20);
        adicionarConjuntosFrequentes(concursos, linhas, 3, 20);

        salvarCsv(destinoCsv, linhas);
    }

    private void salvarCsv(Path destinoCsv, List<String> linhas) {
        try {
            Path parent = destinoCsv.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(destinoCsv, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar CSV de análise", e);
        }
    }

    private void adicionarAnaliseConsecutiva(List<ConcursoComDezenas> concursos, List<String> linhas) {
        for (int i = 1; i < concursos.size(); i++) {
            ConcursoComDezenas anterior = concursos.get(i - 1);
            ConcursoComDezenas atual = concursos.get(i);

            Set<Integer> repetidas = new TreeSet<>(anterior.dezenas());
            repetidas.retainAll(new HashSet<>(atual.dezenas()));

            linhas.add(String.format(
                    "REPETICAO_CONSECUTIVA;%d;%d;%s;%d",
                    anterior.numeroConcurso(),
                    atual.numeroConcurso(),
                    repetidas.stream().map(String::valueOf).collect(Collectors.joining("-")),
                    repetidas.size()
            ));
        }
    }

    private void adicionarAnaliseDezenasEmSequencia(List<ConcursoComDezenas> concursos, List<String> linhas) {
        Map<Integer, Integer> maiorSequencia = new HashMap<>();
        Map<Integer, Integer> sequenciaAtual = new HashMap<>();

        for (ConcursoComDezenas concurso : concursos) {
            Set<Integer> dezenasConcurso = new HashSet<>(concurso.dezenas());
            for (int dezena = 1; dezena <= 25; dezena++) {
                int valorAtual = dezenasConcurso.contains(dezena)
                        ? sequenciaAtual.getOrDefault(dezena, 0) + 1
                        : 0;

                sequenciaAtual.put(dezena, valorAtual);
                maiorSequencia.put(dezena, Math.max(maiorSequencia.getOrDefault(dezena, 0), valorAtual));
            }
        }

        maiorSequencia.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .forEach(entry -> linhas.add(String.format(
                        "DEZENA_EM_SEQUENCIA;;;dezena_%02d;%d",
                        entry.getKey(),
                        entry.getValue()
                )));
    }

    private void adicionarConjuntosFrequentes(List<ConcursoComDezenas> concursos, List<String> linhas, int tamanhoConjunto, int limite) {
        Map<String, Integer> frequencia = new HashMap<>();

        for (ConcursoComDezenas concurso : concursos) {
            List<Integer> ordenadas = concurso.dezenas().stream().sorted().toList();
            List<List<Integer>> combinacoes = combinar(ordenadas, tamanhoConjunto);
            for (List<Integer> combinacao : combinacoes) {
                String chave = combinacao.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("-"));
                frequencia.merge(chave, 1, Integer::sum);
            }
        }

        frequencia.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(limite)
                .forEach(entry -> linhas.add(String.format(
                        "CONJUNTO_FREQUENTE_TAM_%d;;;%s;%d",
                        tamanhoConjunto,
                        entry.getKey(),
                        entry.getValue()
                )));
    }

    private List<List<Integer>> combinar(List<Integer> valores, int tamanho) {
        List<List<Integer>> resultado = new ArrayList<>();
        combinarRecursivo(valores, tamanho, 0, new ArrayList<>(), resultado);
        return resultado;
    }

    private void combinarRecursivo(List<Integer> valores, int tamanho, int inicio, List<Integer> atual, List<List<Integer>> resultado) {
        if (atual.size() == tamanho) {
            resultado.add(new ArrayList<>(atual));
            return;
        }

        for (int i = inicio; i < valores.size(); i++) {
            atual.add(valores.get(i));
            combinarRecursivo(valores, tamanho, i + 1, atual, resultado);
            atual.removeLast();
        }
    }
}
