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
@Table(name = "loans")
public class Loan {

  @Id
  private UUID id;

  @PrePersist
  public void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  @Column(nullable = false)
  private UUID applicationId;

  @Column(nullable = false)
  private UUID customerId; // Nuevo: referencia al microservicio de clientes, sin datos personales

  @Column(nullable = false, unique = true, length = 20)
  private String loanCode;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal principalAmount;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal interestRate;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal moratoryRate; // Nuevo: tasa de mora limitada al 25% de la corriente

  @Column(nullable = false)
  private Integer termMonths; // Nuevo: plazo en meses para cálculo de cuotas

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentFrequency paymentFrequency; // Nuevo: frecuencia de pago (mensual/quincenal/semanal)

  @Column(nullable = false)
  private LocalDate disbursementDate;

  @Column(nullable = false)
  private LocalDate maturityDate;

  @Column(nullable = false, length = 15) // ACTIVO/PAGADO/EN_MORA/CANCELADO
  private String status;

  @Column(nullable = false, length = 100)
  private String sectorEconomico;

  @Column(nullable = false)
  private LocalDateTime createdAt; // Nuevo: trazabilidad legal

  @Column(nullable = false)
  private LocalDateTime updatedAt; // Nuevo: trazabilidad legal

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false;
  private LocalDateTime deletedAt;

}
