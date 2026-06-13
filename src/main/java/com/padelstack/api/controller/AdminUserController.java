package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminUserResponse;
import com.padelstack.api.dto.AdminUserRoleUpdateRequest;
import com.padelstack.api.dto.AdminUserStatusUpdateRequest;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador administrativo de usuarios.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminUserController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AdminUserResponse> all(@RequestParam(required = false) String role,
                                       @RequestParam(required = false) String communityId,
                                       @RequestParam(required = false) Boolean active,
                                       @RequestParam(required = false) String search,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.users(currentUser, role, communityId, active, search);
    }

    @GetMapping("/{uid}")
    public AdminUserResponse one(@PathVariable String uid, Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.user(currentUser, uid);
    }

    @PatchMapping("/{uid}/role")
    public AdminUserResponse updateRole(@PathVariable String uid,
                                        @Valid @RequestBody AdminUserRoleUpdateRequest request,
                                        Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.updateUserRole(currentUser, uid, request);
    }

    @PatchMapping("/{uid}/status")
    public AdminUserResponse updateStatus(@PathVariable String uid,
                                          @Valid @RequestBody AdminUserStatusUpdateRequest request,
                                          Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.updateUserStatus(currentUser, uid, request);
    }
}
