package com.locafilmes.service;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;

import java.util.List;

public interface LocacaoService {
    LocacaoResponseDTO criar(LocacaoRequestDTO dto);
    LocacaoResponseDTO buscarPorId(Long id);
    List<LocacaoResponseDTO> listarTodas();
    List<LocacaoResponseDTO> listarPorUsuario(Long usuarioId);
    LocacaoResponseDTO registrarDevolucao(Long id);
}
