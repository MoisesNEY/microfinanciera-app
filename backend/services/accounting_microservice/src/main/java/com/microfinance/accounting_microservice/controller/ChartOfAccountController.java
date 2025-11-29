package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.service.ChartOfAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/chart-of-accounts")
@RequiredArgsConstructor
public class ChartOfAccountController {

private final ChartOfAccountService chartOfAccountService;

    @GetMapping
    public ResponseEntity<List<ChartOfAccountDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(chartOfAccountService.findAll(deleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(chartOfAccountService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ChartOfAccountDTO> create(@Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chartOfAccountService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.ok(chartOfAccountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        chartOfAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        chartOfAccountService.activate(id);
        return ResponseEntity.ok().build();
    }
}