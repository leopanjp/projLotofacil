package br.com.lotofacil.caixa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class CaixaLotofacilClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaixaLotofacilClient.class);

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));

    private final AtomicReference<HttpClient> httpRef;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public CaixaLotofacilClient() {
        this("https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil");
    }

    public CaixaLotofacilClient(String baseUrl) {
        this(newClient(), new ObjectMapper(), baseUrl);
    }

    public CaixaLotofacilClient(HttpClient httpClient, ObjectMapper objectMapper, String baseUrl) {
        this.httpRef = new AtomicReference<>(httpClient);
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    public ConcursoLotofacil buscarUltimoConcurso() {
        return buscarConcurso(baseUrl);
    }

    public ConcursoLotofacil buscarConcursoPorNumero(int numeroConcurso) {
        return buscarConcurso(baseUrl + "/" + numeroConcurso);
    }

    private ConcursoLotofacil buscarConcurso(String url) {
        LOGGER.info("Consultando resultado Lotofácil em {}", url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json, text/plain, */*")
                .header("user-agent", "Mozilla/5.0")
                .header("referer", "https://loterias.caixa.gov.br/")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpRef.get().send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Resposta HTTP {} para {}", response.statusCode(), url);
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Falha ao consultar API da Caixa. HTTP " + response.statusCode());
            }
            return parseConcurso(response.body());
        } catch (HttpTimeoutException e) {
            resetClient("timeout", e);
            throw new IllegalStateException("Erro ao consultar API da Caixa", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Erro ao consultar API da Caixa", e);
        } catch (IOException e) {
            if (isConnectionLike(e)) {
                resetClient("io/conn", e);
            }
            throw new IllegalStateException("Erro ao consultar API da Caixa", e);
        }
    }

    private void resetClient(String reason, Exception e) {
        LOGGER.warn("CAIXA client reset ({}) devido a: {}", reason, e.toString());
        httpRef.set(newClient());
    }

    private static HttpClient newClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    private boolean isConnectionLike(IOException e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConnectException) {
                return true;
            }
            if (t instanceof java.net.SocketTimeoutException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    ConcursoLotofacil parseConcurso(String json) throws IOException {
        JsonNode node = objectMapper.readTree(json);

        int numero = node.path("numero").asInt();
        LocalDate dataApuracao = LocalDate.parse(node.path("dataApuracao").asText(), DATA_FORMATTER);
        List<Integer> dezenas = new ArrayList<>();
        for (JsonNode dezena : node.path("listaDezenas")) {
            dezenas.add(Integer.parseInt(dezena.asText()));
        }

        BigDecimal valorArrecadado = parseDinheiro(node.path("valorArrecadado").asText());
        boolean acumulado = node.path("acumulado").asBoolean(false);
        BigDecimal valorEstimado = parseDinheiro(node.path("valorEstimadoProximoConcurso").asText());

        return new ConcursoLotofacil(numero, dataApuracao, dezenas, valorArrecadado, acumulado, valorEstimado);
    }

    private BigDecimal parseDinheiro(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        String normalizado = valor
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();

        if (normalizado.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalizado);
    }
}
