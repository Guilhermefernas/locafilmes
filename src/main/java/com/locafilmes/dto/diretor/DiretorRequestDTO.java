package com.locafilmes.dto.diretor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiretorRequestDTO(

        @Schema(example = "Christopher Nolan")
        @NotBlank(message = "O nome do diretor é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Schema(example = "Britânico")
        @Size(max = 60, message = "A nacionalidade deve ter no máximo 60 caracteres")
        String nacionalidade
) {
}
