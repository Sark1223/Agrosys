package com.agrosys.web.controller.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workers")
@RequiredArgsConstructor
@Slf4j
public class workerController {

    private final RestTemplate restTemplate;
    private final JwtHelper jwtHelper;

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    @GetMapping
    public String workersPage(
            @RequestParam(required = false, defaultValue = "usuarios") String tab,
            HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_WORKERS"))
            return "redirect:/access-denied";

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "workers/worker";
    }

    @GetMapping("/get-all")
    @ResponseBody
    public ResponseEntity<Object> getAllWorkers(HttpSession session) {
        try {

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    gatewayBaseUrl + "/api/workers/get-all",
                    HttpMethod.GET,
                    entity,
                    Object.class);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener workers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al obtener workers"));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Object> registerWorker(@RequestBody Map<String, String> body, HttpSession session) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(
                    gatewayBaseUrl + "/api/workers/register",
                    HttpMethod.POST,
                    entity,
                    Object.class);
        } catch (Exception e) {
            log.error("[FAILED] - Error al registrar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al registrar worker"));
        }
    }
}