package com.padelstack.api.service;

import com.padelstack.api.dto.CommunityItemResponse;
import com.padelstack.api.dto.RegistrationMetadataResponse;
import com.padelstack.api.dto.UnitItemResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Servicio encargado de la lógica relacionada con community.
 */
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    /**
     * Crea una instancia de CommunityService con las dependencias necesarias.
     *
     * @param communityRepository repositorio usado por la clase.
     */
    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    /**
     * Gestiona la operación registrationMetadata.
     *
     * @return resultado de la operación.
     */
    public RegistrationMetadataResponse registrationMetadata() {
        List<CommunityDocument> communities = communityRepository.findAllActive();
        List<CommunityItemResponse> items = communities.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(c -> safeText(c.name)))
                .map(c -> new CommunityItemResponse(c.communityId, c.name, unitItemsForCommunity(c)))
                .toList();

        List<UnitItemResponse> unitOptions = communities.stream()
                .filter(Objects::nonNull)
                .flatMap(c -> unitItemsForCommunity(c).stream())
                .sorted(Comparator.comparing(UnitItemResponse::display)
                        .thenComparing(UnitItemResponse::communityId))
                .toList();

        List<String> legacyUnits = unitOptions.stream()
                .map(UnitItemResponse::display)
                .distinct()
                .sorted()
                .toList();

        return new RegistrationMetadataResponse(items, legacyUnits, unitOptions);
    }

    /**
     * Devuelve required community.
     *
     * @param communityId identificador de la comunidad.
     * @return resultado de la operación.
     */
    public CommunityDocument getRequiredCommunity(String communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("Comunidad no encontrada"));
    }

    /**
     * Obtiene una comunidad activa para el alta pública.
     *
     * @param communityId identificador de la comunidad.
     * @return comunidad activa encontrada.
     */
    public CommunityDocument getRequiredActiveCommunityForRegistration(String communityId) {
        if (isBlank(communityId)) {
            throw new BadRequestException("La comunidad seleccionada no existe.");
        }
        CommunityDocument community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BadRequestException("La comunidad seleccionada no existe."));
        if (!Boolean.TRUE.equals(community.active)) {
            throw new BadRequestException("La comunidad seleccionada no existe.");
        }
        return community;
    }

    /**
     * Gestiona la operación validateUnitBelongsToCommunity.
     *
     * @param community valor recibido por el método.
     * @param unitDisplay texto de la vivienda del usuario.
     */
    public void validateUnitBelongsToCommunity(CommunityDocument community, String unitDisplay) {
        if (isBlank(unitDisplay)) {
            throw new BadRequestException("La vivienda seleccionada no existe.");
        }
        if (community.units != null && community.units.contains(unitDisplay)) {
            return;
        }

        boolean existsInAnotherCommunity = communityRepository.findAllActive().stream()
                .filter(Objects::nonNull)
                .filter(c -> !Objects.equals(c.communityId, community.communityId))
                .flatMap(this::safeUnits)
                .anyMatch(unit -> unit.equals(unitDisplay));

        if (existsInAnotherCommunity) {
            throw new BadRequestException("La vivienda seleccionada no pertenece a la comunidad indicada.");
        }

        throw new BadRequestException("La vivienda seleccionada no existe.");
    }

    private List<UnitItemResponse> unitItemsForCommunity(CommunityDocument community) {
        return safeUnits(community)
                .map(unit -> new UnitItemResponse(unit, unit, community.communityId))
                .toList();
    }

    private Stream<String> safeUnits(CommunityDocument community) {
        if (community.units == null) {
            return Stream.empty();
        }
        return community.units.stream()
                .filter(unit -> !isBlank(unit))
                .distinct();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }
}
