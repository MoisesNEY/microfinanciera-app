package com.microfinance.workers_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for updating Position - excludes department field to avoid validation
 * issues
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionUpdateRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede tener más de 50 caracteres")
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede tener más de 120 caracteres")
    private String name;

    @Size(max = 80, message = "El realm role no puede tener más de 80 caracteres")
    private String realmRole;

    @Size(max = 80, message = "El client role no puede tener más de 80 caracteres")
    private String clientRole;

    @Size(max = 200, message = "El client ID no puede tener más de 200 caracteres")
    private String clientId;

    private boolean active = true;
}
