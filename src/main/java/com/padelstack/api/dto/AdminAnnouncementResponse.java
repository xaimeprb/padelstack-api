package com.padelstack.api.dto;

/**
 * Anuncio visible desde el panel de administracion.
 */
public record AdminAnnouncementResponse(
        String announcementId,
        String communityId,
        String title,
        String content,
        boolean visible,
        String publishedAt,
        String createdByUid,
        String createdByName,
        String updatedAt
) {
}
