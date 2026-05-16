package com.padelstack.api.service;

import com.padelstack.api.dto.CreateIncidentResponse;
import com.padelstack.api.dto.IncidentResponse;
import com.padelstack.api.dto.AdminIncidentStatusUpdateRequest;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.model.IncidentStatus;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.IncidentRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado de la lógica relacionada con incident.
 */
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final SecurityService securityService;
    private final PhotoStorageService photoStorageService;
    private final AuditLogService auditLogService;

    /**
     * Crea una instancia de IncidentService con las dependencias necesarias.
     *
     * @param incidentRepository repositorio usado por la clase.
     * @param securityService servicio usado por la clase.
     * @param photoStorageService servicio usado por la clase.
     * @param auditLogService servicio usado por la clase.
     */
    public IncidentService(IncidentRepository incidentRepository,
                           SecurityService securityService,
                           PhotoStorageService photoStorageService,
                           AuditLogService auditLogService) {
        this.incidentRepository = incidentRepository;
        this.securityService = securityService;
        this.photoStorageService = photoStorageService;
        this.auditLogService = auditLogService;
    }

    /**
     * Gestiona la operación mine.
     *
     * @param currentUser usuario que realiza la operación.
     * @return lista de elementos obtenida.
     */
    public List<IncidentResponse> mine(UserDocument currentUser) {
        return incidentRepository.findMine(currentUser.communityId, currentUser.uid).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Gestiona la operación all.
     *
     * @param currentUser usuario que realiza la operación.
     * @return lista de elementos obtenida.
     */
    public List<IncidentResponse> all(UserDocument currentUser) {
        securityService.requireAdmin(currentUser);
        return incidentRepository.findAllByCommunity(currentUser.communityId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param currentUser usuario que realiza la operación.
     * @param title título usado en la operación.
     * @param description descripción usada en la operación.
     * @param photo foto asociada a la incidencia.
     * @return resultado de la operación.
     */
    public CreateIncidentResponse create(UserDocument currentUser,
                                         String title,
                                         String description,
                                         MultipartFile photo) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Datos inválidos");
        }

        String incidentId = UUID.randomUUID().toString().replace("-", "");
        String now = TimeUtils.nowIsoUtc();

        IncidentDocument incident = new IncidentDocument();
        incident.incidentId = incidentId;
        incident.communityId = currentUser.communityId;
        incident.title = title;
        incident.description = description;
        incident.status = IncidentStatus.OPEN.name();
        incident.createdByUid = currentUser.uid;
        incident.createdByName = currentUser.fullName;
        incident.createdByEmail = currentUser.email;
        incident.createdAt = now;
        incident.updatedAt = now;
        incident.updatedByUid = currentUser.uid;

        PhotoStorageService.StoredPhoto storedPhoto = photoStorageService.saveIncidentPhoto(incidentId, photo);
        if (storedPhoto != null) {
            incident.storagePath = storedPhoto.storagePath();
            incident.photoUrl = storedPhoto.photoUrl();
        }

        incidentRepository.upsert(incident);
        auditLogService.log("INCIDENT_CREATED", "incident", incidentId, currentUser,
                java.util.Map.of("status", incident.status));
        return new CreateIncidentResponse(incidentId, incident.status);
    }

    /**
     * Elimina o cancela el registro solicitado si el usuario tiene permisos.
     *
     * @param currentUser usuario que realiza la operación.
     * @param incidentId identificador de la incidencia.
     */
    public void delete(UserDocument currentUser, String incidentId) {
        IncidentDocument incident = getRequiredIncident(currentUser.communityId, incidentId);
        boolean owner = currentUser.uid.equals(incident.createdByUid);
        boolean admin = securityService.isAdmin(currentUser);
        if (!owner && !admin) {
            throw new ForbiddenException("No tienes permisos");
        }
        incidentRepository.delete(incidentId);
        auditLogService.log("INCIDENT_DELETED", "incident", incidentId, currentUser,
                java.util.Map.of("title", incident.title));
    }

    /**
     * Devuelve required incident.
     *
     * @param communityId identificador de la comunidad.
     * @param incidentId identificador de la incidencia.
     * @return resultado de la operación.
     */
    public IncidentDocument getRequiredIncident(String communityId, String incidentId) {
        IncidentDocument incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new NotFoundException("Incidencia no encontrada"));
        if (!communityId.equals(incident.communityId)) {
            throw new NotFoundException("Incidencia no encontrada");
        }
        return incident;
    }

    /**
     * Gestiona la operación updateStatus.
     *
     * @param currentUser usuario que realiza la operación.
     * @param incidentId identificador de la incidencia.
     * @param request datos recibidos en la petición.
     */
    public void updateStatus(UserDocument currentUser, String incidentId, AdminIncidentStatusUpdateRequest request) {
        securityService.requireAdmin(currentUser);
        IncidentDocument incident = getRequiredIncident(currentUser.communityId, incidentId);
        IncidentStatus.valueOf(request.status());

        incident.status = request.status();
        incident.updatedAt = TimeUtils.nowIsoUtc();
        incident.updatedByUid = currentUser.uid;
        incidentRepository.upsert(incident);

        auditLogService.log("INCIDENT_STATUS_UPDATED", "incident", incidentId, currentUser,
                java.util.Map.of("status", request.status()));
    }

    /**
     * Gestiona la operación photoBytes.
     *
     * @param incidentId identificador de la incidencia.
     * @param storagePath valor recibido por el método.
     * @return resultado de la operación.
     */
    public byte[] photoBytes(String incidentId, String storagePath) {
        return photoStorageService.load(storagePath);
    }

    /**
     * Gestiona la operación photoContentType.
     *
     * @param incidentId identificador de la incidencia.
     * @param storagePath valor recibido por el método.
     * @return texto obtenido por el método.
     */
    public String photoContentType(String incidentId, String storagePath) {
        return photoStorageService.contentType(storagePath);
    }

    /**
     * Convierte un modelo interno en un DTO de respuesta.
     *
     * @param incident valor recibido por el método.
     * @return resultado de la operación.
     */
    private IncidentResponse toResponse(IncidentDocument incident) {
        return new IncidentResponse(
                incident.incidentId,
                incident.title,
                incident.description,
                incident.status,
                incident.photoUrl,
                incident.createdByName,
                incident.createdByEmail,
                incident.createdAt
        );
    }
}
