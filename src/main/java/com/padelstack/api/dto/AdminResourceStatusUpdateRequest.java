package com.padelstack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Peticion para activar o desactivar un recurso sin eliminarlo.
 */
public record AdminResourceStatusUpdateRequest(@NotNull Boolean active) {
}
