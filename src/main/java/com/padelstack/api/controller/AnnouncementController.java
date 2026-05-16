package com.padelstack.api.controller;

import com.padelstack.api.dto.AnnouncementResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AnnouncementService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de atender peticiones relacionadas con announcement.
 */
@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AnnouncementController con las dependencias necesarias.
     *
     * @param announcementService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AnnouncementController(AnnouncementService announcementService, SecurityService securityService) {
        this.announcementService = announcementService;
        this.securityService = securityService;
    }

    /**
     * Gestiona la operación all.
     *
     * @param authentication información de autenticación del usuario.
     * @return lista de elementos obtenida.
     */
    @GetMapping
    public List<AnnouncementResponse> all(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.visible(currentUser);
    }
}
