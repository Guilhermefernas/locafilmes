package com.locafilmes.dto.filme;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record FilmeRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @NotNull(message = "O ano de lançamento é obrigatório")
        @Positive(message = "O ano de lançamento deve ser positivo")
        Integer anoLancamento,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser positiva")
        Integer duracaoMinutos,

        @NotNull(message = "O valor da diária é obrigatório")
        @Positive(message = "O valor da diária deve ser positivo")
        BigDecimal valorDiaria,

        @NotNull(message = "A quantidade disponível é obrigatória")
        @PositiveOrZero(message = "A quantidade disponível não pode ser negativa")
        Integer quantidadeDisponivel,

        @NotNull(message = "O diretor é obrigatório")
        Long diretorId,

        @NotEmpty(message = "O filme deve ter ao menos uma categoria")
        Set<Long> categoriaIds
) {
}
