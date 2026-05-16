package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de conflict.
 */
public class ConflictException extends ApiException {
    /**
     * Crea una instancia de ConflictException.
     *
     * @param message mensaje usado para la respuesta o el error.
     */
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
