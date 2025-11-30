package com.microfinance.customer_microservice.application.service;

import com.microfinance.customer_microservice.application.dto.input.OccupationCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.OccupationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IOccupationService {
    OccupationResponseDTO create(OccupationCreateDTO createDTO);
    List<OccupationResponseDTO> getAllActive();
    OccupationResponseDTO getById(UUID id);
    void deactivate(UUID id);
}