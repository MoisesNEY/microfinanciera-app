package com.microfinance.customer_microservice.application.dto.output;
import com.microfinance.customer_microservice.domain.enums.AddressType;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private UUID id;
    private UUID clientId;
    private AddressType addressType;
    private String department;
    private String municipality;
    private String streetAddress;
    private boolean isPrimary;
}
