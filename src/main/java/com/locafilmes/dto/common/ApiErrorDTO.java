package com.locafilmes.dto.common;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrão de retorno de erro da API, usado pelo GlobalExceptionHandler.
 */
public record ApiErrorDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
}
