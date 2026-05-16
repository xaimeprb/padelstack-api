package com.padelstack.api.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones de la API REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Construye la respuesta HTTP para una excepción controlada de la API.
     *
     * @param ex valor recibido por el método.
     * @return respuesta HTTP construida por el método.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    /**
     * Construye la respuesta HTTP para errores de validación.
     *
     * @param ex valor recibido por el método.
     * @return respuesta HTTP construida por el método.
     */
    public ResponseEntity<Map<String, String>> handleValidation(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "Datos inválidos"));
    }

    /**
     * Construye la respuesta HTTP para errores no controlados.
     *
     * @param ex valor recibido por el método.
     * @return respuesta HTTP construida por el método.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", "Error interno del servidor"));
    }
}
