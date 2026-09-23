package com.locafilmes.exception;

/** Lançada quando um recurso (entidade) não é encontrado pelo id informado. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }

    public static ResourceNotFoundException of(String entidade, Long id) {
        return new ResourceNotFoundException(entidade + " não encontrado(a) com id " + id);
    }
}
