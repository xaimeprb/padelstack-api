package com.padelstack.api.dto;

/**
 * DTO que transporta una vivienda asociada a una comunidad.
 *
 * @param unitId identificador estable de la vivienda.
 * @param display texto visible de la vivienda.
 * @param communityId identificador de la comunidad propietaria.
 */
public record UnitItemResponse(String unitId, String display, String communityId) {
}
