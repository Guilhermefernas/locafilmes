package com.locafilmes.dto.filme;

import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;

import java.math.BigDecimal;
import java.util.Set;

public record FilmeResponseDTO(
        Long id,
        String titulo,
        Integer anoLancamento,
        Integer duracaoMinutos,
        BigDecimal valorDiaria,
        Integer quantidadeDisponivel,
        DiretorResponseDTO diretor,
        Set<CategoriaResponseDTO> categorias
) {
}
