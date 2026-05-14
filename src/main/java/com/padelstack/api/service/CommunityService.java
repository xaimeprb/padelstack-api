package com.padelstack.api.service;

import com.padelstack.api.dto.CommunityItemResponse;
import com.padelstack.api.dto.RegistrationMetadataResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public RegistrationMetadataResponse registrationMetadata() {
        List<CommunityDocument> communities = communityRepository.findAllActive();
        List<CommunityItemResponse> items = communities.stream()
                .sorted(Comparator.comparing(c -> c.name))
                .map(c -> new CommunityItemResponse(c.communityId, c.name))
                .toList();

        List<String> units = communities.stream()
                .filter(Objects::nonNull)
                .flatMap(c -> c.units.stream())
                .distinct()
                .sorted()
                .toList();

        return new RegistrationMetadataResponse(items, units);
    }

    public CommunityDocument getRequiredCommunity(String communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("Comunidad no encontrada"));
    }

    public void validateUnitBelongsToCommunity(CommunityDocument community, String unitDisplay) {
        if (community.units == null || !community.units.contains(unitDisplay)) {
            throw new BadRequestException("Datos inválidos");
        }
    }
}
