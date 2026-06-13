package com.padelstack.api.dto;

/**
 * Recurso visible desde el panel de administracion.
 */
public record AdminResourceResponse(
        String resourceId,
        String communityId,
        String name,
        String type,
        String reservationMode,
        Integer slotMinutes,
        String openTime,
        String closeTime,
        String rulesText,
        boolean active
) {
}
