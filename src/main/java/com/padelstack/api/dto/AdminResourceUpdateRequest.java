package com.padelstack.api.dto;

/**
 * Peticion para editar configuracion operativa de un recurso.
 */
public record AdminResourceUpdateRequest(
        String name,
        String communityId,
        String type,
        String reservationMode,
        Integer slotMinutes,
        String openTime,
        String closeTime,
        String rulesText,
        Boolean active
) {
}
