# Pessoas API

API REST para gerenciamento de **Pessoas Físicas e Jurídicas**, desenvolvida como parte do processo seletivo da Webpublico.

---

## Tecnologias

| Tecnologia | Versão | Justificativa |
|---|---|---|
| Java | 8 | Alinhado ao ecossistema da empresa |
| Spring Boot | 2.7.18 | Última versão estável compatível com Java 8 |
| Spring Data JPA + Hibernate | 5.6.x | ORM robusto com suporte a herança JOINED TABLE |
| PostgreSQL | 15 | Banco relacional indicado para o projeto |
| Flyway | 8.x | Versionamento e migração do schema de banco |
| Bean Validation (JSR-380) | 2.0 | Validação declarativa com suporte a CPF/CNPJ |
| SpringDoc OpenAPI | 1.7.0 | Documentação interativa Swagger UI |
| Lombok | 1.18.x | Redução de boilerplate (getters, builders) |
| JUnit 5 + Mockito | 5.x / 4.x | Testes unitários e de integração da camada web |
| Docker + Docker Compose | — | Containerização e orquestração |

---

## Arquitetura

### Richardson Maturity Level 2

A API segue o **Nível 2 do Modelo de Maturidade de Richardson**:

- **Recursos identificados por URI**: `/pessoas`, `/pessoas/fisicas/{id}`, `/pessoas/juridicas/{id}`
- **Verbos HTTP semânticos**: `POST`, `GET`, `PUT`, `DELETE`
- **Códigos de status precisos**: `201 Created`, `200 OK`, `204 No Content`, `404 Not Found`, `409 Conflict`, `422 Unprocessable Entity`
- **Location header**: o `POST` retorna o URI do recurso criado no header `Location`

### Estrutura de pacotes

```
src/main/java/com/webpublico/pessoas/
├── PessoasApiApplication.java     # Ponto de entrada
├── config/
│   └── OpenApiConfig.java         # Configuração do Swagger
├── controller/
│   ├── PessoaController.java      # GET /pessoas (listagem geral)
│   ├── PessoaFisicaController.java
│   └── PessoaJuridicaController.java
├── service/
│   └── PessoaService.java         # Regras de negócio
├── repository/
│   └── PessoaRepository.java      # Acesso a dados (JPQL explícito)
├── domain/
│   ├── Pessoa.java                # Entidade base (herança JOINED TABLE)
│   ├── PessoaFisica.java
│   ├── PessoaJuridica.java
│   └── Endereco.java
├── dto/
│   ├── request/                   # Objetos de entrada (com validações)
│   └── response/                  # Objetos de saída
├── mapper/
│   └── PessoaMapper.java          # Conversão domain ↔ DTO (manual)
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ApiError.java
    ├── PessoaNaoEncontradaException.java
    ├── DocumentoDuplicadoException.java
    └── TipoPessoaIncompativelException.java
```

### Estratégia de herança JPA: JOINED TABLE

Optou-se por `InheritanceType.JOINED` em vez de `SINGLE_TABLE` porque:

- **Integridade relacional**: campos específicos de cada tipo (`nome`, `cpf`, `razao_social`, `cnpj`) são `NOT NULL` nas suas respectivas tabelas, sem colunas nulas por design
- **Normalização**: dados não duplicados, schema mais limpo
- **Custo**: JOINs a mais nas queries — aceitável para o volume esperado

### Carga inicial de dados (seed)

Os dados fictícios são inseridos via **migration Flyway** (`V2__seed_data.sql`). Essa abordagem foi escolhida por:

- Ser **versionada** junto com o schema, garantindo reprodutibilidade em qualquer ambiente
- Executar **automaticamente** na inicialização, sem código Java extra
- Ser facilmente **reversível** ou substituível em ambientes de produção

---

## Como executar

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução
- [Git](https://git-scm.com/)

### 1. Clonar o repositório

```bash
git clone <URL_DO_REPOSITORIO>
cd pessoas-api
```

### 2. Subir a aplicação completa com Docker Compose

```bash
docker compose up --build
```

Isso irá:
1. Compilar o projeto Java dentro de um container Maven (build multi-estágio)
2. Subir o PostgreSQL 15
3. Aguardar o banco estar saudável (healthcheck)
4. Iniciar a aplicação Spring Boot na porta `8080`
5. Executar automaticamente as migrations Flyway (schema + seed)

### 3. Verificar que está funcionando

```bash
curl http://localhost:8080/pessoas
```

Deve retornar a lista de pessoas pré-cadastradas.

### Para parar

```bash
docker compose down
```

Para remover também o volume de dados do banco:

```bash
docker compose down -v
```

---

## Documentação interativa (Swagger UI)

Com a aplicação rodando, acesse:

**http://localhost:8080/swagger-ui.html**

Lá você pode explorar e testar todos os endpoints diretamente pelo navegador.

A especificação OpenAPI em JSON está em: **http://localhost:8080/api-docs**

---

## Endpoints

### Listagem geral

| Método | URI | Descrição | Status de sucesso |
|---|---|---|---|
| `GET` | `/pessoas` | Lista todas as pessoas (resumo) | `200 OK` |

### Pessoas Físicas

| Método | URI | Descrição | Status de sucesso |
|---|---|---|---|
| `POST` | `/pessoas/fisicas` | Cadastrar pessoa física | `201 Created` |
| `GET` | `/pessoas/fisicas/{id}` | Buscar pessoa física por ID | `200 OK` |
| `PUT` | `/pessoas/fisicas/{id}` | Atualizar pessoa física | `200 OK` |
| `DELETE` | `/pessoas/fisicas/{id}` | Remover pessoa física | `204 No Content` |

### Pessoas Jurídicas

| Método | URI | Descrição | Status de sucesso |
|---|---|---|---|
| `POST` | `/pessoas/juridicas` | Cadastrar pessoa jurídica | `201 Created` |
| `GET` | `/pessoas/juridicas/{id}` | Buscar pessoa jurídica por ID | `200 OK` |
| `PUT` | `/pessoas/juridicas/{id}` | Atualizar pessoa jurídica | `200 OK` |
| `DELETE` | `/pessoas/juridicas/{id}` | Remover pessoa jurídica | `204 No Content` |

### Códigos de erro

| Status | Situação |
|---|---|
| `404 Not Found` | ID não encontrado |
| `409 Conflict` | CPF, CNPJ ou e-mail já cadastrado |
| `422 Unprocessable Entity` | Campos inválidos (CPF/CNPJ inválido, campo obrigatório vazio, etc.) ou tipo incompatível |

---

## Exemplos de uso

### Cadastrar Pessoa Física

```bash
curl -X POST http://localhost:8080/pessoas/fisicas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Lucas Domingues",
    "cpf": "529.982.247-25",
    "email": "lucas@email.com",
    "enderecos": [
      {
        "logradouro": "Rua das Flores",
        "numero": "100",
        "bairro": "Centro",
        "cidade": "Maringá",
        "estado": "PR",
        "cep": "87001-000"
      }
    ]
  }'
```

### Cadastrar Pessoa Jurídica

```bash
curl -X POST http://localhost:8080/pessoas/juridicas \
  -H "Content-Type: application/json" \
  -d '{
    "razaoSocial": "Minha Empresa Ltda",
    "cnpj": "11.222.333/0001-81",
    "email": "contato@minhaempresa.com",
    "enderecos": []
  }'
```

### Listar todas as pessoas

```bash
curl http://localhost:8080/pessoas
```

### Buscar por ID

```bash
curl http://localhost:8080/pessoas/fisicas/1
```

### Remover

```bash
curl -X DELETE http://localhost:8080/pessoas/fisicas/1
```

---

## Executando os testes

Para rodar os testes sem Docker (requer Java 8 e Maven instalados localmente):

```bash
./mvnw test
```

Os testes utilizam perfil `test` com banco H2 em memória — sem necessidade de PostgreSQL.

---

## Dados pré-carregados (seed)

A aplicação inicia com os seguintes registros fictícios:

| Tipo | Nome / Razão Social | CPF / CNPJ | E-mail |
|---|---|---|---|
| Física | João da Silva | 123.456.789-09 | joao.silva@email.com |
| Física | Maria Souza | 987.654.321-00 | maria.souza@email.com |
| Física | Carlos Oliveira | 111.222.333-44 | carlos.oliveira@email.com |
| Jurídica | Tech Solutions Ltda | 12.345.678/0001-90 | contato@techsolutions.com.br |
| Jurídica | Inovação Digital S.A. | 98.765.432/0001-10 | financeiro@inovacao.com.br |
