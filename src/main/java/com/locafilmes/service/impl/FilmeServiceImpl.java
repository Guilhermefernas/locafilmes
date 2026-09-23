package com.locafilmes.service.impl;

import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.dto.filme.FilmeRequestDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.entity.Categoria;
import com.locafilmes.entity.Diretor;
import com.locafilmes.entity.Filme;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.repository.CategoriaRepository;
import com.locafilmes.repository.DiretorRepository;
import com.locafilmes.repository.FilmeRepository;
import com.locafilmes.service.FilmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmeServiceImpl implements FilmeService {

    private final FilmeRepository filmeRepository;
    private final DiretorRepository diretorRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public FilmeResponseDTO criar(FilmeRequestDTO dto) {
        Diretor diretor = buscarDiretor(dto.diretorId());
        Set<Categoria> categorias = buscarCategorias(dto.categoriaIds());

        Filme filme = Filme.builder()
                .titulo(dto.titulo())
                .anoLancamento(dto.anoLancamento())
                .duracaoMinutos(dto.duracaoMinutos())
                .valorDiaria(dto.valorDiaria())
                .quantidadeDisponivel(dto.quantidadeDisponivel())
                .diretor(diretor)
                .categorias(categorias)
                .build();

        return toResponseDTO(filmeRepository.save(filme));
    }

    @Override
    @Transactional(readOnly = true)
    public FilmeResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarTodos() {
        return filmeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarPorCategoria(Long categoriaId) {
        return filmeRepository.findByCategorias_IdOrderByTituloAsc(categoriaId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto) {
        Filme filme = buscarEntidade(id);
        Diretor diretor = buscarDiretor(dto.diretorId());
        Set<Categoria> categorias = buscarCategorias(dto.categoriaIds());

        filme.setTitulo(dto.titulo());
        filme.setAnoLancamento(dto.anoLancamento());
        filme.setDuracaoMinutos(dto.duracaoMinutos());
        filme.setValorDiaria(dto.valorDiaria());
        filme.setQuantidadeDisponivel(dto.quantidadeDisponivel());
        filme.setDiretor(diretor);
        filme.setCategorias(categorias);

        return toResponseDTO(filmeRepository.save(filme));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Filme filme = buscarEntidade(id);
        filmeRepository.delete(filme);
    }

    private Filme buscarEntidade(Long id) {
        return filmeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", id));
    }

    private Diretor buscarDiretor(Long diretorId) {
        return diretorRepository.findById(diretorId)
                .orElseThrow(() -> ResourceNotFoundException.of("Diretor", diretorId));
    }

    private Set<Categoria> buscarCategorias(Set<Long> categoriaIds) {
        Set<Categoria> categorias = new HashSet<>(categoriaRepository.findAllById(categoriaIds));
        if (categorias.size() != categoriaIds.size()) {
            throw new ResourceNotFoundException("Uma ou mais categorias informadas não foram encontradas");
        }
        return categorias;
    }

    private FilmeResponseDTO toResponseDTO(Filme filme) {
        DiretorResponseDTO diretorDTO = filme.getDiretor() == null ? null : new DiretorResponseDTO(
                filme.getDiretor().getId(),
                filme.getDiretor().getNome(),
                filme.getDiretor().getNacionalidade()
        );

        Set<CategoriaResponseDTO> categoriasDTO = filme.getCategorias().stream()
                .map(c -> new CategoriaResponseDTO(c.getId(), c.getNome()))
                .collect(Collectors.toSet());

        return new FilmeResponseDTO(
                filme.getId(),
                filme.getTitulo(),
                filme.getAnoLancamento(),
                filme.getDuracaoMinutos(),
                filme.getValorDiaria(),
                filme.getQuantidadeDisponivel(),
                diretorDTO,
                categoriasDTO
        );
    }
}
