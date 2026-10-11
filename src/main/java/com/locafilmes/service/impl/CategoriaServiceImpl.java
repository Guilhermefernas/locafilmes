package com.locafilmes.service.impl;

import com.locafilmes.dto.categoria.CategoriaRequestDTO;
import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.entity.Categoria;
import com.locafilmes.exception.BusinessException;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.mapper.CategoriaMapper;
import com.locafilmes.repository.CategoriaRepository;
import com.locafilmes.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        categoriaRepository.findByNomeIgnoreCase(dto.nome()).ifPresent(c -> {
            throw new BusinessException("Já existe uma categoria com o nome '" + dto.nome() + "'");
        });

        Categoria categoria = Categoria.builder().nome(dto.nome()).build();
        Categoria salva = categoriaRepository.save(categoria);
        log.info("Categoria criada: id={}", salva.getId());
        return categoriaMapper.toResponse(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return categoriaMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarEntidade(id);

        categoriaRepository.findByNomeIgnoreCase(dto.nome())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("Já existe uma categoria com o nome '" + dto.nome() + "'");
                });

        categoria.setNome(dto.nome());
        Categoria salva = categoriaRepository.save(categoria);
        log.info("Categoria atualizada: id={}", salva.getId());
        return categoriaMapper.toResponse(salva);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarEntidade(id);
        categoriaRepository.delete(categoria);
        log.info("Categoria excluida: id={}", id);
    }

    private Categoria buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Categoria", id));
    }
}
