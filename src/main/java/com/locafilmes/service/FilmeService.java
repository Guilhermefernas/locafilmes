package com.locafilmes.service;

import com.locafilmes.dto.filme.FilmeRequestDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;

import java.util.List;

public interface FilmeService {
    FilmeResponseDTO criar(FilmeRequestDTO dto);
    FilmeResponseDTO buscarPorId(Long id);
    List<FilmeResponseDTO> listarTodos();
    List<FilmeResponseDTO> listarPorCategoria(Long categoriaId);
    FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto);
    void excluir(Long id);
}
