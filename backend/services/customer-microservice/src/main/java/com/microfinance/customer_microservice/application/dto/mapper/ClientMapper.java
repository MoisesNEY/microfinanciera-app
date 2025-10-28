package com.microfinance.customer_microservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;
import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "active", target = "active")
    ClientResponseDTO toClientResponseDTO(ClientEntity clientEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClientEntity toClientEntity(ClientCreateDTO clientCreateDTO);
}
