package com.microfinance.payment_microservice.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<PaymentResponse>> getInactivePayments() {
        return ResponseEntity.ok(paymentService.getInactivePayments());
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLoanId(@PathVariable UUID loanId) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoanId(loanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String bearerToken) {
        // El cashierId se obtiene automáticamente del token JWT
        PaymentResponse savedPayment = paymentService.createAndApply(request, bearerToken);
        return ResponseEntity.ok(savedPayment);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<PaymentResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.setActive(id, false));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<PaymentResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.setActive(id, true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable UUID id, 
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String bearerToken) {
        // El cashierId se obtiene automáticamente del token JWT
        return ResponseEntity.ok(paymentService.update(id, request, bearerToken));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}