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

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(transactionService.findAll(deleted));
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<TransactionResponseDTO>> getDeleted() {
    return ResponseEntity.ok(transactionService.findAll(true));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable UUID id, 
            @Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        transactionService.activate(id);
        return ResponseEntity.ok().build();
    }


}