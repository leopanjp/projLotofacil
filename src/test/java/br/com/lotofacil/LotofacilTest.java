package br.com.lotofacil;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LotofacilTest {

    @Test
    void deveGerarSorteioCom15NumerosEntre1e25() {
        SorteioService service = new SorteioService(new SecureRandom());

        Jogo sorteio = service.gerarSorteio();

        assertEquals(15, sorteio.numeros().size());
        assertTrue(sorteio.numeros().stream().allMatch(n -> n >= 1 && n <= 25));
    }

    @Test
    void deveConferirQuantidadeDeAcertosCorretamente() {
        Jogo aposta = new Jogo(new TreeSet<>(Set.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)));
        Jogo sorteio = new Jogo(new TreeSet<>(Set.of(1,2,3,4,5,16,17,18,19,20,21,22,23,24,25)));

        ConferenciaService service = new ConferenciaService();
        ResultadoConferencia resultado = service.conferir(aposta, sorteio);

        assertEquals(5, resultado.acertos());
        assertTrue(!resultado.premiado());
    }
}
