package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de me response.
 *
 * @param uid identificador del usuario.
 * @param email correo electrónico del usuario.
 * @param username nombre de usuario introducido.
 * @param firstName nombre del usuario.
 * @param lastName apellidos del usuario.
 * @param fullName nombre completo del usuario.
 * @param phone teléfono del usuario.
 * @param communityId identificador de la comunidad.
 * @param communityName nombre de la comunidad.
 * @param unitDisplay texto de la vivienda del usuario.
 * @param role rol del usuario.
 * @param active valor recibido por el método.
 */
public record MeResponse(
        String uid,
        String email,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        String communityId,
        String communityName,
        String unitDisplay,
        String role,
        boolean active
) {
}
