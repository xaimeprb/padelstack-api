package com.padelstack.api.dto;

import java.util.List;

public record AvailabilityResponse(
        String resourceId,
        String date,
        String reservationMode,
        List<AvailabilitySlotResponse> slots,
        AvailabilityDayStatusResponse dayStatus
) {
}
