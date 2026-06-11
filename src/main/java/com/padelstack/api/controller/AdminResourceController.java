package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminResourceRulesUpdateRequest;
import com.padelstack.api.dto.ResourceResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.ResourceService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de operaciones de administracion sobre recursos.
 */
@RestController
@RequestMapping("/api/v1/admin/resources")
public class AdminResourceController {

    private final ResourceService resourceService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AdminResourceController con las dependencias necesarias.
     *
     * @param resourceService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AdminResourceController(ResourceService resourceService, SecurityService securityService) {
        this.resourceService = resourceService;
        this.securityService = securityService;
    }

    /**
     * Actualiza solo el texto de reglas de un recurso.
     *
     * @param resourceId identificador del recurso.
     * @param request datos recibidos en la peticion.
     * @param authentication informacion de autenticacion del usuario.
     * @return recurso actualizado.
     */
    @PutMapping("/{resourceId}/rules")
    public ResourceResponse updateRules(@PathVariable String resourceId,
                                        @Valid @RequestBody AdminResourceRulesUpdateRequest request,
                                        Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return resourceService.updateRules(currentUser, resourceId, request);
    }
}
