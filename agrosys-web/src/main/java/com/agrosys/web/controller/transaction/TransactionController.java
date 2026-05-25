package com.agrosys.web.controller.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.transaction.CreatePaymentRequest;
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

    private final GatewayClient gatewayClient;
    private final JwtHelper jwtHelper;

    @GetMapping
    public String transactionPage(HttpSession session, Model model) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return "redirect:/access-denied";
        }
        model.addAttribute("modules", modules);
        return "home/transactions/transactions";
    }

    @GetMapping("/payments")
    public String paymentsPage(HttpSession session, Model model) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return "redirect:/access-denied";
        }
        model.addAttribute("modules", modules);
        return "home/transactions/payments";
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<Response> getHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer plotId,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String url = "/api/transaction/list?startDate=" + startDate + "&endDate=" + endDate;
        if (type != null && !type.isEmpty()) {
            url += "&type=" + type;
        }
        if (plotId != null) {
            url += "&plotId=" + plotId;
        }
        Response response = gatewayClient.get(url, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Integer id, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.get("/api/transaction/" + id, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Response> register(
            @RequestBody TransactionRequest request,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }

        Response response = gatewayClient.post("/api/transaction/register", request, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Response> update(
            @PathVariable Integer id,
            @RequestBody TransactionRequest request,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }

        Response response = gatewayClient.put("/api/transaction/update/" + id, request, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Response> delete(@PathVariable Integer id, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.delete("/api/transaction/delete/" + id, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-pay/weeks")
    public ResponseEntity<Response> getWeeks(HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.get("/api/workers/weekly-pay/weeks", Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-pay/summary")
    public ResponseEntity<Response> getPaymentSummary(@RequestParam Integer weekId, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.get("/api/workers/weekly-pay/summary?weekId=" + weekId, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/weekly-pay/upsert")
    public ResponseEntity<Response> upsertWeeklyPay(@RequestBody Map<String, Object> request, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.post("/api/workers/weekly-pay/upsert", request, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/weekly-pay/create")
    public ResponseEntity<Response> createPayments(@RequestBody CreatePaymentRequest request, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        Response response = gatewayClient.post("/api/transaction/register-payments", request, Response.class, getToken(session));
        return ResponseEntity.ok(response);
    }

    private String getToken(HttpSession session) {
        return session.getAttribute("JWT_TOKEN").toString();
    }
}
