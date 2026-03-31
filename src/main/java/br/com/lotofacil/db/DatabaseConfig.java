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
    private final String username;
    private final String password;

    public DatabaseConfig(String jdbcUrl) {
        this(jdbcUrl, "", "");
    }

    public DatabaseConfig(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public Connection novaConexao() throws SQLException {
        if (username == null || username.isBlank()) {
            return DriverManager.getConnection(jdbcUrl);
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void inicializarSchema() {
        try (Connection connection = novaConexao();
             Statement statement = connection.createStatement()) {
            String script = carregarSchema();
            for (String sql : script.split(";")) {
                String comando = sql.trim();
                if (!comando.isBlank()) {
                    statement.execute(comando);
                }
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Falha ao inicializar schema do banco", e);
        }
    }

    private String carregarSchema() throws IOException {
        String schemaPath = jdbcUrl.contains("postgresql")
                ? "db/schema-postgres.sql"
                : "db/schema-sqlite.sql";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(schemaPath)) {
            if (inputStream == null) {
                throw new IOException("Arquivo " + schemaPath + " não encontrado no classpath.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
