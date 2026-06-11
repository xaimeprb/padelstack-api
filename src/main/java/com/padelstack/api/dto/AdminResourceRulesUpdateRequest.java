package com.padelstack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO que transporta el texto de reglas de un recurso.
 *
 * @param rulesText texto actualizado de las reglas.
 */
public record AdminResourceRulesUpdateRequest(@NotNull String rulesText) {
}
