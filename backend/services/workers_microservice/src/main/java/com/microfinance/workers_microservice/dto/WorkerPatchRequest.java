package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.WorkerStatus;

import java.time.LocalDate;

/**
 * DTO para PATCH: todos los campos son opcionales (pueden venir null).
 * No usamos @NotNull/@NotBlank aquí.
 */
public record WorkerPatchRequest(
  String firstName,
  String lastName,
  String document,
  String email,
  String phone,
  String position,
  String department,
  LocalDate hireDate,
  WorkerStatus status
) {}
