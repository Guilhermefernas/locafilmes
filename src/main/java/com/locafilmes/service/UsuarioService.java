package com.locafilmes.service;

import com.locafilmes.dto.usuario.UsuarioRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO criar(UsuarioRequestDTO dto);
    UsuarioResponseDTO buscarPorId(Long id);
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto);
    void excluir(Long id);
}
