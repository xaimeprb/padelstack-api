package com.padelstack.api.dto;

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
