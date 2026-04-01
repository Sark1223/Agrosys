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
        if (!modules.contains("MODULE_PARCELAS"))
            return "redirect:/access-denied";
        
        model.addAttribute("modules", modules);
        return "home/home";
    }
}
