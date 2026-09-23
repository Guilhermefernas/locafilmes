package com.locafilmes.service.impl;

import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.Filme;
import com.locafilmes.entity.Locacao;
import com.locafilmes.entity.StatusLocacao;
import com.locafilmes.entity.Usuario;
import com.locafilmes.exception.BusinessException;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.repository.FilmeRepository;
import com.locafilmes.repository.LocacaoRepository;
import com.locafilmes.repository.UsuarioRepository;
import com.locafilmes.service.LocacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocacaoServiceImpl implements LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FilmeRepository filmeRepository;

    @Override
    @Transactional
    public LocacaoResponseDTO criar(LocacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", dto.usuarioId()));

        Set<Filme> filmes = new HashSet<>(filmeRepository.findAllById(dto.filmeIds()));
        if (filmes.size() != dto.filmeIds().size()) {
            throw new ResourceNotFoundException("Um ou mais filmes informados não foram encontrados");
        }

        LocalDate hoje = LocalDate.now();

        // Regra de negócio: só é possível alugar um filme com quantidade disponível > 0.
        for (Filme filme : filmes) {
            if (!filme.possuiDisponibilidade()) {
                throw new BusinessException("O filme '" + filme.getTitulo() + "' não possui unidades disponíveis para locação");
            }
        }

        long dias = ChronoUnit.DAYS.between(hoje, dto.dataDevolucaoPrevista());
        if (dias <= 0) {
            throw new BusinessException("A data prevista de devolução deve ser posterior à data de locação");
        }

        // Regra de negócio: valor total = soma das diárias dos filmes * dias alugados.
        BigDecimal somaDiarias = filmes.stream()
                .map(Filme::getValorDiaria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotal = somaDiarias.multiply(BigDecimal.valueOf(dias));

        // Regra de negócio: ao alugar, a quantidade disponível do filme diminui.
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

        return toResponseDTO(locacaoRepository.save(locacao));
    }

    @Override
    @Transactional(readOnly = true)
    public LocacaoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocacaoResponseDTO> listarTodas() {
        return locacaoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocacaoResponseDTO> listarPorUsuario(Long usuarioId) {
        return locacaoRepository.findByUsuario_IdOrderByDataLocacaoDesc(usuarioId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public LocacaoResponseDTO registrarDevolucao(Long id) {
        Locacao locacao = buscarEntidade(id);

        if (locacao.getStatus() == StatusLocacao.FINALIZADA) {
            throw new BusinessException("Esta locação já foi finalizada");
        }

        // Regra de negócio: ao devolver, a quantidade disponível do filme aumenta.
        locacao.getFilmes().forEach(Filme::incrementarDisponibilidade);
        filmeRepository.saveAll(locacao.getFilmes());

        locacao.setDataDevolucao(LocalDate.now());
        locacao.setStatus(StatusLocacao.FINALIZADA);

        return toResponseDTO(locacaoRepository.save(locacao));
    }

    private Locacao buscarEntidade(Long id) {
        return locacaoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Locação", id));
    }

    private LocacaoResponseDTO toResponseDTO(Locacao locacao) {
        Usuario usuario = locacao.getUsuario();
        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole()
        );

        Set<FilmeResponseDTO> filmesDTO = locacao.getFilmes().stream()
                .map(this::toFilmeResponseDTO)
                .collect(Collectors.toSet());

        return new LocacaoResponseDTO(
                locacao.getId(),
                usuarioDTO,
                locacao.getDataLocacao(),
                locacao.getDataDevolucaoPrevista(),
                locacao.getDataDevolucao(),
                locacao.getValorTotal(),
                locacao.getStatus(),
                filmesDTO
        );
    }

    private FilmeResponseDTO toFilmeResponseDTO(Filme filme) {
        DiretorResponseDTO diretorDTO = filme.getDiretor() == null ? null : new DiretorResponseDTO(
                filme.getDiretor().getId(), filme.getDiretor().getNome(), filme.getDiretor().getNacionalidade()
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
