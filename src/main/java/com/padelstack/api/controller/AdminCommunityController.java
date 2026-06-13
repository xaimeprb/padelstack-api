package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminCommunityResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador administrativo de comunidades.
 */
@RestController
@RequestMapping("/api/v1/admin/communities")
public class AdminCommunityController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminCommunityController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AdminCommunityResponse> all(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.communities(currentUser);
    }

    @GetMapping("/{communityId}")
    public AdminCommunityResponse one(@PathVariable String communityId, Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.community(currentUser, communityId);
    }
}
