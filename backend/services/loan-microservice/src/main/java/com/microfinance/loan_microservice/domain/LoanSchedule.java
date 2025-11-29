package com.microfinance.loan_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loan_schedules")
public class LoanSchedule {

  @Id
  private UUID id;

  @PrePersist
  public void prePersist() { if (id == null) id = UUID.randomUUID(); }

  @Column(nullable = false)
  private UUID loanId;

  @Column(nullable = false)
  private Integer installmentNo; // Nuevo: número de cuota para orden legal

  @Column(nullable = false)
  private LocalDate dueDate;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal principalDue;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal interestDue;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal totalDue;

  @Column(nullable = false, precision = 18, scale = 2)
  @Builder.Default
  private BigDecimal principalPaid = BigDecimal.ZERO; // Nuevo: abono a capital

  @Column(nullable = false, precision = 18, scale = 2)
  @Builder.Default
  private BigDecimal interestPaid = BigDecimal.ZERO; // Nuevo: abono a interés

  @Column(nullable = false, precision = 18, scale = 2)
  @Builder.Default
  private BigDecimal totalPaid = BigDecimal.ZERO; // Nuevo: total pagado por cuota

  @Column(nullable = false, length = 15) // PENDIENTE/PAGADA/ATRASADA
  private String status;

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false;
  private LocalDateTime deletedAt;

}
