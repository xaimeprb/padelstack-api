package com.padelstack.api.service;

import com.padelstack.api.dto.AvailabilityDayStatusResponse;
import com.padelstack.api.dto.AvailabilityResponse;
import com.padelstack.api.dto.AvailabilitySlotResponse;
import com.padelstack.api.dto.AdminResourceRulesUpdateRequest;
import com.padelstack.api.dto.ResourceResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.*;
import com.padelstack.api.repository.ResourceRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la lógica relacionada con resource.
 */
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ReservationService reservationService;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

    /**
     * Crea una instancia de ResourceService con las dependencias necesarias.
     *
     * @param resourceRepository repositorio usado por la clase.
     * @param reservationService servicio usado por la clase.
     * @param securityService    servicio usado por la clase.
     * @param auditLogService    servicio usado por la clase.
     */
    public ResourceService(ResourceRepository resourceRepository,
            ReservationService reservationService,
            SecurityService securityService,
            AuditLogService auditLogService) {
        this.resourceRepository = resourceRepository;
        this.reservationService = reservationService;
        this.securityService = securityService;
        this.auditLogService = auditLogService;
    }

    /**
     * Obtiene los recursos disponibles para el usuario actual.
     *
     * @param currentUser usuario que realiza la operación.
     * @return lista de elementos obtenida.
     */
    public List<ResourceResponse> listResources(UserDocument currentUser) {
        return resourceRepository.findActiveByCommunity(currentUser.communityId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Actualiza el texto de reglas de un recurso.
     *
     * @param currentUser usuario que realiza la operacion.
     * @param resourceId  identificador del recurso.
     * @param request     datos recibidos en la peticion.
     * @return recurso actualizado.
     */
    public ResourceResponse updateRules(UserDocument currentUser,
            String resourceId,
            AdminResourceRulesUpdateRequest request) {
        securityService.requireAdmin(currentUser);
        if (request.rulesText() == null) {
            throw new BadRequestException("Datos invalidos");
        }

        ResourceDocument resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));
        checkRulesUpdateScope(currentUser, resource);

        resource.rulesText = request.rulesText().trim();
        resourceRepository.updateRulesText(resource.resourceId, resource.rulesText);
        auditLogService.log("RESOURCE_RULES_UPDATED", "resource", resource.resourceId, currentUser,
                Map.of("communityId", resource.communityId == null ? "" : resource.communityId));
        return toResponse(resource);
    }

    /**
     * Obtiene un recurso obligatorio dentro de una comunidad.
     *
     * @param resourceId  identificador del recurso.
     * @param communityId identificador de la comunidad.
     * @return resultado de la operación.
     */
    public ResourceDocument getRequiredResourceForCommunity(String resourceId, String communityId) {
        ResourceDocument resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));
        if (!communityId.equals(resource.communityId)) {
            throw new NotFoundException("Recurso no encontrado");
        }
        return resource;
    }

    /**
     * Comprueba que el usuario pueda modificar las reglas del recurso.
     *
     * @param currentUser usuario que realiza la operacion.
     * @param resource recurso que se quiere modificar.
     */
    private void checkRulesUpdateScope(UserDocument currentUser, ResourceDocument resource) {
        Role role = securityService.roleOf(currentUser);
        boolean sameCommunity = StringUtils.hasText(currentUser.communityId)
                && currentUser.communityId.equals(resource.communityId);

        if (role == Role.ADMIN && !sameCommunity) {
            throw new ForbiddenException("No tienes permisos");
        }
        if (role == Role.SUPERADMIN && StringUtils.hasText(currentUser.communityId) && !sameCommunity) {
            throw new ForbiddenException("No tienes permisos");
        }

     * Actualiza solo el texto de reglas de un recurso.
     *
     * @param currentUser usuario que realiza la operacion.
     * @param resourceId identificador del recurso.
     * @param request datos recibidos en la peticion.
     * @return recurso actualizado.
     */

    public ResourceResponse updateRules(UserDocument currentUser,
            String resourceId,
            AdminResourceRulesUpdateRequest request) {
        securityService.requireAdmin(currentUser);
        if (request.rulesText() == null) {
            throw new BadRequestException("Datos inválidos");
        }

        ResourceDocument resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));
        checkScope(currentUser, resource);

        resourceRepository.updateRulesText(resource.resourceId, request.rulesText());
        resource.rulesText = request.rulesText();
        auditLogService.log("RESOURCE_RULES_UPDATED", "resource", resource.resourceId, currentUser,
                Map.of("communityId", resource.communityId == null ? "" : resource.communityId));
        return toResponse(resource);
    }

    /**
     * Calcula la disponibilidad de un recurso en una fecha.
     *
     * @param currentUser usuario que realiza la operación.
     * @param resourceId  identificador del recurso.
     * @param date        fecha usada en la operación.
     * @return resultado de la operación.
     */
    public AvailabilityResponse availability(UserDocument currentUser, String resourceId, String date) {
        TimeUtils.parseDate(date);
        ResourceDocument resource = getRequiredResourceForCommunity(resourceId, currentUser.communityId);
        List<ReservationDocument> activeReservations = reservationService.findActiveReservations(resource.communityId,
                resource.resourceId, date);

        ReservationMode mode = ReservationMode.valueOf(resource.reservationMode);
        if (mode == ReservationMode.FULL_DAY) {
            ReservationDocument reservation = activeReservations.stream().findFirst().orElse(null);
            if (reservation == null) {
                return new AvailabilityResponse(
                        resource.resourceId,
                        date,
                        mode.name(),
                        null,
                        new AvailabilityDayStatusResponse(null, AvailabilityStatus.AVAILABLE.name(), false, null));
            }
            boolean ownerCurrentUser = currentUser.uid.equals(reservation.userId);
            return new AvailabilityResponse(
                    resource.resourceId,
                    date,
                    mode.name(),
                    null,
                    new AvailabilityDayStatusResponse(
                            reservation.reservationId,
                            ownerCurrentUser ? AvailabilityStatus.RESERVED_BY_ME.name()
                                    : AvailabilityStatus.RESERVED_BY_OTHER.name(),
                            ownerCurrentUser,
                            null));
        }

        if (resource.slotMinutes == null || resource.openTime == null || resource.closeTime == null) {
            throw new BadRequestException("Datos inválidos");
        }

        List<AvailabilitySlotResponse> slots = buildSlots(currentUser, resource, activeReservations);
        return new AvailabilityResponse(resource.resourceId, date, mode.name(), slots, null);
    }

    /**
     * Construye slots.
     *
     * @param currentUser        usuario que realiza la operación.
     * @param resource           valor recibido por el método.
     * @param activeReservations valor recibido por el método.
     * @return lista de elementos obtenida.
     */
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
                status = ownerCurrentUser ? AvailabilityStatus.RESERVED_BY_ME.name()
                        : AvailabilityStatus.RESERVED_BY_OTHER.name();
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
                    blockReason));
            current = end;
        }
        return result;
    }

    /**
     * <<<<<<< HEAD
     * Convierte un recurso interno en un DTO de respuesta.
     *
     * @param resource recurso recibido desde Firestore.
     * @return DTO de respuesta.
     *         =======
     *         Comprueba que el usuario pueda modificar el recurso indicado.
     *
     * @param currentUser usuario que realiza la operacion.
     * @param resource    recurso que se quiere modificar.
     */
    private void checkScope(UserDocument currentUser, ResourceDocument resource) {
        Role role = securityService.roleOf(currentUser);
        if (role == Role.SUPERADMIN) {
            return;
        }
        if (currentUser.communityId == null || !currentUser.communityId.equals(resource.communityId)) {
            throw new ForbiddenException("No tienes permisos");
        }
    }

    /**
     * Convierte un documento interno en la respuesta usada por la API.
     *
     * @param resource recurso que se convierte.
     * @return respuesta del recurso.
     *         >>>>>>> a67c33b91679d6d2998bb2e871bb77754504c970
     */
    private ResourceResponse toResponse(ResourceDocument resource) {
        return new ResourceResponse(
                resource.resourceId,
                resource.name,
                resource.type,
                resource.reservationMode,
                resource.slotMinutes,
                resource.openTime,
                resource.closeTime,
                resource.rulesText);
    }
}
