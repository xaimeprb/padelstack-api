package com.padelstack.api.security;

import java.util.List;

/**
 * DTO que transporta los datos de authenticated user.
 *
 * @param uid identificador del usuario.
 * @param email correo electrónico del usuario.
 * @param authorities valor recibido por el método.
 */
public record AuthenticatedUser(
        String uid,
        String email,
        List<String> authorities
) {
}
