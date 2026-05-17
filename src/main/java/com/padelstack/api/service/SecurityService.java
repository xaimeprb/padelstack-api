package com.padelstack.api.service;

import com.padelstack.api.exception.BadRequestException;
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
        UserDocument user = userService.getRequiredUser(authenticatedUser(authentication).uid());
        validateUser(user);
        return user;
    }

    /**
     * Comprueba que el usuario tenga permisos de administrador.
     *
     * @param user usuario usado en la operación.
     */
    public void requireAdmin(UserDocument user) {
        Role role = roleOf(user);
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
        Role role = roleOf(user);
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
        Role role = roleOf(user);
        return role == Role.ADMIN || role == Role.SUPERADMIN;
    }

    /**
     * Devuelve el rol del usuario de forma controlada.
     *
     * @param user usuario usado en la operacion.
     * @return rol tecnico del usuario.
     */
    public Role roleOf(UserDocument user) {
        if (user == null || user.role == null || user.role.isBlank()) {
            throw new BadRequestException("Perfil de usuario incompleto");
        }
        try {
            return Role.valueOf(user.role);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Rol de usuario no valido");
        }
    }

    /**
     * Comprueba que el perfil del usuario tenga los datos minimos necesarios.
     *
     * @param user usuario usado en la operacion.
     */
    private void validateUser(UserDocument user) {
        if (user == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (!Boolean.TRUE.equals(user.active)) {
            throw new ForbiddenException("Usuario inactivo");
        }
        roleOf(user);
    }
}
