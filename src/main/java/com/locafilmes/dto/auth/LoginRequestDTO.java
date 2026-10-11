package com.locafilmes.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @Schema(example = "admin@locafilmes.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "123456")
        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
