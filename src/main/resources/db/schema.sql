DROP TABLE IF EXISTS concursos_lotofacil_dezenas;
DROP TABLE IF EXISTS concursos_lotofacil;

CREATE TABLE IF NOT EXISTS concursos_lotofacil (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_concurso INTEGER NOT NULL UNIQUE,
    data_apuracao TEXT NOT NULL,
    dezena_1 INTEGER NOT NULL,
    dezena_2 INTEGER NOT NULL,
    dezena_3 INTEGER NOT NULL,
    dezena_4 INTEGER NOT NULL,
    dezena_5 INTEGER NOT NULL,
    dezena_6 INTEGER NOT NULL,
    dezena_7 INTEGER NOT NULL,
    dezena_8 INTEGER NOT NULL,
    dezena_9 INTEGER NOT NULL,
    dezena_10 INTEGER NOT NULL,
    dezena_11 INTEGER NOT NULL,
    dezena_12 INTEGER NOT NULL,
    dezena_13 INTEGER NOT NULL,
    dezena_14 INTEGER NOT NULL,
    dezena_15 INTEGER NOT NULL,
    criado_em TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_concursos_numero ON concursos_lotofacil(numero_concurso);
