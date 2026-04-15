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
import com.agrosys.web.dto.config.RolRegister;
import com.agrosys.web.dto.config.UserPatch;
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

    // @GetMapping("/{tab}")
    @GetMapping
    public String configPage(@RequestParam(required = false, defaultValue = "usuarios") String tab, HttpSession session,
            Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return "redirect:/access-denied";

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "home/config/config";
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

    @PostMapping("/new-user")
    public ResponseEntity<Response> postUser(
            @RequestParam String nombre,
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
        requestData.setPassword("xxxxxxxx"); // Para reutilizar el DTO, aunque no se usará para actualizar la contraseña
        requestData.setRolId(Integer.valueOf(role));

        Response response = gatewayClient.put("/api/users/update", requestData, Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta de la actualización: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit-password-user")
    public ResponseEntity<Response> updateUser(
            @RequestParam Integer userId,
            @RequestParam String password,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Actualizando usuario");
        UserPatch requestData = new UserPatch();
        requestData.setUserId(userId);
        requestData.setPassword(password);

        Response response = gatewayClient.patch(
                "/api/users/update-password",
                requestData,
                Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta de la actualización: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete-user")
    public ResponseEntity<Response> deleteUser(
            @RequestParam Integer userId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Actualizando usuario");
        UserPatch requestData = new UserPatch();
        requestData.setUserId(userId);
        requestData.setPassword("xxxxxxxx"); // Para reutilizar el DTO, aunque no se usará para actualizar la contraseña

        Response response = gatewayClient.delete(
                "/api/users/delete",
                requestData,
                Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta de la actualización: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-roles")
    public ResponseEntity<Response> getAllRoles(
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Obteniendo lista de roles");

        Response response = gatewayClient.get("/api/roles/get-all", Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/new-role")
    public ResponseEntity<Response> newRole(
            @RequestParam String roleName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Integer> modulos,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        RolRegister requestData = new RolRegister();
        requestData.setName(roleName);
        requestData.setDescription(description);
        requestData.setModulos(modulos);

        log.info("Creando nuevo rol: {} - Description: {} - Modulos: {}", roleName, description, modulos);

        Response response = gatewayClient.post("/api/roles/post", requestData,
        Response.class,
        session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit-role")
    public ResponseEntity<Response> editRole(
            @RequestParam Integer roleId,
            @RequestParam String roleName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Integer> modulos,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        RolRegister requestData = new RolRegister();
        requestData.setName(roleName);
        requestData.setDescription(description);
        requestData.setModulos(modulos);

        log.info("Creando nuevo rol: {} - Description: {} - Modulos: {}", roleName, description, modulos);

        Response response = gatewayClient.put("/api/roles/update/" + roleId, requestData,
        Response.class,
        session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete-role")
    public ResponseEntity<Response> deleteRole(
            @RequestParam Integer roleId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_CONFIG"))
            return ResponseEntity.status(403).build();

        log.info("Actualizando rol");
        Response response = gatewayClient.delete(
                "/api/roles/delete/" + roleId,
                Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        log.info("Respuesta de la actualización: {}", response);

        return ResponseEntity.ok(response);
    }

}
