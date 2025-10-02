package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.dto.LoanDTOs;
import com.microfinance.loan_microservice.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    public List<Loan> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public Loan one(@PathVariable UUID id) {
        return service.one(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@Valid @RequestBody LoanDTOs.Create dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Loan update(@PathVariable UUID id, @Valid @RequestBody LoanDTOs.Create dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
