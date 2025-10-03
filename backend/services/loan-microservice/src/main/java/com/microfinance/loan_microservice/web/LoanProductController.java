package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanProduct;
import com.microfinance.loan_microservice.dto.LoanProductDTOs;
import com.microfinance.loan_microservice.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-products")
public class LoanProductController {
    private final LoanProductService service;

    public LoanProductController(LoanProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanProduct> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public LoanProduct one(@PathVariable UUID id) {
        return service.one(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanProduct create(@Valid @RequestBody LoanProductDTOs.Create dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public LoanProduct update(@PathVariable UUID id, @Valid @RequestBody LoanProductDTOs.Create dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
