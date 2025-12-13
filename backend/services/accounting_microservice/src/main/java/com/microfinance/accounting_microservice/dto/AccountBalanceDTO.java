package com.microfinance.accounting_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceDTO {
    private String accountCode;
    private String accountName;
    private BigDecimal balance;
}
