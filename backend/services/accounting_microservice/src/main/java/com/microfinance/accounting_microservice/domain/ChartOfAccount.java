
package com.microfinance.accounting_microservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chart_of_accounts")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ChartOfAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "account_code", nullable = false, length = 20)
    private String accountCode;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, columnDefinition = "account_type_enum")
    private AccountType accountType;

    @Column(name = "parent_account_id")
    private Integer parentAccountId;
}
