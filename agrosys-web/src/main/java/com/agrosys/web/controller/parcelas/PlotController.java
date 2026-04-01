package com.agrosys.web.controller.parcelas;

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
public class PlotController {

    private final JwtHelper jwtHelper;

    @GetMapping("/plot")
    public String loginPage(HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return "redirect:/access-denied";


        model.addAttribute("modules", modules);
        return "home/parcelas/parcelas";
    }

}
