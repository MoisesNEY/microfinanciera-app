package com.microfinance.loan_microservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class LoanPaymentDTOs {
  public record Create(
      @NotNull UUID loanId, // Nuevo: FK préstamo
      UUID installmentId, // Nuevo: opcional FK cuota
      @NotNull LocalDate paymentDate,
      @NotNull @DecimalMin("0.00") BigDecimal amount, // Nuevo: monto pagado
      @NotBlank String method, // Nuevo: método de pago
      String reference // Nuevo: referencia o comprobante
  ) {}
  public record Update(Create data) {}
}
