package com.microfinance.loan_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loan_products")
public class LoanProduct {

  @Id
  private UUID id;

  @PrePersist
  public void prePersist() {
    if (id == null)
      id = UUID.randomUUID();
  }

  @Column(nullable = false, length = 100)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal minAmount;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal maxAmount;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal interestRate;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal moratoryRate; // Nuevo: tasa de mora con tope 25% de corriente

  @Column(nullable = false)
  private Integer termMonths;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentFrequency paymentFrequency; // Nuevo: frecuencia permitida

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false;
  private ZonedDateTime deletedAt;
}
