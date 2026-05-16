package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminIncidentStatusUpdateRequest;
import com.padelstack.api.dto.DeleteResponse;
import com.padelstack.api.model.UserDocument;
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

    private final IncidentService incidentService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AdminIncidentController con las dependencias necesarias.
     *
     * @param incidentService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AdminIncidentController(IncidentService incidentService, SecurityService securityService) {
        this.incidentService = incidentService;
        this.securityService = securityService;
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
        incidentService.updateStatus(currentUser, incidentId, request);
        return new DeleteResponse(true);
    }
}
