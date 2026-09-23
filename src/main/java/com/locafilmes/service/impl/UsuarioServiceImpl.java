package com.locafilmes.service.impl;

import com.locafilmes.dto.usuario.UsuarioRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.Usuario;
import com.locafilmes.exception.BusinessException;
import com.locafilmes.exception.ResourceNotFoundException;
import com.locafilmes.repository.UsuarioRepository;
import com.locafilmes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        // Regra de negócio: o e-mail do usuário não pode se repetir.
        if (usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail '" + dto.email() + "'");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .role(dto.role())
                .build();

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
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

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}
