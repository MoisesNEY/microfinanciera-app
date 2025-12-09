package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanPayment;
import com.microfinance.loan_microservice.dto.LoanPaymentDTOs;
import com.microfinance.loan_microservice.service.LoanPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/loan-payments")
public class LoanPaymentController {
    private final LoanPaymentService service;

    public LoanPaymentController(LoanPaymentService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'cobrador', 'jefe_caja')")
    @GetMapping
    public List<LoanPayment> all(@RequestParam(value = "loanId", required = false) UUID loanId) {
        if (loanId != null) {
            return service.byLoan(loanId); // Nuevo: listar pagos por préstamo
        }
        return service.all(false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'cobrador', 'jefe_caja')")
    @GetMapping("/{id}")
    public LoanPayment one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'cajero', 'cobrador', 'jefe_caja')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanPayment create(@Valid @RequestBody LoanPaymentDTOs.Create dto,
            @RequestHeader("Authorization") String bearerToken) {
        return service.create(dto, bearerToken);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja')")
    @PutMapping("/{id}")
    public LoanPayment update(@PathVariable UUID id, @Valid @RequestBody LoanPaymentDTOs.Create dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_caja')")
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@PathVariable UUID id) {
        service.Activate(id);
    }
}
