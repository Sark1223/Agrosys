package com.agrosys.worker.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService tokenProvider;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        try {

            // Obtener el token del header
            String jwt = getJwtFromRequest(request);
            log.info("JWT token extracted: {}", jwt != null ? "present" : "not present");

            if (StringUtils.hasText(jwt)) {
                log.info("Validating token...");

                if (tokenProvider.validateToken(jwt)) {
                    log.info("Token is valid");

                    // Obtener username del token
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    // Extraemos los roles que vienen en el token
                    List<GrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(jwt);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Creamos el principal sin necesidad de consultar la DB
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    }
                } else {
                    log.warn("Token JWT inválido para el request: {}", path);
                }
            }
        } catch (Exception ex) {
            log.error("Error en autenticación JWT para path {}: {}", path, ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     * Formato esperado: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}