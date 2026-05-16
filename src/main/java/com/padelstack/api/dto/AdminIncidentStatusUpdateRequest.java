package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta los datos de admin incident status update request.
 *
 * @param status estado usado para filtrar o actualizar datos.
 */
public record AdminIncidentStatusUpdateRequest(@NotBlank String status) {
}
