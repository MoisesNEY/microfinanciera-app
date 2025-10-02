package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanApplication;
import com.microfinance.loan_microservice.dto.LoanApplicationDTOs;
import com.microfinance.loan_microservice.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-applications")
public class LoanApplicationController {
    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanApplication> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public LoanApplication one(@PathVariable UUID id) {
        return service.one(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplication create(@Valid @RequestBody LoanApplicationDTOs.Create dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public LoanApplication update(@PathVariable UUID id, @Valid @RequestBody LoanApplicationDTOs.Create dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
