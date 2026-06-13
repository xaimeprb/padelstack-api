package com.padelstack.api.dto;

import java.util.List;
import java.util.Map;

/**
 * Resumen global para el dashboard administrativo.
 */
public record AdminDashboardResponse(
        Map<String, Long> totals,
        Map<String, Long> usersByRole,
        List<AdminReservationResponse> latestReservations,
        List<AdminAnnouncementResponse> latestAnnouncements,
        List<AdminIncidentResponse> latestIncidents
) {
}
