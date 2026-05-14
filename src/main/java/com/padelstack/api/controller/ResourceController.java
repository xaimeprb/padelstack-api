package com.padelstack.api.controller;

import com.padelstack.api.dto.AvailabilityResponse;
import com.padelstack.api.dto.ResourceResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.ResourceService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@Validated
public class ResourceController {

    private final ResourceService resourceService;
    private final SecurityService securityService;

    public ResourceController(ResourceService resourceService, SecurityService securityService) {
        this.resourceService = resourceService;
        this.securityService = securityService;
    }

    @GetMapping
    public List<ResourceResponse> resources(Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return resourceService.listResources(currentUser);
    }

    @GetMapping("/{resourceId}/availability")
    public AvailabilityResponse availability(@PathVariable String resourceId,
                                             @RequestParam("date") @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String date,
                                             Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return resourceService.availability(currentUser, resourceId, date);
    }
}
