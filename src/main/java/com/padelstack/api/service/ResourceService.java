package com.padelstack.api.service;

import com.padelstack.api.dto.AvailabilityDayStatusResponse;
import com.padelstack.api.dto.AvailabilityResponse;
import com.padelstack.api.dto.AvailabilitySlotResponse;
import com.padelstack.api.dto.ResourceResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.*;
import com.padelstack.api.repository.ResourceRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ReservationService reservationService;

    public ResourceService(ResourceRepository resourceRepository, ReservationService reservationService) {
        this.resourceRepository = resourceRepository;
        this.reservationService = reservationService;
    }

    public List<ResourceResponse> listResources(UserDocument currentUser) {
        return resourceRepository.findActiveByCommunity(currentUser.communityId).stream()
                .map(resource -> new ResourceResponse(
                        resource.resourceId,
                        resource.name,
                        resource.type,
                        resource.reservationMode,
                        resource.slotMinutes,
                        resource.openTime,
                        resource.closeTime,
                        resource.rulesText
                ))
                .toList();
    }

    public ResourceDocument getRequiredResourceForCommunity(String resourceId, String communityId) {
        ResourceDocument resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));
        if (!communityId.equals(resource.communityId)) {
            throw new NotFoundException("Recurso no encontrado");
        }
        return resource;
    }

    public AvailabilityResponse availability(UserDocument currentUser, String resourceId, String date) {
        TimeUtils.parseDate(date);
        ResourceDocument resource = getRequiredResourceForCommunity(resourceId, currentUser.communityId);
        List<ReservationDocument> activeReservations =
                reservationService.findActiveReservations(resource.communityId, resource.resourceId, date);

        ReservationMode mode = ReservationMode.valueOf(resource.reservationMode);
        if (mode == ReservationMode.FULL_DAY) {
            ReservationDocument reservation = activeReservations.stream().findFirst().orElse(null);
            if (reservation == null) {
                return new AvailabilityResponse(
                        resource.resourceId,
                        date,
                        mode.name(),
                        null,
                        new AvailabilityDayStatusResponse(null, AvailabilityStatus.AVAILABLE.name(), false, null)
                );
            }
            boolean ownerCurrentUser = currentUser.uid.equals(reservation.userId);
            return new AvailabilityResponse(
                    resource.resourceId,
                    date,
                    mode.name(),
                    null,
                    new AvailabilityDayStatusResponse(
                            reservation.reservationId,
                            ownerCurrentUser ? AvailabilityStatus.RESERVED_BY_ME.name() : AvailabilityStatus.RESERVED_BY_OTHER.name(),
                            ownerCurrentUser,
                            null
                    )
            );
        }

        if (resource.slotMinutes == null || resource.openTime == null || resource.closeTime == null) {
            throw new BadRequestException("Datos inválidos");
        }

        List<AvailabilitySlotResponse> slots = buildSlots(currentUser, resource, activeReservations);
        return new AvailabilityResponse(resource.resourceId, date, mode.name(), slots, null);
    }

    private List<AvailabilitySlotResponse> buildSlots(UserDocument currentUser,
                                                      ResourceDocument resource,
                                                      List<ReservationDocument> activeReservations) {
        List<AvailabilitySlotResponse> result = new ArrayList<>();
        LocalTime open = TimeUtils.parseTime(resource.openTime);
        LocalTime close = TimeUtils.parseTime(resource.closeTime);

        LocalTime current = open;
        while (!current.plusMinutes(resource.slotMinutes).isAfter(close)) {
            LocalTime end = current.plusMinutes(resource.slotMinutes);
            String startText = TimeUtils.formatTime(current);
            String endText = TimeUtils.formatTime(end);

            ReservationDocument matchingReservation = activeReservations.stream()
                    .filter(r -> startText.equals(r.startTime) && endText.equals(r.endTime))
                    .findFirst()
                    .orElse(null);

            String reservationId = null;
            String status = AvailabilityStatus.AVAILABLE.name();
            boolean ownerCurrentUser = false;
            String blockReason = null;

            if (matchingReservation != null) {
                reservationId = matchingReservation.reservationId;
                ownerCurrentUser = currentUser.uid.equals(matchingReservation.userId);
                status = ownerCurrentUser ? AvailabilityStatus.RESERVED_BY_ME.name() : AvailabilityStatus.RESERVED_BY_OTHER.name();
            } else if (!Boolean.TRUE.equals(resource.active)) {
                status = AvailabilityStatus.BLOCKED.name();
                blockReason = "RESOURCE_INACTIVE";
            }

            result.add(new AvailabilitySlotResponse(
                    reservationId,
                    TimeUtils.buildSlotLabel(startText, endText),
                    startText,
                    endText,
                    status,
                    ownerCurrentUser,
                    blockReason
            ));
            current = end;
        }
        return result;
    }
}
