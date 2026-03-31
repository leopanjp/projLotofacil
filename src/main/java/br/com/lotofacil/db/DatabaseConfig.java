package br.com.lotofacil.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private final String jdbcUrl;

    public DatabaseConfig(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public Connection novaConexao() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public void inicializarSchema() {
        try (Connection connection = novaConexao();
             Statement statement = connection.createStatement()) {
            String script = carregarSchema();
            statement.executeUpdate(script);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Falha ao inicializar schema do banco", e);
        }
    }

    private String carregarSchema() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {
            if (inputStream == null) {
                throw new IOException("Arquivo db/schema.sql não encontrado no classpath.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
