package com.padelstack.api.service;

import com.padelstack.api.dto.AdminStatuteUpsertRequest;
import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.StatuteDocument;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.StatuteRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la lógica relacionada con statute.
 */
@Service
public class StatuteService {

    private final StatuteRepository statuteRepository;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

    /**
     * Crea una instancia de StatuteService con las dependencias necesarias.
     *
     * @param statuteRepository repositorio usado por la clase.
     * @param securityService servicio usado por la clase.
     * @param auditLogService servicio usado por la clase.
     */
    public StatuteService(StatuteRepository statuteRepository,
                          SecurityService securityService,
                          AuditLogService auditLogService) {
        this.statuteRepository = statuteRepository;
        this.securityService = securityService;
        this.auditLogService = auditLogService;
    }

    /**
     * Obtiene la normativa visible para el usuario actual.
     *
     * @param currentUser usuario que realiza la operación.
     * @return resultado de la operación.
     */
    public StatuteResponse currentForUser(UserDocument currentUser) {
        StatuteDocument document = statuteRepository.findById(currentUser.communityId)
                .orElseThrow(() -> new NotFoundException("Estatutos no encontrados"));

        return toResponse(document);
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param currentUser usuario que realiza la operación.
     * @param communityId identificador de la comunidad.
     * @param request datos recibidos en la petición.
     * @return resultado de la operación.
     */
    public StatuteResponse upsert(UserDocument currentUser, String communityId, AdminStatuteUpsertRequest request) {
        securityService.requireAdmin(currentUser);

        if (!Role.SUPERADMIN.name().equals(currentUser.role) && !currentUser.communityId.equals(communityId)) {
            throw new ForbiddenException("No tienes permisos");
        }

        StatuteDocument document = statuteRepository.findById(communityId).orElseGet(StatuteDocument::new);
        document.communityId = communityId;
        document.title = request.title();
        document.content = request.content();
        document.version = request.version();
        document.updatedAt = TimeUtils.nowIsoUtc();
        document.updatedByUid = currentUser.uid;

        statuteRepository.upsert(document);
        auditLogService.log("STATUTE_UPSERTED", "statute", communityId, currentUser,
                java.util.Map.of("version", request.version()));
        return toResponse(document);
    }

    /**
     * Convierte un modelo interno en un DTO de respuesta.
     *
     * @param document valor recibido por el método.
     * @return resultado de la operación.
     */
    private StatuteResponse toResponse(StatuteDocument document) {
        return new StatuteResponse(
                document.communityId,
                document.title,
                document.content,
                document.version,
                document.updatedAt,
                document.updatedByUid
        );
    }
}
