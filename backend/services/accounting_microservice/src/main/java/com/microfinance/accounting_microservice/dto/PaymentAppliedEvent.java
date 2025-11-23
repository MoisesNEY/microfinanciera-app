package com.microfinance.accounting_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAppliedEvent {
    private UUID paymentId;
    private UUID loanId;
    private BigDecimal capitalAmount;
    private BigDecimal interestAmount;
    private BigDecimal moratoryAmount;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private String description;

    // Cuentas contables involucradas (deben venir del productor o config)
    private Integer cashAccountId;
    private Integer loanReceivableAccountId;
    private Integer interestIncomeAccountId;
    private Integer moratoryIncomeAccountId;
}
