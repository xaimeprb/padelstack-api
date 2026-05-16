package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de unauthorized.
 */
public class UnauthorizedException extends ApiException {
    /**
     * Crea una instancia de UnauthorizedException.
     *
     * @param message mensaje usado para la respuesta o el error.
     */
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
