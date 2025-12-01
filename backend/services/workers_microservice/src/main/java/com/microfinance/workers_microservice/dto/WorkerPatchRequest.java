package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.DocumentType;
import com.microfinance.workers_microservice.domain.WorkerStatus;

import java.time.LocalDate;

public record WorkerPatchRequest(
        String firstName,
        String lastName,
        String username,

        DocumentType documentType,
        String documentNumber,

        String email,
        String phone,

        Long departmentId,
        Long positionId,

        LocalDate hireDate,
        WorkerStatus status
) {}
