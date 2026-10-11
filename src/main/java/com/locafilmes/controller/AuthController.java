package com.locafilmes.controller;

import com.locafilmes.dto.auth.LoginRequestDTO;
import com.locafilmes.dto.auth.LoginResponseDTO;
import com.locafilmes.dto.auth.RegisterRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;
import com.locafilmes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e cadastro público de cliente. Não exigem token.")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Valida e-mail e senha e devolve o token JWT.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "Cadastrar cliente", description = "Cadastro público. O perfil criado é sempre CLIENTE.")
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }
}
