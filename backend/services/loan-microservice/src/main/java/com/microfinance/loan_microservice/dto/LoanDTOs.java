package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import com.microfinance.loan_microservice.domain.PaymentFrequency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanDTOs {
  public record Create(
      @NotNull UUID applicationId,
      @NotNull UUID customerId, // Nuevo: vínculo legal al cliente externo
      @NotBlank String loanCode,
      @NotNull @DecimalMin("0.01") BigDecimal principalAmount,
      @NotNull @DecimalMin("0.00") BigDecimal interestRate,
      @NotNull @DecimalMin("0.00") BigDecimal moratoryRate, // Nuevo: tasa moratoria con tope
      @NotNull @Min(1) Integer termMonths, // Nuevo: plazo en meses
      @NotNull PaymentFrequency paymentFrequency, // Nuevo: frecuencia de pago
      @NotNull LocalDate disbursementDate,
      @NotNull LocalDate maturityDate,
      @NotBlank String status,
      @NotBlank String sectorEconomico
  ) {}
  public record Update(Create data) {}
}
