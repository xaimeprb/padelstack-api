package com.padelstack.api.dto;

import java.util.List;

/**
 * DTO que transporta los datos de registration metadata response.
 *
 * @param communities valor recibido por el método.
 * @param units viviendas legacy mantenidas para clientes Android existentes.
 * @param unitOptions viviendas disponibles con su comunidad asociada.
 */
public record RegistrationMetadataResponse(
        List<CommunityItemResponse> communities,
        List<String> units,
        List<UnitItemResponse> unitOptions
) {
}
