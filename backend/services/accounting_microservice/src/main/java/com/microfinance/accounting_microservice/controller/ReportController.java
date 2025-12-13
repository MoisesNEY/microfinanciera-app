package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.BalanceSheetDTO;
import com.microfinance.accounting_microservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/balance-sheet")
    public ResponseEntity<BalanceSheetDTO> getBalanceSheet(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getBalanceSheet(date));
    }
}
