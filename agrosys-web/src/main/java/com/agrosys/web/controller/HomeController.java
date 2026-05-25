package com.agrosys.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    // @Autowired
    private final JwtHelper jwtHelper;

    @GetMapping("/home")
    public String loginPage(HttpSession session, Model model) {
        // Si ya hay sesión, redirigir al home
        if (session.getAttribute("JWT_TOKEN") == null) {
            return "login";
        }
        List<String> modules = jwtHelper.getUserModules(session);
        log.info("modules", modules);
        model.addAttribute("modules", modules);
        if (modules.contains("MODULE_DASHBOARD_ADMIN")) {
            log.info("ENTRO AQUI");
            model.addAttribute("admin", "estoy entrando");
            return "home/home";
        } else if (modules.contains("MODULE_DASHBOARD_USER")) {
            log.info("ENTRO AQUI 2");
            return "home/home";
        } else
            return "redirect:/access-denied";
    }
}
