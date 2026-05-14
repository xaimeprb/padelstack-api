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

@Service
public class StatuteService {

    private final StatuteRepository statuteRepository;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

    public StatuteService(StatuteRepository statuteRepository,
                          SecurityService securityService,
                          AuditLogService auditLogService) {
        this.statuteRepository = statuteRepository;
        this.securityService = securityService;
        this.auditLogService = auditLogService;
    }

    public StatuteResponse currentForUser(UserDocument currentUser) {
        StatuteDocument document = statuteRepository.findById(currentUser.communityId)
                .orElseThrow(() -> new NotFoundException("Estatutos no encontrados"));

        return toResponse(document);
    }

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
