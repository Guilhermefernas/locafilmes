package com.locafilmes.service.impl;

import com.locafilmes.dto.diretor.DiretorRequestDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.entity.Diretor;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.mapper.DiretorMapper;
import com.locafilmes.repository.DiretorRepository;
import com.locafilmes.service.DiretorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiretorServiceImpl implements DiretorService {

    private final DiretorRepository diretorRepository;
    private final DiretorMapper diretorMapper;

    @Override
    @Transactional
    public DiretorResponseDTO criar(DiretorRequestDTO dto) {
        Diretor diretor = Diretor.builder()
                .nome(dto.nome())
                .nacionalidade(dto.nacionalidade())
                .build();

        Diretor salvo = diretorRepository.save(diretor);
        log.info("Diretor criado: id={}", salvo.getId());
        return diretorMapper.toResponse(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public DiretorResponseDTO buscarPorId(Long id) {
        return diretorMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiretorResponseDTO> listarTodos() {
        return diretorRepository.findAll().stream()
                .map(diretorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DiretorResponseDTO atualizar(Long id, DiretorRequestDTO dto) {
        Diretor diretor = buscarEntidade(id);
        diretor.setNome(dto.nome());
        diretor.setNacionalidade(dto.nacionalidade());
        Diretor salvo = diretorRepository.save(diretor);
        log.info("Diretor atualizado: id={}", salvo.getId());
        return diretorMapper.toResponse(salvo);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Diretor diretor = buscarEntidade(id);
        diretorRepository.delete(diretor);
        log.info("Diretor excluido: id={}", id);
    }

    private Diretor buscarEntidade(Long id) {
        return diretorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Diretor", id));
    }
}
