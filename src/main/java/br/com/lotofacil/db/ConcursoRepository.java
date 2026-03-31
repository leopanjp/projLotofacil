package br.com.lotofacil.db;

import br.com.lotofacil.caixa.ConcursoLotofacil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ConcursoRepository {

    private final DatabaseConfig databaseConfig;

    public ConcursoRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public boolean existeConcurso(int numeroConcurso) {
        String sql = "SELECT 1 FROM concursos_lotofacil WHERE numero_concurso = ?";
        try (Connection connection = databaseConfig.novaConexao();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, numeroConcurso);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar concurso " + numeroConcurso, e);
        }
    }

    public void salvar(ConcursoLotofacil concurso) {
        if (existeConcurso(concurso.numero())) {
            return;
        }

        String insertConcurso = """
                INSERT INTO concursos_lotofacil (
                  numero_concurso,
                  data_apuracao,
                  dezena_1, dezena_2, dezena_3, dezena_4, dezena_5,
                  dezena_6, dezena_7, dezena_8, dezena_9, dezena_10,
                  dezena_11, dezena_12, dezena_13, dezena_14, dezena_15
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConfig.novaConexao()) {
            connection.setAutoCommit(false);

            try (PreparedStatement concursoStatement = connection.prepareStatement(insertConcurso)) {
                concursoStatement.setInt(1, concurso.numero());
                concursoStatement.setString(2, concurso.dataApuracao().toString());
                for (int i = 0; i < 15; i++) {
                    concursoStatement.setInt(i + 3, concurso.dezenas().get(i));
                }
                concursoStatement.executeUpdate();

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao salvar concurso " + concurso.numero(), e);
        }
    }

    public List<ConcursoComDezenas> listarTodosComDezenas() {
        String sql = """
                SELECT
                    c.numero_concurso,
                    c.data_apuracao,
                    c.dezena_1, c.dezena_2, c.dezena_3, c.dezena_4, c.dezena_5,
                    c.dezena_6, c.dezena_7, c.dezena_8, c.dezena_9, c.dezena_10,
                    c.dezena_11, c.dezena_12, c.dezena_13, c.dezena_14, c.dezena_15
                FROM concursos_lotofacil c
                ORDER BY c.numero_concurso
                """;

        try (Connection connection = databaseConfig.novaConexao();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<ConcursoComDezenas> concursos = new java.util.ArrayList<>();
            while (resultSet.next()) {
                List<Integer> dezenas = new java.util.ArrayList<>();
                for (int i = 1; i <= 15; i++) {
                    dezenas.add(resultSet.getInt("dezena_" + i));
                }

                concursos.add(new ConcursoComDezenas(
                        resultSet.getInt("numero_concurso"),
                        LocalDate.parse(resultSet.getString("data_apuracao")),
                        dezenas
                ));
            }
            return concursos;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar concursos", e);
        }
    }
}
