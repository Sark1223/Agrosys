package com.agrosys.web.controller.config;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.config.RegisterRequest;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {
    private final JwtHelper jwtHelper;
    private final GatewayClient gatewayClient;

    @GetMapping
    public String loginPage(HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return "redirect:/access-denied";

        model.addAttribute("modules", modules);
        return "home/config/config";
    }

    @PostMapping("/new-user")
    public ResponseEntity<Response> postUser(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String role,
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Creando nuevo usuario: {} {} - Role: {} - Username: {}", nombre, apellido, role, username);

        RegisterRequest requestData = new RegisterRequest();
        requestData.setFirstName(nombre);
        requestData.setLastName(apellido);
        requestData.setUserName(username);
        requestData.setPassword(password);
        requestData.setRolId(Integer.valueOf(role));

        Response response = gatewayClient.post("/api/users/register", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-users")
    public ResponseEntity<Response> getAllUsers(
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Obteniendo lista de usuarios");

        Response response = gatewayClient.get("/api/users/get-all", Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta del registro: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit-user")
    public ResponseEntity<Response> updateUser(
        @RequestParam Integer userId,
        @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String role,
            @RequestParam String username,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Actualizando usuario: {} {} - Role: {} - Username: {}", nombre, apellido, role, username);

        RegisterRequest requestData = new RegisterRequest();
        requestData.setUserId(userId);
        requestData.setFirstName(nombre);
        requestData.setLastName(apellido);
        requestData.setUserName(username);
        requestData.setPassword("xxxxxxxx"); //Para reutilizar el DTO, aunque no se usará para actualizar la contraseña
        requestData.setRolId(Integer.valueOf(role));

        Response response = gatewayClient.post("/api/users/update", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta de la actualización: {}", response);

        return ResponseEntity.ok(response);
    }
}
