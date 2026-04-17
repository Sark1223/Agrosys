package com.agrosys.web.controller.plot;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.plot.PlotRegister;
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
    public String plots(@RequestParam(required = false, defaultValue = "plot") String tab, HttpSession session,
            Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return "redirect:/access-denied";

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "home/plot/plot";
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllPlots(
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Obteniendo lista de usuarios");

        Response response = gatewayClient.get("/api/plot/get-all", Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/new-plot")
    public ResponseEntity<Response> postPlot(
            @RequestParam String plotName,
            @RequestParam(required = false) String description,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        PlotRegister requestData = new PlotRegister();
        requestData.setName(plotName);
        requestData.setDescription(description);

        log.info("Creacion de parcela: {}", requestData);

        Response response = gatewayClient.post("/api/plot/post", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit-plot")
    public ResponseEntity<Response> updatePlot(
            @RequestParam Integer plotId,
            @RequestParam String plotName,
            @RequestParam(required = false) String description,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Modificación de parcela: {}", plotName);
        PlotRegister requestData = new PlotRegister();
        requestData.setName(plotName);
        requestData.setDescription(description);

        Response response = gatewayClient.put("/api/plot/update/" + plotId, requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del actualización: {}", response);

        return ResponseEntity.ok(response);
    }

}
