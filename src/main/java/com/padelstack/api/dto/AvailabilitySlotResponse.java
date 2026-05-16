package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de availability slot response.
 *
 * @param reservationId identificador de la reserva.
 * @param label valor recibido por el método.
 * @param startTime valor recibido por el método.
 * @param endTime valor recibido por el método.
 * @param status estado usado para filtrar o actualizar datos.
 * @param ownerCurrentUser valor recibido por el método.
 * @param blockReason valor recibido por el método.
 */
public record AvailabilitySlotResponse(
        String reservationId,
        String label,
        String startTime,
        String endTime,
        String status,
        boolean ownerCurrentUser,
        String blockReason
) {
}
