package com.padelstack.api.controller;

import com.padelstack.api.dto.RegistrationMetadataResponse;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.repository.IncidentRepository;
import com.padelstack.api.service.CommunityService;
import com.padelstack.api.service.PhotoStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de atender peticiones relacionadas con public.
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    private final CommunityService communityService;
    private final IncidentRepository incidentRepository;
    private final PhotoStorageService photoStorageService;

    /**
     * Crea una instancia de PublicController con las dependencias necesarias.
     *
     * @param communityService servicio usado por la clase.
     * @param incidentRepository repositorio usado por la clase.
     * @param photoStorageService servicio usado por la clase.
     */
    public PublicController(CommunityService communityService,
                            IncidentRepository incidentRepository,
                            PhotoStorageService photoStorageService) {
        this.communityService = communityService;
        this.incidentRepository = incidentRepository;
        this.photoStorageService = photoStorageService;
    }

    /**
     * Gestiona la operación registrationMetadata.
     *
     * @return resultado de la operación.
     */
    @GetMapping("/registration-metadata")
    public RegistrationMetadataResponse registrationMetadata() {
        return communityService.registrationMetadata();
    }

    /**
     * Gestiona la operación incidentPhoto.
     *
     * @param incidentId identificador de la incidencia.
     * @param path ruta del recurso solicitado.
     * @return respuesta HTTP construida por el método.
     */
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
