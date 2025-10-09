package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.service.ChartOfAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chart-of-accounts")
@RequiredArgsConstructor
public class ChartOfAccountController {

 private final ChartOfAccountService chartOfAccountService;

    @GetMapping
    public ResponseEntity<List<ChartOfAccountDTO>> getAll() {
        return ResponseEntity.ok(chartOfAccountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(chartOfAccountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChartOfAccountDTO> create(@Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.ok(chartOfAccountService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> update(
            @PathVariable("id") Integer id, 
            @Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.ok(chartOfAccountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        chartOfAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}