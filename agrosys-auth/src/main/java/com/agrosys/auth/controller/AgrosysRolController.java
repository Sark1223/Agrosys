package com.agrosys.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.auth.dto.Response;
import com.agrosys.auth.dto.Rol.RolRegister;
import com.agrosys.auth.repository.RolRepository;
import com.agrosys.auth.service.RolService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class AgrosysRolController {

    private final RolService rolService;
    
    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_CONFIG')")
    public ResponseEntity<Response> getAllRoles() {
        List<RolRepository.RolProjection> roles = rolService.getAllRoles();
        Response successResponse = Response.builder()
                .success(true)
                .message("Roles obtenidos exitosamente")
                .data(roles)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/get-by-id/{rolId}")
    @PreAuthorize("hasAuthority('MODULE_CONFIG')")
    public ResponseEntity<Response> getRolById(@PathVariable Integer rolId) {
        RolRepository.RolProjection role = rolService.getRolById(rolId);
        Response successResponse = Response.builder()
                .success(true)
                .message("Rol obtenido exitosamente")
                .data(role)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('MODULE_CONFIG')")
    public ResponseEntity<Response> postMethodName(@RequestBody RolRegister entity) {
        Response response = rolService.createRol(entity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('MODULE_CONFIG')")
    public ResponseEntity<Response> putMethodName(@PathVariable Integer id, @RequestBody RolRegister entity) {
        
        Response response = rolService.updateRol(id, entity);  
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_CONFIG')")
    public ResponseEntity<Response> deleteMethodName(@PathVariable Integer id) {
        Response response = rolService.deleteRol(id);
        return ResponseEntity.ok(response); 
    }
    
}
