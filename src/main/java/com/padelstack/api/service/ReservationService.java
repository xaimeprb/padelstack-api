package com.padelstack.api.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.padelstack.api.dto.CreateReservationRequest;
import com.padelstack.api.dto.CreateReservationResponse;
import com.padelstack.api.dto.ReservationSummaryResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.ConflictException;
import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.ReservationDocument;
import com.padelstack.api.model.ReservationMode;
import com.padelstack.api.model.ReservationStatus;
import com.padelstack.api.model.ResourceDocument;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.ReservationRepository;
import com.padelstack.api.repository.ResourceRepository;
import com.padelstack.api.util.FirestoreSupport;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio encargado de la lógica relacionada con reservation.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final Firestore firestore;
    private final AuditLogService auditLogService;

    /**
     * Crea una instancia de ReservationService con las dependencias necesarias.
     *
     * @param reservationRepository repositorio usado por la clase.
     * @param resourceRepository repositorio usado por la clase.
     * @param firestore valor recibido por el método.
     * @param auditLogService servicio usado por la clase.
     */
    public ReservationService(ReservationRepository reservationRepository,
                              ResourceRepository resourceRepository,
                              Firestore firestore,
                              AuditLogService auditLogService) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.firestore = firestore;
        this.auditLogService = auditLogService;
    }

    /**
     * Busca las reservas activas de un recurso en una fecha.
     *
     * @param communityId identificador de la comunidad.
     * @param resourceId identificador del recurso.
     * @param date fecha usada en la operación.
     * @return lista de elementos obtenida.
     */
    public List<ReservationDocument> findActiveReservations(String communityId, String resourceId, String date) {
        return reservationRepository.findActiveByResourceAndDate(communityId, resourceId, date);
    }

    /**
     * Obtiene las reservas del usuario actual filtradas por estado.
     *
     * @param currentUser usuario que realiza la operación.
     * @param status estado usado para filtrar o actualizar datos.
     * @return lista de elementos obtenida.
     */
    public List<ReservationSummaryResponse> myReservations(UserDocument currentUser, String status) {
        String normalized = status == null || status.isBlank()
                ? ReservationStatus.ACTIVE.name()
                : status;

        return reservationRepository.findByUserAndStatus(currentUser.communityId, currentUser.uid, normalized).stream()
                .map(r -> new ReservationSummaryResponse(
                        r.reservationId,
                        r.resourceId,
                        r.resourceName,
                        r.date,
                        r.startTime,
                        r.endTime,
                        Boolean.TRUE.equals(r.allDay),
                        r.slotLabel,
                        r.status
                ))
                .toList();
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param currentUser usuario que realiza la operación.
     * @param request datos recibidos en la petición.
     * @return resultado de la operación.
     */
    public CreateReservationResponse create(UserDocument currentUser, CreateReservationRequest request) {
        if (!Boolean.TRUE.equals(currentUser.active)) {
            throw new ForbiddenException("No tienes permisos");
        }

        TimeUtils.parseDate(request.date());

        ResourceDocument resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado"));

        if (!currentUser.communityId.equals(resource.communityId)) {
            throw new NotFoundException("Recurso no encontrado");
        }

        if (!Boolean.TRUE.equals(resource.active)) {
            throw new BadRequestException("Datos inválidos");
        }

        String resolvedResourceId = resolveResourceId(resource, request.resourceId());
        String reservationId = UUID.randomUUID().toString().replace("-", "");
        String now = TimeUtils.nowIsoUtc();
        ReservationMode mode = ReservationMode.valueOf(resource.reservationMode);

        ReservationDocument reservation = new ReservationDocument();
        reservation.reservationId = reservationId;
        reservation.communityId = currentUser.communityId;
        reservation.userId = currentUser.uid;
        reservation.userEmail = currentUser.email;
        reservation.userFullName = currentUser.fullName;
        reservation.resourceId = resolvedResourceId;
        reservation.resourceName = resource.name;
        reservation.date = request.date();
        reservation.status = ReservationStatus.ACTIVE.name();
        reservation.createdAt = now;
        reservation.updatedAt = now;

        if (mode == ReservationMode.FULL_DAY) {
            if (!Boolean.TRUE.equals(request.allDay())) {
                throw new BadRequestException("Datos inválidos");
            }
            reservation.allDay = true;
            reservation.startTime = null;
            reservation.endTime = null;
            reservation.slotLabel = null;
        } else {
            if (Boolean.TRUE.equals(request.allDay()) || request.startTime() == null || request.endTime() == null) {
                throw new BadRequestException("Datos inválidos");
            }
            validateSlot(resource, request.startTime(), request.endTime());
            reservation.allDay = false;
            reservation.startTime = request.startTime();
            reservation.endTime = request.endTime();
            reservation.slotLabel = TimeUtils.buildSlotLabel(request.startTime(), request.endTime());
        }

        saveReservationWithConflictCheck(currentUser, reservation, mode);

        auditLogService.log(
                "RESERVATION_CREATED",
                "reservation",
                reservation.reservationId,
                currentUser,
                buildAuditDetails(reservation)
        );

        return new CreateReservationResponse(reservation.reservationId, reservation.status);
    }

    /**
     * Resuelve el identificador real del recurso a utilizar.
     *
     * @param resource valor recibido por el método.
     * @param fallbackResourceId valor recibido por el método.
     * @return texto obtenido por el método.
     */
    private String resolveResourceId(ResourceDocument resource, String fallbackResourceId) {
        if (resource != null && resource.resourceId != null && !resource.resourceId.isBlank()) {
            return resource.resourceId;
        }
        if (fallbackResourceId != null && !fallbackResourceId.isBlank()) {
            return fallbackResourceId;
        }
        throw new BadRequestException("Datos inválidos");
    }

    /**
     * Prepara los detalles que se guardan en el registro de auditoría.
     *
     * @param reservation valor recibido por el método.
     * @return resultado de la operación.
     */
    private Map<String, Object> buildAuditDetails(ReservationDocument reservation) {
        Map<String, Object> details = new HashMap<>();
        details.put("resourceId", reservation.resourceId);
        details.put("date", reservation.date);
        return details;
    }

    /**
     * Valida que el tramo horario encaje con la configuración del recurso.
     *
     * @param resource valor recibido por el método.
     * @param startTimeText valor recibido por el método.
     * @param endTimeText valor recibido por el método.
     */
    private void validateSlot(ResourceDocument resource, String startTimeText, String endTimeText) {
        if (resource.slotMinutes == null || resource.openTime == null || resource.closeTime == null) {
            throw new BadRequestException("Datos inválidos");
        }

        LocalTime open = TimeUtils.parseTime(resource.openTime);
        LocalTime close = TimeUtils.parseTime(resource.closeTime);
        LocalTime start = TimeUtils.parseTime(startTimeText);
        LocalTime end = TimeUtils.parseTime(endTimeText);

        if (!start.plusMinutes(resource.slotMinutes).equals(end)) {
            throw new BadRequestException("Datos inválidos");
        }
        if (start.isBefore(open) || end.isAfter(close) || !start.isBefore(end)) {
            throw new BadRequestException("Datos inválidos");
        }
    }

    /**
     * Guarda la reserva comprobando antes si existe conflicto de horario.
     *
     * @param currentUser usuario que realiza la operación.
     * @param reservation valor recibido por el método.
     * @param mode valor recibido por el método.
     */
    private void saveReservationWithConflictCheck(UserDocument currentUser,
                                                  ReservationDocument reservation,
                                                  ReservationMode mode) {
        FirestoreSupport.await(firestore.runTransaction(transaction -> {
            Query query = firestore.collection("reservations")
                    .whereEqualTo("communityId", currentUser.communityId)
                    .whereEqualTo("resourceId", reservation.resourceId)
                    .whereEqualTo("date", reservation.date)
                    .whereEqualTo("status", ReservationStatus.ACTIVE.name());

            var existingDocs = transaction.get(query).get().getDocuments();
            List<ReservationDocument> existing = existingDocs.stream()
                    .map(doc -> doc.toObject(ReservationDocument.class))
                    .toList();

            boolean conflict;
            if (mode == ReservationMode.FULL_DAY) {
                conflict = !existing.isEmpty();
            } else {
                LocalTime newStart = TimeUtils.parseTime(reservation.startTime);
                LocalTime newEnd = TimeUtils.parseTime(reservation.endTime);
                conflict = existing.stream().anyMatch(current -> {
                    if (current.startTime == null || current.endTime == null) {
                        return true;
                    }
                    LocalTime currentStart = TimeUtils.parseTime(current.startTime);
                    LocalTime currentEnd = TimeUtils.parseTime(current.endTime);
                    return newStart.isBefore(currentEnd) && newEnd.isAfter(currentStart);
                });
            }

            if (conflict) {
                throw new ConflictException("La reserva ya no está disponible");
            }

            transaction.set(
                    firestore.collection("reservations").document(reservation.reservationId),
                    reservation
            );
            return null;
        }));
    }

    /**
     * Elimina o cancela el registro solicitado si el usuario tiene permisos.
     *
     * @param currentUser usuario que realiza la operación.
     * @param reservationId identificador de la reserva.
     */
    public void delete(UserDocument currentUser, String reservationId) {
        ReservationDocument reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

        if (!currentUser.communityId.equals(reservation.communityId)) {
            throw new NotFoundException("Reserva no encontrada");
        }
        if (!currentUser.uid.equals(reservation.userId)) {
            throw new ForbiddenException("No tienes permisos");
        }

        reservation.status = ReservationStatus.CANCELLED.name();
        reservation.updatedAt = TimeUtils.nowIsoUtc();
        reservation.cancelledAt = reservation.updatedAt;
        reservationRepository.upsert(reservation);

        auditLogService.log(
                "RESERVATION_CANCELLED",
                "reservation",
                reservation.reservationId,
                currentUser,
                buildAuditDetails(reservation)
        );
    }
}