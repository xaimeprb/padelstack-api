package com.padelstack.api.dto;

import java.util.Map;

/**
 * Registro de auditoria visible desde el panel de administracion.
 */
public record AdminAuditLogResponse(
        String auditLogId,
        String createdAt,
        String actorUid,
        String actorName,
        String actorEmail,
        String action,
        String entityType,
        String entityId,
        String description,
        Map<String, Object> metadata
) {
}
