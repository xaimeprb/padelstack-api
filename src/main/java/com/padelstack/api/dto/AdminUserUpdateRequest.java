package com.padelstack.api.dto;

/**
 * Peticion para editar datos basicos de un usuario desde PanelAdmin.
 */
public record AdminUserUpdateRequest(
        String username,
        String firstName,
        String lastName,
        String phone,
        String communityId,
        String unitDisplay
) {
}
