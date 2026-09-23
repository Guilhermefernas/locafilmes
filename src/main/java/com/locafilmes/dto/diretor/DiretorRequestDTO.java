package com.locafilmes.dto.diretor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiretorRequestDTO(

        @NotBlank(message = "O nome do diretor é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 60, message = "A nacionalidade deve ter no máximo 60 caracteres")
        String nacionalidade
) {
}
