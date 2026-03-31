package br.com.lotofacil;

import br.com.lotofacil.analise.AnaliseRepeticaoService;
import br.com.lotofacil.caixa.CaixaLotofacilClient;
import br.com.lotofacil.config.AppConfig;
import br.com.lotofacil.db.ConcursoRepository;
import br.com.lotofacil.db.DatabaseConfig;
import br.com.lotofacil.sync.SincronizacaoSorteiosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Iniciando aplicação Lotofácil.");
        AppConfig config = new AppConfig();
        DatabaseConfig databaseConfig = new DatabaseConfig(
                config.databaseUrl(),
                config.databaseUsername(),
                config.databasePassword()
        );
        databaseConfig.inicializarSchema();
        LOGGER.info("Schema do banco inicializado.");

        ConcursoRepository repository = new ConcursoRepository(databaseConfig);
        CaixaLotofacilClient caixaClient = new CaixaLotofacilClient(config.caixaBaseUrl());
        SincronizacaoSorteiosService sincronizacao = new SincronizacaoSorteiosService(caixaClient, repository);
        LOGGER.info("Iniciando sincronização dos concursos faltantes.");
        try {
            sincronizacao.buscarNovosSorteios();
        } catch (Exception e) {
            LOGGER.warn("Não foi possível sincronizar agora com a Caixa. A análise seguirá com dados locais.");
        }

        String caminho = args.length > 1 && "--gerar-analise-csv".equals(args[0])
                ? args[1]
                : "saida/analise_repeticoes_lotofacil.csv";
        LOGGER.info("Carregando concursos salvos no banco para análise.");

        var concursos = repository.listarTodosComDezenas();
        if (concursos.size() < 2) {
            LOGGER.warn("Sincronização concluída, mas não há concursos suficientes para análise (mínimo 2). Total atual: {}", concursos.size());
            return;
        }

        AnaliseRepeticaoService analiseService = new AnaliseRepeticaoService();
        LOGGER.info("Gerando análise CSV em {}.", caminho);
        analiseService.gerarCsvAnalise(concursos, Path.of(caminho));
        LOGGER.info("Análise concluída. CSV gerado em: {}", caminho);
    }
}
