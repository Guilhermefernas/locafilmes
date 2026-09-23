package com.locafilmes.service.impl;

import com.locafilmes.dto.diretor.DiretorRequestDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.entity.Diretor;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.repository.DiretorRepository;
import com.locafilmes.service.DiretorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiretorServiceImpl implements DiretorService {

    private final DiretorRepository diretorRepository;

    @Override
    @Transactional
    public DiretorResponseDTO criar(DiretorRequestDTO dto) {
        Diretor diretor = Diretor.builder()
                .nome(dto.nome())
                .nacionalidade(dto.nacionalidade())
                .build();

        return toResponseDTO(diretorRepository.save(diretor));
    }

    @Override
    @Transactional(readOnly = true)
    public DiretorResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiretorResponseDTO> listarTodos() {
        return diretorRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public DiretorResponseDTO atualizar(Long id, DiretorRequestDTO dto) {
        Diretor diretor = buscarEntidade(id);
        diretor.setNome(dto.nome());
        diretor.setNacionalidade(dto.nacionalidade());
        return toResponseDTO(diretorRepository.save(diretor));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Diretor diretor = buscarEntidade(id);
        diretorRepository.delete(diretor);
    }

    private Diretor buscarEntidade(Long id) {
        return diretorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Diretor", id));
    }

    private DiretorResponseDTO toResponseDTO(Diretor diretor) {
        return new DiretorResponseDTO(diretor.getId(), diretor.getNome(), diretor.getNacionalidade());
    }
}
