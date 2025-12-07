package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanApplicationDTOs {
  public record Create(
      @NotNull UUID customerId, // Nuevo: referencia al customer-microservice
      @NotNull UUID loanProductId,
      @NotNull @DecimalMin("0.01") BigDecimal requestedAmount,
      @NotNull @Min(1) Integer termMonths,
      @NotBlank String status,
      @NotNull LocalDate applicationDate,
      LocalDate approvedDate,
      UUID officerId // Opcional: se obtiene automáticamente del token JWT si no se proporciona
  ) {}
  
  public record Update(Create data) {}
}
