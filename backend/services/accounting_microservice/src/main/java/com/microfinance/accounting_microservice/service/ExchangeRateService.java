package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.ExchangeRateDTO;
import com.microfinance.accounting_microservice.service.exchangerate.BankScraperService;
import com.microfinance.accounting_microservice.service.exchangerate.BcnSoapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final BcnSoapService bcnSoapService;
    private final BankScraperService bankScraperService;

    // Cacheable: Almacena el resultado por 30 min (configurado en caffeine spec)
    // El nombre del cache "exchange-rates" debe coincidir con configuración si se
    // especificara,
    // pero caffeine default lo manejará.
    @Cacheable(value = "exchangeRates", unless = "#result == null")
    public ExchangeRateDTO getCurrentExchangeRate() {
        log.info("Consultando tasas de cambio externas (sin caché)...");

        Double officialRate = null;
        BankScraperService.BankRateResult bankResult = null;
        String status = "CURRENT";

        // 1. Obtener Tasa Oficial BCN
        try {
            officialRate = bcnSoapService.getOfficialRate();
        } catch (Exception e) {
            log.error("Fallo al obtener tasa BCN: {}", e.getMessage());
            status = "STALE"; // Indica que hubo problemas (aunque si es primera vez será null)
        }

        // Si falló BCN, no tenemos tasa oficial
        if (officialRate == null) {
            log.warn("No se pudo obtener la tasa oficial del BCN.");
            status = "ERROR";
            // No asignamos valor hardcoded, dejamos officialRate en null.
        }

        // 2. Obtener Tasas Bancarias (Scraping)
        // Solo intentamos si tenemos tasa oficial para referencias (o si la lógica de
        // scraping fuera independiente)
        if (officialRate != null) {
            try {
                bankResult = bankScraperService.getBankRates();
            } catch (Exception e) {
                log.error("Fallo al obtener tasas bancarias: {}", e.getMessage());
            }
        }

        // Fallback lógico si falla scraping: Calcular spreads estimados (Solo si
        // tenemos oficial)
        if (bankResult == null && officialRate != null) {
            // ... (existing estimation logic)
            double estimatedBuy = officialRate * 0.99;
            double estimatedSell = officialRate * 1.01;
            bankResult = new BankScraperService.BankRateResult(estimatedBuy, estimatedSell, "Estimado (Calculado)");
        }

        return ExchangeRateDTO.builder()
                .currency("USD")
                .officialRate(officialRate)
                .buyRate(bankResult != null ? bankResult.buy : null)
                .sellRate(bankResult != null ? bankResult.sell : null)
                .bankSource(bankResult != null ? bankResult.bankName : "Desconocido")
                .lastUpdate(ZonedDateTime.now())
                .status(status)
                .build();
    }
}
