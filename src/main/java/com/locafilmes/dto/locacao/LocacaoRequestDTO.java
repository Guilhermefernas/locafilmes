package com.locafilmes.dto.locacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record LocacaoRequestDTO(

        @Schema(example = "2")
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @Schema(example = "[1]")
        @NotEmpty(message = "A locação deve conter ao menos um filme")
        Set<Long> filmeIds,

        @Schema(example = "2026-12-31")
        @NotNull(message = "A data prevista de devolução é obrigatória")
        @Future(message = "A data prevista de devolução deve ser futura")
        LocalDate dataDevolucaoPrevista
) {
}
