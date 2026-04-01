package com.agrosys.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        try {

            if (shouldSkipAuthentication(path)) {
                filterChain.doFilter(request, response);
                return;
            }
            // Obtener el token del header
            String jwt = getJwtFromRequest(request);
            log.debug("JWT token extracted: {}", jwt != null ? "present" : "not present");

            if (StringUtils.hasText(jwt)) {
                log.debug("Validating token...");

                if (tokenProvider.validateToken(jwt)) {
                    log.debug("Token is valid");

                    // Obtener username del token
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    log.debug("Username from token: {}", username);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Cargar los detalles del usuario
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.info("UserDetails loaded: {}", userDetails != null ? "success" : "failed");

                        if (userDetails != null) {
                            // Crear objeto de autenticación
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // Establecer autenticación en el contexto de seguridad
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("Usuario autenticado exitosamente: {}", username);
                        }
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

    private boolean shouldSkipAuthentication(String path) {
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/auth/login") ||
                path.equals("/") ||
                path.equals("/error");
    }
}