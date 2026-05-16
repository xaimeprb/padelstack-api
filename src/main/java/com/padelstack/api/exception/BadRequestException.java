package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de bad request.
 */
public class BadRequestException extends ApiException {
    /**
     * Crea una instancia de BadRequestException.
     *
     * @param message mensaje usado para la respuesta o el error.
     */
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
