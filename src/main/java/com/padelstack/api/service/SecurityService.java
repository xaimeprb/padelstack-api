package com.padelstack.api.service;

import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.UnauthorizedException;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public AuthenticatedUser authenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("Unauthorized");
        }
        return user;
    }

    public UserDocument currentUser(Authentication authentication) {
        return userService.getRequiredUser(authenticatedUser(authentication).uid());
    }

    public void requireAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        if (role != Role.ADMIN && role != Role.SUPERADMIN) {
            throw new ForbiddenException("No tienes permisos");
        }
    }

    public void requireSuperAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        if (role != Role.SUPERADMIN) {
            throw new ForbiddenException("No tienes permisos");
        }
    }

    public boolean isAdmin(UserDocument user) {
        Role role = Role.valueOf(user.role);
        return role == Role.ADMIN || role == Role.SUPERADMIN;
    }
}
