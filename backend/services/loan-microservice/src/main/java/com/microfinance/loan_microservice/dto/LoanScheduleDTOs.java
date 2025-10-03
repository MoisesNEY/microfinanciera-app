package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanScheduleDTOs {
  public record Create(
      @NotNull UUID loanId,
      @NotNull LocalDate dueDate,
      @NotNull @DecimalMin("0.00") BigDecimal principalDue,
      @NotNull @DecimalMin("0.00") BigDecimal interestDue,
      @NotNull @DecimalMin("0.00") BigDecimal totalDue,
      @NotBlank String status
  ) {}
  public record Update(Create data) {}
}
