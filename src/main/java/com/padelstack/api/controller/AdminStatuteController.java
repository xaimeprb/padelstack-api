package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminStatuteUpsertRequest;
import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AdminService;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.StatuteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con admin statute.
 */
@RestController
@RequestMapping("/api/v1/admin/statutes")
public class AdminStatuteController {

    private final AdminService adminService;
    private final StatuteService statuteService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de AdminStatuteController con las dependencias necesarias.
     *
     * @param statuteService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public AdminStatuteController(AdminService adminService,
                                  StatuteService statuteService,
                                  SecurityService securityService) {
        this.adminService = adminService;
        this.statuteService = statuteService;
        this.securityService = securityService;
    }

    /**
     * Lista estatutos para SUPERADMIN.
     */
    @GetMapping
    public java.util.List<StatuteResponse> all(@RequestParam(required = false) String communityId,
                                               Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return adminService.statutes(currentUser, communityId);
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param communityId identificador de la comunidad.
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PutMapping("/{communityId}")
    public StatuteResponse upsert(@PathVariable String communityId,
                                  @Valid @RequestBody AdminStatuteUpsertRequest request,
                                  Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        securityService.requireSuperAdmin(currentUser);
        return statuteService.upsert(currentUser, communityId, request);
    }
}
