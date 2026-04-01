package com.agrosys.web.utils;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Validation {
    public static boolean hasAccess(HttpSession session, String module) {
        try {
            @SuppressWarnings("unchecked")
            List<String> modules = (List<String>) session.getAttribute("MODULES");
            return Optional.ofNullable(modules)
                    .map(list -> list.contains(module))
                    .orElse(false);
        } catch (ClassCastException e) {
            log.error("Error al validar acceso: MODULES no es una lista válida", e);
            return false;
        }
    }
    
}
