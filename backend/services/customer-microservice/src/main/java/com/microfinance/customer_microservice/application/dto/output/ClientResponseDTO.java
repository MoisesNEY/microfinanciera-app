package com.microfinance.customer_microservice.application.dto.output;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.domain.enums.DocumentType;
import com.microfinance.customer_microservice.domain.enums.Gender;
import com.microfinance.customer_microservice.domain.enums.Marital_Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private UUID id;
    private String clientCode;
    private String firstName;
    private String lastName;
    private DocumentType idDocumentType;
    private String idDocumentNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private UUID nationalityId;
    private String nationalityName;
    private String nationalityDemonym;
    private UUID occupationId;
    private String occupationName;
    private Marital_Status maritalStatus;
    private String economicActivity;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<AddressResponseDTO> addresses;
    private List<ContactInfoResponseDTO> contactInfoList;
}