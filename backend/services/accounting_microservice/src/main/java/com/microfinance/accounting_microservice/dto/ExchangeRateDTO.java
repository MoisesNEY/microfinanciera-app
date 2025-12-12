package com.microfinance.accounting_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO {
    private String currency; // "USD"
    private Double officialRate; // Tasa oficial BCN
    private Double buyRate; // Tasa de compra bancaria
    private Double sellRate; // Tasa de venta bancaria
    private String bankSource; // "Banco Lafise", "BAC", "Banpro"
    private java.time.ZonedDateTime lastUpdate;
    private String status; // "CURRENT", "STALE", "ERROR"
}
