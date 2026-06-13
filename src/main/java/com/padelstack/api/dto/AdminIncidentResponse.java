package com.padelstack.api.dto;

/**
 * Incidencia visible desde el panel de administracion.
 */
public record AdminIncidentResponse(
        String incidentId,
        String communityId,
        String title,
        String description,
        String status,
        String photoUrl,
        String storagePath,
        String createdByUid,
        String createdByName,
        String createdByEmail,
        String createdAt,
        String updatedAt,
        String updatedByUid
) {
}
