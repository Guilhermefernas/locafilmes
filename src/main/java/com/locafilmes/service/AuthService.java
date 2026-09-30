package com.locafilmes.service;

import com.locafilmes.dto.auth.LoginRequestDTO;
import com.locafilmes.dto.auth.LoginResponseDTO;
import com.locafilmes.dto.auth.RegisterRequestDTO;
import com.locafilmes.dto.usuario.UsuarioResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    UsuarioResponseDTO registrar(RegisterRequestDTO dto);
}
