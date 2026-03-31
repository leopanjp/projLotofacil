package br.com.lotofacil.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties properties;

    public AppConfig() {
        this.properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar application.properties", e);
        }
    }

    public String databaseUrl() {
        return get("spring.datasource.url", "jdbc:sqlite:lotofacil.db");
    }

    public String databaseUsername() {
        return get("spring.datasource.username", "");
    }

    public String databasePassword() {
        return get("spring.datasource.password", "");
    }

    public String caixaBaseUrl() {
        return get("caixa.lotofacil.base-url", "https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil");
    }

    private String get(String key, String fallback) {
        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return properties.getProperty(key, fallback);
    }
}
