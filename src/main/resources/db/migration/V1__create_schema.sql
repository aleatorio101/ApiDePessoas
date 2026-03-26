-- =============================================================
-- V1: Criação do schema inicial
-- Estratégia de herança: JOINED TABLE
--   - Tabela 'pessoa' contém os campos comuns
--   - Tabelas filhas 'pessoa_fisica' e 'pessoa_juridica'
--     armazenam somente os atributos específicos de cada tipo
--   - Esta estratégia é preferível a SINGLE_TABLE para manter
--     integridade relacional (sem colunas nullable por design)
-- =============================================================

CREATE TABLE pessoa (
    id          BIGSERIAL       PRIMARY KEY,
    tipo        VARCHAR(1)      NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    criado_em   TIMESTAMP       NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE pessoa_fisica (
    id      BIGINT  PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    nome    VARCHAR(255)    NOT NULL,
    cpf     VARCHAR(14)     NOT NULL UNIQUE
);

CREATE TABLE pessoa_juridica (
    id              BIGINT  PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    razao_social    VARCHAR(255)    NOT NULL,
    cnpj            VARCHAR(18)     NOT NULL UNIQUE
);

CREATE TABLE endereco (
    id              BIGSERIAL       PRIMARY KEY,
    pessoa_id       BIGINT          NOT NULL REFERENCES pessoa(id) ON DELETE CASCADE,
    logradouro      VARCHAR(255)    NOT NULL,
    numero          VARCHAR(20)     NOT NULL,
    complemento     VARCHAR(100),
    bairro          VARCHAR(100)    NOT NULL,
    cidade          VARCHAR(100)    NOT NULL,
    estado          CHAR(2)         NOT NULL,
    cep             VARCHAR(9)      NOT NULL
);

CREATE INDEX idx_pessoa_tipo        ON pessoa(tipo);
CREATE INDEX idx_endereco_pessoa_id ON endereco(pessoa_id);
CREATE INDEX idx_pessoa_fisica_cpf  ON pessoa_fisica(cpf);
CREATE INDEX idx_pessoa_juridica_cnpj ON pessoa_juridica(cnpj);
