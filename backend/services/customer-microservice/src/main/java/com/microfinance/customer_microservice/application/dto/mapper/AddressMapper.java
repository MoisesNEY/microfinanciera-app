package com.microfinance.customer_microservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.domain.entity.AddressEntity;
import com.microfinance.customer_microservice.infrastructure.repository.ClientRepository;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;

import org.mapstruct.Named;
import java.util.UUID;


@Mapper(componentModel = "spring")
public abstract class AddressMapper {

    @Autowired
    protected ClientRepository clientRepository;

    @Mapping(source = "client", target = "clientId", qualifiedByName = "ClientEntityToUUID")
    public abstract AddressResponseDTO toAddressResponseDTO(AddressEntity addressEntity);

    @Mapping(source = "clientId", target = "client", qualifiedByName = "UUIDToClientEntity")
    @Mapping(target = "id", ignore = true)
    public abstract AddressEntity toAddressEntity(AddressCreateDTO addressCreateDTO);


    @Named("ClientEntityToUUID")
    protected UUID clientEntityToUUID(ClientEntity clientEntity) {
        return clientEntity != null ? clientEntity.getId() : null;
    }

    @Named("UUIDToClientEntity")
    protected ClientEntity UUIDToClientEntity(UUID clientId) {
        return clientId != null ? clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found")) : null;
    }
}