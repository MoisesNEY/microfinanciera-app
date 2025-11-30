package com.microfinance.customer_microservice.application.service;

import com.microfinance.customer_microservice.application.dto.input.NationalityCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.NationalityResponseDTO;

import java.util.List;
import java.util.UUID;

public interface INationalityService {
    NationalityResponseDTO create(NationalityCreateDTO createDTO);
    List<NationalityResponseDTO> getAllActive();
    NationalityResponseDTO getById(UUID id);
    void deactivate(UUID id);
}