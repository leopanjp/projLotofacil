package br.com.lotofacil.sync;

import br.com.lotofacil.caixa.CaixaLotofacilClient;
import br.com.lotofacil.caixa.ConcursoLotofacil;
import br.com.lotofacil.db.ConcursoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SincronizacaoSorteiosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacaoSorteiosService.class);

    private final CaixaLotofacilClient caixaClient;
    private final ConcursoRepository repository;

    public SincronizacaoSorteiosService(CaixaLotofacilClient caixaClient, ConcursoRepository repository) {
        this.caixaClient = caixaClient;
        this.repository = repository;
    }

    public void buscarNovosSorteios() {
        LOGGER.info("Consultando último concurso na origem oficial da Caixa.");
        ConcursoLotofacil ultimo = caixaClient.buscarUltimoConcurso();
        LOGGER.info("Último concurso disponível na Caixa: {} ({}).", ultimo.numero(), ultimo.dataApuracao());

        int totalSalvos = 0;
        for (int numero = ultimo.numero(); numero >= 1; numero--) {
            if (repository.existeConcurso(numero)) {
                LOGGER.info("Encontrado concurso {} já salvo no banco. Encerrando backfill.", numero);
                break;
            }

            try {
                ConcursoLotofacil concurso = numero == ultimo.numero()
                        ? ultimo
                        : caixaClient.buscarConcursoPorNumero(numero);

                repository.salvar(concurso);
                totalSalvos++;
                if (totalSalvos % 50 == 0) {
                    LOGGER.info("Progresso do backfill: {} concursos novos salvos até agora...", totalSalvos);
                }
            } catch (Exception e) {
                LOGGER.warn("Não foi possível salvar concurso {}. Seguindo para o próximo.", numero, e);
            }
        }

        LOGGER.info("Sincronização finalizada. {} novo(s) concurso(s) salvo(s).", totalSalvos);
    }
}
