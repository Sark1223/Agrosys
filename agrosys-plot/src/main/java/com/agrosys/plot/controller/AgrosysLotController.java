package com.agrosys.plot.controller;

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

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.lot.ConfigRegister;
import com.agrosys.plot.dto.lot.LotRegister;
import com.agrosys.plot.service.LotService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/lot")
@RequiredArgsConstructor
@Slf4j
public class AgrosysLotController {

    private final LotService lotService;

    @GetMapping("/config/get-all")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> getAllConfigs() {
        log.info("[REQUEST] - [Obteniedo las configuraciones de parcelas]");
        Response response = lotService.getAllConfigs();
        return ResponseEntity.ok(response);
    }
    
    // ======================== TRADE MODE ========================

    @PostMapping("/config/trade-mode/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> postTradeMode(@Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.postTradeMode(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/config/trade-mode/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putTradeMode(@PathVariable Integer id, @Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.putTradeMode(id, request);
        return ResponseEntity.ok(response);
    }

    // ======================== PRODUCT UNIT ========================
    @PostMapping("/config/product-unit/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> postProductUnit(@Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.postProductUnit(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/config/product-unit/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putProductUnit(@PathVariable Integer id, @Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.putProductUnit(id, request);
        return ResponseEntity.ok(response);
    }

    // ======================== PRODUCT TYPE ========================
    @PostMapping("/config/product-type/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> postProductType(@Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.postProductType(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/config/product-type/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putProductType(@PathVariable Integer id, @Valid @RequestBody ConfigRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.putProductType(id, request);
        return ResponseEntity.ok(response);
    }

    // ======================== LOT ========================

    @GetMapping("/plantations/select")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> getAllPlantations() {
        log.info("[REQUEST] - [Obteniedo plantaciones para select]");
        Response response = lotService.getAllPlantations();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-plantation/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> getLotsByPlantation(@PathVariable Integer id) {
        log.info("[REQUEST] - [Obteniedo lotes por plantación]");
        Response response = lotService.getLotsByPlantation(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> postLot(@Valid @RequestBody LotRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.postLot(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}/{lotTradeId}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> updateLot(@PathVariable Integer id, @PathVariable Integer lotTradeId, @Valid @RequestBody LotRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = lotService.updateLot(id, lotTradeId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}/{lotTradeId}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Response> deleteLot(@PathVariable Integer id, @PathVariable Integer lotTradeId) {
        log.info("[REQUEST] - [Eliminando lote]");
        Response response = lotService.deleteLot(id, lotTradeId);
        return ResponseEntity.ok(response);
    }
}
