package com.padelstack.api.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.padelstack.api.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de seguridad que lee el token de Firebase y prepara la autenticación de Spring.
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer ";
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * Indica si una petición debe saltarse el filtro de autenticación.
     *
     * @param request datos recibidos en la petición.
     * @return true si se cumple la condición, false en caso contrario.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return antPathMatcher.match("/api/v1/public/**", path)
                || antPathMatcher.match("/actuator/health", path);
    }

    /**
     * Procesa la petición HTTP y añade la autenticación si el token es válido.
     *
     * @param request datos recibidos en la petición.
     * @param response valor recibido por el método.
     * @param filterChain valor recibido por el método.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(PREFIX)) {
            throw new UnauthorizedException("Unauthorized");
        }

        String token = authorizationHeader.substring(PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            AuthenticatedUser principal = new AuthenticatedUser(uid, email, List.of("ROLE_AUTHENTICATED"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.authorities().stream().map(SimpleGrantedAuthority::new).toList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
