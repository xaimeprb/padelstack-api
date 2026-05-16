package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO que transporta los datos de admin statute upsert request.
 *
 * @param title título usado en la operación.
 * @param content valor recibido por el método.
 * @param version valor recibido por el método.
 */
public record AdminStatuteUpsertRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Integer version
) {
}
