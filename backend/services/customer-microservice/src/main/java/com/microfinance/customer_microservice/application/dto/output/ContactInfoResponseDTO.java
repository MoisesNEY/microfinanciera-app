package com.microfinance.customer_microservice.application.dto.output;
import com.microfinance.customer_microservice.domain.enums.ContactType;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoResponseDTO {
    private UUID id;
    private UUID clientId;
    private ContactType contactType;
    private String contactValue;
    private boolean isPrimary;
}
