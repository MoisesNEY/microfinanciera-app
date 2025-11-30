package com.microfinance.customer_microservice.application.service.impl;

import com.microfinance.customer_microservice.application.dto.input.OccupationCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.OccupationResponseDTO;
import com.microfinance.customer_microservice.application.service.IOccupationService;
import com.microfinance.customer_microservice.domain.entity.OccupationEntity;
import com.microfinance.customer_microservice.infrastructure.repository.OccupationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OccupationServiceImpl implements IOccupationService {

    private final OccupationRepository occupationRepository;

    @Override
    public OccupationResponseDTO create(OccupationCreateDTO createDTO) {
        if (occupationRepository.existsByName(createDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una ocupación con este nombre");
        }

        OccupationEntity occupation = new OccupationEntity();
        occupation.setName(createDTO.getName());
        occupation.setDescription(createDTO.getDescription());

        occupation = occupationRepository.save(occupation);
        return toResponseDTO(occupation);
    }

    @Override
    public List<OccupationResponseDTO> getAllActive() {
        return occupationRepository.findByActiveTrue().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OccupationResponseDTO getById(UUID id) {
        OccupationEntity occupation = occupationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Occupation not found"));
        return toResponseDTO(occupation);
    }

    @Override
    public void deactivate(UUID id) {
        OccupationEntity occupation = occupationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Occupation not found"));
        occupation.setActive(false);
        occupationRepository.save(occupation);
    }

    private OccupationResponseDTO toResponseDTO(OccupationEntity occupation) {
        OccupationResponseDTO responseDTO = new OccupationResponseDTO();
        responseDTO.setId(occupation.getId());
        responseDTO.setName(occupation.getName());
        responseDTO.setDescription(occupation.getDescription());
        responseDTO.setActive(occupation.isActive());
        responseDTO.setCreatedAt(occupation.getCreatedAt());
        responseDTO.setUpdatedAt(occupation.getUpdatedAt());
        return responseDTO;
    }
}