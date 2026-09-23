package com.locafilmes.exception;

import com.locafilmes.dto.common.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tratamento centralizado de exceções da API (RNF06 / requisito 5 do trabalho).
 * Garante que o cliente da API sempre receba um JSON de erro padronizado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDTO> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return construirResposta(HttpStatus.BAD_REQUEST, "Erro de validação", "Um ou mais campos estão inválidos", request, detalhes);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenerico(Exception ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Ocorreu um erro inesperado. Tente novamente mais tarde.", request, null);
    }

    private ResponseEntity<ApiErrorDTO> construirResposta(HttpStatus status, String erro, String mensagem,
                                                            HttpServletRequest request, List<String> detalhes) {
        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                request.getRequestURI(),
                detalhes
        );
        return ResponseEntity.status(status).body(body);
    }
}
