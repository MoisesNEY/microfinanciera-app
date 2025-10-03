package com.microfinance.customer_microservice.application.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.microfinance.customer_microservice.domain.enums.AddressType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDTO {
    
    private AddressType addressType;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    @Size(max = 100, message = "Municipality cannot exceed 100 characters")
    private String municipality;

    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    private String streetAddress;

    private Boolean isPrimary;
}
