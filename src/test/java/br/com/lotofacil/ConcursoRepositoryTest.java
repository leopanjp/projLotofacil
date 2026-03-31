package br.com.lotofacil;

import br.com.lotofacil.caixa.ConcursoLotofacil;
import br.com.lotofacil.db.ConcursoRepository;
import br.com.lotofacil.db.DatabaseConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcursoRepositoryTest {

    @Test
    void deveSalvarConcursoNoBanco() throws IOException {
        Path dbFile = Path.of("target", "concurso-repository-test.db");
        Files.deleteIfExists(dbFile);

        DatabaseConfig db = new DatabaseConfig("jdbc:sqlite:" + dbFile);
        db.inicializarSchema();
        ConcursoRepository repository = new ConcursoRepository(db);

        ConcursoLotofacil concurso = new ConcursoLotofacil(
                3301,
                LocalDate.of(2026, 3, 30),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                new BigDecimal("123456.78"),
                false,
                new BigDecimal("1700000.00")
        );

        repository.salvar(concurso);

        assertTrue(repository.existeConcurso(3301));
    }
}
