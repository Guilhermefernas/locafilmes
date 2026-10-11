package com.locafilmes.service.impl;

import com.locafilmes.dto.usuario.UsuarioRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.Usuario;
import com.locafilmes.exception.BusinessException;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.mapper.UsuarioMapper;
import com.locafilmes.repository.UsuarioRepository;
import com.locafilmes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail '" + dto.email() + "'");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .role(dto.role())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuario criado: id={} role={}", salvo.getId(), salvo.getRole());
        return usuarioMapper.toResponse(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioMapper.toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);

        usuarioRepository.findByEmailIgnoreCase(dto.email())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(u -> {
                    throw new BusinessException("Já existe um usuário cadastrado com o e-mail '" + dto.email() + "'");
                });

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(dto.role());

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuario atualizado: id={} role={}", salvo.getId(), salvo.getRole());
        return usuarioMapper.toResponse(salvo);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario excluido: id={}", id);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", id));
    }
}
