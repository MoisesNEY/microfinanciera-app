package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanPayment;
import com.microfinance.loan_microservice.dto.LoanPaymentDTOs;
import com.microfinance.loan_microservice.service.LoanPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-payments")
public class LoanPaymentController {
    private final LoanPaymentService service;

    public LoanPaymentController(LoanPaymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanPayment> all(@RequestParam(value = "loanId", required = false) UUID loanId) {
        if (loanId != null) {
            return service.byLoan(loanId); // Nuevo: listar pagos por préstamo
        }
        return service.all();
    }

    @GetMapping("/{id}")
    public LoanPayment one(@PathVariable UUID id) {
        return service.one(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanPayment create(@Valid @RequestBody LoanPaymentDTOs.Create dto, @RequestHeader("Authorization") String bearerToken) {
        return service.create(dto, bearerToken);
    }

    @PutMapping("/{id}")
    public LoanPayment update(@PathVariable UUID id, @Valid @RequestBody LoanPaymentDTOs.Create dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
