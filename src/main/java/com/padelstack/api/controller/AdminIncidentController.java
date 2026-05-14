package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminIncidentStatusUpdateRequest;
import com.padelstack.api.dto.DeleteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.IncidentService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/incidents")
public class AdminIncidentController {

    private final IncidentService incidentService;
    private final SecurityService securityService;

    public AdminIncidentController(IncidentService incidentService, SecurityService securityService) {
        this.incidentService = incidentService;
        this.securityService = securityService;
    }

    @PutMapping("/{incidentId}/status")
    public DeleteResponse updateStatus(@PathVariable String incidentId,
                                       @Valid @RequestBody AdminIncidentStatusUpdateRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        incidentService.updateStatus(currentUser, incidentId, request);
        return new DeleteResponse(true);
    }
}
