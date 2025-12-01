package com.microfinance.customer_microservice.application.service.impl;

import com.microfinance.customer_microservice.application.dto.input.NationalityCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.NationalityResponseDTO;
import com.microfinance.customer_microservice.application.service.INationalityService;
import com.microfinance.customer_microservice.domain.entity.NationalityEntity;
import com.microfinance.customer_microservice.infrastructure.repository.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NationalityServiceImpl implements INationalityService {

    private final NationalityRepository nationalityRepository;

    @Override
    public NationalityResponseDTO create(NationalityCreateDTO createDTO) {
        if (nationalityRepository.existsByName(createDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una nacionalidad con este nombre");
        }

        NationalityEntity nationality = new NationalityEntity();
        nationality.setName(createDTO.getName());
        nationality.setDemonym(createDTO.getDemonym());

        nationality = nationalityRepository.save(nationality);
        return toResponseDTO(nationality);
    }

    @Override
    public List<NationalityResponseDTO> getAllActive() {
        return nationalityRepository.findByActiveTrue().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NationalityResponseDTO getById(UUID id) {
        NationalityEntity nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nationality not found"));
        return toResponseDTO(nationality);
    }

    @Override
    public void deactivate(UUID id) {
        NationalityEntity nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nationality not found"));
        nationality.setActive(false);
        nationalityRepository.save(nationality);
    }

    private NationalityResponseDTO toResponseDTO(NationalityEntity nationality) {
        NationalityResponseDTO responseDTO = new NationalityResponseDTO();
        responseDTO.setId(nationality.getId());
        responseDTO.setName(nationality.getName());
        responseDTO.setDemonym(nationality.getDemonym());
        responseDTO.setActive(nationality.isActive());
        responseDTO.setCreatedAt(nationality.getCreatedAt());
        responseDTO.setUpdatedAt(nationality.getUpdatedAt());
        return responseDTO;
    }
}