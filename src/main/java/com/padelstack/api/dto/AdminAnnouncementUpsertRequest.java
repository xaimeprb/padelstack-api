package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta los datos de admin announcement upsert request.
 *
 * @param communityId identificador de la comunidad.
 * @param title título usado en la operación.
 * @param content valor recibido por el método.
 * @param visible indica si el elemento debe estar visible.
 * @param publishedAt valor recibido por el método.
 */
public record AdminAnnouncementUpsertRequest(
        String communityId,
        @NotBlank String title,
        @NotBlank String content,
        Boolean visible,
        String publishedAt
) {
}
