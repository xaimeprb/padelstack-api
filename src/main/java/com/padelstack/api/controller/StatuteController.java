package com.padelstack.api.controller;

import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.StatuteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statutes")
public class StatuteController {

    private final StatuteService statuteService;
    private final SecurityService securityService;

    public StatuteController(StatuteService statuteService, SecurityService securityService) {
        this.statuteService = statuteService;
        this.securityService = securityService;
    }

    @GetMapping
    public StatuteResponse current(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return statuteService.currentForUser(currentUser);
    }
}
