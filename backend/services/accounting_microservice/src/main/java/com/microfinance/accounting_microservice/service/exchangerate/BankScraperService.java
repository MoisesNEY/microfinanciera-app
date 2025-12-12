package com.microfinance.accounting_microservice.service.exchangerate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class BankScraperService {

    @Value("${exchange-rate.banks.lafise-url}")
    private String lafiseUrl;

    @Value("${exchange-rate.banks.bac-url}")
    private String bacUrl;

    @Value("${exchange-rate.banks.scraping-timeout}")
    private int timeout;

    public BankRateResult getBankRates() {
        // Intentar scraping en orden de preferencia/facilidad
        // 1. Lafise (Suele ser más estable para scraping)
        try {
            return scrapeLafise();
        } catch (Exception e) {
            log.warn("Error scraping Lafise, intentando siguiente banco: {}", e.getMessage());

            // 2. Fallback a valores estimados basados en oficial si scraping falla
            // (En un entorno real implementaríamos scraping a BAC/Banpro aquí,
            // pero para esta implementación inicial usaremos un fallback controlado)
            return null;
        }
    }

    private BankRateResult scrapeLafise() throws IOException {
        log.debug("Iniciando scraping a Lafise: {}", lafiseUrl);
        // En ausencia de estructura HTML garantizada, retornamos null para activar
        // fallback
        // En producción, esto se calibraría con el HTML real del banco.
        return null;
    }

    public static class BankRateResult {
        public Double buy;
        public Double sell;
        public String bankName;

        public BankRateResult(Double buy, Double sell, String bankName) {
            this.buy = buy;
            this.sell = sell;
            this.bankName = bankName;
        }
    }
}
