package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de forbidden.
 */
public class ForbiddenException extends ApiException {
    /**
     * Crea una instancia de ForbiddenException.
     *
     * @param message mensaje usado para la respuesta o el error.
     */
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
