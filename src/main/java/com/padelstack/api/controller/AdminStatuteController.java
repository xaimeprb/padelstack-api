package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminStatuteUpsertRequest;
import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.StatuteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/statutes")
public class AdminStatuteController {

    private final StatuteService statuteService;
    private final SecurityService securityService;

    public AdminStatuteController(StatuteService statuteService, SecurityService securityService) {
        this.statuteService = statuteService;
        this.securityService = securityService;
    }

    @PutMapping("/{communityId}")
    public StatuteResponse upsert(@PathVariable String communityId,
                                  @Valid @RequestBody AdminStatuteUpsertRequest request,
                                  Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return statuteService.upsert(currentUser, communityId, request);
    }
}
