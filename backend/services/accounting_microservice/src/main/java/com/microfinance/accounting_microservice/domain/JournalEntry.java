// JournalEntry.java
package com.microfinance.accounting_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalEntry {
    @Id
    private UUID id;

    @PrePersist
    public void ensureId(){
        if (this.id == null) this.id = UUID.randomUUID();
    }

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal debitAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal creditAmount;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;
}
