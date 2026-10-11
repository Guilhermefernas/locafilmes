package com.locafilmes.service.impl;

import com.locafilmes.dto.filme.FilmeRequestDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.entity.Categoria;
import com.locafilmes.entity.Diretor;
import com.locafilmes.entity.Filme;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.mapper.FilmeMapper;
import com.locafilmes.repository.CategoriaRepository;
import com.locafilmes.repository.DiretorRepository;
import com.locafilmes.repository.FilmeRepository;
import com.locafilmes.service.FilmeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmeServiceImpl implements FilmeService {

    private final FilmeRepository filmeRepository;
    private final DiretorRepository diretorRepository;
    private final CategoriaRepository categoriaRepository;
    private final FilmeMapper filmeMapper;

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

        Filme salvo = filmeRepository.save(filme);
        log.info("Filme criado: id={}", salvo.getId());
        return filmeMapper.toResponse(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public FilmeResponseDTO buscarPorId(Long id) {
        return filmeMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarTodos() {
        return filmeRepository.findAll().stream()
                .map(filmeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw ResourceNotFoundException.of("Categoria", categoriaId);
        }
        return filmeRepository.findByCategorias_IdOrderByTituloAsc(categoriaId).stream()
                .map(filmeMapper::toResponse)
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

        Filme salvo = filmeRepository.save(filme);
        log.info("Filme atualizado: id={}", salvo.getId());
        return filmeMapper.toResponse(salvo);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Filme filme = buscarEntidade(id);
        filmeRepository.delete(filme);
        log.info("Filme excluido: id={}", id);
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
}
