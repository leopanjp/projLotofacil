CREATE TABLE IF NOT EXISTS concursos_lotofacil (
    id BIGSERIAL PRIMARY KEY,
    numero_concurso INTEGER NOT NULL UNIQUE,
    data_apuracao DATE NOT NULL,
    dezena_1 SMALLINT NOT NULL,
    dezena_2 SMALLINT NOT NULL,
    dezena_3 SMALLINT NOT NULL,
    dezena_4 SMALLINT NOT NULL,
    dezena_5 SMALLINT NOT NULL,
    dezena_6 SMALLINT NOT NULL,
    dezena_7 SMALLINT NOT NULL,
    dezena_8 SMALLINT NOT NULL,
    dezena_9 SMALLINT NOT NULL,
    dezena_10 SMALLINT NOT NULL,
    dezena_11 SMALLINT NOT NULL,
    dezena_12 SMALLINT NOT NULL,
    dezena_13 SMALLINT NOT NULL,
    dezena_14 SMALLINT NOT NULL,
    dezena_15 SMALLINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_concursos_numero ON concursos_lotofacil(numero_concurso);
