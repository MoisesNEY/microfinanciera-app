// TransactionRequestDTO.java
package com.microfinance.accounting_microservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionRequestDTO {
    private String transactionType;
    private UUID relatedEntityId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
}

