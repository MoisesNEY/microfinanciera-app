package com.microfinance.customer_microservice.application.service.impl;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.service.IAddressService;
import com.microfinance.customer_microservice.domain.entity.AddressEntity;
import com.microfinance.customer_microservice.infrastructure.repository.AddressRepository;
import com.microfinance.customer_microservice.application.dto.mapper.AddressMapper;


@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    
    public AddressResponseDTO createAddress(AddressCreateDTO addressCreateDTO) {
        AddressEntity address =  addressMapper.toAddressEntity(addressCreateDTO);
        address = addressRepository.save(address);
        return addressMapper.toAddressResponseDTO(address);
    }

    public AddressResponseDTO getAddressById(UUID id) {
        AddressEntity address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        return addressMapper.toAddressResponseDTO(address);
        }

    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
            .map(addressMapper::toAddressResponseDTO)
            .toList();
    }

    public void deleteAddress(UUID id) {
        addressRepository.deleteById(id);
    }

    public AddressResponseDTO updateAddress(UUID id, AddressUpdateDTO addressUpdateDTO) {
        AddressEntity address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (addressUpdateDTO.getAddressType() != null) {
            address.setAddressType(addressUpdateDTO.getAddressType());
        }
        if (addressUpdateDTO.getDepartment() != null) {
            address.setDepartment(addressUpdateDTO.getDepartment());
        }
        if (addressUpdateDTO.getMunicipality() != null) {
            address.setMunicipality(addressUpdateDTO.getMunicipality());
        }
        if (addressUpdateDTO.getAddressType() != null) {
            address.setAddressType(addressUpdateDTO.getAddressType());
        }
        if (addressUpdateDTO.getIsPrimary() != null) {
            address.setPrimary(addressUpdateDTO.getIsPrimary());
        }

        return addressMapper.toAddressResponseDTO(addressRepository.save(address));
    }

    public AddressResponseDTO replaceAddress(UUID id, AddressReplaceDTO addressReplaceDTO) {
        AddressEntity address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setAddressType(addressReplaceDTO.getAddressType());
        address.setDepartment(addressReplaceDTO.getDepartment());
        address.setMunicipality(addressReplaceDTO.getMunicipality());
        address.setStreetAddress(addressReplaceDTO.getStreetAddress());
        address.setPrimary(addressReplaceDTO.isPrimary());

        return addressMapper.toAddressResponseDTO(addressRepository.save(address));
    }
}
