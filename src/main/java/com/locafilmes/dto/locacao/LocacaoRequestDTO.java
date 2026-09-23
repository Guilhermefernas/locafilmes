package com.locafilmes.dto.locacao;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record LocacaoRequestDTO(

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotEmpty(message = "A locação deve conter ao menos um filme")
        Set<Long> filmeIds,

        @NotNull(message = "A data prevista de devolução é obrigatória")
        @Future(message = "A data prevista de devolução deve ser futura")
        LocalDate dataDevolucaoPrevista
) {
}
