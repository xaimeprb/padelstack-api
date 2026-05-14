package com.padelstack.api.dto;

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
