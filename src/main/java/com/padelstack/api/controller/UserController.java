package com.padelstack.api.controller;

import com.padelstack.api.dto.BootstrapUserRequest;
import com.padelstack.api.dto.MeResponse;
import com.padelstack.api.dto.SimpleCreatedResponse;
import com.padelstack.api.security.AuthenticatedUser;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con user.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de UserController con las dependencias necesarias.
     *
     * @param userService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * Crea o actualiza el usuario inicial en la comunidad correspondiente.
     *
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PostMapping("/users/bootstrap")
    public SimpleCreatedResponse bootstrap(@Valid @RequestBody BootstrapUserRequest request,
                                           Authentication authentication) {
        AuthenticatedUser auth = securityService.authenticatedUser(authentication);
        userService.bootstrap(auth, request);
        return new SimpleCreatedResponse(true);
    }

    /**
     * Construye la respuesta con los datos del usuario actual.
     *
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        AuthenticatedUser auth = securityService.authenticatedUser(authentication);
        return userService.me(auth.uid());
    }
}
