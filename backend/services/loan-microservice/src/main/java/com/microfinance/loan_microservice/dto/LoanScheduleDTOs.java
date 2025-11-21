package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanScheduleDTOs {
  public record Create(
      @NotNull UUID loanId, // Nuevo: FK préstamo
      @NotNull @Min(1) Integer installmentNo, // Nuevo: número de cuota
      @NotNull LocalDate dueDate,
      @NotNull @DecimalMin("0.00") BigDecimal principalDue,
      @NotNull @DecimalMin("0.00") BigDecimal interestDue,
      @NotNull @DecimalMin("0.00") BigDecimal totalDue,
      @DecimalMin("0.00") BigDecimal principalPaid, // Nuevo: capital pagado
      @DecimalMin("0.00") BigDecimal interestPaid, // Nuevo: interés pagado
      @DecimalMin("0.00") BigDecimal totalPaid, // Nuevo: total pagado
      @NotBlank String status
  ) {}
  public record Update(Create data) {}
}
