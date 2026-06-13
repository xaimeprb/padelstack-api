package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminAuditLogResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador administrativo de auditoria.
 */
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminAuditLogController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AdminAuditLogResponse> all(@RequestParam(required = false) String actorUid,
                                           @RequestParam(required = false) String action,
                                           @RequestParam(required = false) String entityType,
                                           @RequestParam(required = false) String entityId,
                                           @RequestParam(required = false) String dateFrom,
                                           @RequestParam(required = false) String dateTo,
                                           @RequestParam(required = false) String search,
                                           Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.auditLogs(currentUser, actorUid, action, entityType, entityId, dateFrom, dateTo, search);
    }
}
