package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chart-of-accounts")
@RequiredArgsConstructor
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;

    @PostMapping
    public ResponseEntity<ChartOfAccountDTO> create(@RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.ok(chartOfAccountService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ChartOfAccountDTO>> getAll() {
        return ResponseEntity.ok(chartOfAccountService.findAll());
    }
}
