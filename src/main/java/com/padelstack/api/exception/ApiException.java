package com.padelstack.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de la API usada para representar errores de api.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    /**
     * Crea una instancia de ApiException.
     *
     * @param status estado usado para filtrar o actualizar datos.
     * @param message mensaje usado para la respuesta o el error.
     */
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    /**
     * Devuelve status.
     *
     * @return resultado de la operación.
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Devuelve message.
     *
     * @return texto obtenido por el método.
     */
    @Override
    public String getMessage() {
        return message;
    }
}
