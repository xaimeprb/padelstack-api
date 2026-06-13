package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminReservationResponse;
import com.padelstack.api.dto.AdminReservationStatusUpdateRequest;
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
 * Controlador administrativo de reservas.
 */
@RestController
@RequestMapping("/api/v1/admin/reservations")
public class AdminReservationController {

    private final AdminService adminService;
    private final SecurityService securityService;

    public AdminReservationController(AdminService adminService, SecurityService securityService) {
        this.adminService = adminService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AdminReservationResponse> all(@RequestParam(required = false) String dateFrom,
                                              @RequestParam(required = false) String dateTo,
                                              @RequestParam(required = false) String resourceId,
                                              @RequestParam(required = false) String userId,
                                              @RequestParam(required = false) String communityId,
                                              @RequestParam(required = false) String status,
                                              Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.reservations(currentUser, dateFrom, dateTo, resourceId, userId, communityId, status);
    }

    @GetMapping("/{reservationId}")
    public AdminReservationResponse one(@PathVariable String reservationId, Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.reservation(currentUser, reservationId);
    }

    @PatchMapping("/{reservationId}/status")
    public AdminReservationResponse updateStatus(@PathVariable String reservationId,
                                                 @Valid @RequestBody AdminReservationStatusUpdateRequest request,
                                                 Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.updateReservationStatus(currentUser, reservationId, request);
    }
}
