package com.agrosys.web.controller.plot;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.plot.PlotRegister;
import com.agrosys.web.dto.plot.plantation.PlantatioRegister;
import com.agrosys.web.dto.plot.plantation.PlantatioStageRegister;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/plots")
@RequiredArgsConstructor
@Validated
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

        log.info("Obteniendo lista de plots");

        Response response = gatewayClient.get("/api/plot/get-all", Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/new")
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

    @PostMapping("/edit")
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

    // ==================== PLANTACIONES ====================
    @PostMapping("/plantations/new")
    public ResponseEntity<Response> postPlantation(
            @RequestParam Integer plotId,
            @RequestParam String plantationName,
            // @RequestParam String startAt,
            // @RequestParam(required = false) String endAt,
            @RequestParam(required = false) String notes,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        PlantatioRegister requestData = new PlantatioRegister();
        requestData.setName(plantationName);
        // requestData.setStartAt(startAt);
        // requestData.setEndAt(endAt);
        requestData.setNotas(notes);
        requestData.setPlotId(plotId);

        log.info("Creacion de plantacion: {}", requestData);

        Response response = gatewayClient.post("/api/plantation/post", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plantation/edit")
    public ResponseEntity<Response> updatePlantation(
            @RequestParam Integer plantationId,
            @RequestParam Integer plotId,
            @RequestParam String plantationName,
            // @RequestParam String startAt,
            // @RequestParam(required = false) String endAt,
            @RequestParam(required = false) String notes,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Modificación de plantacion: {}", plantationName);
        PlantatioRegister requestData = new PlantatioRegister();
        requestData.setName(plantationName);
        requestData.setNotas(notes);
        requestData.setPlotId(plotId);

        Response response = gatewayClient.put("/api/plantation/update/" + plantationId, requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del actualización: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plantation/delete")
    public ResponseEntity<Response> deletePlantation(
            @RequestParam Integer plantationId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Eliminación de plantacion: {}", plantationId);

        Response response = gatewayClient.delete("/api/plantation/delete/" + plantationId, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del eliminación: {}", response);
        return ResponseEntity.ok(response);
    }

    // ================= PLANTATION STAGES =================

    @GetMapping("/plantation/{plantationId}/stages")
    public ResponseEntity<Response> getStagesByPlantationId(
            @PathVariable Integer plantationId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Obteniendo lista de estados de plantacion: {}", plantationId);

        Response response = gatewayClient.get("/api/plantation/stages/by-plantation-id/" + plantationId, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("estados obtenidos: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plantation/stage/new")
    public ResponseEntity<Response> postPlantationStage(
            @RequestParam Integer plantationId,
            @RequestParam Integer stageId,
            @RequestParam String startAt,
            @RequestParam(required = false) String endAt,
            @RequestParam(required = false) String notes,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        PlantatioStageRegister requestData = new PlantatioStageRegister();
        requestData.setPlantatioId(plantationId);
        requestData.setStageId(stageId);
        requestData.setStartAt(startAt);
        if (stageId == 4) {
            requestData.setEndAt(startAt);
        } else {
            requestData.setEndAt(endAt.equals("") ? "false" : endAt);
        }

        requestData.setNotas(notes);

        log.info("Creacion de etapa de plantacion: {}", requestData);

        Response response = gatewayClient.post("/api/plantation/insert-plantation-stage", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plantation/stage/edit")
    public ResponseEntity<Response> updatePlantationStage(
            @RequestParam Integer plantationId,
            @RequestParam Integer stageId,
            @RequestParam String startAt,
            @RequestParam String endAt,
            @RequestParam(required = false) String notes,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Modificación de etapa de plantacion: {}", stageId);
        PlantatioStageRegister requestData = new PlantatioStageRegister();
        requestData.setPlantatioId(plantationId);
        requestData.setStageId(stageId);
        requestData.setStartAt(startAt);
        if (stageId == 4) {
            requestData.setEndAt(startAt);
        } else {
            requestData.setEndAt(endAt);
        }
        requestData.setNotas(notes);

        Response response = gatewayClient.put("/api/plantation/update-plantation-stage", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del actualización: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plantation/stage/delete")
    public ResponseEntity<Response> deletePlantationStage(
            @RequestParam Integer plantationId,
            @RequestParam Integer stageId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_PARCELAS"))
            return ResponseEntity.status(403).build();

        log.info("Eliminación de plantacion: {}", plantationId);

        Response response = gatewayClient.delete("/api/plantation/delete/" + plantationId, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del eliminación: {}", response);
        return ResponseEntity.ok(response);
    }
}
