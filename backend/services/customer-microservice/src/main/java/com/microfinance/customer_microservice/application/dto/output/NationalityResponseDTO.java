package com.microfinance.customer_microservice.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NationalityResponseDTO {
    private UUID id;
    private String name;
    private String demonym;
    private boolean active;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}