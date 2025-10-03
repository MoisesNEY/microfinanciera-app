package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.*;
import com.microfinance.accounting_microservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }
}
