package com.agrosys.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.warn("Acceso denegado: No hay sesión activa.");
            response.sendRedirect("/login?expired=true");
            return false;
        }

        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || !jwtHelper.isTokenValid(token)) {
            log.warn("Acceso denegado: Token inexistente o expirado.");
            session.invalidate(); // Limpiamos la sesión por seguridad
            response.sendRedirect("/login?expired=true");
            return false;
        }

        request.setAttribute("JWT_TOKEN", token);
        log.debug("Token JWT válido para usuario: {}", session.getAttribute("USERNAME"));
        return true;
    }
}