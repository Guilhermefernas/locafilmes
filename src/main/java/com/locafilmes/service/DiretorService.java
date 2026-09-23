package com.locafilmes.service;

import com.locafilmes.dto.diretor.DiretorRequestDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;

import java.util.List;

public interface DiretorService {
    DiretorResponseDTO criar(DiretorRequestDTO dto);
    DiretorResponseDTO buscarPorId(Long id);
    List<DiretorResponseDTO> listarTodos();
    DiretorResponseDTO atualizar(Long id, DiretorRequestDTO dto);
    void excluir(Long id);
}
