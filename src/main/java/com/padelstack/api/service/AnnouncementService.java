package com.padelstack.api.service;

import com.padelstack.api.dto.AdminAnnouncementUpsertRequest;
import com.padelstack.api.dto.AnnouncementResponse;
import com.padelstack.api.model.AnnouncementDocument;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.AnnouncementRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado de la lógica relacionada con announcement.
 */
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

    /**
     * Crea una instancia de AnnouncementService con las dependencias necesarias.
     *
     * @param announcementRepository repositorio usado por la clase.
     * @param securityService servicio usado por la clase.
     * @param auditLogService servicio usado por la clase.
     */
    public AnnouncementService(AnnouncementRepository announcementRepository,
                               SecurityService securityService,
                               AuditLogService auditLogService) {
        this.announcementRepository = announcementRepository;
        this.securityService = securityService;
        this.auditLogService = auditLogService;
    }

    /**
     * Gestiona la operación visible.
     *
     * @param currentUser usuario que realiza la operación.
     * @return lista de elementos obtenida.
     */
    public List<AnnouncementResponse> visible(UserDocument currentUser) {
        return announcementRepository.findVisibleByCommunity(currentUser.communityId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param currentUser usuario que realiza la operación.
     * @param request datos recibidos en la petición.
     * @return resultado de la operación.
     */
    public AnnouncementResponse create(UserDocument currentUser, AdminAnnouncementUpsertRequest request) {
        securityService.requireAdmin(currentUser);
        String announcementId = UUID.randomUUID().toString().replace("-", "");
        String now = TimeUtils.nowIsoUtc();

        AnnouncementDocument document = new AnnouncementDocument();
        document.announcementId = announcementId;
        document.communityId = resolveTargetCommunity(currentUser, request.communityId());
        document.title = request.title();
        document.content = request.content();
        document.visible = request.visible() == null ? Boolean.TRUE : request.visible();
        document.publishedAt = StringUtils.hasText(request.publishedAt()) ? request.publishedAt() : now;
        document.createdByUid = currentUser.uid;
        document.createdByName = currentUser.fullName;
        document.updatedAt = now;

        announcementRepository.upsert(document);
        auditLogService.log("ANNOUNCEMENT_CREATED", "announcement", announcementId, currentUser,
                java.util.Map.of("communityId", document.communityId));
        return toResponse(document);
    }

    /**
     * Gestiona la operación update.
     *
     * @param currentUser usuario que realiza la operación.
     * @param announcementId valor recibido por el método.
     * @param request datos recibidos en la petición.
     * @return resultado de la operación.
     */
    public AnnouncementResponse update(UserDocument currentUser, String announcementId, AdminAnnouncementUpsertRequest request) {
        securityService.requireAdmin(currentUser);
        AnnouncementDocument document = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new com.padelstack.api.exception.NotFoundException("Anuncio no encontrado"));

        checkScope(currentUser, document.communityId);

        document.title = request.title();
        document.content = request.content();
        document.visible = request.visible() == null ? document.visible : request.visible();
        document.publishedAt = StringUtils.hasText(request.publishedAt()) ? request.publishedAt() : document.publishedAt;
        document.updatedAt = TimeUtils.nowIsoUtc();

        announcementRepository.upsert(document);
        auditLogService.log("ANNOUNCEMENT_UPDATED", "announcement", announcementId, currentUser,
                java.util.Map.of("communityId", document.communityId));
        return toResponse(document);
    }

    /**
     * Gestiona la operación resolveTargetCommunity.
     *
     * @param currentUser usuario que realiza la operación.
     * @param requestedCommunityId valor recibido por el método.
     * @return texto obtenido por el método.
     */
    private String resolveTargetCommunity(UserDocument currentUser, String requestedCommunityId) {
        if (Role.SUPERADMIN.name().equals(currentUser.role) && StringUtils.hasText(requestedCommunityId)) {
            return requestedCommunityId;
        }
        return currentUser.communityId;
    }

    /**
     * Gestiona la operación checkScope.
     *
     * @param currentUser usuario que realiza la operación.
     * @param targetCommunityId valor recibido por el método.
     */
    private void checkScope(UserDocument currentUser, String targetCommunityId) {
        if (!Role.SUPERADMIN.name().equals(currentUser.role) && !currentUser.communityId.equals(targetCommunityId)) {
            throw new com.padelstack.api.exception.ForbiddenException("No tienes permisos");
        }
    }

    /**
     * Convierte un modelo interno en un DTO de respuesta.
     *
     * @param document valor recibido por el método.
     * @return resultado de la operación.
     */
    private AnnouncementResponse toResponse(AnnouncementDocument document) {
        return new AnnouncementResponse(
                document.announcementId,
                document.title,
                document.content,
                Boolean.TRUE.equals(document.visible),
                document.publishedAt,
                document.createdByUid,
                document.createdByName,
                document.updatedAt
        );
    }
}
