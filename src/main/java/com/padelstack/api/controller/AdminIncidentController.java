package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminIncidentResponse;
import com.padelstack.api.dto.AdminIncidentStatusUpdateRequest;
import com.padelstack.api.dto.DeleteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.IncidentService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con admin incident.
 */
@RestController
@RequestMapping("/api/v1/admin/incidents")
public class AdminIncidentController {

    private final AdminService adminService;
    private final IncidentService incidentService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AdminIncidentController con las dependencias necesarias.
     *
     * @param incidentService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AdminIncidentController(AdminService adminService,
                                   IncidentService incidentService,
                                   SecurityService securityService) {
        this.adminService = adminService;
        this.incidentService = incidentService;
        this.securityService = securityService;
    }

    /**
     * Lista incidencias para SUPERADMIN.
     */
    @GetMapping
    public java.util.List<AdminIncidentResponse> all(@RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String communityId,
                                                     @RequestParam(required = false) String userId,
                                                     @RequestParam(required = false) String search,
                                                     Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.incidents(currentUser, status, communityId, userId, search);
    }

    /**
     * Gestiona la operación updateStatus.
     *
     * @param incidentId identificador de la incidencia.
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PutMapping("/{incidentId}/status")
    public DeleteResponse updateStatus(@PathVariable String incidentId,
                                       @Valid @RequestBody AdminIncidentStatusUpdateRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        securityService.requireSuperAdmin(currentUser);
        incidentService.updateStatus(currentUser, incidentId, request);
        return new DeleteResponse(true);
    }
}
