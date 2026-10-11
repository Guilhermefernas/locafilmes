package com.locafilmes.service;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.security.UsuarioAutenticado;

import java.util.List;

public interface LocacaoService {
    LocacaoResponseDTO criar(LocacaoRequestDTO dto, UsuarioAutenticado logado);
    LocacaoResponseDTO buscarPorId(Long id, UsuarioAutenticado logado);
    List<LocacaoResponseDTO> listar(Long usuarioId, UsuarioAutenticado logado);
    LocacaoResponseDTO registrarDevolucao(Long id, UsuarioAutenticado logado);
}
