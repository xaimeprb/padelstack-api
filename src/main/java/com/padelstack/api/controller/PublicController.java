package com.padelstack.api.controller;

import com.padelstack.api.dto.RegistrationMetadataResponse;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.repository.IncidentRepository;
import com.padelstack.api.service.CommunityService;
import com.padelstack.api.service.PhotoStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    private final CommunityService communityService;
    private final IncidentRepository incidentRepository;
    private final PhotoStorageService photoStorageService;

    public PublicController(CommunityService communityService,
                            IncidentRepository incidentRepository,
                            PhotoStorageService photoStorageService) {
        this.communityService = communityService;
        this.incidentRepository = incidentRepository;
        this.photoStorageService = photoStorageService;
    }

    @GetMapping("/registration-metadata")
    public RegistrationMetadataResponse registrationMetadata() {
        return communityService.registrationMetadata();
    }

    @GetMapping("/incidents/{incidentId}/photo")
    public ResponseEntity<byte[]> incidentPhoto(@PathVariable String incidentId, @RequestParam("path") String path) {
        IncidentDocument incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new com.padelstack.api.exception.NotFoundException("Foto no encontrada"));
        if (incident.storagePath == null || !incident.storagePath.equals(path)) {
            throw new com.padelstack.api.exception.NotFoundException("Foto no encontrada");
        }
        String contentType = photoStorageService.contentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(photoStorageService.load(path));
    }
}
