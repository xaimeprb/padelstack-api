package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

public record BootstrapUserRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phone,
        @NotBlank String communityId,
        String communityName,
        @NotBlank String unitDisplay
) {
}
