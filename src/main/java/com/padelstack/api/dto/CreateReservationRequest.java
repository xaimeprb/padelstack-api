package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta los datos de create reservation request.
 *
 * @param resourceId identificador del recurso.
 * @param date fecha usada en la operación.
 * @param startTime valor recibido por el método.
 * @param endTime valor recibido por el método.
 * @param allDay indica si la reserva ocupa el día completo.
 */
public record CreateReservationRequest(
        @NotBlank String resourceId,
        @NotBlank String date,
        String startTime,
        String endTime,
        Boolean allDay
) {
}
