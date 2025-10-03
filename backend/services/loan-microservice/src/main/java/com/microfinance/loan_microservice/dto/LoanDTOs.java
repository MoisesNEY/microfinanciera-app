package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanDTOs {
  public record Create(
      @NotNull UUID applicationId,
      @NotBlank String loanCode,
      @NotNull @DecimalMin("0.01") BigDecimal principalAmount,
      @NotNull @DecimalMin("0.00") BigDecimal interestRate,
      @NotNull LocalDate disbursementDate,
      @NotNull LocalDate maturityDate,
      @NotBlank String status,
      @NotBlank String sectorEconomico
  ) {}
  public record Update(Create data) {}
}
