package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.DocumentType;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record WorkerUpdateRequest(
        @NotBlank @Size(max = 120) String firstName,
        @NotBlank @Size(max = 120) String lastName,

        @NotNull DocumentType documentType,
        @NotBlank @Size(max = 50) String documentNumber,

        @Email @NotBlank @Size(max = 150) String email,
        @Size(max = 20) String phone,

        @NotNull Long departmentId,
        @NotNull Long positionId,

        @NotNull LocalDate hireDate,
        @NotNull WorkerStatus status
) {}
