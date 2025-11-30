package com.microfinance.customer_microservice.application.dto.input;

import java.time.LocalDate;
import java.util.UUID;

import com.microfinance.customer_microservice.domain.enums.DocumentType;
import com.microfinance.customer_microservice.domain.enums.Gender;
import com.microfinance.customer_microservice.domain.enums.Marital_Status;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateDTO {

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    private DocumentType idDocumentType;

    @Size(max = 50, message = "Document number cannot exceed 50 characters")
    private String idDocumentNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    private UUID nationalityId;

    private UUID occupationId;

    private Marital_Status maritalStatus;

    @Size(max = 100, message = "Economic activity cannot exceed 100 characters")
    private String economicActivity;

    private Boolean active;
}