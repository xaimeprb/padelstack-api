package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminProfileResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador del perfil administrativo actual.
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminProfileController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminProfileController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    /**
     * Devuelve el perfil del usuario autenticado si es SUPERADMIN.
     */
    @GetMapping("/me")
    public AdminProfileResponse me(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.me(currentUser);
    }
}
