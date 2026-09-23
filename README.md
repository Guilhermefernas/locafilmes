# LocaFilmes API

API REST de uma locadora de filmes, desenvolvida com **Spring Boot 3**, **Spring Data JPA**,
**PostgreSQL** e **Flyway**, como parte do Trabalho 1 (Backend com Spring Boot).

> **Etapa desta entrega:** CRUDs básicos das entidades e seus relacionamentos (Aula 9).
> Autenticação/autorização com **JWT** e restrição por perfil (`ADMIN`/`CLIENTE`) serão
> implementadas na próxima etapa (Aula 10). Por isso, todos os endpoints estão liberados
> (`permitAll`) por enquanto, para facilitar os testes no Postman/Swagger. A senha do
> usuário já é gravada criptografada com **BCrypt**.

## Equipe

| Integrante | Responsabilidades |
|---|---|
| Guilherme Ferreira | Entidades `Usuario` e `Locacao` (CRUD e regras de negócio), tratamento de exceções |
| Diego Marcello | Entidades `Filme`, `Categoria` e `Diretor` (CRUD e regras de negócio), migrations Flyway, Swagger |

## Tecnologias

- Java 17 + Spring Boot 3.3
- Spring Data JPA / Hibernate
- Spring Security (BCrypt — JWT na próxima etapa)
- PostgreSQL 16
- Flyway (versionamento do banco)
- Bean Validation (`@NotBlank`, `@Email`, `@Positive`, etc.)
- Springdoc OpenAPI (Swagger UI)
- Lombok

## Estrutura de pastas

```
locafilmes/
├── pom.xml
├── docker-compose.yml              # PostgreSQL para desenvolvimento local
└── src/main/
    ├── java/com/locafilmes/
    │   ├── LocaFilmesApplication.java
    │   ├── entity/                 # Diretor, Categoria, Filme, Usuario, Locacao, Role, StatusLocacao
    │   ├── repository/             # Interfaces Spring Data JPA
    │   ├── dto/                    # RequestDTO / ResponseDTO por entidade (nunca expõe a entity)
    │   ├── service/  + service/impl/  # Regras de negócio
    │   ├── controller/             # Endpoints REST
    │   ├── exception/              # ResourceNotFoundException, BusinessException, GlobalExceptionHandler
    │   └── config/                 # SecurityConfig (BCrypt + libera endpoints por ora)
    └── resources/
        ├── application.yml
        └── db/migration/
            ├── V1__create_schema.sql   # Cria as 7 tabelas (5 entidades + 2 de junção N:N)
            └── V2__seed_data.sql       # Dados de teste (diretores, categorias, filmes)
```

## Como rodar o projeto

### 1. Subir o banco de dados PostgreSQL

Com Docker instalado:

```bash
docker-compose up -d
```

Isso cria o banco `locafilmes` em `localhost:5432` (usuário `postgres`, senha `postgres`).
Se preferir um PostgreSQL já instalado localmente, apenas crie o banco `locafilmes` e
ajuste `src/main/resources/application.yml` com suas credenciais.

### 2. Rodar a aplicação

Este projeto **não inclui o Maven Wrapper** (`mvnw`). Rode com o Maven instalado na sua
máquina, ou simplesmente abra o projeto no IntelliJ/Eclipse/VS Code e execute a classe
`LocaFilmesApplication` (o próprio Maven do projeto e o Java 17 são baixados/detectados
automaticamente pela IDE).

```bash
mvn spring-boot:run
```

> Se preferir gerar o wrapper depois, com o Maven já instalado rode `mvn -N wrapper:wrapper`
> na raiz do projeto e passe a poder usar `./mvnw spring-boot:run`.

Ao iniciar, o Flyway roda automaticamente as migrations e a API sobe em `http://localhost:8080`.

### 3. Documentação interativa (Swagger)

```
http://localhost:8080/swagger-ui.html
```

## Credenciais de teste

Como o login com JWT ainda não foi implementado nesta etapa, crie os usuários de teste
pelo próprio endpoint (a senha é criptografada automaticamente com BCrypt):

```
POST /api/usuarios
{
  "nome": "Administrador",
  "email": "admin@locafilmes.com",
  "senha": "123456",
  "role": "ADMIN"
}
```

```
POST /api/usuarios
{
  "nome": "Cliente Teste",
  "email": "cliente@locafilmes.com",
  "senha": "123456",
  "role": "CLIENTE"
}
```

## Endpoints disponíveis

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/diretores` | Cadastrar diretor |
| GET | `/api/diretores` | Listar diretores |
| GET | `/api/diretores/{id}` | Buscar diretor por id |
| PUT | `/api/diretores/{id}` | Atualizar diretor |
| DELETE | `/api/diretores/{id}` | Excluir diretor |
| POST | `/api/categorias` | Cadastrar categoria |
| GET | `/api/categorias` | Listar categorias |
| GET/PUT/DELETE | `/api/categorias/{id}` | Buscar / atualizar / excluir categoria |
| POST | `/api/filmes` | Cadastrar filme (vincula diretor + categorias) |
| GET | `/api/filmes` | Listar todos os filmes |
| GET | `/api/filmes?categoriaId=1` | **RF06** Listar filmes por categoria |
| GET/PUT/DELETE | `/api/filmes/{id}` | Buscar / atualizar / excluir filme |
| POST | `/api/usuarios` | Cadastrar usuário (senha criptografada) |
| GET | `/api/usuarios` | Listar usuários |
| GET/PUT/DELETE | `/api/usuarios/{id}` | Buscar / atualizar / excluir usuário |
| POST | `/api/locacoes` | **RF07** Registrar locação (um ou mais filmes) |
| GET | `/api/locacoes` | Listar todas as locações |
| GET | `/api/locacoes?usuarioId=1` | **RF09** Listar locações de um cliente |
| GET | `/api/locacoes/{id}` | Buscar locação por id |
| PATCH | `/api/locacoes/{id}/devolucao` | **RF08** Registrar devolução |

## Exemplos de request / response

### Cadastrar diretor
```
POST /api/diretores
{
  "nome": "Christopher Nolan",
  "nacionalidade": "Britânico"
}
```
```json
{
  "id": 1,
  "nome": "Christopher Nolan",
  "nacionalidade": "Britânico"
}
```

### Cadastrar filme
```
POST /api/filmes
{
  "titulo": "Interestelar",
  "anoLancamento": 2014,
  "duracaoMinutos": 169,
  "valorDiaria": 9.90,
  "quantidadeDisponivel": 5,
  "diretorId": 1,
  "categoriaIds": [3]
}
```
```json
{
  "id": 1,
  "titulo": "Interestelar",
  "anoLancamento": 2014,
  "duracaoMinutos": 169,
  "valorDiaria": 9.90,
  "quantidadeDisponivel": 5,
  "diretor": { "id": 1, "nome": "Christopher Nolan", "nacionalidade": "Britânico" },
  "categorias": [ { "id": 3, "nome": "Drama" } ]
}
```

### Registrar uma locação
```
POST /api/locacoes
{
  "usuarioId": 2,
  "filmeIds": [1, 2],
  "dataDevolucaoPrevista": "2026-10-05"
}
```
```json
{
  "id": 1,
  "usuario": { "id": 2, "nome": "Cliente Teste", "email": "cliente@locafilmes.com", "role": "CLIENTE" },
  "dataLocacao": "2026-09-23",
  "dataDevolucaoPrevista": "2026-10-05",
  "dataDevolucao": null,
  "valorTotal": 208.80,
  "status": "ATIVA",
  "filmes": [ { "id": 1, "titulo": "Interestelar", "...": "..." } ]
}
```
> Valor total = soma das diárias dos filmes × dias entre a locação e a devolução prevista.
> A quantidade disponível de cada filme é decrementada automaticamente.

### Registrar devolução
```
PATCH /api/locacoes/1/devolucao
```
```json
{
  "id": 1,
  "status": "FINALIZADA",
  "dataDevolucao": "2026-09-25",
  "...": "..."
}
```
> A quantidade disponível de cada filme é incrementada automaticamente.

### Exemplo de erro de validação (400)
```json
{
  "timestamp": "2026-09-23T22:10:00",
  "status": 400,
  "erro": "Erro de validação",
  "mensagem": "Um ou mais campos estão inválidos",
  "caminho": "/api/filmes",
  "detalhes": ["valorDiaria: O valor da diária deve ser positivo"]
}
```

### Exemplo de regra de negócio violada (422)
```json
{
  "timestamp": "2026-09-23T22:12:00",
  "status": 422,
  "erro": "Regra de negócio violada",
  "mensagem": "O filme 'Interestelar' não possui unidades disponíveis para locação",
  "caminho": "/api/locacoes",
  "detalhes": null
}
```

## Relacionamentos implementados

- `Diretor` **1:N** `Filme`
- `Filme` **N:N** `Categoria` (tabela `filme_categoria`)
- `Usuario` **1:N** `Locacao`
- `Locacao` **N:N** `Filme` (tabela `locacao_filme`)

## Regras de negócio implementadas

- O e-mail do usuário não pode se repetir.
- Só é possível alugar um filme com quantidade disponível maior que zero.
- O valor total da locação é a soma das diárias dos filmes multiplicada pelos dias alugados.
- Ao alugar, a quantidade disponível do filme diminui; ao devolver, ela aumenta.
- Nome de categoria não pode se repetir.

## Próximas etapas (conforme cronograma do trabalho)

- [ ] Autenticação e autorização com JWT (Aula 10)
- [ ] Restrição de endpoints por Role (`ADMIN` x `CLIENTE`)
- [ ] Refino de camadas / logs (Aula 11)
- [ ] Ajustes finais + Swagger completo (Aula 12)
- [ ] Frontend + apresentação final (Aula 13)
