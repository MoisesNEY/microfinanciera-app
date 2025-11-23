package com.microfinance.accounting_microservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.accounting_microservice.dto.PaymentAppliedEvent;
import com.microfinance.accounting_microservice.service.PaymentAccountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accounting")
@RequiredArgsConstructor
public class PaymentAccountingController {

    private final PaymentAccountingService paymentAccountingService;
    private final ObjectMapper objectMapper;

    @PostMapping("/payment-applied")
    public ResponseEntity<?> recordPayment(@RequestBody String requestBody) {
        try {
            log.info("Received payment applied request");
            log.debug("Raw JSON received: {}", requestBody);

            JsonNode jsonNode = objectMapper.readTree(requestBody);
            log.info("JSON structure:");
            jsonNode.fields().forEachRemaining(entry -> log.info("  Field: {} = {} (type: {})",
                entry.getKey(),
                entry.getValue().asText(),
                entry.getValue().getNodeType()));

            PaymentAppliedEvent event = objectMapper.readValue(requestBody, PaymentAppliedEvent.class);
            log.info("Successfully deserialized payment: {}", event.getPaymentId());

            var transaction = paymentAccountingService.recordPayment(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            log.error("Error processing payment applied request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing payment: " + e.getMessage());
        }
    }
}
