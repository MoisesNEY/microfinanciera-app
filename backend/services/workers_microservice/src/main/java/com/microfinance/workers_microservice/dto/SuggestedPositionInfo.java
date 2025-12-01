package com.microfinance.workers_microservice.dto;

public record SuggestedPositionInfo(
        Long departmentId,
        String departmentName,
        Long positionId,
        String positionName
) {}