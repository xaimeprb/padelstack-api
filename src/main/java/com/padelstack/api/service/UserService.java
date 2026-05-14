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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommunityService communityService;

    public UserService(UserRepository userRepository, CommunityService communityService) {
        this.userRepository = userRepository;
        this.communityService = communityService;
    }

    public void bootstrap(AuthenticatedUser authUser, BootstrapUserRequest request) {
        CommunityDocument community = communityService.getRequiredCommunity(request.communityId());
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

    public UserDocument getRequiredUser(String uid) {
        return userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

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
