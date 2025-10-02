package com.microfinance.loan_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan {

  @Id
  private UUID id;

  @PrePersist
  public void prePersist() { if (id == null) id = UUID.randomUUID(); }

  @Column(nullable = false)
  private UUID applicationId;

  @Column(nullable = false, unique = true, length = 20)
  private String loanCode;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal principalAmount;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal interestRate;

  @Column(nullable = false)
  private LocalDate disbursementDate;

  @Column(nullable = false)
  private LocalDate maturityDate;

  @Column(nullable = false, length = 15) // ACTIVO/PAGADO/EN_MORA/CANCELADO
  private String status;

  @Column(nullable = false, length = 100)
  private String sectorEconomico;
}
