package com.microfinance.payment_microservice.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microfinance.payment_microservice.dto.PaymentRequest;
import com.microfinance.payment_microservice.dto.PaymentResponse;
import com.microfinance.payment_microservice.service.PaymentService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'jefe_caja', 'cobrador', 'jefe_cobranza', 'contador', 'asistente_contable', 'tesorero', 'subgerente', 'jefe_ti', 'jefe_creditos', 'jefe_finanzas', 'analista_financiero')")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'jefe_caja', 'cobrador', 'jefe_cobranza', 'contador', 'asistente_contable', 'tesorero', 'subgerente', 'jefe_ti', 'jefe_creditos', 'jefe_finanzas', 'analista_financiero')")
    @GetMapping("/inactive")
    public ResponseEntity<List<PaymentResponse>> getInactivePayments() {
        return ResponseEntity.ok(paymentService.getInactivePayments());
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'jefe_caja', 'cobrador', 'jefe_cobranza', 'contador', 'asistente_contable', 'tesorero', 'subgerente', 'jefe_ti', 'analista_financiero', 'jefe_finanzas', 'jefe_creditos')")
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLoanId(@PathVariable UUID loanId) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoanId(loanId));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'jefe_caja', 'cobrador', 'jefe_cobranza', 'contador', 'asistente_contable', 'tesorero', 'subgerente', 'jefe_ti', 'analista_financiero', 'jefe_finanzas', 'jefe_creditos')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'cobrador', 'jefe_caja', 'jefe_cobranza')")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String bearerToken) {
        // El cashierId se obtiene automáticamente del token JWT
        PaymentResponse savedPayment = paymentService.createAndApply(request, bearerToken);
        return ResponseEntity.ok(savedPayment);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja', 'jefe_cobranza', 'contador')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<PaymentResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.setActive(id, false));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja', 'jefe_cobranza', 'contador')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<PaymentResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.setActive(id, true));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja', 'jefe_cobranza', 'contador')")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable UUID id,
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String bearerToken) {
        // El cashierId se obtiene automáticamente del token JWT
        return ResponseEntity.ok(paymentService.update(id, request, bearerToken));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja', 'jefe_cobranza', 'contador')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}