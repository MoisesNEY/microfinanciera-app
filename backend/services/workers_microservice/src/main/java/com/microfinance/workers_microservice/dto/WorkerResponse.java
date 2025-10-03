package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.WorkerStatus;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

public record WorkerResponse(
  UUID id, String firstName, String lastName, String document, String phone,
  String email, String position, String department, LocalDate hireDate,
  WorkerStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt
) {}
