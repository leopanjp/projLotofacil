# Projeto Lotofácil (Java 21)

Projeto em **Java 21** para:
- consultar resultados oficiais da **Lotofácil** no endpoint da Caixa;
- armazenar os concursos em banco local SQLite;
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

Script de criação: `src/main/resources/db/schema.sql`

### 3) Sincronização + análise
Classes:
- `SincronizacaoSorteiosService`: busca e persiste concursos novos
- `AnaliseRepeticaoService`: gera o CSV de repetição após a sincronização

## Requisitos
- Java 21+
- Maven 3.9+

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
