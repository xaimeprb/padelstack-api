package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de resource response.
 *
 * @param resourceId identificador del recurso.
 * @param name valor recibido por el método.
 * @param type valor recibido por el método.
 * @param reservationMode valor recibido por el método.
 * @param slotMinutes valor recibido por el método.
 * @param openTime valor recibido por el método.
 * @param closeTime valor recibido por el método.
 * @param rulesText valor recibido por el método.
 */
public record ResourceResponse(
        String resourceId,
        String name,
        String type,
        String reservationMode,
        Integer slotMinutes,
        String openTime,
        String closeTime,
        String rulesText
) {
}
