package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de incident response.
 *
 * @param incidentId identificador de la incidencia.
 * @param title título usado en la operación.
 * @param description descripción usada en la operación.
 * @param status estado usado para filtrar o actualizar datos.
 * @param photoUrl valor recibido por el método.
 * @param createdByName valor recibido por el método.
 * @param createdByEmail valor recibido por el método.
 * @param createdAt fecha de creación usada como identificador.
 */
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
