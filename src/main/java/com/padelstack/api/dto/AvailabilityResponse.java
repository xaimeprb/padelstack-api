package com.padelstack.api.dto;

import java.util.List;

/**
 * DTO que transporta los datos de availability response.
 *
 * @param resourceId identificador del recurso.
 * @param date fecha usada en la operación.
 * @param reservationMode valor recibido por el método.
 * @param slots valor recibido por el método.
 * @param dayStatus valor recibido por el método.
 */
public record AvailabilityResponse(
        String resourceId,
        String date,
        String reservationMode,
        List<AvailabilitySlotResponse> slots,
        AvailabilityDayStatusResponse dayStatus
) {
}
