package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de not found.
 */
public class NotFoundException extends ApiException {
    /**
     * Crea una instancia de NotFoundException.
     *
     * @param message mensaje usado para la respuesta o el error.
     */
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
