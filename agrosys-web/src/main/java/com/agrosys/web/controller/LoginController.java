package com.agrosys.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrosys.web.dto.login.LoginResponse;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final GatewayClient gatewayClient;
    private final JwtHelper jwtHelper;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "expired", required = false) String expired,
            HttpSession session,
            Model model) {

        if (session.getAttribute("JWT_TOKEN") != null) {
            return "redirect:/home";
        }

        if (expired != null) {
            model.addAttribute("info", "Tu sesión ha expirado.");
        }
        
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {
        try {
            LoginResponse response = gatewayClient.login(username, password);

            // Guardar token en sesión
            session.setAttribute("JWT_TOKEN", response.getToken());
            session.setAttribute("USERNAME", username);
            session.setAttribute("NAME", response.getUserName());
            session.setAttribute("USER_ID", response.getUserId());

            log.info("Usuario {} autenticado correctamente", username, response);

            return "redirect:/home";

        } catch (Exception e) {
            log.error("Error de autenticación para usuario: {}", username, e);
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}