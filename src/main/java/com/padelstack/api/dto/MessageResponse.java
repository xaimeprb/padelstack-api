package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de message response.
 *
 * @param message mensaje usado para la respuesta o el error.
 */
public record MessageResponse(String message) {
}
