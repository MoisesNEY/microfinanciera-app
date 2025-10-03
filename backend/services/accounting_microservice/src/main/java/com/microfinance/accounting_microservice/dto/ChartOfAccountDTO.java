// ChartOfAccountDTO.java
package com.microfinance.accounting_microservice.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChartOfAccountDTO {
    private Integer id;
    private String accountCode;
    private String accountName;
    private String accountType;
    private Integer parentAccountId;
}
