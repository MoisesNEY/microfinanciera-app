package com.microfinance.customer_microservice.application.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;
import com.microfinance.customer_microservice.domain.entity.ContactInfoEntity;
import com.microfinance.customer_microservice.infrastructure.repository.ClientRepository;

@Mapper(componentModel = "spring")
public abstract class ContactInfoMapper {

    @Autowired
    protected ClientRepository clientRepository;


    @Mapping(source = "client", target = "clientId" , qualifiedByName = "ClientEntityToUUID")
    public abstract ContactInfoResponseDTO toContactInfoResponseDTO(ContactInfoEntity contactInfoEntity);

    @Mapping(source = "clientId", target = "client", qualifiedByName = "UUIDToClientEntity")
    @Mapping(target = "id", ignore = true)
    public abstract ContactInfoEntity toContactInfoEntity(ContactInfoCreateDTO contactInfoEntity);




    @Named("ClientEntityToUUID")
    protected UUID ClientEntityToUUID(ClientEntity clientEntity) {
        return clientEntity != null ? clientEntity.getId() : null;
    }

    @Named("UUIDToClientEntity")
    protected ClientEntity UUIDToClientEntity(UUID clientId) {
        return clientId != null ? clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found")) : null;
    }
}
