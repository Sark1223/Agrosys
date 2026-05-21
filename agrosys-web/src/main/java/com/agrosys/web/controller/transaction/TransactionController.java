package com.agrosys.web.controller.transaction;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.transaction.TransactionRequest;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final JwtHelper jwtHelper;
    private final GatewayClient gatewayClient;

    @GetMapping
    public String transactionPage(HttpSession session, Model model) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return "redirect:/access-denied";
        }
        model.addAttribute("modules", modules);
        return "home/transactions/transactions";
    }

    @GetMapping("/history")
    public ResponseEntity<Response> getHistory(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String type,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }

        String url = "/api/transaction/list?startDate=" + startDate + "&endDate" + endDate;
        if (type != null && !type.isEmpty()) {
            url += "&type=" + type;
        }
        Response response = gatewayClient.get(url, Response.class, session.getAttribute("JWT_TOKEN").toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> create(
            @RequestParam String transactionType,
            @RequestParam LocalDate createAt,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) Integer lotTradeId,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }

        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(transactionType);
        request.setCreateAt(createAt);
        request.setAmount(amount);
        request.setDescription(description);
        request.setLotTradeId(lotTradeId);

        Response response = gatewayClient.post("api/transaction/register", request, Response.class, session.getAttribute("JWT_TOKEN").toString());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> update(
            @PathVariable Integer id,
            @RequestParam String transactionType,
            @RequestParam LocalDate createAt,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) Integer lotTradeId,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }

        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(transactionType);
        request.setCreateAt(createAt);
        request.setAmount(amount);
        request.setDescription(description);
        request.setLotTradeId(lotTradeId);

        Response response = gatewayClient.put("api/transaction/update" + id, request, Response.class, session.getAttribute("JWT_TOKEN").toString());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable Integer id, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.delete("api/transaction/delete/" + id, Response.class, session.getAttribute("JWT_TOKEN").toString());
        return ResponseEntity.ok(response);
    }
}
