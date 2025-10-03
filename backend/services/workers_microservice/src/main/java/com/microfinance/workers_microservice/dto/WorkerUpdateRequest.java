package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.WorkerStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record WorkerUpdateRequest(
  @NotBlank @Size(max=120) String firstName,
  @NotBlank @Size(max=120) String lastName,
  @NotBlank @Size(max=50)  String document,
  @Email @NotBlank @Size(max=150) String email,
  @Size(max=20) String phone,
  @NotBlank @Size(max=50)  String position,
  @Size(max=50) String department,
  @NotNull LocalDate hireDate,
  @NotNull WorkerStatus status
) {}
