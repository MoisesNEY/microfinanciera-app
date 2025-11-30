package com.microfinance.workers_microservice.dto;

import java.util.List;

public record KeycloakUserWithoutWorkerResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        List<String> realmRoles,
        List<String> clientRoles,
        Long suggestedDepartmentId,
        String suggestedDepartmentName,
        Long suggestedPositionId,
        String suggestedPositionName
) {}