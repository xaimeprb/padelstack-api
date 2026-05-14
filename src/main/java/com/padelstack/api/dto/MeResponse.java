package com.padelstack.api.dto;

public record MeResponse(
        String uid,
        String email,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        String communityId,
        String communityName,
        String unitDisplay,
        String role,
        boolean active
) {
}
