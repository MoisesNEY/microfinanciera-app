package com.microfinance.payment_microservice.web;

import java.util.List;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microfinance.payment_microservice.dto.PaymentRequest;
import com.microfinance.payment_microservice.dto.PaymentResponse;
import com.microfinance.payment_microservice.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request, @RequestHeader("Authorization") String bearerToken) {
        PaymentResponse savedPayment = paymentService.createAndApply(request, bearerToken);
        return ResponseEntity.ok(savedPayment);
    }
    
    @PostMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable UUID id, @RequestBody PaymentRequest request) {
        PaymentResponse updated = paymentService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLoanId(@PathVariable UUID loanId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByLoanId(loanId);
        return ResponseEntity.ok(payments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
