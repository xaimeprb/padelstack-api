package com.padelstack.api.controller;

import com.padelstack.api.dto.BootstrapUserRequest;
import com.padelstack.api.dto.MeResponse;
import com.padelstack.api.dto.SimpleCreatedResponse;
import com.padelstack.api.security.AuthenticatedUser;
import com.padelstack.api.service.SecurityService;
import com.padelstack.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @PostMapping("/users/bootstrap")
    public SimpleCreatedResponse bootstrap(@Valid @RequestBody BootstrapUserRequest request,
                                           Authentication authentication) {
        AuthenticatedUser auth = securityService.authenticatedUser(authentication);
        userService.bootstrap(auth, request);
        return new SimpleCreatedResponse(true);
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        AuthenticatedUser auth = securityService.authenticatedUser(authentication);
        return userService.me(auth.uid());
    }
}
