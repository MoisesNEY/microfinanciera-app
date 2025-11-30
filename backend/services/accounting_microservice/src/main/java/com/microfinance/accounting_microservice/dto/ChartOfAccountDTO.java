// ChartOfAccountDTO.java
package com.microfinance.accounting_microservice.dto;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChartOfAccountDTO {
    private UUID id;
    private String accountCode;
    private String accountName;
    private String logicalName;
    private String accountType;
    private UUID parentAccountId;
}
