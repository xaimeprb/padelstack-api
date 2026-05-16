package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminAnnouncementUpsertRequest;
import com.padelstack.api.dto.AnnouncementResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AnnouncementService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con admin announcement.
 */
@RestController
@RequestMapping("/api/v1/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AdminAnnouncementController con las dependencias necesarias.
     *
     * @param announcementService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AdminAnnouncementController(AnnouncementService announcementService, SecurityService securityService) {
        this.announcementService = announcementService;
        this.securityService = securityService;
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PostMapping
    public AnnouncementResponse create(@Valid @RequestBody AdminAnnouncementUpsertRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.create(currentUser, request);
    }

    /**
     * Gestiona la operación update.
     *
     * @param announcementId valor recibido por el método.
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PutMapping("/{id}")
    public AnnouncementResponse update(@PathVariable("id") String announcementId,
                                       @Valid @RequestBody AdminAnnouncementUpsertRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.update(currentUser, announcementId, request);
    }
}
