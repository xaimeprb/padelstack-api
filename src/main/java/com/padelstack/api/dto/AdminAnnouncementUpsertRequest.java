package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminAnnouncementUpsertRequest(
        String communityId,
        @NotBlank String title,
        @NotBlank String content,
        Boolean visible,
        String publishedAt
) {
}
