package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion para cambiar el rol de un usuario.
 */
public record AdminUserRoleUpdateRequest(@NotBlank String role) {
}
