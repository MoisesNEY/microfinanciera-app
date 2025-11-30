package com.microfinance.workers_microservice.dto;

import java.util.List;
import java.util.UUID;

public record InconsistentRoleResponse(
        UUID workerId,
        String firstName,
        String lastName,
        String email,
        String positionName,
        List<String> expectedRoles,
        List<String> actualRoles
) {}
