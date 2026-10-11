package com.locafilmes.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrão de retorno de erro da API, usado pelo GlobalExceptionHandler.
 */
@Schema(name = "ApiErrorDTO", description = "Erro padrão da API")
public record ApiErrorDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
}
