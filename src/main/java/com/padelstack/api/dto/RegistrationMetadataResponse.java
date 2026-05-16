package com.padelstack.api.dto;

import java.util.List;

/**
 * DTO que transporta los datos de registration metadata response.
 *
 * @param communities valor recibido por el método.
 * @param units valor recibido por el método.
 */
public record RegistrationMetadataResponse(
        List<CommunityItemResponse> communities,
        List<String> units
) {
}
