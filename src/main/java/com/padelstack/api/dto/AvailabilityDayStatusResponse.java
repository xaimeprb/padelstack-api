package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de availability day status response.
 *
 * @param reservationId identificador de la reserva.
 * @param status estado usado para filtrar o actualizar datos.
 * @param ownerCurrentUser valor recibido por el método.
 * @param blockReason valor recibido por el método.
 */
public record AvailabilityDayStatusResponse(
        String reservationId,
        String status,
        boolean ownerCurrentUser,
        String blockReason
) {
}
