package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.TransactionRequestDTO;
import com.microfinance.accounting_microservice.dto.TransactionResponseDTO;
import com.microfinance.accounting_microservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'auditor_interno', 'jefe_contabilidad', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(transactionService.findAll(deleted));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'auditor_interno', 'jefe_contabilidad', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/deleted")
    public ResponseEntity<List<TransactionResponseDTO>> getDeleted() {
        return ResponseEntity.ok(transactionService.findAll(true));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'auditor_interno', 'jefe_contabilidad', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        transactionService.activate(id);
        return ResponseEntity.ok().build();
    }

}