package com.locafilmes.mapper;

import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}
