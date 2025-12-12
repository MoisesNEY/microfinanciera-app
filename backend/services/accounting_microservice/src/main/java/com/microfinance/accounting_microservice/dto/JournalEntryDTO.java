// JournalEntryDTO.java
package com.microfinance.accounting_microservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryDTO {
    private UUID id;
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private ZonedDateTime entryDate;
}
