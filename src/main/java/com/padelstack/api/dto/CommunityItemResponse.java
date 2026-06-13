package com.padelstack.api.dto;

import java.util.List;

/**
 * DTO que transporta los datos de community item response.
 *
 * @param communityId identificador de la comunidad.
 * @param name valor recibido por el método.
 * @param units viviendas asociadas a esta comunidad.
 */
public record CommunityItemResponse(String communityId, String name, List<UnitItemResponse> units) {
}
