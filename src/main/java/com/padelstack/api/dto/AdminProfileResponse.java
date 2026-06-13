package com.padelstack.api.dto;

/**
 * Perfil administrativo del usuario autenticado.
 */
public record AdminProfileResponse(
        String uid,
        String email,
        String displayName,
        String role,
        boolean active,
        String communityId,
        String communityName
) {
}
