package com.agrosys.plot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.plot.PlotRegister;
import com.agrosys.plot.repository.PlotRepository;
import com.agrosys.plot.service.PlotService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/plot")
@RequiredArgsConstructor
@Slf4j
public class AgrosysPlotController {

    private final PlotService plotService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> getAllPlots() {
        List<PlotRepository.PlotProjection> plots = plotService.getAllRoles();
        Response successResponse = Response.builder()
                .success(true)
                .message("Parcelas obtenidas exitosamente")
                .data(plots) // Aquí iría la lista real de usuarios
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> postPlot(@Valid @RequestBody PlotRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = plotService.postPlot(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putPlot(@PathVariable Integer id, @Valid @RequestBody PlotRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = plotService.updatePlot(id, request);
        return ResponseEntity.ok(response);
    }
}
