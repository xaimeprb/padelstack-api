package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReservationRequest(
        @NotBlank String resourceId,
        @NotBlank String date,
        String startTime,
        String endTime,
        Boolean allDay
) {
}
