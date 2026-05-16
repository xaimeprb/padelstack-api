package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de reservation summary response.
 *
 * @param reservationId identificador de la reserva.
 * @param resourceId identificador del recurso.
 * @param resourceName valor recibido por el método.
 * @param date fecha usada en la operación.
 * @param startTime valor recibido por el método.
 * @param endTime valor recibido por el método.
 * @param allDay indica si la reserva ocupa el día completo.
 * @param slotLabel texto del tramo horario.
 * @param status estado usado para filtrar o actualizar datos.
 */
public record ReservationSummaryResponse(
        String reservationId,
        String resourceId,
        String resourceName,
        String date,
        String startTime,
        String endTime,
        boolean allDay,
        String slotLabel,
        String status
) {
}
