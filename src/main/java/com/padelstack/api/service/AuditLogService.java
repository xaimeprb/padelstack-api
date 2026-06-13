package com.padelstack.api.service;

import com.padelstack.api.model.AuditLogDocument;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.AuditLogRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Servicio encargado de la lógica relacionada con audit log.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Crea una instancia de AuditLogService con las dependencias necesarias.
     *
     * @param auditLogRepository repositorio usado por la clase.
     */
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Guarda una acción en el registro de auditoría.
     *
     * @param action valor recibido por el método.
     * @param entityType valor recibido por el método.
     * @param entityId valor recibido por el método.
     * @param actor valor recibido por el método.
     * @param details valor recibido por el método.
     */
    public void log(String action,
                    String entityType,
                    String entityId,
                    UserDocument actor,
                    Map<String, Object> details) {
        AuditLogDocument log = new AuditLogDocument();
        log.logId = UUID.randomUUID().toString().replace("-", "");
        log.action = action;
        log.entityType = entityType;
        log.entityId = entityId;
        log.communityId = actor.communityId;
        log.actorUid = actor.uid;
        log.actorName = displayName(actor);
        log.actorEmail = actor.email;
        log.createdAt = TimeUtils.nowIsoUtc();
        log.details = details;
        auditLogRepository.upsert(log);
    }

    private static String displayName(UserDocument user) {
        String fullName = safe(user.fullName).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }
        String joined = (safe(user.firstName) + " " + safe(user.lastName)).trim();
        if (!joined.isBlank()) {
            return joined;
        }
        if (!safe(user.username).isBlank()) {
            return user.username;
        }
        if (!safe(user.email).isBlank()) {
            return user.email;
        }
        return user.uid;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
