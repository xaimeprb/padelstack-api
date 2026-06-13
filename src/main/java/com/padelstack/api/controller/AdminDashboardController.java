package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminDashboardResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador del dashboard administrativo.
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminDashboardController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    /**
     * Devuelve contadores y ultimos registros globales.
     */
    @GetMapping
    public AdminDashboardResponse dashboard(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.dashboard(currentUser);
    }
}
