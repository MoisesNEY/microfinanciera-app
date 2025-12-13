package com.microfinance.accounting_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSheetDTO {
    private List<AccountBalanceDTO> assets;
    private List<AccountBalanceDTO> liabilities;
    private List<AccountBalanceDTO> equity;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;
}
