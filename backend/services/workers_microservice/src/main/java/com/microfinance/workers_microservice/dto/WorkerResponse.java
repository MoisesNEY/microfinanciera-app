package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.*;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

public record WorkerResponse(
        UUID id,
        String firstName,
        String lastName,
        String username,

        DocumentType documentType,
        String documentNumber,

        String phone,
        String email,

        Long departmentId,
        String departmentName,

        Long positionId,
        String positionName,

        String realmRole,
        String clientRole,

        LocalDate hireDate,
        WorkerStatus status,

        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
