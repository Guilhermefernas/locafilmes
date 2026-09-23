package com.locafilmes.exception;

/** Lançada quando uma regra de negócio é violada (ex.: filme sem disponibilidade, e-mail duplicado). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
