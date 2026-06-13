package com.padelstack.api.dto;

import java.util.List;

/**
 * Comunidad visible desde el panel de administracion.
 */
public record AdminCommunityResponse(
        String communityId,
        String name,
        boolean active,
        long usersCount,
        long resourcesCount,
        int unitsCount,
        List<String> units
) {
}
