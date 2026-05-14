package com.padelstack.api.dto;

public record ResourceResponse(
        String resourceId,
        String name,
        String type,
        String reservationMode,
        Integer slotMinutes,
        String openTime,
        String closeTime,
        String rulesText
) {
}
