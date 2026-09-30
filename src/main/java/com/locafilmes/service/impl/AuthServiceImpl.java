package com.locafilmes.service.impl;

import com.locafilmes.dto.auth.LoginRequestDTO;
import com.locafilmes.dto.auth.LoginResponseDTO;
import com.locafilmes.dto.auth.RegisterRequestDTO;
import com.locafilmes.dto.usuario.UsuarioRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.entity.Role;
import com.locafilmes.security.JwtService;
import com.locafilmes.security.UsuarioAutenticado;
import com.locafilmes.service.AuthService;
import com.locafilmes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));
        } catch (BadCredentialsException e) {
            log.warn("Falha de login para o e-mail {}", dto.email());
            throw e;
        }

        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
        String token = jwtService.gerarToken(usuario);
        log.info("Login realizado com sucesso: usuário id={} ({})", usuario.getId(), usuario.getRole());

        UsuarioResponseDTO usuarioDTO = usuarioService.buscarPorId(usuario.getId());
        return new LoginResponseDTO(token, "Bearer", jwtService.getExpiracaoSegundos(), usuarioDTO);
    }

    @Override
    public UsuarioResponseDTO registrar(RegisterRequestDTO dto) {
        // Cadastro público sempre cria CLIENTE; ADMIN só é criado por outro ADMIN (POST /api/usuarios).
        UsuarioResponseDTO criado = usuarioService.criar(
                new UsuarioRequestDTO(dto.nome(), dto.email(), dto.senha(), Role.CLIENTE));
        log.info("Novo cliente cadastrado: id={}", criado.id());
        return criado;
    }
}
