package com.locafilmes.mapper;

import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.entity.Filme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmeMapper {

    private final DiretorMapper diretorMapper;
    private final CategoriaMapper categoriaMapper;

    public FilmeResponseDTO toResponse(Filme filme) {
        DiretorResponseDTO diretor = filme.getDiretor() == null ? null : diretorMapper.toResponse(filme.getDiretor());
        Set<CategoriaResponseDTO> categorias = filme.getCategorias() == null
                ? Set.of()
                : filme.getCategorias().stream().map(categoriaMapper::toResponse).collect(Collectors.toSet());

        return new FilmeResponseDTO(
                filme.getId(),
                filme.getTitulo(),
                filme.getAnoLancamento(),
                filme.getDuracaoMinutos(),
                filme.getValorDiaria(),
                filme.getQuantidadeDisponivel(),
                diretor,
                categorias
        );
    }
}
