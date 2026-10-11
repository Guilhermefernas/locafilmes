package com.locafilmes.service.impl;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.entity.Filme;
import com.locafilmes.entity.Locacao;
import com.locafilmes.entity.StatusLocacao;
import com.locafilmes.entity.Usuario;
import com.locafilmes.exception.BusinessException;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.mapper.LocacaoMapper;
import com.locafilmes.repository.FilmeRepository;
import com.locafilmes.repository.LocacaoRepository;
import com.locafilmes.repository.UsuarioRepository;
import com.locafilmes.security.UsuarioAutenticado;
import com.locafilmes.service.LocacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocacaoServiceImpl implements LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FilmeRepository filmeRepository;
    private final LocacaoMapper locacaoMapper;

    @Override
    @Transactional
    public LocacaoResponseDTO criar(LocacaoRequestDTO dto, UsuarioAutenticado logado) {
        garantirAcesso(logado, dto.usuarioId());

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", dto.usuarioId()));

        Set<Filme> filmes = new HashSet<>(filmeRepository.findAllById(dto.filmeIds()));
        if (filmes.size() != dto.filmeIds().size()) {
            throw new ResourceNotFoundException("Um ou mais filmes informados não foram encontrados");
        }

        LocalDate hoje = LocalDate.now();

        for (Filme filme : filmes) {
            if (!filme.possuiDisponibilidade()) {
                throw new BusinessException("O filme '" + filme.getTitulo() + "' não possui unidades disponíveis para locação");
            }
        }

        long dias = ChronoUnit.DAYS.between(hoje, dto.dataDevolucaoPrevista());
        if (dias <= 0) {
            throw new BusinessException("A data prevista de devolução deve ser posterior à data de locação");
        }

        BigDecimal somaDiarias = filmes.stream()
                .map(Filme::getValorDiaria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotal = somaDiarias.multiply(BigDecimal.valueOf(dias));

        filmes.forEach(Filme::decrementarDisponibilidade);
        filmeRepository.saveAll(filmes);

        Locacao locacao = Locacao.builder()
                .usuario(usuario)
                .dataLocacao(hoje)
                .dataDevolucaoPrevista(dto.dataDevolucaoPrevista())
                .valorTotal(valorTotal)
                .status(StatusLocacao.ATIVA)
                .filmes(filmes)
                .build();

        Locacao salva = locacaoRepository.save(locacao);
        log.info("Locacao criada: id={} usuarioId={}", salva.getId(), usuario.getId());
        return locacaoMapper.toResponse(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public LocacaoResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        Locacao locacao = buscarEntidade(id);
        garantirAcesso(logado, locacao.getUsuario().getId());
        return locacaoMapper.toResponse(locacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocacaoResponseDTO> listar(Long usuarioId, UsuarioAutenticado logado) {
        if (logado == null) {
            throw new AccessDeniedException("Acesso negado à locação de outro usuário");
        }
        if (!logado.isAdmin()) {
            garantirAcesso(logado, usuarioId == null ? logado.getId() : usuarioId);
            return locacaoRepository.findByUsuario_IdOrderByDataLocacaoDesc(logado.getId()).stream()
                    .map(locacaoMapper::toResponse)
                    .toList();
        }
        if (usuarioId != null) {
            return locacaoRepository.findByUsuario_IdOrderByDataLocacaoDesc(usuarioId).stream()
                    .map(locacaoMapper::toResponse)
                    .toList();
        }
        return locacaoRepository.findAll().stream()
                .map(locacaoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LocacaoResponseDTO registrarDevolucao(Long id, UsuarioAutenticado logado) {
        Locacao locacao = buscarEntidade(id);
        garantirAcesso(logado, locacao.getUsuario().getId());

        if (locacao.getStatus() == StatusLocacao.FINALIZADA) {
            throw new BusinessException("Esta locação já foi finalizada");
        }

        locacao.getFilmes().forEach(Filme::incrementarDisponibilidade);
        filmeRepository.saveAll(locacao.getFilmes());

        locacao.setDataDevolucao(LocalDate.now());
        locacao.setStatus(StatusLocacao.FINALIZADA);

        Locacao salva = locacaoRepository.save(locacao);
        log.info("Locacao devolvida: id={}", salva.getId());
        return locacaoMapper.toResponse(salva);
    }

    private Locacao buscarEntidade(Long id) {
        return locacaoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Locação", id));
    }

    private void garantirAcesso(UsuarioAutenticado logado, Long donoId) {
        if (logado == null || (!logado.isAdmin() && !logado.getId().equals(donoId))) {
            throw new AccessDeniedException("Acesso negado à locação de outro usuário");
        }
    }
}
