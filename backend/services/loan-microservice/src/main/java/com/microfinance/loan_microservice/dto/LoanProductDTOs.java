package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public class LoanProductDTOs {
  public record Create(
      @NotBlank String name,
      String description,
      @NotNull @DecimalMin("0.01") BigDecimal minAmount,
      @NotNull @DecimalMin("0.01") BigDecimal maxAmount,
      @NotNull @DecimalMin("0.00") BigDecimal interestRate,
      @NotNull @Min(1) Integer termMonths,
      @NotNull Boolean isActive
  ) {}
  public record Update(Create data) {}
  public record View(
      UUID id, String name, String description,
      BigDecimal minAmount, BigDecimal maxAmount,
      BigDecimal interestRate, Integer termMonths, Boolean isActive
  ) {}
}
