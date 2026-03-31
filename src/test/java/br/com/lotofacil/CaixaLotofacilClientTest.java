package br.com.lotofacil.caixa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaixaLotofacilClientTest {

    @Test
    void deveParsearRespostaDaCaixa() throws Exception {
        String json = """
                {
                  "numero": 3301,
                  "dataApuracao": "30/03/2026",
                  "listaDezenas": ["01", "03", "05", "07", "09", "11", "13", "15", "17", "19", "21", "22", "23", "24", "25"],
                  "valorArrecadado": "R$ 12.345.678,90",
                  "acumulado": false,
                  "valorEstimadoProximoConcurso": "R$ 1.700.000,00"
                }
                """;

        CaixaLotofacilClient client = new CaixaLotofacilClient(HttpClient.newHttpClient(), new ObjectMapper(), "http://localhost");
        ConcursoLotofacil concurso = client.parseConcurso(json);

        assertEquals(3301, concurso.numero());
        assertEquals(15, concurso.dezenas().size());
        assertEquals("2026-03-30", concurso.dataApuracao().toString());
        assertEquals("12345678.90", concurso.valorArrecadado().toString());
    }
}
