package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta los datos de bootstrap user request.
 *
 * @param username nombre de usuario introducido.
 * @param firstName nombre del usuario.
 * @param lastName apellidos del usuario.
 * @param phone teléfono del usuario.
 * @param communityId identificador de la comunidad.
 * @param communityName nombre de la comunidad.
 * @param unitDisplay texto de la vivienda del usuario.
 */
public record BootstrapUserRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phone,
        @NotBlank String communityId,
        String communityName,
        @NotBlank String unitDisplay
) {
}
