package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanApplicationDTOs {
  public record Create(
      @NotNull UUID clientId,
      @NotNull UUID loanProductId,
      @NotNull @DecimalMin("0.01") BigDecimal requestedAmount,
      @NotNull @Min(1) Integer termMonths,
      @NotBlank String status,
      @NotNull LocalDate applicationDate,
      LocalDate approvedDate,  
      @NotNull UUID officerId
  ) {}
  
  public record Update(Create data) {}
}