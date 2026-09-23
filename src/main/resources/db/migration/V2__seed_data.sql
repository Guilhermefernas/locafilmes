-- ================================================================
-- LocaFilmes - V2: dados iniciais para facilitar os testes no Postman/Swagger
-- ================================================================

INSERT INTO diretores (nome, nacionalidade) VALUES
  ('Christopher Nolan', 'Britânico'),
  ('Greta Gerwig', 'Americana');

INSERT INTO categorias (nome) VALUES
  ('Ação'),
  ('Comédia'),
  ('Drama');

INSERT INTO filmes (titulo, ano_lancamento, duracao_minutos, valor_diaria, quantidade_disponivel, diretor_id) VALUES
  ('Interestelar', 2014, 169, 9.90, 5, 1),
  ('Barbie', 2023, 114, 7.50, 3, 2);

INSERT INTO filme_categoria (filme_id, categoria_id) VALUES
  (1, 3), -- Interestelar - Drama
  (2, 2); -- Barbie - Comédia

-- Usuários NÃO são inseridos aqui de propósito: crie-os pelo endpoint
-- POST /api/usuarios para que a senha seja criptografada corretamente com BCrypt.
-- Sugestão para os testes (ver README.md):
--   POST /api/usuarios {"nome":"Administrador","email":"admin@locafilmes.com","senha":"123456","role":"ADMIN"}
--   POST /api/usuarios {"nome":"Cliente Teste","email":"cliente@locafilmes.com","senha":"123456","role":"CLIENTE"}
