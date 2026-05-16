package com.padelstack.api.dto;

/**
 * DTO que transporta los datos de community item response.
 *
 * @param communityId identificador de la comunidad.
 * @param name valor recibido por el método.
 */
public record CommunityItemResponse(String communityId, String name) {
}
