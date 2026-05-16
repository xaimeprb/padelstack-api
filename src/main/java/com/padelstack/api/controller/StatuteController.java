package com.padelstack.api.controller;

import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.StatuteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con statute.
 */
@RestController
@RequestMapping("/api/v1/statutes")
public class StatuteController {

    private final StatuteService statuteService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de StatuteController con las dependencias necesarias.
     *
     * @param statuteService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public StatuteController(StatuteService statuteService, SecurityService securityService) {
        this.statuteService = statuteService;
        this.securityService = securityService;
    }

    /**
     * Gestiona la operación current.
     *
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @GetMapping
    public StatuteResponse current(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return statuteService.currentForUser(currentUser);
    }
}
