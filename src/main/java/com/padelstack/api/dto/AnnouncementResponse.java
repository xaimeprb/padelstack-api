package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de announcement response.
 *
 * @param announcementId valor recibido por el método.
 * @param title título usado en la operación.
 * @param content valor recibido por el método.
 * @param visible indica si el elemento debe estar visible.
 * @param publishedAt valor recibido por el método.
 * @param createdByUid valor recibido por el método.
 * @param createdByName valor recibido por el método.
 * @param updatedAt valor recibido por el método.
 */
public record AnnouncementResponse(
        String announcementId,
        String title,
        String content,
        boolean visible,
        String publishedAt,
        String createdByUid,
        String createdByName,
        String updatedAt
) {
}
