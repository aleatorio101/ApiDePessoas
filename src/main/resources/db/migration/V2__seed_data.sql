-- =============================================================
-- V2: Carga inicial de dados fictícios (seed)
-- Decisão: usar migration Flyway para o load inicial garante que
-- os dados sejam inseridos junto com o schema, de forma
-- versionada e reproduzível em qualquer ambiente.
-- =============================================================

-- -----------------------------------------------
-- Pessoas Físicas
-- -----------------------------------------------
INSERT INTO pessoa (tipo, email) VALUES ('F', 'joao.silva@email.com');
INSERT INTO pessoa_fisica (id, nome, cpf)
    VALUES (currval('pessoa_id_seq'), 'João da Silva', '123.456.789-09');

INSERT INTO pessoa (tipo, email) VALUES ('F', 'maria.souza@email.com');
INSERT INTO pessoa_fisica (id, nome, cpf)
    VALUES (currval('pessoa_id_seq'), 'Maria Souza', '987.654.321-00');

INSERT INTO pessoa (tipo, email) VALUES ('F', 'carlos.oliveira@email.com');
INSERT INTO pessoa_fisica (id, nome, cpf)
    VALUES (currval('pessoa_id_seq'), 'Carlos Oliveira', '111.222.333-44');

-- -----------------------------------------------
-- Pessoas Jurídicas
-- -----------------------------------------------
INSERT INTO pessoa (tipo, email) VALUES ('J', 'contato@techsolutions.com.br');
INSERT INTO pessoa_juridica (id, razao_social, cnpj)
    VALUES (currval('pessoa_id_seq'), 'Tech Solutions Ltda', '12.345.678/0001-90');

INSERT INTO pessoa (tipo, email) VALUES ('J', 'financeiro@inovacao.com.br');
INSERT INTO pessoa_juridica (id, razao_social, cnpj)
    VALUES (currval('pessoa_id_seq'), 'Inovação Digital S.A.', '98.765.432/0001-10');

-- -----------------------------------------------
-- Endereços para as pessoas acima
-- (ids 1 a 5 na ordem de inserção)
-- -----------------------------------------------
INSERT INTO endereco (pessoa_id, logradouro, numero, bairro, cidade, estado, cep)
    VALUES (1, 'Rua das Flores', '123', 'Centro', 'São Paulo', 'SP', '01310-100');

INSERT INTO endereco (pessoa_id, logradouro, numero, complemento, bairro, cidade, estado, cep)
    VALUES (1, 'Av. Paulista', '1000', 'Ap 42', 'Bela Vista', 'São Paulo', 'SP', '01310-200');

INSERT INTO endereco (pessoa_id, logradouro, numero, bairro, cidade, estado, cep)
    VALUES (2, 'Rua XV de Novembro', '500', 'Centro', 'Curitiba', 'PR', '80020-310');

INSERT INTO endereco (pessoa_id, logradouro, numero, bairro, cidade, estado, cep)
    VALUES (4, 'Rua Néo Alves Martins', '878', 'Zona 03', 'Maringá', 'PR', '87030-000');
