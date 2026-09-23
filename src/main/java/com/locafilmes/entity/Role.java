package com.locafilmes.entity;

/**
 * Perfis de acesso do usuário.
 * ADMIN: cadastra/altera/exclui filmes, diretores e categorias, e gerencia usuários.
 * CLIENTE: consulta o catálogo e realiza locações.
 */
public enum Role {
    ADMIN,
    CLIENTE
}
