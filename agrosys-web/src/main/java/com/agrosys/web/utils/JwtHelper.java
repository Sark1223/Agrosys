package com.agrosys.web.utils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtHelper {

    @Value("${jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("JWT válido para usuario: {}", claims.getSubject());
            return claims;

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado");
            throw new RuntimeException("Token expirado");

        } catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            log.error("Token inválido", e);
            throw new RuntimeException("Token inválido");
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserModules(HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        // El token NO debería ser null aquí porque el Interceptor ya filtró
        if (token == null) {
            log.error("Error crítico: El Interceptor dejó pasar una sesión sin token");
            return Collections.emptyList();
        }

        try {
            Map<String, Object> claims = getClaims(token);
            log.info("Acceso a módulos: {}", claims.get("modules"));
            return (List<String>) claims.get("modules");
        } catch (Exception e) {
            log.error("Error al extraer módulos del token: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            log.info("Token Expiracion: {}", claims.getExpiration());
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
