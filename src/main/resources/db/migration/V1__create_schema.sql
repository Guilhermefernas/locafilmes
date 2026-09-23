-- ================================================================
-- LocaFilmes - V1: criação do schema (segue o DER do documento de análise)
-- ================================================================

CREATE TABLE diretores (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    nacionalidade  VARCHAR(60)
);

CREATE TABLE categorias (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(60) NOT NULL,
    CONSTRAINT uk_categorias_nome UNIQUE (nome)
);

CREATE TABLE filmes (
    id                     BIGSERIAL PRIMARY KEY,
    titulo                 VARCHAR(150) NOT NULL,
    ano_lancamento         INTEGER,
    duracao_minutos        INTEGER,
    valor_diaria           NUMERIC(10,2) NOT NULL,
    quantidade_disponivel  INTEGER NOT NULL DEFAULT 0,
    diretor_id             BIGINT,
    CONSTRAINT fk_filmes_diretor FOREIGN KEY (diretor_id) REFERENCES diretores (id)
);

-- N:N Filme <-> Categoria
CREATE TABLE filme_categoria (
    filme_id     BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (filme_id, categoria_id),
    CONSTRAINT fk_fc_filme     FOREIGN KEY (filme_id)     REFERENCES filmes (id)     ON DELETE CASCADE,
    CONSTRAINT fk_fc_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id) ON DELETE CASCADE
);

CREATE TABLE usuarios (
    id     BIGSERIAL PRIMARY KEY,
    nome   VARCHAR(100) NOT NULL,
    email  VARCHAR(150) NOT NULL,
    senha  VARCHAR(255) NOT NULL,
    role   VARCHAR(20)  NOT NULL,
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);

CREATE TABLE locacoes (
    id                        BIGSERIAL PRIMARY KEY,
    usuario_id                BIGINT NOT NULL,
    data_locacao              DATE NOT NULL,
    data_devolucao_prevista   DATE NOT NULL,
    data_devolucao            DATE,
    valor_total               NUMERIC(10,2) NOT NULL,
    status                    VARCHAR(20) NOT NULL,
    CONSTRAINT fk_locacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- N:N Locacao <-> Filme
CREATE TABLE locacao_filme (
    locacao_id  BIGINT NOT NULL,
    filme_id    BIGINT NOT NULL,
    PRIMARY KEY (locacao_id, filme_id),
    CONSTRAINT fk_lf_locacao FOREIGN KEY (locacao_id) REFERENCES locacoes (id) ON DELETE CASCADE,
    CONSTRAINT fk_lf_filme   FOREIGN KEY (filme_id)   REFERENCES filmes (id)
);

CREATE INDEX idx_filmes_diretor   ON filmes (diretor_id);
CREATE INDEX idx_locacoes_usuario ON locacoes (usuario_id);
