package com.padelstack.api.controller;

import com.padelstack.api.dto.AdminAnnouncementUpsertRequest;
import com.padelstack.api.dto.AnnouncementResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.AnnouncementService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;
    private final SecurityService securityService;

    public AdminAnnouncementController(AnnouncementService announcementService, SecurityService securityService) {
        this.announcementService = announcementService;
        this.securityService = securityService;
    }

    @PostMapping
    public AnnouncementResponse create(@Valid @RequestBody AdminAnnouncementUpsertRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.create(currentUser, request);
    }

    @PutMapping("/{id}")
    public AnnouncementResponse update(@PathVariable("id") String announcementId,
                                       @Valid @RequestBody AdminAnnouncementUpsertRequest request,
                                       Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return announcementService.update(currentUser, announcementId, request);
    }
}
