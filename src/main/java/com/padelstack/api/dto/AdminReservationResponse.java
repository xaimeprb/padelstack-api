package com.padelstack.api.dto;

/**
 * Reserva visible desde el panel de administracion.
 */
public record AdminReservationResponse(
        String reservationId,
        String communityId,
        String userId,
        String userEmail,
        String userName,
        String resourceId,
        String resourceName,
        String date,
        String startTime,
        String endTime,
        Boolean allDay,
        String slotLabel,
        String status,
        String createdAt,
        String updatedAt,
        String cancelledAt,
        String cancelledByUid,
        String cancelledByName,
        String cancellationReason
) {
}
