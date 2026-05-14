package com.padelstack.api.dto;

public record AvailabilityDayStatusResponse(
        String reservationId,
        String status,
        boolean ownerCurrentUser,
        String blockReason
) {
}
