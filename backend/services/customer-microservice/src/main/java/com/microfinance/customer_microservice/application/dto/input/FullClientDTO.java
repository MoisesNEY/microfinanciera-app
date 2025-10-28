package com.microfinance.customer_microservice.application.dto.input;

import lombok.Data;
import java.util.List;

@Data
public class FullClientDTO {
    private ClientCreateDTO client;
    private List<AddressCreateDTO> addresses;
    private List<ContactInfoCreateDTO> contacts;
}