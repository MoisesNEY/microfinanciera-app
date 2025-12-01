package com.microfinance.customer_microservice.application.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NationalityCreateDTO {

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Demonym cannot be empty")
    @Size(max = 100, message = "Demonym cannot exceed 100 characters")
    private String demonym;
}