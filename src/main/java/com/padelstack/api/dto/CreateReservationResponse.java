package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de create reservation response.
 *
 * @param reservationId identificador de la reserva.
 * @param status estado usado para filtrar o actualizar datos.
 */
public record CreateReservationResponse(
        String reservationId,
        String status
) {
}
