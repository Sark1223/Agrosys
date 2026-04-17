package com.agrosys.web.controller.worker;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        log.info("[MODULES] - Módulos del usuario: {}", modules); // 👈 agrega esto

        if (!modules.contains("MODULE_TRABAJADORES")) {
            return "redirect:/access-denied";
        }

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "home/workers/worker";
    }

    @GetMapping("/get-all")
    @ResponseBody
    public ResponseEntity<Object> getAllWorkers(HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN"); // ✅

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    gatewayBaseUrl + "/api/workers/get-all",
                    HttpMethod.GET,
                    entity,
                    Object.class);
        } catch (RestClientException e) {
            log.error("[FAILED] - Error al obtener workers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al obtener workers"));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Object> registerWorker(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN"); // ✅

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(
                    gatewayBaseUrl + "/api/workers/register",
                    HttpMethod.POST,
                    entity,
                    Object.class);
        } catch (RestClientException e) {
            log.error("[FAILED] - Error al registrar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al registrar worker"));
        }
    }
}
