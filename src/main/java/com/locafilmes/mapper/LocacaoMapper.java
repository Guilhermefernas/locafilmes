package com.locafilmes.mapper;

import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.entity.Locacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocacaoMapper {

    private final UsuarioMapper usuarioMapper;
    private final FilmeMapper filmeMapper;

    public LocacaoResponseDTO toResponse(Locacao locacao) {
        Set<FilmeResponseDTO> filmes = locacao.getFilmes() == null
                ? Set.of()
                : locacao.getFilmes().stream().map(filmeMapper::toResponse).collect(Collectors.toSet());

        return new LocacaoResponseDTO(
                locacao.getId(),
                usuarioMapper.toResponse(locacao.getUsuario()),
                locacao.getDataLocacao(),
                locacao.getDataDevolucaoPrevista(),
                locacao.getDataDevolucao(),
                locacao.getValorTotal(),
                locacao.getStatus(),
                filmes
        );
    }
}
