// TransactionResponseDTO.java
package com.microfinance.accounting_microservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionResponseDTO {
    private UUID id;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
}
