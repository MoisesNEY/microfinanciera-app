package com.microfinance.workers_microservice.dto;

import com.microfinance.workers_microservice.domain.WorkerStatus;

import java.util.UUID;

public record WorkerWithoutKeycloakResponse(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String positionName,
        WorkerStatus status
) {}
