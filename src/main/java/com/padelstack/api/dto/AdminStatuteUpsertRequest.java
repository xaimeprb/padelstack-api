package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminStatuteUpsertRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Integer version
) {
}
