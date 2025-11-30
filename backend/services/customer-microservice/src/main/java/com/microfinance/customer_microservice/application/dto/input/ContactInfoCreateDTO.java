package com.microfinance.customer_microservice.application.dto.input;

import com.microfinance.customer_microservice.domain.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoCreateDTO {

    @NotNull(message = "Contact Type cannot be null")
    private ContactType contactType;

    @NotBlank(message = "Contact Value cannot be empty")
    @Size(max = 100, message = "Contact Value cannot exceed 100 characters")
    private String contactValue;

    @NotNull(message = "Is Primary cannot be null")
    private boolean isPrimary;
}