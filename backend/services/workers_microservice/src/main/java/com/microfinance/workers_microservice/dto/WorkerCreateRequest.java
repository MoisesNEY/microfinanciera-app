package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.DocumentType;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record WorkerCreateRequest(
        @NotBlank @Size(max = 120) String firstName,
        @NotBlank @Size(max = 120) String lastName,
        @NotBlank @Size(max = 80)  String username,
        @NotBlank String password,

        @NotNull DocumentType documentType,
        @NotBlank @Size(max = 50) String documentNumber,

        @Email @NotBlank @Size(max = 150) String email,
        @Size(max = 20) String phone,

        @NotNull Long departmentId,
        @NotNull Long positionId,

        @NotNull LocalDate hireDate,
        @NotNull WorkerStatus status
) {}
