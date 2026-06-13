package com.padelstack.api.dto;

/**
 * Usuario visible desde el panel de administracion.
 */
public record AdminUserResponse(
        String uid,
        String email,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String displayName,
        String phone,
        String communityId,
        String communityName,
        String unitDisplay,
        String role,
        boolean active,
        String createdAt,
        String updatedAt
) {
}
