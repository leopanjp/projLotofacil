# Projeto Lotofácil (Java 21)

Projeto em **Java 21** para:
- consultar resultados oficiais da **Lotofácil** no endpoint da Caixa;
- armazenar os concursos em banco local (SQLite para testes) ou PostgreSQL por configuração;
- buscar todos os concursos faltantes ao iniciar a aplicação;
- executar a análise somente após a sincronização concluir;
- conferir apostas com 15 números de 1 a 25.

## Arquitetura

### 1) Integração com Caixa
Classe: `CaixaLotofacilClient`
- Busca o último concurso (`/api/lotofacil`)
- Busca concurso por número (`/api/lotofacil/{numero}`)
- A página oficial da Lotofácil (`/Paginas/Lotofacil.aspx`) exibe o resultado por concurso e navegação anterior/próximo, e o cliente usa essa estratégia para consultar cada concurso individualmente.

> Endpoint padrão usado: `https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil`

### 2) Banco de dados
Foi projetado um banco relacional com a tabela:
- `concursos_lotofacil`, contendo:
  - dados do concurso (`numero_concurso`, `data_apuracao`);
  - dezenas em colunas separadas (`dezena_1` até `dezena_15`).

Scripts de criação:
- `src/main/resources/db/schema-sqlite.sql`
- `src/main/resources/db/schema-postgres.sql`

### 3) Sincronização + análise
Classes:
- `SincronizacaoSorteiosService`: busca e persiste concursos novos
- `AnaliseRepeticaoService`: gera o CSV de repetição após a sincronização

## Requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL (opcional, para execução com datasource externo)

## Configuração
Arquivo: `src/main/resources/application.properties`

- Por padrão, o projeto roda local com SQLite:
  - `spring.datasource.url=jdbc:sqlite:lotofacil.db`
  - `spring.datasource.username=`
  - `spring.datasource.password=`
- Para PostgreSQL, sobrescreva via `application.properties` ou variáveis de ambiente:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- `caixa.lotofacil.base-url`

## Executar testes
```bash
mvn test
```

## Rodar aplicação
```bash
mvn -q exec:java -Dexec.mainClass="br.com.lotofacil.App" -DskipTests
```

A aplicação inicializa o schema, sincroniza todos os concursos faltantes e em seguida gera o CSV.

## Gerar CSV de análise de repetição
Para personalizar o caminho do CSV:

```bash
mvn -q exec:java -Dexec.mainClass="br.com.lotofacil.App" -Dexec.args="--gerar-analise-csv saida/analise_repeticoes_lotofacil.csv" -DskipTests
```

O relatório inclui:
- repetição de dezenas entre concursos consecutivos;
- maior sequência de concursos seguidos por dezena;
- conjuntos frequentes (pares e trincas) que se repetem em concursos variados.

## Rodar com Docker (PostgreSQL + App)
O projeto inclui `Dockerfile` (build multi-stage) e `docker-compose.yml`.

Ao subir com compose:
- o PostgreSQL é iniciado;
- a aplicação Java é compilada no build da imagem;
- o container da app só inicia depois do banco saudável.

```bash
docker compose up --build
```

Arquivos relevantes:
- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`
