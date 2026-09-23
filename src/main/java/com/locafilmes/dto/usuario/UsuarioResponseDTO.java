package com.locafilmes.dto.usuario;

import com.locafilmes.entity.Role;

// Nunca expõe a senha do usuário.
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {
}
