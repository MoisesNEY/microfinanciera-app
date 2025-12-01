package com.microfinance.customer_microservice.application.dto.input;

import java.time.LocalDate;
import java.util.UUID;

import com.microfinance.customer_microservice.domain.enums.DocumentType;
import com.microfinance.customer_microservice.domain.enums.Gender;
import com.microfinance.customer_microservice.domain.enums.Marital_Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDTO {
    
    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @NotNull(message = "Document type cannot be null")
    private DocumentType idDocumentType;

    @NotBlank(message = "Document number cannot be empty")
    @Size(max = 50, message = "Document number cannot exceed 50 characters")
    private String idDocumentNumber;

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @NotNull(message = "Nationality ID cannot be null")
    private UUID nationalityId;

    @NotNull(message = "Occupation ID cannot be null")
    private UUID occupationId;

    private Marital_Status maritalStatus;

    @NotBlank(message = "Economic activity cannot be empty")
    @Size(max = 100, message = "Economic activity cannot exceed 100 characters")
    private String economicActivity;
}