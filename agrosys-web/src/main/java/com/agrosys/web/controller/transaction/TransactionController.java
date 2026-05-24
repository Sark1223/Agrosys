package com.agrosys.web.controller.transaction;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/weekly-pay/weeks")
    @ResponseBody
    public ResponseEntity<Response> getWeeks(HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.get("/api/workers/weekly-pay/weeks", Response.class, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-pay/summary")
    @ResponseBody
    public ResponseEntity<Response> getSummary(@RequestParam String weekId, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.get("/api/workers/weekly-pay/summary?weekId=" + weekId, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/weekly-pay/upsert")
    @ResponseBody
    public ResponseEntity<Response> upsert(
            @RequestParam Integer workerId,
            @RequestParam String weekId,
            @RequestParam java.math.BigDecimal amount,
            HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");

        com.agrosys.web.dto.weeklypay.UpsertRequest request = new com.agrosys.web.dto.weeklypay.UpsertRequest();
        request.setWorkerId(workerId);
        request.setWeekId(weekId);
        request.setAmount(amount);

        Response response = gatewayClient.post("/api/workers/weekly-pay/upsert", request, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/weekly-pay/create")
    @ResponseBody
    public ResponseEntity<Response> createPayments(@RequestBody com.agrosys.web.dto.weeklypay.CreatePaymentRequest request, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.post("/api/transaction/weekly-pay/create", request, Response.class, token);
        return ResponseEntity.ok(response);
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
        String token = (String) session.getAttribute("JWT_TOKEN");
        String url = "/api/transaction/list?startDate=" + startDate + "&endDate=" + endDate;
        if (type != null) url += "&type=" + type;
        if (plotId != null) url += "&plotId=" + plotId;
        Response response = gatewayClient.get(url, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Response> getById(@PathVariable Integer id, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.get("/api/transaction/" + id, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Response> register(@RequestBody com.agrosys.web.dto.transaction.TransactionRequest request, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.post("/api/transaction/register", request, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Response> update(@PathVariable Integer id, @RequestBody com.agrosys.web.dto.transaction.TransactionRequest request, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.put("/api/transaction/update/" + id, request, Response.class, token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Response> delete(@PathVariable Integer id, HttpSession session) {
        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_FINANZAS")) {
            return ResponseEntity.status(403).build();
        }
        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.delete("/api/transaction/delete/" + id, Response.class, token);
        return ResponseEntity.ok(response);
    }
}
