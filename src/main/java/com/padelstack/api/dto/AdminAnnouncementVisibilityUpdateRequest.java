package com.padelstack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Peticion para mostrar u ocultar un anuncio.
 */
public record AdminAnnouncementVisibilityUpdateRequest(@NotNull Boolean visible) {
}
