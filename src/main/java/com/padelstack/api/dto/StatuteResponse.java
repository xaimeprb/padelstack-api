package com.padelstack.api.dto;

public record StatuteResponse(
        String communityId,
        String title,
        String content,
        Integer version,
        String updatedAt,
        String updatedByUid
) {
}
