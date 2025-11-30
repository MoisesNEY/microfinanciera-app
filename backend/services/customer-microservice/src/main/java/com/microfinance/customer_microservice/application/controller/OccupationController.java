package com.microfinance.customer_microservice.application.controller;

import com.microfinance.customer_microservice.application.dto.input.OccupationCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.OccupationResponseDTO;
import com.microfinance.customer_microservice.application.service.IOccupationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/occupations")
@RequiredArgsConstructor
public class OccupationController {

    private final IOccupationService occupationService;

    @PostMapping
    public ResponseEntity<OccupationResponseDTO> create(@Valid @RequestBody OccupationCreateDTO createDTO) {
        OccupationResponseDTO created = occupationService.create(createDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OccupationResponseDTO>> getAllActive() {
        List<OccupationResponseDTO> occupations = occupationService.getAllActive();
        return ResponseEntity.ok(occupations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OccupationResponseDTO> getById(@PathVariable UUID id) {
        OccupationResponseDTO occupation = occupationService.getById(id);
        return ResponseEntity.ok(occupation);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        occupationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}