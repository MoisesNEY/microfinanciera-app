package com.microfinance.customer_microservice.application.service;
import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;

public interface IAddressService {
    AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO);
    AddressResponseDTO getAddressById(UUID id);
    List<AddressResponseDTO> getAllAddresses();
    void deleteAddress(UUID id);
    AddressResponseDTO updateAddress(UUID id, AddressUpdateDTO addressUpdateDTO);
    AddressResponseDTO replaceAddress(UUID id, AddressReplaceDTO addressReplaceDTO);
}
