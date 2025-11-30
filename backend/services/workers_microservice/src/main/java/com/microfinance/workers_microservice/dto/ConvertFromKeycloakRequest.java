package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.DocumentType;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ConvertFromKeycloakRequest(
        @NotNull DocumentType documentType,
        @NotBlank @Size(max = 50) String documentNumber,
        @Size(max = 20) String phone,

        @NotNull Long departmentId,
        @NotNull Long positionId,

        @NotNull LocalDate hireDate,
        @NotNull WorkerStatus status
) {}
