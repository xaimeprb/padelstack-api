package com.padelstack.api.dto;

import java.util.List;

public record RegistrationMetadataResponse(
        List<CommunityItemResponse> communities,
        List<String> units
) {
}
