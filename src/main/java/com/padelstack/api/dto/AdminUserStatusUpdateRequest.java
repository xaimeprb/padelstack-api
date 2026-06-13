package com.padelstack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Peticion para cambiar el estado activo/inactivo de un usuario.
 */
public record AdminUserStatusUpdateRequest(@NotNull Boolean active) {
}
