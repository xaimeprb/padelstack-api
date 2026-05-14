package com.padelstack.api.security;

import java.util.List;

public record AuthenticatedUser(
        String uid,
        String email,
        List<String> authorities
) {
}
