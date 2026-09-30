# LocaFilmes API

API REST de uma locadora de filmes, desenvolvida com **Spring Boot 3**, **Spring Data JPA**,
**PostgreSQL** e **Flyway**, como parte do Trabalho 1 (Backend com Spring Boot).

> **Etapa desta entrega:** Autenticação e autorização com **JWT** + **Spring Security** (Aula 10).
> Os CRUDs da Aula 9 continuam iguais, agora protegidos por token e por perfil (`ADMIN` / `CLIENTE`).

## Equipe

| Integrante | Responsabilidades |
|---|---|
| Guilherme Ferreira | Entidades `Usuario` e `Locacao` (CRUD e regras de negócio), tratamento de exceções |
| Diego Marcello | Entidades `Filme`, `Categoria` e `Diretor` (CRUD e regras de negócio), migrations Flyway, Swagger |

## Tecnologias

- Java 17 + Spring Boot 3.3
- Spring Data JPA / Hibernate
- Spring Security 6 + JWT (biblioteca jjwt 0.12) + BCrypt
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
    │   ├── security/               # JwtService, JwtAuthenticationFilter, UsuarioAutenticado, CustomUserDetailsService, handlers 401/403
    │   ├── exception/              # ResourceNotFoundException, BusinessException, GlobalExceptionHandler
    │   └── config/                 # SecurityConfig (JWT stateless + @PreAuthorize), OpenApiConfig (botão Authorize no Swagger)
    └── resources/
        ├── application.yml
        └── db/migration/
            ├── V1__create_schema.sql   # Cria as 7 tabelas (5 entidades + 2 de junção N:N)
            ├── V2__seed_data.sql       # Dados de teste (diretores, categorias, filmes)
            └── V3__seed_usuarios_seguranca.sql  # Usuários admin e cliente (senha em BCrypt)
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

## Segurança (JWT)

**Fluxo:** o cliente faz `POST /api/auth/login` com e-mail e senha, recebe um token JWT e envia esse
token em todas as demais requisições no header `Authorization: Bearer <token>`. A API é *stateless*
(sem sessão). O token expira em 1 hora (`JWT_EXPIRATION_MS`).

**Configuração:** o segredo de assinatura fica em `security.jwt.secret` (`application.yml`) e pode ser
sobrescrito pela variável de ambiente `JWT_SECRET` (mínimo 32 caracteres). O valor padrão é apenas para
desenvolvimento.

### Credenciais de teste (criadas pela migration V3)

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin@locafilmes.com` | `123456` |
| CLIENTE | `cliente@locafilmes.com` | `123456` |

### Regras de autorização

| Recurso | Sem token | CLIENTE | ADMIN |
|---|---|---|---|
| `POST /api/auth/login`, `POST /api/auth/register`, Swagger | ✅ | ✅ | ✅ |
| `GET` filmes, categorias, diretores | ❌ 401 | ✅ | ✅ |
| `POST/PUT/DELETE` filmes, categorias, diretores | ❌ 401 | ❌ 403 | ✅ |
| `/api/usuarios/**` (CRUD de usuários) | ❌ 401 | ❌ 403 | ✅ |
| `/api/locacoes/**` | ❌ 401 | ✅ só as próprias (senão 403) | ✅ todas |

O cadastro público (`/api/auth/register`) sempre cria usuários com perfil `CLIENTE`. Só um `ADMIN`
consegue criar outro `ADMIN`, via `POST /api/usuarios`.

### Como testar no Swagger

1. Abra `http://localhost:8080/swagger-ui.html`.
2. Execute `POST /api/auth/login` com o admin e copie o valor de `token` da resposta.
3. Clique em **Authorize**, cole o token (sem a palavra `Bearer`) e confirme.
4. Agora os demais endpoints funcionam com esse perfil.

### Exemplos de request / response

**Login**
```
POST /api/auth/login
{
  "email": "admin@locafilmes.com",
  "senha": "123456"
}
```
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiraEmSegundos": 3600,
  "usuario": { "id": 1, "nome": "Administrador", "email": "admin@locafilmes.com", "role": "ADMIN" }
}
```

**Cadastro de cliente**
```
POST /api/auth/register
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "senha": "123456"
}
```
```json
{ "id": 3, "nome": "Maria Silva", "email": "maria@email.com", "role": "CLIENTE" }
```

**Chamada autenticada**
```
GET /api/filmes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Sem token ou token inválido (401)**
```json
{
  "timestamp": "2026-09-29T20:00:00",
  "status": 401,
  "erro": "Não autenticado",
  "mensagem": "Token de acesso ausente, inválido ou expirado",
  "caminho": "/api/filmes",
  "detalhes": null
}
```

**Perfil sem permissão (403)** — ex.: CLIENTE tentando `POST /api/filmes`
```json
{
  "timestamp": "2026-09-29T20:01:00",
  "status": 403,
  "erro": "Acesso negado",
  "mensagem": "Você não tem permissão para acessar este recurso",
  "caminho": "/api/filmes",
  "detalhes": null
}
```

**Credenciais erradas (401)**
```json
{
  "status": 401,
  "erro": "Não autenticado",
  "mensagem": "E-mail ou senha inválidos",
  "caminho": "/api/auth/login"
}
```

## Endpoints disponíveis

> Exceto `/api/auth/**`, todos exigem `Authorization: Bearer <token>`. Veja a tabela de permissões acima.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Login (retorna o token JWT) |
| POST | `/api/auth/register` | Auto-cadastro de cliente |
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
| POST | `/api/usuarios` | Cadastrar usuário com qualquer perfil (ADMIN) |
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
- Senhas são gravadas com BCrypt; o cadastro público cria apenas CLIENTE.
- CLIENTE só cria, consulta e devolve as próprias locações.

## Próximas etapas (conforme cronograma do trabalho)

- [x] Autenticação e autorização com JWT (Aula 10)
- [x] Restrição de endpoints por Role (`ADMIN` x `CLIENTE`)
- [ ] Refino de camadas / logs (Aula 11)
- [ ] Ajustes finais + Swagger completo (Aula 12)
- [ ] Frontend + apresentação final (Aula 13)
