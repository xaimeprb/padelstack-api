package com.padelstack.api.service;

import com.padelstack.api.dto.BootstrapUserRequest;
import com.padelstack.api.dto.MeResponse;
import com.padelstack.api.exception.NotFoundException;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.model.Role;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.UserRepository;
import com.padelstack.api.security.AuthenticatedUser;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio encargado de la lógica relacionada con user.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommunityService communityService;

    /**
     * Crea una instancia de UserService con las dependencias necesarias.
     *
     * @param userRepository repositorio usado por la clase.
     * @param communityService servicio usado por la clase.
     */
    public UserService(UserRepository userRepository, CommunityService communityService) {
        this.userRepository = userRepository;
        this.communityService = communityService;
    }

    /**
     * Crea o actualiza el usuario inicial en la comunidad correspondiente.
     *
     * @param authUser usuario autenticado obtenido desde Firebase.
     * @param request datos recibidos en la petición.
     */
    public void bootstrap(AuthenticatedUser authUser, BootstrapUserRequest request) {
        CommunityDocument community = communityService.getRequiredActiveCommunityForRegistration(request.communityId());
        communityService.validateUnitBelongsToCommunity(community, request.unitDisplay());

        Optional<UserDocument> existingOpt = userRepository.findById(authUser.uid());
        String now = TimeUtils.nowIsoUtc();

        UserDocument document = existingOpt.orElseGet(UserDocument::new);
        boolean alreadyExists = existingOpt.isPresent();

        document.uid = authUser.uid();
        document.email = authUser.email();
        document.username = request.username();
        document.firstName = request.firstName();
        document.lastName = request.lastName();
        document.fullName = request.firstName() + " " + request.lastName();
        document.phone = request.phone();
        document.communityId = community.communityId;
        document.communityName = community.name;
        document.unitDisplay = request.unitDisplay();
        document.role = alreadyExists && document.role != null ? document.role : Role.NEIGHBOR.name();
        document.active = alreadyExists && document.active != null ? document.active : Boolean.TRUE;
        document.createdAt = alreadyExists && document.createdAt != null ? document.createdAt : now;
        document.updatedAt = now;

        userRepository.upsert(document);
    }

    /**
     * Obtiene un usuario obligatorio o lanza una excepción si no existe.
     *
     * @param uid identificador del usuario.
     * @return resultado de la operación.
     */
    public UserDocument getRequiredUser(String uid) {
        return userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    /**
     * Construye la respuesta con los datos del usuario actual.
     *
     * @param uid identificador del usuario.
     * @return resultado de la operación.
     */
    public MeResponse me(String uid) {
        UserDocument user = getRequiredUser(uid);
        return new MeResponse(
                user.uid,
                user.email,
                user.username,
                user.firstName,
                user.lastName,
                user.fullName,
                user.phone,
                user.communityId,
                user.communityName,
                user.unitDisplay,
                user.role,
                Boolean.TRUE.equals(user.active)
        );
    }
}
