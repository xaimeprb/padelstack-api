package com.padelstack.api.service;

import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.UnauthorizedException;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la lógica relacionada con security.
 */
@Service
public class SecurityService {

    private final UserService userService;

    /**
     * Crea una instancia de SecurityService con las dependencias necesarias.
     *
     * @param userService servicio usado por la clase.
     */
    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene los datos del usuario autenticado desde Spring Security.
     *
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    public AuthenticatedUser authenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("Unauthorized");
        }
        return user;
    }

    /**
     * Obtiene el usuario actual completo a partir de la autenticación.
     *
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    public UserDocument currentUser(Authentication authentication) {
        return userService.getRequiredUser(authenticatedUser(authentication).uid());
    }

    /**
     * Comprueba que el usuario tenga permisos de administrador.
     *
     * @param user usuario usado en la operación.
     */
    public void requireAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        if (role != Role.ADMIN && role != Role.SUPERADMIN) {
            throw new ForbiddenException("No tienes permisos");
        }
    }

    /**
     * Comprueba que el usuario tenga permisos de superadministrador.
     *
     * @param user usuario usado en la operación.
     */
    public void requireSuperAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        if (role != Role.SUPERADMIN) {
            throw new ForbiddenException("No tienes permisos");
        }
    }

    /**
     * Indica si el usuario tiene rol de administrador o superadministrador.
     *
     * @param user usuario usado en la operación.
     * @return true si se cumple la condición, false en caso contrario.
     */
    public boolean isAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        return role == Role.ADMIN || role == Role.SUPERADMIN;
    }
}
