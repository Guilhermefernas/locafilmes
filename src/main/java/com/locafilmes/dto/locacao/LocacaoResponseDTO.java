package com.locafilmes.dto.locacao;

import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.StatusLocacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record LocacaoResponseDTO(
        Long id,
        UsuarioResponseDTO usuario,
        LocalDate dataLocacao,
        LocalDate dataDevolucaoPrevista,
        LocalDate dataDevolucao,
        BigDecimal valorTotal,
        StatusLocacao status,
        Set<FilmeResponseDTO> filmes
) {
}
