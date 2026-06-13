package com.padelstack.api.service;

import com.padelstack.api.dto.AdminAnnouncementResponse;
import com.padelstack.api.dto.AdminCommunityResponse;
import com.padelstack.api.dto.AdminDashboardResponse;
import com.padelstack.api.dto.AdminIncidentResponse;
import com.padelstack.api.dto.AdminProfileResponse;
import com.padelstack.api.dto.AdminReservationResponse;
import com.padelstack.api.dto.AdminReservationStatusUpdateRequest;
import com.padelstack.api.dto.AdminResourceResponse;
import com.padelstack.api.dto.AdminUserResponse;
import com.padelstack.api.dto.AdminUserRoleUpdateRequest;
import com.padelstack.api.dto.AdminUserStatusUpdateRequest;
import com.padelstack.api.dto.StatuteResponse;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.ForbiddenException;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.AnnouncementDocument;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.model.IncidentStatus;
import com.padelstack.api.model.ReservationDocument;
import com.padelstack.api.model.ReservationStatus;
import com.padelstack.api.model.ResourceDocument;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.StatuteDocument;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.AnnouncementRepository;
import com.padelstack.api.repository.CommunityRepository;
import com.padelstack.api.repository.IncidentRepository;
import com.padelstack.api.repository.ReservationRepository;
import com.padelstack.api.repository.ResourceRepository;
import com.padelstack.api.repository.StatuteRepository;
import com.padelstack.api.repository.UserRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Operaciones administrativas globales consumidas por PanelAdmin.
 */
@Service
public class AdminService {

    private static final int DASHBOARD_LIMIT = 5;

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final AnnouncementRepository announcementRepository;
    private final StatuteRepository statuteRepository;
    private final IncidentRepository incidentRepository;
    private final SecurityService securityService;
    private final AuditLogService auditLogService;

    /**
     * Crea el servicio administrativo.
     */
    public AdminService(UserRepository userRepository,
                        CommunityRepository communityRepository,
                        ResourceRepository resourceRepository,
                        ReservationRepository reservationRepository,
                        AnnouncementRepository announcementRepository,
                        StatuteRepository statuteRepository,
                        IncidentRepository incidentRepository,
                        SecurityService securityService,
                        AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
        this.announcementRepository = announcementRepository;
        this.statuteRepository = statuteRepository;
        this.incidentRepository = incidentRepository;
        this.securityService = securityService;
        this.auditLogService = auditLogService;
    }

    /**
     * Devuelve el perfil del superadministrador actual.
     */
    public AdminProfileResponse me(UserDocument currentUser) {
        requireSuperAdmin(currentUser);
        return new AdminProfileResponse(
                currentUser.uid,
                currentUser.email,
                displayName(currentUser),
                currentUser.role,
                Boolean.TRUE.equals(currentUser.active),
                currentUser.communityId,
                currentUser.communityName
        );
    }

    /**
     * Devuelve el dashboard global.
     */
    public AdminDashboardResponse dashboard(UserDocument currentUser) {
        requireSuperAdmin(currentUser);
        List<UserDocument> users = userRepository.findAll();
        List<CommunityDocument> communities = communityRepository.findAll();
        List<ResourceDocument> resources = resourceRepository.findAll();
        List<ReservationDocument> reservations = reservationRepository.findAll();
        List<AnnouncementDocument> announcements = announcementRepository.findAll();
        List<IncidentDocument> incidents = incidentRepository.findAll();

        Map<String, Long> totals = Map.of(
                "users", (long) users.size(),
                "communities", (long) communities.size(),
                "resources", (long) resources.size(),
                "reservations", (long) reservations.size(),
                "announcements", (long) announcements.size(),
                "incidents", (long) incidents.size()
        );

        Map<String, Long> usersByRole = users.stream()
                .collect(Collectors.groupingBy(user -> blankToDefault(user.role, "Sin rol"), Collectors.counting()));

        return new AdminDashboardResponse(
                totals,
                usersByRole,
                reservations.stream()
                        .sorted(desc(AdminService::reservationSortDate))
                        .limit(DASHBOARD_LIMIT)
                        .map(this::toReservationResponse)
                        .toList(),
                announcements.stream()
                        .sorted(desc(announcement -> safe(announcement.publishedAt) + safe(announcement.updatedAt)))
                        .limit(DASHBOARD_LIMIT)
                        .map(this::toAnnouncementResponse)
                        .toList(),
                incidents.stream()
                        .sorted(desc(incident -> safe(incident.createdAt) + safe(incident.updatedAt)))
                        .limit(DASHBOARD_LIMIT)
                        .map(this::toIncidentResponse)
                        .toList()
        );
    }

    /**
     * Lista usuarios con filtros opcionales.
     */
    public List<AdminUserResponse> users(UserDocument currentUser,
                                         String role,
                                         String communityId,
                                         Boolean active,
                                         String search) {
        requireSuperAdmin(currentUser);
        return userRepository.findAll().stream()
                .filter(user -> isBlank(role) || Objects.equals(user.role, role))
                .filter(user -> isBlank(communityId) || Objects.equals(user.communityId, communityId))
                .filter(user -> active == null || Boolean.TRUE.equals(user.active) == active)
                .filter(user -> matchesUserSearch(user, search))
                .sorted(desc(user -> safe(user.createdAt) + safe(user.updatedAt)))
                .map(this::toUserResponse)
                .toList();
    }

    /**
     * Devuelve un usuario concreto.
     */
    public AdminUserResponse user(UserDocument currentUser, String uid) {
        requireSuperAdmin(currentUser);
        return toUserResponse(getRequiredUser(uid));
    }

    /**
     * Cambia el rol de un usuario.
     */
    public AdminUserResponse updateUserRole(UserDocument currentUser,
                                            String uid,
                                            AdminUserRoleUpdateRequest request) {
        requireSuperAdmin(currentUser);
        Role nextRole = parseRole(request.role());
        UserDocument target = getRequiredUser(uid);
        if (Objects.equals(currentUser.uid, target.uid) && nextRole != Role.SUPERADMIN) {
            throw new ForbiddenException("No puedes quitarte a ti mismo el rol SUPERADMIN.");
        }

        String previousRole = target.role;
        target.role = nextRole.name();
        target.updatedAt = TimeUtils.nowIsoUtc();
        userRepository.upsert(target);
        auditLogService.log("ADMIN_USER_ROLE_UPDATED", "user", target.uid, currentUser,
                Map.of("previousRole", blankToDefault(previousRole, ""), "role", target.role));
        return toUserResponse(target);
    }

    /**
     * Cambia el estado activo/inactivo de un usuario.
     */
    public AdminUserResponse updateUserStatus(UserDocument currentUser,
                                              String uid,
                                              AdminUserStatusUpdateRequest request) {
        requireSuperAdmin(currentUser);
        UserDocument target = getRequiredUser(uid);
        if (Objects.equals(currentUser.uid, target.uid) && !Boolean.TRUE.equals(request.active())) {
            throw new ForbiddenException("No puedes desactivar tu propia cuenta.");
        }

        Boolean previousActive = target.active;
        target.active = request.active();
        target.updatedAt = TimeUtils.nowIsoUtc();
        userRepository.upsert(target);
        auditLogService.log("ADMIN_USER_STATUS_UPDATED", "user", target.uid, currentUser,
                Map.of("previousActive", previousActive == null ? "" : previousActive, "active", target.active));
        return toUserResponse(target);
    }

    /**
     * Lista comunidades con conteos calculados.
     */
    public List<AdminCommunityResponse> communities(UserDocument currentUser) {
        requireSuperAdmin(currentUser);
        List<UserDocument> users = userRepository.findAll();
        List<ResourceDocument> resources = resourceRepository.findAll();
        return communityRepository.findAll().stream()
                .sorted(Comparator.comparing(community -> safe(community.name) + safe(community.communityId)))
                .map(community -> toCommunityResponse(community, users, resources))
                .toList();
    }

    /**
     * Devuelve una comunidad concreta.
     */
    public AdminCommunityResponse community(UserDocument currentUser, String communityId) {
        requireSuperAdmin(currentUser);
        CommunityDocument community = communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("Comunidad no encontrada"));
        return toCommunityResponse(community, userRepository.findAll(), resourceRepository.findAll());
    }

    /**
     * Lista recursos.
     */
    public List<AdminResourceResponse> resources(UserDocument currentUser) {
        requireSuperAdmin(currentUser);
        return resourceRepository.findAll().stream()
                .sorted(Comparator.comparing(resource -> safe(resource.resourceId)))
                .map(this::toResourceResponse)
                .toList();
    }

    /**
     * Devuelve un recurso concreto.
     */
    public AdminResourceResponse resource(UserDocument currentUser, String resourceId) {
        requireSuperAdmin(currentUser);
        return toResourceResponse(resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado")));
    }

    /**
     * Lista reservas con filtros opcionales.
     */
    public List<AdminReservationResponse> reservations(UserDocument currentUser,
                                                       String dateFrom,
                                                       String dateTo,
                                                       String resourceId,
                                                       String userId,
                                                       String communityId,
                                                       String status) {
        requireSuperAdmin(currentUser);
        return reservationRepository.findAll().stream()
                .filter(reservation -> isBlank(dateFrom) || safe(reservation.date).compareTo(dateFrom) >= 0)
                .filter(reservation -> isBlank(dateTo) || safe(reservation.date).compareTo(dateTo) <= 0)
                .filter(reservation -> isBlank(resourceId) || Objects.equals(reservation.resourceId, resourceId))
                .filter(reservation -> isBlank(userId) || Objects.equals(reservation.userId, userId))
                .filter(reservation -> isBlank(communityId) || Objects.equals(reservation.communityId, communityId))
                .filter(reservation -> isBlank(status) || Objects.equals(reservation.status, status))
                .sorted(desc(AdminService::reservationSortDate))
                .map(this::toReservationResponse)
                .toList();
    }

    /**
     * Devuelve una reserva concreta.
     */
    public AdminReservationResponse reservation(UserDocument currentUser, String reservationId) {
        requireSuperAdmin(currentUser);
        return toReservationResponse(getRequiredReservation(reservationId));
    }

    /**
     * Cancela o deja sin cambios una reserva segun el modelo actual.
     */
    public AdminReservationResponse updateReservationStatus(UserDocument currentUser,
                                                           String reservationId,
                                                           AdminReservationStatusUpdateRequest request) {
        requireSuperAdmin(currentUser);
        ReservationDocument reservation = getRequiredReservation(reservationId);
        ReservationStatus nextStatus = parseReservationStatus(request.status());
        ReservationStatus currentStatus = parseReservationStatus(reservation.status);
        if (nextStatus == ReservationStatus.ACTIVE && currentStatus != ReservationStatus.ACTIVE) {
            throw new BadRequestException("La reactivacion de reservas requiere validar disponibilidad.");
        }

        reservation.status = nextStatus.name();
        reservation.updatedAt = TimeUtils.nowIsoUtc();
        if (nextStatus == ReservationStatus.CANCELLED && reservation.cancelledAt == null) {
            reservation.cancelledAt = reservation.updatedAt;
        }
        reservationRepository.upsert(reservation);
        auditLogService.log("ADMIN_RESERVATION_STATUS_UPDATED", "reservation", reservation.reservationId, currentUser,
                Map.of("status", reservation.status));
        return toReservationResponse(reservation);
    }

    /**
     * Lista anuncios.
     */
    public List<AdminAnnouncementResponse> announcements(UserDocument currentUser, String communityId) {
        requireSuperAdmin(currentUser);
        return announcementRepository.findAll().stream()
                .filter(announcement -> isBlank(communityId) || Objects.equals(announcement.communityId, communityId))
                .sorted(desc(announcement -> safe(announcement.publishedAt) + safe(announcement.updatedAt)))
                .map(this::toAnnouncementResponse)
                .toList();
    }

    /**
     * Lista estatutos.
     */
    public List<StatuteResponse> statutes(UserDocument currentUser, String communityId) {
        requireSuperAdmin(currentUser);
        return statuteRepository.findAll().stream()
                .filter(statute -> isBlank(communityId) || Objects.equals(statute.communityId, communityId))
                .sorted(Comparator.comparing(statute -> safe(statute.communityId)))
                .map(this::toStatuteResponse)
                .toList();
    }

    /**
     * Lista incidencias.
     */
    public List<AdminIncidentResponse> incidents(UserDocument currentUser,
                                                 String status,
                                                 String communityId,
                                                 String userId,
                                                 String search) {
        requireSuperAdmin(currentUser);
        return incidentRepository.findAll().stream()
                .filter(incident -> isBlank(status) || Objects.equals(incident.status, status))
                .filter(incident -> isBlank(communityId) || Objects.equals(incident.communityId, communityId))
                .filter(incident -> isBlank(userId) || Objects.equals(incident.createdByUid, userId))
                .filter(incident -> matchesIncidentSearch(incident, search))
                .sorted(desc(incident -> safe(incident.createdAt) + safe(incident.updatedAt)))
                .map(this::toIncidentResponse)
                .toList();
    }

    private void requireSuperAdmin(UserDocument currentUser) {
        securityService.requireSuperAdmin(currentUser);
    }

    private UserDocument getRequiredUser(String uid) {
        return userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }

    private ReservationDocument getRequiredReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada."));
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (RuntimeException ex) {
            throw new BadRequestException("Rol no valido.");
        }
    }

    private ReservationStatus parseReservationStatus(String status) {
        try {
            return ReservationStatus.valueOf(status);
        } catch (RuntimeException ex) {
            throw new BadRequestException("Estado de reserva no valido.");
        }
    }

    private boolean matchesUserSearch(UserDocument user, String search) {
        if (isBlank(search)) {
            return true;
        }
        String normalized = search.toLowerCase(Locale.ROOT).trim();
        return contains(user.uid, normalized)
                || contains(user.email, normalized)
                || contains(user.username, normalized)
                || contains(displayName(user), normalized);
    }

    private boolean matchesIncidentSearch(IncidentDocument incident, String search) {
        if (isBlank(search)) {
            return true;
        }
        String normalized = search.toLowerCase(Locale.ROOT).trim();
        return contains(incident.incidentId, normalized)
                || contains(incident.title, normalized)
                || contains(incident.description, normalized)
                || contains(incident.createdByName, normalized)
                || contains(incident.createdByEmail, normalized);
    }

    private boolean contains(String value, String normalizedSearch) {
        return safe(value).toLowerCase(Locale.ROOT).contains(normalizedSearch);
    }

    private AdminUserResponse toUserResponse(UserDocument user) {
        return new AdminUserResponse(
                user.uid,
                user.email,
                user.username,
                user.firstName,
                user.lastName,
                user.fullName,
                displayName(user),
                user.phone,
                user.communityId,
                user.communityName,
                user.unitDisplay,
                user.role,
                Boolean.TRUE.equals(user.active),
                user.createdAt,
                user.updatedAt
        );
    }

    private AdminCommunityResponse toCommunityResponse(CommunityDocument community,
                                                       List<UserDocument> users,
                                                       List<ResourceDocument> resources) {
        String communityId = community.communityId;
        long usersCount = users.stream().filter(user -> Objects.equals(user.communityId, communityId)).count();
        long resourcesCount = resources.stream().filter(resource -> Objects.equals(resource.communityId, communityId)).count();
        List<String> units = community.units == null ? List.of() : community.units;
        return new AdminCommunityResponse(
                communityId,
                community.name,
                Boolean.TRUE.equals(community.active),
                usersCount,
                resourcesCount,
                units.size(),
                units
        );
    }

    private AdminResourceResponse toResourceResponse(ResourceDocument resource) {
        return new AdminResourceResponse(
                resource.resourceId,
                resource.communityId,
                resource.name,
                resource.type,
                resource.reservationMode,
                resource.slotMinutes,
                resource.openTime,
                resource.closeTime,
                resource.rulesText,
                Boolean.TRUE.equals(resource.active)
        );
    }

    private AdminReservationResponse toReservationResponse(ReservationDocument reservation) {
        return new AdminReservationResponse(
                reservation.reservationId,
                reservation.communityId,
                reservation.userId,
                reservation.userEmail,
                reservation.userFullName,
                reservation.resourceId,
                reservation.resourceName,
                reservation.date,
                reservation.startTime,
                reservation.endTime,
                reservation.allDay,
                reservation.slotLabel,
                reservation.status,
                reservation.createdAt,
                reservation.updatedAt,
                reservation.cancelledAt
        );
    }

    private AdminAnnouncementResponse toAnnouncementResponse(AnnouncementDocument announcement) {
        return new AdminAnnouncementResponse(
                announcement.announcementId,
                announcement.communityId,
                announcement.title,
                announcement.content,
                Boolean.TRUE.equals(announcement.visible),
                announcement.publishedAt,
                announcement.createdByUid,
                announcement.createdByName,
                announcement.updatedAt
        );
    }

    private StatuteResponse toStatuteResponse(StatuteDocument statute) {
        return new StatuteResponse(
                statute.communityId,
                statute.title,
                statute.content,
                statute.version,
                statute.updatedAt,
                statute.updatedByUid
        );
    }

    private AdminIncidentResponse toIncidentResponse(IncidentDocument incident) {
        return new AdminIncidentResponse(
                incident.incidentId,
                incident.communityId,
                incident.title,
                incident.description,
                parseIncidentStatus(incident.status).name(),
                incident.photoUrl,
                incident.storagePath,
                incident.createdByUid,
                incident.createdByName,
                incident.createdByEmail,
                incident.createdAt,
                incident.updatedAt,
                incident.updatedByUid
        );
    }

    private IncidentStatus parseIncidentStatus(String status) {
        try {
            return IncidentStatus.valueOf(status);
        } catch (RuntimeException ex) {
            return IncidentStatus.OPEN;
        }
    }

    private static String reservationSortDate(ReservationDocument reservation) {
        return safe(reservation.date) + safe(reservation.createdAt);
    }

    private static <T> Comparator<T> desc(Function<T, String> getter) {
        return Comparator.comparing(getter, Comparator.reverseOrder());
    }

    private static String displayName(UserDocument user) {
        String fullName = safe(user.fullName).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }
        String joined = (safe(user.firstName) + " " + safe(user.lastName)).trim();
        if (!joined.isBlank()) {
            return joined;
        }
        if (!isBlank(user.username)) {
            return user.username;
        }
        if (!isBlank(user.email)) {
            return user.email;
        }
        return user.uid;
    }

    private static String blankToDefault(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
