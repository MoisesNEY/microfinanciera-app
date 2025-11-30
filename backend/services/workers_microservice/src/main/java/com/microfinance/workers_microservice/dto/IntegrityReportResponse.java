package com.microfinance.workers_microservice.dto;

import java.util.List;

public record IntegrityReportResponse(
        List<WorkerWithoutKeycloakResponse> workersWithoutKeycloakUsers,
        List<KeycloakUserWithoutWorkerResponse> keycloakUsersWithoutWorkers,
        List<InconsistentRoleResponse> inconsistentRoles
) {}
