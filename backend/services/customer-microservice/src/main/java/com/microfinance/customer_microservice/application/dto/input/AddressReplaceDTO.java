package com.microfinance.customer_microservice.application.dto.input;

import com.microfinance.customer_microservice.domain.enums.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressReplaceDTO {

    @NotNull(message = "Address type cannot be null")
    private AddressType addressType;

    @NotBlank(message = "Department cannot be empty")
    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    @NotBlank(message = "Municipality cannot be empty")
    @Size(max = 100, message = "Municipality cannot exceed 100 characters")
    private String municipality;

    @NotBlank(message = "Street address cannot be empty")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    private String streetAddress;

    @NotNull(message = "Is primary cannot be null")
    private boolean isPrimary;

}
    