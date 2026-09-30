package com.locafilmes.dto.auth;

import com.locafilmes.dto.usuario.UsuarioResponseDTO;

public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEmSegundos,
        UsuarioResponseDTO usuario
) {
}
