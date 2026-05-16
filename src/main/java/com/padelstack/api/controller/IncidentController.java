package com.padelstack.api.controller;

import com.padelstack.api.dto.CreateIncidentResponse;
import com.padelstack.api.dto.DeleteResponse;
import com.padelstack.api.dto.IncidentResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.IncidentService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador REST encargado de atender peticiones relacionadas con incident.
 */
@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de IncidentController con las dependencias necesarias.
     *
     * @param incidentService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public IncidentController(IncidentService incidentService, SecurityService securityService) {
        this.incidentService = incidentService;
        this.securityService = securityService;
    }

    /**
     * Gestiona la operación mine.
     *
     * @param authentication información de autenticación del usuario.
     * @return lista de elementos obtenida.
     */
    @GetMapping("/mine")
    public List<IncidentResponse> mine(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.mine(currentUser);
    }

    /**
     * Gestiona la operación all.
     *
     * @param authentication información de autenticación del usuario.
     * @return lista de elementos obtenida.
     */
    @GetMapping
    public List<IncidentResponse> all(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.all(currentUser);
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param title título usado en la operación.
     * @param description descripción usada en la operación.
     * @param photo foto asociada a la incidencia.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public CreateIncidentResponse create(@RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestPart(value = "photo", required = false) MultipartFile photo,
                                         Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.create(currentUser, title, description, photo);
    }

    /**
     * Elimina o cancela el registro solicitado si el usuario tiene permisos.
     *
     * @param incidentId identificador de la incidencia.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @DeleteMapping("/{incidentId}")
    public DeleteResponse delete(@PathVariable String incidentId,
                                 Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        incidentService.delete(currentUser, incidentId);
        return new DeleteResponse(true);
    }
}
