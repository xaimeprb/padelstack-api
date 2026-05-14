package com.padelstack.api.dto;

public record IncidentResponse(
        String incidentId,
        String title,
        String description,
        String status,
        String photoUrl,
        String createdByName,
        String createdByEmail,
        String createdAt
) {
}
