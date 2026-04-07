package com.agrosys.web.controller.plot;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agrosys.web.dto.Response;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/plots")
@RequiredArgsConstructor
@Slf4j
public class PlotController {

    private final JwtHelper jwtHelper;
    private final GatewayClient gatewayClient;

    @GetMapping
    public String plots(HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return "redirect:/access-denied";


        model.addAttribute("modules", modules);
        return "home/plot/plot";
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllUsers(
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Obteniendo lista de usuarios");

        Response response = gatewayClient.get("/api/plot/get-all", Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }


}
