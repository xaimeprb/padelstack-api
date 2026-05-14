package com.padelstack.api.dto;

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
