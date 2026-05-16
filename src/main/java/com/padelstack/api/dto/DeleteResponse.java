package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de delete response.
 *
 * @param deleted valor recibido por el método.
 */
public record DeleteResponse(boolean deleted) {
}
