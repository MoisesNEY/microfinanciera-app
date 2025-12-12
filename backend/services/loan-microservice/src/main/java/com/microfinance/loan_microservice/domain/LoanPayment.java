package com.microfinance.loan_microservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loan_payments")
public class LoanPayment {

  @Id
  private UUID id;

  @Column(nullable = false)
  private UUID loanId; // Nuevo: vínculo obligatorio al préstamo

  @Column
  private UUID installmentId; // Nuevo: opcional para mapear a cuota específica

  @Column(nullable = false)
  private LocalDate paymentDate; // Nuevo: fecha de pago trazable

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal amount; // Nuevo: monto abonado

  @Column(nullable = false, length = 30)
  private String method; // Nuevo: método de pago

  @Column(length = 100)
  private String reference; // Nuevo: referencia o comprobante

  @Column(nullable = false, updatable = false)
  private ZonedDateTime createdAt; // Timestamp de creación del pago

  @Column
  private ZonedDateTime updatedAt; // Timestamp de última actualización

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false;

  private ZonedDateTime deletedAt;

  @PrePersist
  protected void onCreate() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    if (createdAt == null) {
      createdAt = ZonedDateTime.now();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = ZonedDateTime.now();
  }
}
