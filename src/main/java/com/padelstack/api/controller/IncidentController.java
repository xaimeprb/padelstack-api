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

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final SecurityService securityService;

    public IncidentController(IncidentService incidentService, SecurityService securityService) {
        this.incidentService = incidentService;
        this.securityService = securityService;
    }

    @GetMapping("/mine")
    public List<IncidentResponse> mine(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.mine(currentUser);
    }

    @GetMapping
    public List<IncidentResponse> all(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.all(currentUser);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public CreateIncidentResponse create(@RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestPart(value = "photo", required = false) MultipartFile photo,
                                         Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return incidentService.create(currentUser, title, description, photo);
    }

    @DeleteMapping("/{incidentId}")
    public DeleteResponse delete(@PathVariable String incidentId,
                                 Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        incidentService.delete(currentUser, incidentId);
        return new DeleteResponse(true);
    }
}
