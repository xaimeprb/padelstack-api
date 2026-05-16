package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de create incident response.
 *
 * @param incidentId identificador de la incidencia.
 * @param status estado usado para filtrar o actualizar datos.
 */
public record CreateIncidentResponse(
        String incidentId,
        String status
) {
}
