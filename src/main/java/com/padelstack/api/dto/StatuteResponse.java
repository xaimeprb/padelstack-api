package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de statute response.
 *
 * @param communityId identificador de la comunidad.
 * @param title título usado en la operación.
 * @param content valor recibido por el método.
 * @param version valor recibido por el método.
 * @param updatedAt valor recibido por el método.
 * @param updatedByUid valor recibido por el método.
 */
public record StatuteResponse(
        String communityId,
        String title,
        String content,
        Integer version,
        String updatedAt,
        String updatedByUid
) {
}
