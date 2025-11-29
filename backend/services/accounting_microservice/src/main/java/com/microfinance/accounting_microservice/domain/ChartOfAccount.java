package com.microfinance.accounting_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "chart_of_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartOfAccount {
    @Id
    private UUID id;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @Column(name = "account_code", nullable = false, length = 20)
    private String accountCode;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    // QUITAR: , columnDefinition = "account_type_enum"
    private AccountType accountType;

    @Column(name = "parent_account_id")
    private UUID parentAccountId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
