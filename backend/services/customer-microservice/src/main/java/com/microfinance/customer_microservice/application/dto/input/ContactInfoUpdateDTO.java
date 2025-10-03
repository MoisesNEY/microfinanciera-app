package com.microfinance.customer_microservice.application.dto.input;

import com.microfinance.customer_microservice.domain.enums.ContactType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoUpdateDTO {

    private ContactType contactType;

    @Size(max = 100, message = "Contact Value cannot exceed 100 characters")
    private String contactValue;

    private Boolean isPrimary;

}
