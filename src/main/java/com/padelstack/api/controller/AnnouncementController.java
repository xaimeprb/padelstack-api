package com.padelstack.api.controller;

import com.padelstack.api.dto.AnnouncementResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AnnouncementService;
import com.padelstack.api.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final SecurityService securityService;

    public AnnouncementController(AnnouncementService announcementService, SecurityService securityService) {
        this.announcementService = announcementService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AnnouncementResponse> all(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.visible(currentUser);
    }
}
