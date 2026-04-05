package com.agrosys.web.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtSessionFilter extends OncePerRequestFilter {

    private static final String[] PUBLIC_PATHS = {
            "/login",
            "/api/auth/login",
            "/css/",
            "/js/",
            "/images/",
            "/error"
    };

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Permitir acceso a rutas públicas
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Verificar token en sesión
        HttpSession session = request.getSession(false);
        String token = null;

        if (session != null) {
            token = (String) session.getAttribute("JWT_TOKEN");
        }

        if (token == null) {
            log.warn("No JWT token found in session for path: {}", path);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Agregar token al header para las llamadas al gateway
        // Se puede usar un RequestContextHolder o pasar manualmente en cada llamada

        filterChain.doFilter(request, response);
    }
}