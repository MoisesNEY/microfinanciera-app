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
@Table(name = "loan_applications")
public class LoanApplication {

  @Id
  private UUID id;

  @PrePersist
  public void prePersist() { if (id == null) id = UUID.randomUUID(); }

  @Column(nullable = false)
  private UUID customerId;        // Nuevo: referencia al microservicio de clientes

  @Column(nullable = false)
  private UUID loanProductId;   // FK logica a loan_products

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal requestedAmount;

  @Column(nullable = false)
  private Integer termMonths;

  @Column(nullable = false, length = 15) // PENDIENTE/APROBADA/RECHAZADA
  private String status;

  @Column(nullable = false)
  private LocalDate applicationDate;

  private LocalDate approvedDate;

  @Column(nullable = false)
  private UUID officerId;       // logica a ms_workers
}
