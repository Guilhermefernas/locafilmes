package com.locafilmes.service;

import com.locafilmes.dto.categoria.CategoriaRequestDTO;
import com.locafilmes.dto.categoria.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO criar(CategoriaRequestDTO dto);
    CategoriaResponseDTO buscarPorId(Long id);
    List<CategoriaResponseDTO> listarTodas();
    CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto);
    void excluir(Long id);
}
