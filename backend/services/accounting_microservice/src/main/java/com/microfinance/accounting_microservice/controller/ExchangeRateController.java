package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.ExchangeRateDTO;
import com.microfinance.accounting_microservice.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExchangeRateDTO> getCurrentRate() {
        return ResponseEntity.ok(exchangeRateService.getCurrentExchangeRate());
    }
}
