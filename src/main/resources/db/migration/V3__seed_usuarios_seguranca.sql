-- ================================================================
-- LocaFilmes - V3: usuários iniciais para autenticação JWT
-- Senha de ambos: 123456 (armazenada com hash BCrypt, nunca em texto puro)
-- Sem um ADMIN previamente cadastrado não seria possível usar os endpoints
-- protegidos (o CRUD de usuários agora exige perfil ADMIN).
-- ON CONFLICT: não sobrescreve caso o e-mail já exista no banco local.
-- ================================================================

INSERT INTO usuarios (nome, email, senha, role) VALUES
  ('Administrador', 'admin@locafilmes.com',   '$2b$10$A0naw2Xw/9kkzvrJaX.kz.cv6LVlv8Hff0VvzClavbByn0t0Iatc2', 'ADMIN'),
  ('Cliente Teste', 'cliente@locafilmes.com', '$2b$10$S83FGCNsaSMooM7xC0teB.9UQ5hIxlch5iucVLfKRQo4gKGNzGv4.', 'CLIENTE')
ON CONFLICT (email) DO NOTHING;
